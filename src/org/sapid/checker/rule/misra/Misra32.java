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
import org.sapid.checker.cx.wrapper.CExpressionElement;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CLiteralElement;
import org.sapid.checker.cx.wrapper.CTagElement;
import org.sapid.checker.cx.wrapper.CVariableReference;
import org.sapid.checker.cx.wrapper.CExpressionElement.Sort;
import org.sapid.checker.cx.wrapper.type.StandardType;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * MISRA-C ルール 32 列挙子並びにおいて，すべての項目が明白に初期化されない限り，第一項以外のメンバを'='を使用して初期化してはならない
 *
 * @author Eiji Hirumuta
 */
public class Misra32 implements CheckerClass {
	/** ルールのレベル */
	private final static int LEVEL = 1;

	/** ルールのメッセージ */
	private final static String MESSAGE = "MISRA-C Rule 32";

	/** 処理系のint型で表現できる最大値 */
	private final static int INT_MAX = (1<<(StandardType.getIntLength()-1))-1 ;

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

		// enum 宣言を探す
		NodeList tags = cfile.getElem().getElementsByTagName("Tag");
		for (int i = 0; i < tags.getLength(); i++) {
			CTagElement tag = new CTagElement((Element) tags.item(i));
			if (tag.getFirstChildNode("kw").getTextContent().equals("enum")) {
				// 中の変数を取得し，チェックする
				Element[] enums = tag.getChildrenNode("Enum");
				// 初期化式から始まる場合
				if (enums[0].getTextContent().contains("=")) {
					for (int j = 1; j < 2; j++) {
						// 二つ目以降 =
						if (enums[j].getTextContent().contains("=")) {
							for (int k = j + 1; k < enums.length; k++) {
								if (!(enums[k].getTextContent().contains("="))) {
									problemNodes.add(enums[k]);
									break;
								}
							}
						} else {
							// 二つ目以降 = でない
							int max_f = 0;
							for (int k = j + 1; k < enums.length; k++) {
								if ((enums[k].getTextContent().contains("="))) {
									max_f = 1;
									problemNodes.add(enums[k]);
									break;
								}
							}
							// 2つ目以降がオーバフローしないかどうかチェック
							/*
							 * if (max_f == 0) { if (checkMaximum(enums[0])) {
							 * problemNodes.add(enums[1]); } }
							 */

							if (max_f == 0) {
								CElement celem = new CElement(enums[0]);
								CLiteralElement[] ls = celem.getLiterals();
								int value = Integer.parseInt(ls[0]
										.getTextContent());
								if ((value + enums.length - 1) > INT_MAX ) {
									problemNodes.add(enums[1]);
								}

							}
						}
					}
				} else {
					// 初期化しない式から始まる場合
					for (int j = 1; j < enums.length; j++) {
						if (enums[j].getTextContent().contains("=")) {
							problemNodes.add(enums[j]);
							break;
						}
					}
				}
			}
		}


		// enum の変数宣言を探す
		CExpressionElement[] es = cfile.getExpressions();
		for (int i = 0; i < es.length; i++) {
			if (es[i].getSortEnum() == Sort.SIZEOF) {
				CExpressionElement[] ess = es[i].getExpressions();
				for (int j = 0; j < ess.length; j++) {
					if (ess[j].isVarRef()) {
						CVariableReference varRef = new CVariableReference(
								ess[j].getElem());
						CDeclarationElement dec = varRef.getDeclaration();
						// enum かどうかチェック
						Element[] kw = dec.getChildrenNode("kw");
						for (int k = 0; k < kw.length; k++) {
							if (kw[k].getTextContent().equals("enum")) {
								// 最後の ident が変数名になる
								Element[] idents = dec.getChildrenNode("ident");
								if (idents[idents.length - 1].getTextContent()
										.equals(
												ess[j].getElem()
														.getTextContent())) {
									problemNodes.add(ess[j].getElem());
								}
							}
						}
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
