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

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.Result;
import org.sapid.checker.cx.flow.FlowStatementElement;
import org.sapid.checker.cx.wrapper.CExpressionElement;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CFlowElement;
import org.sapid.checker.cx.wrapper.CStatementElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;

/**
 * CFlowElement のテストを行うクラス
 * 
 * @author Eiji Hirumuta
 */
public class Flow001 implements CheckerClass {
	/** ルールのレベル */
	private final static int LEVEL = 1;

	/** ルールのメッセージ */
	private final static String MESSAGE = "flow 001";

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
		CFileElement cfile = new CFileElement(file.getDOM());

		CStatementElement[] statements = cfile.getStatments();
		for (int i = 0; i < statements.length; i++) {

			FlowStatementElement aistatement = new FlowStatementElement(statements[i].getElem());
			CFlowElement[] flows = aistatement.getFlows();
			for (int j = 0; j < flows.length; j++) {
				CFlowElement res = null;

				/* f001 */
				if (flows[j].getId().equals("f001")) {
					res = checkFlow(cfile, flows[j], "s50331649", "s58720258",
							"control_normal", "", "", "s58720258", null, null);
				}
				/* f002 */
				if (flows[j].getId().equals("f002")) {
					res = checkFlow(cfile, flows[j], "s58720261", "s58720266",
							"data_dependence", "e58720263", "s33554435",
							"s58720266", "e58720263", "b");
				}
				/* f003 */
				if (flows[j].getId().equals("f003")) {
					res = checkFlow(cfile, flows[j], "s58720261", "s58720266",
							"control_normal", "", "", "s58720266", null, null);
				}
				/* f004 */
				if (flows[j].getId().equals("f004")) {
					res = checkFlow(cfile, flows[j], "s58720258", "s58720261",
							"control_normal", "", "", "s58720261", null, null);
				}

				if (res != null) {
					// 重複するエレメントは problemNodes に追加
					problemNodes.add(res.getElem());
				}

			}

		}
		/* 検出結果を返り値に追加 */
		for (Iterator<Element> itr = problemNodes.iterator(); itr.hasNext();) {
			results.add(new Result(null, new NodeOffsetUtil(itr.next())
					.getRange(), LEVEL, MESSAGE));
		}
		return results;
	}

	private CFlowElement checkFlow(CFileElement cfile, CFlowElement flow,
			String stmt_id, String next, String sort, String expr_id,
			String dep_id, String elem_stmt_id, String elem_expr_id, String var) {
		/* test: getFlowIdStmt() */
		if (!flow.getFlowIdStmt().equals(stmt_id)) {
			return null;
		}

		/* test: getFlowIdNext() */
		if (!flow.getFlowIdNext().equals(next)) {
			return null;
		}

		/* test: getFlowSort() */
		if (!flow.getFlowSort().equals(sort)) {
			return null;
		}

		/* test: getFlowIdExpr() */
		if (!flow.getFlowIdExpr().equals(expr_id)) {
			return null;
		}

		/* test: getFlowIdDepend()() */
		if (!flow.getFlowIdDepend().equals(dep_id)) {
			return null;
		}

		/* test: getCStatementElementByNext(cfile) */
		if (!flow.getCStatementElementByNext(cfile).getId()
				.equals(elem_stmt_id)) {
			return null;
		}

		/* test: getCExpressionElementByExprId(cfile) */
		CExpressionElement e = flow.getCExpressionElementByExprId(cfile);
		if (e == null) {
			if (!(elem_expr_id == null)) {
				return null;
			}
		} else if (!e.getId().equals(elem_expr_id)) {
			return null;
		}

		/* test: getElementByDepId(cfile) */
		Element d = flow.getElementByDepId(cfile);
		if (d == null) {
			if (!(var == null)) {
				return null;
			}
		} else if (!d.getTextContent().equals(var)) {
			return null;
		}

		return flow;
	}
}