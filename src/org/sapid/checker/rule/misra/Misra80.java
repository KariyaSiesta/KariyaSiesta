/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.rule.misra;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.Result;
import org.sapid.checker.cx.wrapper.CExpressionElement;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CVariableReference;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;

/**
 * MISRA-C �롼�� 80
 * @author Eiji Hirumuta
 */
public class Misra80 implements CheckerClass {
    /** �롼��Υ�٥� */
    private final static int LEVEL = 1;

    /** �롼��Υ�å����� */
    private final static String MESSAGE = "MISRA-C Rule 80";

    /** ������� */
    List<Result> results = new ArrayList<Result>();

    /** ��ȿ�Ȥ��Ƹ��Ф���Ρ��ɤν��� */
    Set<Element> problemNodes = new HashSet<Element>();

    /*
     * �ե�����Υ롼������å����˸ƤФ��
     * @return results
     */
    public List<Result> check(IFile file, CheckRule rule) {
        CFileElement cfile = new CFileElement(file.getDOM());
        CExpressionElement[] expressions = cfile.getExpressions();
        for (int i = 0; i < expressions.length; i++) {
        	CVariableReference varRef = new CVariableReference(expressions[i].getElem());
        	if (varRef.isFunction()) {
                CExpressionElement[] varRefexpressions = varRef.getExpressions();
                for (int j = 0; j < varRefexpressions.length; j++) {
                	CVariableReference varRefexpression = new CVariableReference(varRefexpressions[j].getElem());
                    if (varRefexpression.isFunction()) {
    					if (varRefexpression.getFirstChildNode("literal") == null && varRefexpression.getDeclaration().getType().equals("void")) {
    						problemNodes.add(varRefexpression.getElem());
    						// System.out.println(varRefexpression.getElem().getTextContent());
    					}
                    }
                }
        	}
        	//CAssignExpressionElement assign = new CAssignExpressionElement(ss[i].getElem());
           	//assign.
        	//System.out.println(assign.getElem().getTextContent());
        	//System.out.println(ss[i].getElem().getTextContent());
        }
        for (Iterator<Element> itr = problemNodes.iterator(); itr.hasNext();) {
            results.add(new Result(null, new NodeOffsetUtil(itr.next())
                    .getRange(), LEVEL, MESSAGE));
        }
        return results;
    }
}
