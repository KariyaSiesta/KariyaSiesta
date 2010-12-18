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
import org.sapid.checker.cx.wrapper.CForStatementElement;
import org.sapid.checker.cx.wrapper.CStatementElement;
import org.sapid.checker.cx.wrapper.CVariableReference;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;

/**
 * MISRA-C �롼�� �ƥ�ץ졼��
 * 
 * @author Eiji Hirumuta
 */
public class Misra66 implements CheckerClass {
	/** �롼��Υ�٥� */
	private final static int LEVEL = 1;

	/** �롼��Υ�å����� */
	private final static String MESSAGE = "MISRA-C Rule 66";

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
		CStatementElement[] statements = cfile.getStatments();

		// forʸ�Υ��ơ��ȥ��Ȥ�õ��
		for (int i = 0; i < statements.length; i++) {
			if (statements[i].getSort().equals("For")) {
				CForStatementElement forstatement = new CForStatementElement(
						statements[i].getElem());
				List<String> defidList = new ArrayList<String>();

				// �롼�פξ��ʸ�����ѿ������
				CExpressionElement conditionexps = forstatement.getConditionExpression();
				if (conditionexps.isVarRef()) {
				    CVariableReference cvarRef = new CVariableReference(conditionexps.getElem());
				    String defid = cvarRef.getDefinitionId();
				    defidList.add(defid);
				}
				for (CExpressionElement conditionExp : conditionexps.getExpressions()) {
	                if (conditionExp.isVarRef()) {
	                    CVariableReference cvarRef = new CVariableReference(conditionExp.getElem());
	                    String defid = cvarRef.getDefinitionId();
	                    defidList.add(defid);
	                }
                }

				// �롼�פ����ü��Ǹ���
				CExpressionElement incrementalexps = forstatement.getIncrementalExpression();
				if (incrementalexps.isVarRef()) {
				    CVariableReference cvarRef = new CVariableReference(incrementalexps.getElem());
				    if (!(defidList.contains(cvarRef.getDefinitionId()))) {
				        problemNodes.add(incrementalexps.getElem());
				    }
				}
				for (CExpressionElement incrementalExp : incrementalexps.getExpressions()) {
	                if (incrementalExp.isVarRef()) {
	                    CVariableReference cvarRef = new CVariableReference(incrementalExp.getElem());
	                    if (!(defidList.contains(cvarRef.getDefinitionId()))) {
	                        problemNodes.add(incrementalExp.getElem());
	                    }
	                }
                }
				
				// �롼�פν�����Ǹ���
				CExpressionElement initialexps = forstatement.getInitialExpression();
				if (initialexps.isVarRef()) {
						CVariableReference cvarRef = new CVariableReference(initialexps.getElem());
						if (!(defidList.contains(cvarRef.getDefinitionId()))) {
							problemNodes.add(initialexps.getElem());
						}
				}
				for (CExpressionElement initialExp : initialexps.getExpressions()) {   
	                if (initialExp.isVarRef()) {
                        CVariableReference cvarRef = new CVariableReference(initialExp.getElem());
                        if (!(defidList.contains(cvarRef.getDefinitionId()))) {
                            problemNodes.add(initialExp.getElem());
                        }
	                }
                }
			}
		}

		for (Iterator<Element> itr = problemNodes.iterator(); itr.hasNext();) {
			results.add(new Result(null, new NodeOffsetUtil(itr.next())
					.getRange(), LEVEL, MESSAGE));
		}
		return results;
	}

}