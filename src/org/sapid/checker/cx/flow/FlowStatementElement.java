/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.flow;

import java.util.ArrayList;
import java.util.List;

import org.sapid.checker.cx.wrapper.CFlowElement;
import org.sapid.checker.cx.wrapper.CStatementElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * フロー情報を扱う Stmt 要素を表すクラス
 * 
 * @author Eiji Hirumuta
 */
public class FlowStatementElement extends CStatementElement {
	/**
	 * コンストラクタ
	 * 
	 * @param elem
	 */
	public FlowStatementElement(Element elem) {
		super(elem);
		// TODO Auto-generated constructor stub
	}

	/**
	 * ステートメントの id を返す．ない場合は null を返す．
	 */
	public String getStmtId() {
		Node sort = elem.getAttributes().getNamedItem("id");
		if (sort != null) {
			return sort.toString().replace("id=\"", "").replace("\"", "");
		}
		return null;
	}

	/**
	 * is 系は自分が if/switch/for/while/do であるかどうか判断する
	 */
	public boolean isIf() {
		Node sort = elem.getAttributes().getNamedItem("sort");
		if (sort != null) {
			return "If".equals(sort.toString().replace("sort=\"", "").replace(
					"\"", ""));
		}
		return false;
	}

	public boolean isSwitch() {
		Node sort = elem.getAttributes().getNamedItem("sort");
		if (sort != null) {
			return "Switch".equals(sort.toString().replace("sort=\"", "")
					.replace("\"", ""));
		}
		return false;
	}

	public boolean isFor() {
		Node sort = elem.getAttributes().getNamedItem("sort");
		if (sort != null) {
			return "For".equals(sort.toString().replace("sort=\"", "").replace(
					"\"", ""));
		}
		return false;
	}

	public boolean isWhile() {
		Node sort = elem.getAttributes().getNamedItem("sort");
		if (sort != null) {
			return "While".equals(sort.toString().replace("sort=\"", "")
					.replace("\"", ""));
		}
		return false;
	}

	public boolean isDo() {
		Node sort = elem.getAttributes().getNamedItem("sort");
		if (sort != null) {
			return "Do".equals(sort.toString().replace("sort=\"", "").replace(
					"\"", ""));
		}
		return false;
	}

	/**
	 * loopback のフローを持つステートメントかチェックする
	 */
	public boolean isLoopback() {
		CFlowElement[] flows = getFlows();
		for (int i = 0; i < flows.length; i++) {
			Node sort = flows[i].getElem().getAttributes().getNamedItem("sort");
			if (sort != null) {
				return "loopback".equals(sort.toString().replace("sort=\"", "")
						.replace("\"", ""));
			}
		}
		return false;
	}

	/**
	 * loopback を持つとき，その次を差すステートメントの id を取得する．ない場合は null を返す
	 * @return
	 */
	public String getLoopbackId() {
		CFlowElement[] flows = getFlows();
		for (int i = 0; i < flows.length; i++) {
			Node sort = flows[i].getElem().getAttributes().getNamedItem("sort");
			if (sort != null
					&& "loopback".equals(sort.toString().replace("sort=\"", "")
							.replace("\"", ""))) {
				Node next = flows[i].getElem().getAttributes().getNamedItem(
						"next");
				if (next != null) {
					return next.toString().replace("sort=\"", "").replace("\"",
							"");
				}

			}
		}
		return null;
	}

	/**
	 * flow 要素を取得し配列で返す．存在しない場合は長さ 0 の配列を返す
	 * 
	 * @author Eiji Hirumuta
	 * 
	 * @return
	 */
	public CFlowElement[] getFlows() {
		List<CFlowElement> list = new ArrayList<CFlowElement>();
		Node node = elem.getPreviousSibling();
		while (node != null && node.getNodeName().equals("flow")) {
			list.add(new CFlowElement((Element) node));
			node = node.getPreviousSibling();
		}

		return (CFlowElement[]) list.toArray(new CFlowElement[list.size()]);
	}

}
