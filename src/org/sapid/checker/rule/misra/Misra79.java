/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.rule.misra;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.Result;
import org.sapid.checker.cx.wrapper.CAssignExpressionElement;
import org.sapid.checker.cx.wrapper.CExpressionElement;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CVariableReference;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;

/**
 * MISRA-C �롼�� 79
 * 
 * @author Eiji Hirumuta
 */
public class Misra79 implements CheckerClass {
	/** �롼��Υ�٥� */
	private final static int LEVEL = 1;

	/** �롼��Υ�å����� */
	private final static String MESSAGE = "MISRA-C Rule 79";

	/** ������� */
	List<Result> results = new ArrayList<Result>();

	/** ��ȿ�Ȥ��Ƹ��Ф���Ρ��ɤν��� */
	Set<Element> problemNodes = new HashSet<Element>();

	public List<Result> check(IFile file, CheckRule rule) {
		CFileElement cfile = new CFileElement(file.getDOM());
		CExpressionElement[] expressions = cfile.getExpressions();
		
		for (CExpressionElement expression : expressions) {
			if (!expression.isAssign()) {
				continue;
			}
			// ����ʸ�ΤȤ�
			CAssignExpressionElement assign = new CAssignExpressionElement(
					expression.getElem());
			// ���դ����
			CExpressionElement right = assign.getRightHandExpression();
			if (right == null) {
				continue;
			}
			for (CExpressionElement rightExpression : right.getExpressions()) {
				if (!rightExpression.isVarRef()) {
					continue;
				}
				CVariableReference rightvar = new CVariableReference(
						rightExpression.getElem());
				if (rightvar.isFunction()) {
					if (rightvar.getDeclaration().getType().equals("void")) {
						// problemNodes.add(rightvar.getElem());
						results
								.add(new Result(null, new NodeOffsetUtil(
										rightvar.getElem()).getRange(), LEVEL,
										MESSAGE));
					}
				}
			}

		}

		/* problemNodes convert results
		for (Iterator<Element> itr = problemNodes.iterator(); itr.hasNext();) {
			results.add(new Result(null, new NodeOffsetUtil(itr.next())
					.getRange(), LEVEL, MESSAGE));
		}
		*/
		return results;
	}
}
