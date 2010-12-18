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
import org.sapid.checker.cx.wrapper.CDeclarationElement;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * MISRA-C �롼�� 108 ��¤�����϶����Τ�����ˤ����Ƥϡ����ι�¤�����϶����ΤΤ��٤ƤΥ��Ф��������ǻ��ꤵ��ʤ���Фʤ�ʤ�
 *
 * @author Eiji Hirumuta
 */
public class Misra108 implements CheckerClass {
	/** �롼��Υ�٥� */
	private final static int LEVEL = 1;

	/** �롼��Υ�å����� */
	private final static String MESSAGE = "MISRA-C Rule 108";

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

		CDeclarationElement[] ds = cfile.getDeclarations();
		for (int i = 0; i < ds.length; i++) {
			Element[] members = ds[i].getChildrenNode("Member");
			for (int j = 0; j < members.length; j++) {
				// System.out.println(members[j].getTextContent());
				NodeList idents = members[j].getElementsByTagName("ident");
				for (int k = 0; k < idents.getLength(); k++) {
					// System.out.println(idents.item(k).getTextContent());
					if (checkIncomplete(idents.item(k))) {
						problemNodes.add((Element) idents.item(k));
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

	/*
	 * ���������������ɤ��������å�����
	 *
	 * @return
	 */
	private boolean checkIncomplete(Node node) {
		try {

			// Ĺ���ʤ�������ξ��
			Node node1 = node.getNextSibling();
			if ( node1 != null && node1.getTextContent().equals("[") ){
				Node node2 = node1.getNextSibling();
				if( node2 != null && node2.getTextContent().equals("]") ){
					return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}