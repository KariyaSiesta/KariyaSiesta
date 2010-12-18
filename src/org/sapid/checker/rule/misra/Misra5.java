/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.rule.misra;

import java.util.ArrayList;
import java.util.List;

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.Result;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CLiteralElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;

/**
 * MISRA-C  5
 * @author Toshinori OSUKA
 */
public class Misra5 implements CheckerClass {
    /** ¦´ */
    private final static int LEVEL = 1;

    /** ¦´ */
    private final static String MESSAGE = "MISRA-C Rule 5";

    /**  */
    private final static String SINGLE_CHAR_LITERAL = "[A-Za-z0-9]";
    /**  */
    private final static String SINGLE_SYMBOL_LITERAL = "[\\p{Punct}&&[^@`$]]";
    /**  */
    private final static String OTHER_LITERAL = "[ \\s\\a\\r\\cV]";
    /**  */
    private final static String EXT_LITERAL = "[\\\\[tvfabnr\\\"\\\'\\?\\\\]]";
    /**  (16) \x00\xFF */
    private final static String EXT_HEX_LITERAL = "(\\\\x[0-9a-fA-F][0-9a-fA-F])";
    /**  (8) \0\377 */
    private final static String EXT_OCTAL_LITERAL = "(\\\\[0-3]?[0-7]?[0-7])";

    /**  */
    List<Result> results = new ArrayList<Result>();

    public List<Result> check(IFile file, CheckRule rule) {
        CFileElement cfile = new CFileElement(file.getDOM());

        CLiteralElement[] literals = cfile.getLiterals();
        for (int i = 0; i < literals.length; i++) {
        	// §Þ¦´
            if (!literals[i].isChar() && !literals[i].isString()){
                continue;
            }
            // 
            int len = literals[i].getTextContent().length();
            // 
            String content = literals[i].getTextContent().substring(1,len-1);

            if (literals[i].isChar()){
                if (CheckCharContent(content)){
                	continue;
                }
            } else if (literals[i].isString()){
                if (CheckStringContent(content)){
                   	continue;
                   	}
                }
            results.add(new Result(null, new NodeOffsetUtil(literals[i].getElem()).getRange(), LEVEL, MESSAGE));
            }
        return results;
        }

    private boolean CheckCharContent(String content){
    	if (content.matches(SINGLE_CHAR_LITERAL) || content.matches(SINGLE_SYMBOL_LITERAL) ||
    			content.matches(OTHER_LITERAL)   || content.matches(EXT_LITERAL) ||
    			content.matches(EXT_HEX_LITERAL) || content.matches(EXT_OCTAL_LITERAL)) {
            return true;
        }
		return false;
    }

    private boolean CheckStringContent(String content){
    	if (content.matches( "(" + SINGLE_CHAR_LITERAL +"|"+ SINGLE_SYMBOL_LITERAL +"|"+ OTHER_LITERAL +"|"+
    			EXT_LITERAL +"|"+ EXT_HEX_LITERAL +"|"+ EXT_OCTAL_LITERAL + ")*" )){
            return true;
        }
		return false;
    }
}
