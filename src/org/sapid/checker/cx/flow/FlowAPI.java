/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.flow;

import java.util.ArrayList;
import java.util.List;

import org.sapid.checker.cx.wrapper.CElement;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CFlowElement;
import org.sapid.checker.cx.wrapper.CFunctionElement;
import org.sapid.checker.cx.wrapper.CStatementElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * ��ݲ��¹Ԥǻ��Ѥ���ե졼����
 * 
 * @author Eiji Hirumuta
 */
public class FlowAPI {

	/** **/
	protected final int INT_MAX = 32767;
	protected final int INT_MIN = -32768;

	public static void debug(String string) {
		System.out.println("FOR DEBUG: " + string);
	}

	/**
	 * ����ʸ�����֤������Ĥ���ʤ����� null ���֤�
	 * 
	 * @param Document
	 */
	public static CStatementElement[] getDivisionStatements(Document doc) {
		List<CStatementElement> list = new ArrayList<CStatementElement>();
		NodeList ops = doc.getElementsByTagName("op");
		for (int i = 0; i < ops.getLength(); i++) {
			if (ops.item(i).getTextContent().equals("/")) {
				Node tmp = ops.item(i);
				while ((tmp = tmp.getParentNode()) != null) {
					if (tmp.getNodeName().equals("Stmt")) {
						CStatementElement s = new CStatementElement(
								(Element) tmp);
						list.add(s);
						// return s;
					}
				}
			}
		}
		return (CStatementElement[]) list.toArray(new CStatementElement[list
				.size()]);
	}

	/**
	 * �ѥ���������롥branch_true, branch_false, switch_default, switch_case
	 * �ˤ�ä�ʬ�����롥���Ԥ������� null ���֤���
	 * 
	 * @param functionElement
	 */
	public static List<List<FlowStatementElement>> createPath(Document doc,
			CFunctionElement functionElement) {
		List<List<FlowStatementElement>> lists = new ArrayList<List<FlowStatementElement>>();
		Element[] starts = getStartElements(doc);
		for (int i = 0; i < starts.length; i++) {
			//System.out.println("starts:" + starts.length);
			FlowStatementElement ai_start = new FlowStatementElement(starts[i]);

			createPathSub(doc, lists, 0, ai_start);
		}

		return lists;
	}

	private static void createPathSub(Document doc,
			List<List<FlowStatementElement>> lists, int index,
			FlowStatementElement ai_stmt) {

		// ��ʬ���ɲ�
		addItemToPath(lists, index, ai_stmt);

		CFlowElement[] aiflows = getBranchAIFlowElements(ai_stmt);
		int route = aiflows.length;

		if (route == 0) {
			// path end

		} else if (route == 1) {
			// no branch (control_normal)
			if (aiflows[0].getFlowSort().equals("control_normal")) {
				Element stmt_next = getNextElementBySort(doc,
						ai_stmt.getElem(), "control_normal");
				if (stmt_next != null) {
					FlowStatementElement ai_stmt_next = new FlowStatementElement(
							stmt_next);
					createPathSub(doc, lists, index, ai_stmt_next);
				}
			} else if  (aiflows[0].getFlowSort().equals("switch_default")) {

				// TODO when switch_default only

			}

		} else {
			// branch
			List<Integer> indexs = new ArrayList<Integer>();
			indexs.add(index);
			for (int i = 1; i < aiflows.length; i++) {
				indexs.add(duplicateList(lists, index));
			}
			for (int i = 0; i < indexs.size(); i++) {
				Element stmt_next = getNextElementBySort(doc,
						ai_stmt.getElem(), aiflows[i].getFlowSort());
				FlowStatementElement ai_stmt_next = new FlowStatementElement(
						stmt_next);
				createPathSub(doc, lists, indexs.get(i), ai_stmt_next);
			}

		}
	}

	private static void addItemToPath(List<List<FlowStatementElement>> lists,
			int index, FlowStatementElement ai_stmt) {

		if (lists.size() < (index + 1)) {
			List<FlowStatementElement> lists_new = new ArrayList<FlowStatementElement>();
			lists_new.add(ai_stmt);
			lists.add(lists_new);
		} else {
			lists.get(index).add(ai_stmt);
		}

	}

	/**
	 * index �Υꥹ�Ȥ򿷤����Ǹ��ʣ������
	 * 
	 * @param lists
	 * @param index
	 * @return
	 */
	private static int duplicateList(List<List<FlowStatementElement>> lists,
			int index) {
		int new_index = lists.size();
		List<FlowStatementElement> ls = new ArrayList<FlowStatementElement>();
		for (int i = 0; i < lists.get(index).size(); i++) {
			ls.add(lists.get(index).get(i));
		}
		lists.add(ls);

		return new_index;
	}

	/**
	 * Stmt ���Ǥ� control_normal, branch_true, branch_false, switch_default,
	 * switch_case �Υե����Ĥ�Ĵ�١������Υե����Ǥ�������֤����ʤ�����Ĺ�� 0 ��������֤�
	 */
	public static CFlowElement[] getBranchAIFlowElements(
			FlowStatementElement ai_stmt) {
		List<CFlowElement> list = new ArrayList<CFlowElement>();
		CFlowElement[] ai_flows = ai_stmt.getFlows();
		for (int i = 0; i < ai_flows.length; i++) {
			String sort = ai_flows[i].getFlowSort();
			if (sort.equals("control_normal") || sort.equals("branch_true")
					|| sort.equals("branch_false")
					|| sort.equals("switch_default")
					|| sort.equals("switch_case")) {
				list.add(ai_flows[i]);
			}
		}

		return (CFlowElement[]) list.toArray(new CFlowElement[list.size()]);
	}

	/**
	 * ������ Stmt ���ǤΥե��Τ�����°���� sort �Ǥ���ե��� next ������ Stmt ���Ǥ��֤����ʤ����� null ���֤�
	 * 
	 * @param doc
	 * @param stmt
	 * @param sort
	 * @return
	 */
	public static Element getNextElementBySort(Document doc, Element stmt,
			String sort) {
		FlowStatementElement ais = new FlowStatementElement(stmt);
		String next_id = "";
		CFlowElement[] flows = ais.getFlows();

		// get next id
		for (int i = 0; i < flows.length; i++) {
			Node ori_sort = flows[i].getElem().getAttributes().getNamedItem(
					"sort");
			if ((ori_sort != null)
					&& (sort.equals(ori_sort.toString().replace("sort=\"", "")
							.replace("\"", "")))) {
				Node next = flows[i].getElem().getAttributes().getNamedItem(
						"next");
				if (next != null) {
					next_id = next.toString().replace("next=\"", "").replace(
							"\"", "");
				}
			}
		}

		// get element
		Element[] next_stmt = getElementsByNodeNameAndSort(doc, "Stmt", "id",
				next_id);
		if (next_stmt.length != 0) {
			return next_stmt[0];
		}
		return null;
	}

	/**
	 * �Ρ���̾��°��̾��°���ͤǻ��ꤵ������Ǥ�������֤����ʤ�����Ĺ�� 0 ��������֤�
	 */
	public static Element[] getElementsByNodeNameAndSort(Document doc,
			String nodename, String sort, String value) {
		List<Element> list = new ArrayList<Element>();
		NodeList nodes = doc.getElementsByTagName(nodename);
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i).getAttributes().getNamedItem(sort);
			if (node != null) {
				String new_value = node.toString().replace(sort + "=\"", "")
						.replace("\"", "");
				if (new_value.equals(value)) {
					list.add((Element) nodes.item(i));
				}
			}
		}
		return (Element[]) list.toArray(new Element[list.size()]);
	}

	/**
	 * �ؿ��λϤޤ�Ȥʤ륹�ơ��ȥ��Ȥ�������롥�ʤ�����Ĺ�� 0 ��������֤�
	 * 
	 * @param doc
	 * @return
	 */
	public static Element[] getStartElements(Document doc) {
		List<Element> list = new ArrayList<Element>();
		CFileElement cfile = new CFileElement(doc);

		CFunctionElement[] funcs = cfile.getFunctions();
		for (int i = 0; i < funcs.length; i++) {
			Element[] flows = funcs[i].getChildrenNode("flow");
			for (int j = 0; j < flows.length; j++) {
				Node tmp;
				while ((tmp = flows[j].getNextSibling()) != null) {
					if ("Stmt".equals(tmp.getNodeName())) {
						list.add((Element) tmp);
						break;
					}
				}
			}
		}

		return (Element[]) list.toArray(new Element[list.size()]);
	}

	/*
	 * for DEBUG
	 */
	public static void printPaths(List<List<FlowStatementElement>> paths) {
		System.out.println("PRINT_START");
		if (paths != null) {
			for (int i = 0; i < paths.size(); i++) {
				System.out.println(" PATH:" + (i + 1));
				for (FlowStatementElement stmt : paths.get(i)) {
					System.out.println("  ID:" + stmt.getStmtId());

				}
			}
		}

		System.out.println("PRINT_END");
	}

	/**
	 * CElement �������ꡤ�Ƥ򤿤ɤ뤳�ȤǤ��Υ��ơ��ȥ������Ǥ��֤�
	 * @param elem
	 * @return
	 */
	public static CStatementElement getStatementElementByParent(
			CElement elem) {
		Node tmp = (Node) elem.getElem();
		while ((tmp = tmp.getParentNode()) != null) {
			if (tmp.getNodeName().equals("Stmt")) {
				CStatementElement s = new CStatementElement((Element) tmp);
				return s;
			}
		}
		
		return null;
	}

}
