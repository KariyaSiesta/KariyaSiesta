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
import org.sapid.checker.cx.wrapper.CFunctionElement;
import org.sapid.checker.cx.wrapper.CParameterElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;

/**
 * MISRA-C �롼�� 81 ���ΰ������褷�ư�����񤭴����ʤ����Ȥ�տޤ�����ϡ������Ϥ��δؿ��ΰ����� const �����Ҥ���Ѥ���٤��Ǥ���
 * 
 * @author Eiji Hirumuta
 */
public class Misra81 implements CheckerClass {
	/** �롼��Υ�٥� */
	private final static int LEVEL = 1;

	/** �롼��Υ�å����� */
	private final static String MESSAGE = "MISRA-C Rule 81";

	/** ������� */
	List<Result> results = new ArrayList<Result>();

	/** ��ȿ�Ȥ��Ƹ��Ф���Ρ��ɤν��� */
	Set<Element> problemNodes = new HashSet<Element>();

	/*
	 * �ե�����Υ롼������å����˸ƤФ��
	 * 
	 * @return results
	 */
	public List<Result> check(IFile file, CheckRule rule) {
		CFileElement cfile = new CFileElement(file.getDOM());
		CFunctionElement[] functions = cfile.getFunctions();
		for (int i = 0; i < functions.length; i++) {
			CParameterElement[] params = functions[i].getParams();
			for (int j = 0; j < params.length; j++) {
				// �ݥ��󥿤��ɤ��������å�����
				if (params[j].getIdent().getPreviousSibling().getTextContent().equals("*")) {
					// const �����Ҥ����Ѥ���Ƥ��ʤ��������å�����
					Element[] consts = params[j].getChildNodesByNodeNameAndText("kw", "const");
					if (consts.length == 0) {
						problemNodes.add(params[j].getElem());
					}
				}
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