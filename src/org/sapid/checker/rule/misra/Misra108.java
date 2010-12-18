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
import org.sapid.checker.cx.wrapper.CDeclarationElement;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * MISRA-C ルール 108 構造体又は共用体の定義においては，その構造体又は共用体のすべてのメンバが完全型で指定されなければならない
 *
 * @author Eiji Hirumuta
 */
public class Misra108 implements CheckerClass {
	/** ルールのレベル */
	private final static int LEVEL = 1;

	/** ルールのメッセージ */
	private final static String MESSAGE = "MISRA-C Rule 108";

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

		CDeclarationElement[] ds = cfile.getDeclarations();
		for (int i = 0; i < ds.length; i++) {
			Element[] members = ds[i].getChildrenNode("Member");
			for (int j = 0; j < members.length; j++) {
				// System.out.println(members[j].getTextContent());
				NodeList idents = members[j].getElementsByTagName("ident");
				for (int k = 0; k < idents.getLength(); k++) {
					// System.out.println(idents.item(k).getTextContent());
					if (checkIncomplete(idents.item(k))) {
						problemNodes.add((Element) idents.item(k));
					}
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

	/*
	 * 引数が完全型かどうかチェックする
	 *
	 * @return
	 */
	private boolean checkIncomplete(Node node) {
		try {

			// 長さなしの配列の場合
			Node node1 = node.getNextSibling();
			if ( node1 != null && node1.getTextContent().equals("[") ){
				Node node2 = node1.getNextSibling();
				if( node2 != null && node2.getTextContent().equals("]") ){
					return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}