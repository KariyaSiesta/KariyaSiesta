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
import org.sapid.checker.cx.wrapper.CControlStatementElement;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CStatementElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;

/**
 * MISRA-C Rule 53
 * @author Toshinori OSUKA
 */
public class Misra53 implements CheckerClass {

    /** ルールのレベル */
    private final static int LEVEL = 1;

    /** ルールのメッセージ */
    private final static String MESSAGE = "MISRA-C Rule 53";

    /** 検査結果 */
    List<Result> results = new ArrayList<Result>();

    public List<Result> check(IFile file, CheckRule rule) {
        CFileElement cfile = new CFileElement(file.getDOM()
                .getDocumentElement());

        CStatementElement[] stmts = cfile.getStatments();
        for (int i = 0; i < stmts.length; i++) {
            // 副作用を持つものは無視
            if (stmts[i].hasSideEffect()) {
                continue;
            }
            // 制御文も無視
            Element parent = (Element) stmts[i].getElem().getParentNode();
            if (CControlStatementElement.isControlStatement(parent)) {
                if (CControlStatementElement.getInstance(parent)
                		.getConditionExpression().equals(stmts[i])) {
                    continue;
                }
            }

            results.add(new Result(null, new NodeOffsetUtil(stmts[i].getElem())
                    .getRange(), LEVEL, MESSAGE));
        }
        return results;
    }

}
