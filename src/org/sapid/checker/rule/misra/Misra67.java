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
 * @author Eiji Hirumuta
 */
public class Misra67 implements CheckerClass {
    /** �롼��Υ�٥� */
    private final static int LEVEL = 1;

    /** �롼��Υ�å����� */
    private final static String MESSAGE = "MISRA-C Rule 67";

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
        CStatementElement[] statements = cfile.getStatments();
        
        // forʸ�Υ��ơ��ȥ��Ȥ�õ��
        for (int i = 0; i < statements.length; i++) {
        	/*
        	if (statements[i].getSort() == null) {
        		continue;
        	}
        	*/
        	if (statements[i].getSort().equals("For")) {
            	// �롼�פξ�Ｐ�ǻ��Ȥ���Ƥ����ѿ����ѹ��򸫤Ĥ���
            	CForStatementElement forstatement = new CForStatementElement(statements[i].getElem());
            	CExpressionElement conditionexps = forstatement.getConditionExpression();
            	Set<String> countVars = new HashSet<String>();
            	if (conditionexps.isVarRef()) {
            	    CVariableReference cvarRef = new CVariableReference(conditionexps.getElem());
            	    countVars.add(cvarRef.getDefinitionId());
            	}
            	for (CExpressionElement conditionExp : conditionexps.getExpressions()) {
                    if (conditionExp.isVarRef()) {
                        CVariableReference cvarRef = new CVariableReference(conditionExp.getElem());
                        countVars.add(cvarRef.getDefinitionId());
                    }
                }
            	if (countVars.size() > 0) {
                    Element[] blocksforelements = forstatement.getTrueBlock();
                    lookforChangepoint(countVars, blocksforelements);
                }
        	}
        }
        
        for (Iterator<Element> itr = problemNodes.iterator(); itr.hasNext();) {
            results.add(new Result(null, new NodeOffsetUtil(itr.next())
                    .getRange(), LEVEL, MESSAGE));
        }
        return results;
    }

    /*
     * forʸ����(getTrueBlock�᥽�åɤ��֤���)���顢��������ѿ����ѹ������ս��õ��
     */
	private void lookforChangepoint(Set<String> countVars, Element[] blocksforelements) {
		for (int k = 0; k < blocksforelements.length; k++) {
			if (blocksforelements[k].getNodeName().equals("Stmt")) {
				CStatementElement s = new CStatementElement(blocksforelements[k]);
				CExpressionElement[] blockforexpressions = s.getExpressions();
				
				for (int l = 0; l < blockforexpressions.length; l++) {
					if (blockforexpressions[l].isAssign()) {
						CAssignExpressionElement assign = new CAssignExpressionElement(blockforexpressions[l].getElem());
						CExpressionElement leftexpression = assign.getLeftHandExpression();
						CVariableReference cvarRef = new CVariableReference(leftexpression.getElem());
						for (String countVar : countVars) {
	                        if (cvarRef.getDefinitionId().equals(countVar)) {
	                            problemNodes.add(leftexpression.getElem());
	                            break;
	                        }
                        }
						
					} else if (blockforexpressions[l].isDecrement()) {
						CExpressionElement[] expression = blockforexpressions[l].getExpressions();
						for (int m = 0; m < expression.length; m++) {
							CVariableReference cvarRef = new CVariableReference(expression[m].getElem());
							for (String countVar : countVars) {
	                            if (cvarRef.getDefinitionId().equals(countVar)) {
	                                problemNodes.add(expression[m].getElem());
	                                break;
	                            }
                            }
						}
					} else if (blockforexpressions[l].isIncrement()) {
						CExpressionElement[] expression = blockforexpressions[l].getExpressions();
						for (int m = 0; m < expression.length; m++) {
							CVariableReference cvarRef = new CVariableReference(expression[m].getElem());
							for (String countVar : countVars) {
	                            if (cvarRef.getDefinitionId().equals(countVar)) {
	                                problemNodes.add(expression[m].getElem());
	                                break;
	                            }
                            }
						}
					}
				}
			}
		}
	}


}