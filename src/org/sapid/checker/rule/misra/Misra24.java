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
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;

/**
 * MISRA-C �롼�� 24 ���̻Ҥ�Ʊ������ñ�̤ˤ����������ڤӳ�������Ʊ���˻��äƤϤʤ�ʤ�
 * 
 * @author Eiji Hirumuta
 */
public class Misra24 implements CheckerClass {
	/** �롼��Υ�٥� */
	private final static int LEVEL = 1;

	/** �롼��Υ�å����� */
	private final static String MESSAGE = "MISRA-C Rule 24";

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

		CDeclarationElement[] decs = cfile.getVarialbeDeclarations();
		for (int i = 0; i < decs.length; i++) {
			if (!("static".equals(decs[i].getStorage()))) continue;

			String name = decs[i].getName();

			// �����Ҥ�̾������¾�η�����Ӥ��Ƥ���
			Set<Element> candidateNodes = new HashSet<Element>();
			candidateNodes.add(decs[i].getElem());
			boolean conflict = false;

			CElement scope = decs[i].getScope();
			CDeclarationElement[] otherDecs = scope.getVarialbeDeclarations();
			for (int j = 0; j < otherDecs.length; j++) {
				if (name.equals(otherDecs[j].getName())) {
					// Ʊ��̾���Τ�ΤϤ��٤Ʒٹ����
					if ("extern".equals(otherDecs[j].getStorage())) {
						candidateNodes.add(otherDecs[j].getElem());
						conflict = true;
					}
				}
			}

			// ���֤ä����Τ߷ٹ�
			if (conflict) problemNodes.addAll(candidateNodes);
		}

		/* ���з�̤��֤��ͤ��ɲ� */
		for (Iterator<Element> itr = problemNodes.iterator(); itr.hasNext();) {
			results.add(new Result(null, new NodeOffsetUtil(itr.next())
					.getRange(), LEVEL, MESSAGE));
		}
		return results;
	}

}