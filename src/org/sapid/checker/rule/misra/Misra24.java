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
import org.sapid.checker.cx.wrapper.CElement;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;

/**
 * MISRA-C ルール 24 識別子は同一翻訳単位において内部及び外部結合を同時に持ってはならない
 * 
 * @author Eiji Hirumuta
 */
public class Misra24 implements CheckerClass {
	/** ルールのレベル */
	private final static int LEVEL = 1;

	/** ルールのメッセージ */
	private final static String MESSAGE = "MISRA-C Rule 24";

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

		CDeclarationElement[] decs = cfile.getVarialbeDeclarations();
		for (int i = 0; i < decs.length; i++) {
			if (!("static".equals(decs[i].getStorage()))) continue;

			String name = decs[i].getName();

			// 修飾子と名前から他の結合と比較していく
			Set<Element> candidateNodes = new HashSet<Element>();
			candidateNodes.add(decs[i].getElem());
			boolean conflict = false;

			CElement scope = decs[i].getScope();
			CDeclarationElement[] otherDecs = scope.getVarialbeDeclarations();
			for (int j = 0; j < otherDecs.length; j++) {
				if (name.equals(otherDecs[j].getName())) {
					// 同じ名前のものはすべて警告候補
					if ("extern".equals(otherDecs[j].getStorage())) {
						candidateNodes.add(otherDecs[j].getElem());
						conflict = true;
					}
				}
			}

			// ダブった場合のみ警告
			if (conflict) problemNodes.addAll(candidateNodes);
		}

		/* 検出結果を返り値に追加 */
		for (Iterator<Element> itr = problemNodes.iterator(); itr.hasNext();) {
			results.add(new Result(null, new NodeOffsetUtil(itr.next())
					.getRange(), LEVEL, MESSAGE));
		}
		return results;
	}

}