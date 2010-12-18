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
 * // TODO - 全体的に修正する必要あり．正しく検出できない可能性が高い．
 * Flow Rule 001: 0割チェック 数式の再帰的な除算と，if 文による分岐まで解析
 * 
 * @author Eiji Hirumuta
 */
public class FlowRule001 implements CheckerClass {
	/** ルールのレベル */
	private final static int LEVEL = 1;

	/** ルールのメッセージ */
	private final static String MESSAGE = "Flow Rule 001";

	/** 検査結果 */
	List<Result> results = new ArrayList<Result>();

	/** 違反として検出するノードの集合 */
	Set<Element> problemNodes = new HashSet<Element>();

	/*
	 * ファイルのルールチェック時に呼ばれる
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

		/* 検出結果を返り値に追加 */
		for (Iterator<Element> itr = problemNodes.iterator(); itr.hasNext();) {
			results.add(new Result(null, new NodeOffsetUtil(itr.next())
					.getRange(), LEVEL, MESSAGE));
		}
		return results;
	}

	/**
	 * 分母式が含まれるパスを抽出し，(//TODO分母式以後のステートメントを除去したパスを返す)． 重複している場合はユニークにする
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
	 * 演算文字列 (例：1+2) を受取り，それの結果を返す．
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
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
		return null;
	}

	/**
	 * 変数の def を取得・計算し，その変数の value にセットする
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

			// 右辺から変数にセットする
			NodeList def_right_childs = (def_right.getElem()).getChildNodes();
			if (def_right_childs.getLength() == 1) {
				String name = def_right_childs.item(0).getNodeName();

				if (name.equals("literal")) {
					/* 分母が一つのリテラルからなるとき */
					var.setValue(Double.valueOf(def_right_childs.item(0)
							.getTextContent()));
				} else if (name.equals("ident")) {
					/* 分母が一つの変数からなるとき */
					FlowVariableConcrete new_var = new FlowVariableConcrete();
					new_var.setByIdentNodeExceptValue(def_right_childs.item(0));
					setVariables(doc, new_var, path);
					setVariableValue(var, new_var);
				}

			} else {
				/* 分母の中の式が一つではないとき */
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
	 * 変数の value を新しい変数の value に置き換える．
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
	 * 分母の式を受け取り，0割をチェックする．infinity, nanで割ろうとした際にもエラーとする．
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
	 * 計算式の変数を，変数リストの各 value に置換し，その文字列を返す．
	 * 
	 * 変数のうち一つでも value を持ってない場合は null を返す．
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
	 * 演算式を受け取り，変数をリストに追加する
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
	 * 変数がリストの中にあるかどうか調べ，存在しない場合には変数をリストに追加する
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
	 * 定義式を受け取り，その右辺式を返す関数
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
	 * 引数である変数参照式の def を，パスのステートメントの中から探し， CExpressionElement (def の式そのもの) を取得する
	 * 
	 * @param doc
	 * @param 参照される変数の式
	 * @return
	 */
	private CExpressionElement[] getDefineExpressionsInPath(Document doc,
			CExpressionElement e, List<FlowStatementElement> path) {
		List<CExpressionElement> list = new ArrayList<CExpressionElement>();
		if (path != null) {
			for (int i = 0; i < path.size(); i++) {
				CFlowElement[] flows = path.get(i).getFlows();
				for (int j = 0; j < flows.length; j++) {
					// TODO 自分の箇所で切るように改良
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
	 * 引数である変数参照式の def の CExpressionElement (def の式そのもの) を取得する
	 * 
	 * @param doc
	 * @param 参照される変数の式
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
	 * 除算の割る方(分母)の式のリストを返す．見つからない場合は null を返す
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