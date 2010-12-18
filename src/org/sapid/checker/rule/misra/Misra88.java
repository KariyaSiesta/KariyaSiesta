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
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CIncludeElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * MISRA-C �롼�� 88 ��ɸ��ʸ�����#include����Υإå��ե�����̾�˸���ƤϤʤ�ʤ� (", /*, \��3�ĤϽ���)
 * @author Eiji Hirumuta
 */
public class Misra88 implements CheckerClass {
    /** �롼��Υ�٥� */
    private final static int LEVEL = 1;

    /** �롼��Υ�å����� */
    private final static String MESSAGE = "MISRA-C Rule 88";

    /** ������� */
    List<Result> results = new ArrayList<Result>();

    /** ��ȿ�Ȥ��Ƹ��Ф���Ρ��ɤν��� */
    Set<Element> problemNodes = new HashSet<Element>();

    /*
     * �ե�����Υ롼������å����˸ƤФ��
     * @return results
     */
    public List<Result> check(IFile file, CheckRule rule) {
        List<Element> includeList = new ArrayList<Element>();
        NodeList nodeList = file.getDOM().getElementsByTagName("Include");
        for(int i = 0; i < nodeList.getLength(); i++) {
            includeList.add((Element) nodeList.item(i));
        }
        for (Element element : includeList) {
			CIncludeElement include = new CIncludeElement(element);
        	if (include.getHFile().contains("'")) {
				problemNodes.add(include.getElem());
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