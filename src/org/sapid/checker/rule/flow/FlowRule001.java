/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.rule.flow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.Result;
import org.sapid.checker.cx.flow.FlowAPI;
import org.sapid.checker.cx.flow.FlowStatementElement;
import org.sapid.checker.cx.flow.FlowVariableConcrete;
import org.sapid.checker.cx.wrapper.CExpressionElement;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CFlowElement;
import org.sapid.checker.cx.wrapper.CFunctionElement;
import org.sapid.checker.cx.wrapper.CStatementElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * // TODO - ����Ū�˽�������ɬ�פ��ꡥ���������ФǤ��ʤ���ǽ�����⤤��
 * Flow Rule 001: 0������å� �����κƵ�Ū�ʽ����ȡ�if ʸ�ˤ��ʬ���ޤǲ���
 * 
 * @author Eiji Hirumuta
 */
public class FlowRule001 implements CheckerClass {
	/** �롼��Υ�٥� */
	private final static int LEVEL = 1;

	/** �롼��Υ�å����� */
	private final static String MESSAGE = "Flow Rule 001";

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
		Document doc = file.getDOM();
		CFileElement cfile = new CFileElement(doc);

		// System.out.println("----- source code -----");
		// System.out.println(cfile.getElem().getTextContent());
		// System.out.println("----- /source code -----");

		// System.out.println("SOP");

		/* create paths */
		CFunctionElement[] funcs = cfile.getFunctions();
		List<List<FlowStatementElement>> paths = new ArrayList<List<FlowStatementElement>>();
		for (int i = 0; i < funcs.length; i++) {
			paths = FlowAPI.createPath(doc, funcs[i]);
			// AIAPI.printPaths(paths);
		}

		CExpressionElement[] denos = getDenominatorExpressions(doc);
		for (int i = 0; i < denos.length; i++) {
			// System.out.println("---");
			List<List<FlowStatementElement>> paths_denom = createDenominatorPath(
					paths, denos[i]);
			for (int j = 0; j < paths_denom.size(); j++) {
				// System.out.println("-----" + j);
				List<FlowVariableConcrete> vars = new ArrayList<FlowVariableConcrete>();
				extractVariables(vars, denos[i]);
				// printVariableList(vars);
				for (FlowVariableConcrete var : vars) {
					setVariables(doc, var, paths_denom.get(j));
				}
				// printVariableList(vars);
				check0Divided(doc, denos[i], vars);
			}
		}

		// System.out.println("EOP");

		/* ���з�̤��֤��ͤ��ɲ� */
		for (Iterator<Element> itr = problemNodes.iterator(); itr.hasNext();) {
			results.add(new Result(null, new NodeOffsetUtil(itr.next())
					.getRange(), LEVEL, MESSAGE));
		}
		return results;
	}

	/**
	 * ʬ�켰���ޤޤ��ѥ�����Ф���(//TODOʬ�켰�ʸ�Υ��ơ��ȥ��Ȥ������ѥ����֤�)�� ��ʣ���Ƥ�����ϥ�ˡ����ˤ���
	 * 
	 * @param paths
	 * @return
	 */
	private List<List<FlowStatementElement>> createDenominatorPath(
			List<List<FlowStatementElement>> paths, CExpressionElement denom) {
		List<List<FlowStatementElement>> list = new ArrayList<List<FlowStatementElement>>();
		CStatementElement stmt = FlowAPI.getStatementElementByParent(denom);
		String stmt_id = stmt.getId();

		if (paths != null) {
			for (int i = 0; i < paths.size(); i++) {
				for (FlowStatementElement stmt_ai : paths.get(i)) {
					if (stmt_ai.getStmtId().equals(stmt_id)) {
						list.add(paths.get(i));
						break;
					}
				}
			}
		}

		return list;
	}

	/**
	 * �黻ʸ���� (�㡧1+2) �����ꡤ����η�̤��֤���
	 * 
	 * @param cal
	 */
	private Double calculate(String calc) {
		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
		try {
		    Object ret = scriptEngine.eval(calc);
		    if (ret instanceof Double) {
                Double doubleRet = (Double) ret;
                return doubleRet;
		    } else if (ret instanceof Float) {
                Float floatRet = (Float) ret;
                return floatRet.doubleValue();
            } else if (ret instanceof Integer) {
                Integer integerRet = (Integer) ret;
                return integerRet.doubleValue();
            }
		    
		    throw new IllegalArgumentException();
        } catch (ScriptException e) {
            // TODO ��ư�������줿 catch �֥�å�
            e.printStackTrace();
        }
		return null;
	}

	/**
	 * �ѿ��� def ��������׻����������ѿ��� value �˥��åȤ���
	 * 
	 * @param doc
	 * @param var
	 */
	private void setVariables(Document doc, FlowVariableConcrete var,
			List<FlowStatementElement> path) {
		// CExpressionElement[] defs = getDefineExpressions(doc,
		// var.getExpressElem());
		CExpressionElement[] defs = getDefineExpressionsInPath(doc, var
				.getExpressElem(), path);

		for (int i = 0; i < defs.length; i++) {
			CExpressionElement def_right = getRightHandExpression(defs[i]);

			// ���դ����ѿ��˥��åȤ���
			NodeList def_right_childs = (def_right.getElem()).getChildNodes();
			if (def_right_childs.getLength() == 1) {
				String name = def_right_childs.item(0).getNodeName();

				if (name.equals("literal")) {
					/* ʬ�줬��ĤΥ�ƥ�뤫��ʤ�Ȥ� */
					var.setValue(Double.valueOf(def_right_childs.item(0)
							.getTextContent()));
				} else if (name.equals("ident")) {
					/* ʬ�줬��Ĥ��ѿ�����ʤ�Ȥ� */
					FlowVariableConcrete new_var = new FlowVariableConcrete();
					new_var.setByIdentNodeExceptValue(def_right_childs.item(0));
					setVariables(doc, new_var, path);
					setVariableValue(var, new_var);
				}

			} else {
				/* ʬ�����μ�����ĤǤϤʤ��Ȥ� */
				List<FlowVariableConcrete> new_vars = new ArrayList<FlowVariableConcrete>();
				extractVariables(new_vars, def_right);
				for (FlowVariableConcrete new_var : new_vars) {
					setVariables(doc, new_var, path);
				}
				String calc = replaceVariables(def_right, new_vars);
				setVariableValue(var, calculate(calc));
			}
		}
	}

	/**
	 * �ѿ��� value �򿷤����ѿ��� value ���֤������롥
	 * 
	 * @param var
	 * @param new_var
	 */

	private void setVariableValue(FlowVariableConcrete var,
			FlowVariableConcrete new_var) {
		if (new_var.getValue() != null) {
			var.setValue(new_var.getValue());
		}
	}

	private void setVariableValue(FlowVariableConcrete var, Double new_value) {
		var.setValue(new_value);

	}

	/**
	 * ʬ��μ��������ꡤ0�������å����롥infinity, nan�ǳ���Ȥ����ݤˤ⥨�顼�Ȥ��롥
	 * 
	 * @param doc
	 * @param denos
	 * @param i
	 * @param vars
	 */
	private void check0Divided(Document doc, CExpressionElement deno,
			List<FlowVariableConcrete> vars) {
		// printVariableList(vars);

		String calc = replaceVariables(deno, vars);
		Double res = calculate(calc);
		// System.out.println("TEXT: " + deno.getElem().getTextContent());
		// System.out.println("CALC: " + calc);
		// System.out.println("RES:" + res);
		int size = this.problemNodes.size();
		System.out.println(size);
		if (res == 0.0 || res.isInfinite() || res.isNaN()) {
			problemNodes.add(deno.getElem());
			// System.out.println("GOOD!");
		} else {
			// System.out.println("BAD!!");
		}

	}

	/**
	 * �׻������ѿ����ѿ��ꥹ�Ȥγ� value ���ִ���������ʸ������֤���
	 * 
	 * �ѿ��Τ�����ĤǤ� value ����äƤʤ����� null ���֤���
	 * 
	 * @param deno
	 * @param vars
	 * @return
	 */
	private String replaceVariables(CExpressionElement deno,
			List<FlowVariableConcrete> vars) {
		Node clone = deno.getElem().cloneNode(true);
		NodeList idents = ((Element) clone).getElementsByTagName("ident");
		for (int i = 0; i < idents.getLength(); i++) {
			// String name = idents.item(i).getTextContent();
			String defid = idents.item(i).getAttributes().getNamedItem("defid")
					.toString().replace("defid=\"", "").replace("\"", "");
			for (FlowVariableConcrete var : vars) {
				// if (name.equals(var.getName())) {
				if (defid.equals(var.getDefid())) {
					if (var.getValue() == null) {
						return null;
					}
					idents.item(i).setTextContent(
							"(" + var.getValue().toString() + ")");
				}
			}
		}
		return clone.getTextContent();
	}

	/**
	 * �黻���������ꡤ�ѿ���ꥹ�Ȥ��ɲä���
	 * 
	 * @param vars
	 * @param denominator
	 */
	private void extractVariables(List<FlowVariableConcrete> vars,
			CExpressionElement denominator) {
		NodeList nodes_ident = denominator.getElem().getElementsByTagName(
				"ident");

		for (int i = 0; i < nodes_ident.getLength(); i++) {
			FlowVariableConcrete var = new FlowVariableConcrete();
			var.setByIdentNodeExceptValue(nodes_ident.item(i));
			addVariableList(vars, var);
			// printVariableList(vars);
		}

	}

	/**
	 * for DEBUG
	private void printVariableList(List<FlowVariableConcrete> vars) {
		System.out.println("DEBUG_PRINT_START:AIVariable List");
		for (FlowVariableConcrete var : vars) {
			System.out.println(" NAME:" + var.getName());
			System.out.println(" DEFID:" + var.getDefid());
			if (var.getValue() != null) {
				System.out.println(" VALUE:" + var.getValue());
			} else {
				System.out.println(" VALUE:null");
			}
		}
		System.out.println("DEBUG_PRINT_END: AIVariable List");
	}
	*/

	/**
	 * �ѿ����ꥹ�Ȥ���ˤ��뤫�ɤ���Ĵ�١�¸�ߤ��ʤ����ˤ��ѿ���ꥹ�Ȥ��ɲä���
	 * 
	 * @param vars
	 * @param new_var
	 */
	private void addVariableList(List<FlowVariableConcrete> vars,
			FlowVariableConcrete new_var) {
		for (FlowVariableConcrete var : vars) {
			if (var.getDefid().equals(new_var.getDefid())) {
				return;
			}
		}
		vars.add(new_var);
	}

	/**
	 * ������������ꡤ���α��ռ����֤��ؿ�
	 * 
	 */
	private CExpressionElement getRightHandExpression(
			CExpressionElement def_expr) {
		if (def_expr.isAssign()) {
			Element[] es = def_expr.getChildrenNode("Expr");
			return new CExpressionElement(es[1]);
		}
		return null;
	}

	/**
	 * �����Ǥ����ѿ����ȼ��� def �򡤥ѥ��Υ��ơ��ȥ��Ȥ��椫��õ���� CExpressionElement (def �μ����Τ��) ���������
	 * 
	 * @param doc
	 * @param ���Ȥ�����ѿ��μ�
	 * @return
	 */
	private CExpressionElement[] getDefineExpressionsInPath(Document doc,
			CExpressionElement e, List<FlowStatementElement> path) {
		List<CExpressionElement> list = new ArrayList<CExpressionElement>();
		if (path != null) {
			for (int i = 0; i < path.size(); i++) {
				CFlowElement[] flows = path.get(i).getFlows();
				for (int j = 0; j < flows.length; j++) {
					// TODO ��ʬ�βս���ڤ�褦�˲���
					if (flows[j].getFlowIdExpr().equals(e.getId())) {
						list.add(new CExpressionElement(flows[j]
								.getFlowCStatementElement().getChildrenNode(
										"Expr")[0]));
					}
				}
			}
		}

		return (CExpressionElement[]) list.toArray(new CExpressionElement[list
				.size()]);
	}

	/**
	 * �����Ǥ����ѿ����ȼ��� def �� CExpressionElement (def �μ����Τ��) ���������
	 * 
	 * @param doc
	 * @param ���Ȥ�����ѿ��μ�
	 * @return private CExpressionElement[] getDefineExpressions(Document doc,
	 *         CExpressionElement e) { List<CExpressionElement> list = new
	 *         ArrayList<CExpressionElement>(); NodeList flows =
	 *         doc.getElementsByTagName("flow");
	 * 
	 *         for (int i = 0; i < flows.getLength(); i++) { CFlowElement flow =
	 *         new CFlowElement((Element) flows.item(i)); if
	 *         (flow.getFlowIdExpr().equals(e.getId())) { list.add(new
	 *         CExpressionElement(flow.getFlowCStatementElement()
	 *         .getChildrenNode("Expr")[0])); } }
	 * 
	 *         return (CExpressionElement[]) list.toArray(new
	 *         CExpressionElement[list .size()]); }
	 */

	/**
	 * �����γ����(ʬ��)�μ��Υꥹ�Ȥ��֤������Ĥ���ʤ����� null ���֤�
	 * 
	 * @param file
	 */
	private CExpressionElement[] getDenominatorExpressions(Document doc) {
		List<CExpressionElement> list = new ArrayList<CExpressionElement>();

		NodeList ops = doc.getElementsByTagName("op");
		for (int i = 0; i < ops.getLength(); i++) {
			if (ops.item(i).getTextContent().equals("/")) {
				CExpressionElement res = new CExpressionElement((Element) ops
						.item(i).getNextSibling());
				list.add(res);
			}
		}
		return (CExpressionElement[]) list.toArray(new CExpressionElement[list
				.size()]);
	}

}