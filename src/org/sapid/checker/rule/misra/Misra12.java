/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.rule.misra;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.Result;
import org.sapid.checker.cx.wrapper.CDeclarationElement;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CLabelElement;
import org.sapid.checker.cx.wrapper.CNamespace;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;

/**
 * MISRA-C ルール 12
 * @author Toshinori OSUKA
 */
public class Misra12 implements CheckerClass {
    /** ルールのレベル */
    private final static int LEVEL = 1;

    /** ルールのメッセージ */
    private final static String MESSAGE = "MISRA-C Rule 12";

    /** 検査結果 */
    List<Result> results = new ArrayList<Result>();

    public List<Result> check(IFile file, CheckRule rule) {

        CFileElement cfile = new CFileElement(file.getDOM());

        List<Element> idents = new ArrayList<Element>();

        CDeclarationElement[] cdecls = cfile.getDeclarations();
        for (int i = 0; i < cdecls.length; i++) {
            idents.add(cdecls[i].getIdent());
            detect(idents, cdecls[i].getIdent());
        }

        CLabelElement[] clabels = cfile.getLabels();
        for (int i = 0; i < clabels.length; i++) {
	    if (clabels[i].getIdent() == null) {
		continue;
	    }
            idents.add(clabels[i].getIdent());
            detect(idents, clabels[i].getIdent());
        }

        return results;
    }

    private void detect(List<Element> idents, Element ident) {
        for (Iterator<Element> itr = idents.iterator(); itr.hasNext();) {
            Element node = itr.next();
            if (node.getTextContent().equals(ident.getTextContent())
                    && new CNamespace(node).getSort() != new CNamespace(ident)
                            .getSort()) {
                results.add(new Result(null, new NodeOffsetUtil(ident)
                        .getRange(), LEVEL, MESSAGE));
                break;
            }
        }
    }

}
