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
import org.sapid.checker.cx.wrapper.CElement;
import org.sapid.checker.cx.wrapper.CExpressionElement;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CLiteralElement;
import org.sapid.checker.cx.wrapper.CTagElement;
import org.sapid.checker.cx.wrapper.CVariableReference;
import org.sapid.checker.cx.wrapper.CExpressionElement.Sort;
import org.sapid.checker.cx.wrapper.type.StandardType;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * MISRA-C �롼�� 32 �����¤Ӥˤ����ơ����٤Ƥι��ܤ�����˽��������ʤ��¤ꡤ����ʳ��Υ��Ф�'='����Ѥ��ƽ�������ƤϤʤ�ʤ�
 *
 * @author Eiji Hirumuta
 */
public class Misra32 implements CheckerClass {
	/** �롼��Υ�٥� */
	private final static int LEVEL = 1;

	/** �롼��Υ�å����� */
	private final static String MESSAGE = "MISRA-C Rule 32";

	/** �����Ϥ�int����ɽ���Ǥ�������� */
	private final static int INT_MAX = (1<<(StandardType.getIntLength()-1))-1 ;

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

		// enum �����õ��
		NodeList tags = cfile.getElem().getElementsByTagName("Tag");
		for (int i = 0; i < tags.getLength(); i++) {
			CTagElement tag = new CTagElement((Element) tags.item(i));
			if (tag.getFirstChildNode("kw").getTextContent().equals("enum")) {
				// ����ѿ���������������å�����
				Element[] enums = tag.getChildrenNode("Enum");
				// �����������Ϥޤ���
				if (enums[0].getTextContent().contains("=")) {
					for (int j = 1; j < 2; j++) {
						// ����ܰʹ� =
						if (enums[j].getTextContent().contains("=")) {
							for (int k = j + 1; k < enums.length; k++) {
								if (!(enums[k].getTextContent().contains("="))) {
									problemNodes.add(enums[k]);
									break;
								}
							}
						} else {
							// ����ܰʹ� = �Ǥʤ�
							int max_f = 0;
							for (int k = j + 1; k < enums.length; k++) {
								if ((enums[k].getTextContent().contains("="))) {
									max_f = 1;
									problemNodes.add(enums[k]);
									break;
								}
							}
							// 2���ܰʹߤ������Хե����ʤ����ɤ��������å�
							/*
							 * if (max_f == 0) { if (checkMaximum(enums[0])) {
							 * problemNodes.add(enums[1]); } }
							 */

							if (max_f == 0) {
								CElement celem = new CElement(enums[0]);
								CLiteralElement[] ls = celem.getLiterals();
								int value = Integer.parseInt(ls[0]
										.getTextContent());
								if ((value + enums.length - 1) > INT_MAX ) {
									problemNodes.add(enums[1]);
								}

							}
						}
					}
				} else {
					// ��������ʤ�������Ϥޤ���
					for (int j = 1; j < enums.length; j++) {
						if (enums[j].getTextContent().contains("=")) {
							problemNodes.add(enums[j]);
							break;
						}
					}
				}
			}
		}


		// enum ���ѿ������õ��
		CExpressionElement[] es = cfile.getExpressions();
		for (int i = 0; i < es.length; i++) {
			if (es[i].getSortEnum() == Sort.SIZEOF) {
				CExpressionElement[] ess = es[i].getExpressions();
				for (int j = 0; j < ess.length; j++) {
					if (ess[j].isVarRef()) {
						CVariableReference varRef = new CVariableReference(
								ess[j].getElem());
						CDeclarationElement dec = varRef.getDeclaration();
						// enum ���ɤ��������å�
						Element[] kw = dec.getChildrenNode("kw");
						for (int k = 0; k < kw.length; k++) {
							if (kw[k].getTextContent().equals("enum")) {
								// �Ǹ�� ident ���ѿ�̾�ˤʤ�
								Element[] idents = dec.getChildrenNode("ident");
								if (idents[idents.length - 1].getTextContent()
										.equals(
												ess[j].getElem()
														.getTextContent())) {
									problemNodes.add(ess[j].getElem());
								}
							}
						}
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
