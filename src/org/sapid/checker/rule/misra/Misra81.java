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
import org.sapid.checker.cx.wrapper.CParameterElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;

/**
 * MISRA-C ルール 81 その引数が決して引数を書き換えないことを意図する場合は，参照渡しの関数の引数に const 修飾子を使用するべきである
 * 
 * @author Eiji Hirumuta
 */
public class Misra81 implements CheckerClass {
	/** ルールのレベル */
	private final static int LEVEL = 1;

	/** ルールのメッセージ */
	private final static String MESSAGE = "MISRA-C Rule 81";

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
		CFunctionElement[] functions = cfile.getFunctions();
		for (int i = 0; i < functions.length; i++) {
			CParameterElement[] params = functions[i].getParams();
			for (int j = 0; j < params.length; j++) {
				// ポインタかどうかチェックする
				if (params[j].getIdent().getPreviousSibling().getTextContent().equals("*")) {
					// const 修飾子が使用されていないかチェックする
					Element[] consts = params[j].getChildNodesByNodeNameAndText("kw", "const");
					if (consts.length == 0) {
						problemNodes.add(params[j].getElem());
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

}