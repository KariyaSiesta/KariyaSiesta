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
 * �ե�����򰷤� Stmt ���Ǥ�ɽ�����饹
 * 
 * @author Eiji Hirumuta
 */
public class FlowStatementElement extends CStatementElement {
	/**
	 * ���󥹥ȥ饯��
	 * 
	 * @param elem
	 */
	public FlowStatementElement(Element elem) {
		super(elem);
		// TODO Auto-generated constructor stub
	}

	/**
	 * ���ơ��ȥ��Ȥ� id ���֤����ʤ����� null ���֤���
	 */
	public String getStmtId() {
		Node sort = elem.getAttributes().getNamedItem("id");
		if (sort != null) {
			return sort.toString().replace("id=\"", "").replace("\"", "");
		}
		return null;
	}

	/**
	 * is �Ϥϼ�ʬ�� if/switch/for/while/do �Ǥ��뤫�ɤ���Ƚ�Ǥ���
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
	 * loopback �Υե�����ĥ��ơ��ȥ��Ȥ������å�����
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
	 * loopback ����ĤȤ������μ��򺹤����ơ��ȥ��Ȥ� id ��������롥�ʤ����� null ���֤�
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
	 * flow ���Ǥ������������֤���¸�ߤ��ʤ�����Ĺ�� 0 ��������֤�
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
