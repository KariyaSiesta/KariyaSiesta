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
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;

/**
 * MISRA-C �롼�� 123 <signal.h> �ˤ��륷���ʥ����ϻ��Ѥ��ƤϤʤ�ʤ�
 * @author Eiji Hirumuta
 */
public class Misra123 implements CheckerClass {
    /** �롼��Υ�٥� */
    private final static int LEVEL = 1;

    /** �롼��Υ�å����� */
    private final static String MESSAGE = "MISRA-C Rule 123";

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
        	if (expressions[i].getElem().getTextContent().equals("signal")) {
                // ��ʣ���륨����Ȥ� problemNodes ���ɲ�
        		problemNodes.add(expressions[i].getElem());
			}
        }
        
        /* ���з�̤��֤��ͤ��ɲ� */
        for (Iterator<Element> itr = problemNodes.iterator(); itr.hasNext();) {
            results.add(new Result(null, new NodeOffsetUtil(itr.next())
                    .getRange(), LEVEL, MESSAGE));
        }
        return results;
    }

}