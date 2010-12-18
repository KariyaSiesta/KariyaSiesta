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
import org.sapid.checker.cx.wrapper.CAssignExpressionElement;
import org.sapid.checker.cx.wrapper.CDeclarationElement;
import org.sapid.checker.cx.wrapper.CExpressionElement;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CLiteralElement;
import org.sapid.checker.cx.wrapper.CVariableReference;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;

/**
 * MISRA-C �롼�� 18 Ŭ�ڤ������줬���ѤǤ���ʤ顤����Ϸ��򼨤��������Ĥ��ʤ줫�Фʤ�ʤ�
 * 
 * @author Eiji Hirumuta
 */
public class Misra18 implements CheckerClass {
	/** �롼��Υ�٥� */
	private final static int LEVEL = 1;

	/** �롼��Υ�å����� */
	private final static String MESSAGE = "MISRA-C Rule 18";

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

		CExpressionElement[] expressions = cfile.getExpressions();
		for (int i = 0; i < expressions.length; i++) {

			if (expressions[i].isAssign()) {
				CAssignExpressionElement ae = new CAssignExpressionElement(
						expressions[i].getElem());
				// ���դΥ����å�
				CExpressionElement rightElem = ae.getRightHandExpression();
				// ���ѱ黻�ʤΤǿ����Τ�
				if (rightElem.isArith()) {
					Set<String> sign = new HashSet<String>();
					Set<String> byteType = new HashSet<String>();
					// ��ƥ���õ���Ф�
					CLiteralElement[] ls = rightElem.getLiterals();
					for (int j = 0; j < ls.length; j++) {
						// System.out.println(ls[j].getElem().getTextContent());
						String l = ls[j].getElem().getTextContent();
						l = l.toUpperCase();
						if (l.contains("UL")) {
							sign.add("unsigned");
							byteType.add("long");
						} else if (l.contains("L")) {
							sign.add("signed");
							byteType.add("long");
						} else if (l.contains("U")) {
							sign.add("unsigned");
							byteType.add("int");
						} else {
							sign.add("signed");
							byteType.add("int");
						}
					}

					// �ѿ����Ȥ�õ��
					CExpressionElement[] es = rightElem.getExpressions();
					for (int j = 0; j < es.length; j++) {
						if (es[j].isVarRef()) {
							CVariableReference rightVarRef = new CVariableReference(
									es[j].getElem());
							CDeclarationElement rightDec = rightVarRef
									.getDeclaration();

							Element[] kws = rightDec.getChildrenNode("kw");
							for (int k = 0; k < kws.length; k++) {
								if (kws[k].getTextContent().contains("signed")) {
									sign.add(kws[k].getTextContent());
								}
								if (kws[k].getTextContent().contains("long")) {
									byteType.add(kws[k].getTextContent());
								}
							}
						}
					}
					
					// ���դα黻�� signed �� unsigned ���԰��פξ��
					if (sign.size() != 1) {
						problemNodes.add(rightElem.getElem());
					} else {
						// ���դΥ����å�
						CExpressionElement leftElem = ae
								.getLeftHandExpression();
						CVariableReference leftVarRef = new CVariableReference(
								leftElem.getElem());
						CDeclarationElement leftDec = leftVarRef
								.getDeclaration();
						Element[] kws = leftDec.getChildrenNode("kw");
						int long_f = 0;
						for (int k = 0; k < kws.length; k++) {
							if (kws[k].getTextContent().contains("signed")) {
								sign.add(kws[k].getTextContent());
							}
							if (kws[k].getTextContent().contains("long")) {
								long_f = 1;
							}
						}
						// ���դȺ��դ� signed �� unsigned ���԰��פξ��
						if (sign.size() != 1) {
							problemNodes.add(leftElem.getElem());
						}
						// ���դ� long �ǤϤʤ������ġ����դ� long�Ǥ���Ȥ�
						if (long_f == 0 && byteType.contains("long")) {
							problemNodes.add(leftElem.getElem());
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