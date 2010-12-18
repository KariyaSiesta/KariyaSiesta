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
import org.sapid.checker.cx.wrapper.CExpressionElement;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * MISRA-C �롼�� 47
 * @author Toshinori OSUKA
 */
public class Misra47 implements CheckerClass {
    /** �롼��Υ�٥� */
    private final static int LEVEL = 1;

    /** �롼��Υ�å����� */
    private final static String MESSAGE = "MISRA-C Rule 47";

    /** ������� */
    List<Result> results = new ArrayList<Result>();

    public List<Result> check(IFile file, CheckRule rule) {

        CFileElement cfile = new CFileElement(file.getDOM());

        CExpressionElement[] exprs = cfile.getExpressions();
        for (int i = 0; i < exprs.length; i++) {
            if (isIgnorable(exprs[i])) {
                continue;
            }
            boolean isWeak = isWeak(exprs[i]);
            // Ϣ³����黻�� CX-model �ǤϿƻҴط��� Expr �ˤʤ�
            Node parent = exprs[i].getElem().getParentNode();
            if (!CExpressionElement.isExpression(parent)) {
                continue;
            }
            CExpressionElement pexpr = new CExpressionElement((Element) parent);
            if (isIgnorable(pexpr) || isWeak && isWeak(pexpr)) {
                continue;
            }
            results.add(new Result(null, new NodeOffsetUtil(pexpr.getElem())
                    .getRange(), LEVEL, MESSAGE));
        }
        return results;
    }

    /**
     * �黻�ǤϤʤ���Ρ���̱黻�����
     * @param expr
     * @return
     */
    private boolean isIgnorable(CExpressionElement expr) {
        return expr.getFirstChildNode("op") == null || expr.isParen();
    }

    /**
     * Ϣ³���Ƥ�����Τʤ��黻�Ҥ��ɤ���
     * @param expr
     * @return
     */
    private boolean isWeak(CExpressionElement expr) {
        return expr.isAssign() || expr.isIncrement() || expr.isDecrement()
                || expr.isArith();
    }
}
