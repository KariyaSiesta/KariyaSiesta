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
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CFunctionElement;
import org.sapid.checker.cx.wrapper.CStatementElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;

/**
 * TODO - 記述が不十分．
 * MISRA-C ルール 82
 * 
 * @author Eiji Hirumuta
 */
public class Misra82 implements CheckerClass {
	/** ルールのレベル */
	private final static int LEVEL = 1;

	/** ルールのメッセージ */
	private final static String MESSAGE = "MISRA-C Rule 82";

	/** 検査結果 */
	List<Result> results = new ArrayList<Result>();

	/** 違反として検出するノードの集合 */
	Set<Element> problemNodes = new HashSet<Element>();

	public List<Result> check(IFile file, CheckRule rule) {
		CFileElement cfile = new CFileElement(file.getDOM());
		CFunctionElement[] functions = cfile.getFunctions();
		for (int i = 0; i < functions.length; i++) {
			CStatementElement[] statements = functions[i].getStatments();
			int returnCount = 0;
			for (int j = 0; j < statements.length; j++) {
				if (statements[j].isReturnStatement()) {
					returnCount++;
				}
			}
			switch (returnCount) {
			case 0:
				/* OK case */
				break;
			case 1:
				/*
				// TODO
				// return文が一つのvoid関数が、関数の最後に到達するか判断不可能
				if (functions[i].getType().equals("void")) {
					PathGraph graph = new PathGraph(functions[i]);
					List<List<GraphNode<Element>>> paths = graph.toPathList();
					for (Iterator<List<GraphNode<Element>>> itr = paths
							.iterator(); itr.hasNext();) {
						List<GraphNode<Element>> path = itr.next();
						System.out.println("path size >>" + path.size());
						checkPath(path);
					}
				}
				// */
				break;
			default:
				problemNodes.add(functions[i].getElem());
				break;
			}

		}
		for (Iterator<Element> itr = problemNodes.iterator(); itr.hasNext();) {
			results.add(new Result(null, new NodeOffsetUtil(itr.next())
					.getRange(), LEVEL, MESSAGE));
		}
		return results;
	}

	/*
	 * パスチェック
	 * 
	 * @param path
	private void checkPath(List<GraphNode<Element>> path) {
		for (Iterator<GraphNode<Element>> itr = path.iterator(); itr.hasNext();) {
			GraphNode<Element> node = itr.next();
			//System.out.println(node.getContent().getNodeName());
			System.out.println(node.getContent().getTextContent());

		}
	}
	 */

}
