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
import org.sapid.checker.cx.wrapper.CFunctionElement;
import org.sapid.checker.cx.wrapper.CPrototypeElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * MISRA-C ルール 74 引数の一部でも識別子が与えられた場合、宣言や定義で使用された識別子は等しくなければならない
 * 
 * @author Eiji Hirumuta
 */
public class Misra74 implements CheckerClass {
	/** ルールのレベル */
	private final static int LEVEL = 1;

	/** ルールのメッセージ */
	private final static String MESSAGE = "MISRA-C Rule 74";

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

		/* Get Declaration of functions */
		CFunctionElement[] fs = cfile.getFunctions();
		for (int i = 0; i < fs.length; i++) {
			CDeclarationElement[] es = fs[i].getDeclarations();
			/* Get name of function */
			List<String> paramList = new ArrayList<String>();
			for (int j = 0; j < es.length; j++) {
				/* Get name of argument of function */
				paramList.add(es[j].getElem().getTextContent());

			}
			if (!(checkPrototypeParameter(file.getDOM(), fs[i].getName(), paramList))) {
				problemNodes.add(fs[i].getElem());
			}
		}

		// problemNodes.add(expressions[i].getElem());
		for (Iterator<Element> itr = problemNodes.iterator(); itr.hasNext();) {
			results.add(new Result(null, new NodeOffsetUtil(itr.next())
					.getRange(), LEVEL, MESSAGE));
		}
		return results;
	}

	/*
	 * 関数名と引数のリストを受け取り，それがプロトタイプ宣言の引数と等しいか確認する
	 * 
	 * @return boolean
	 */
	private boolean checkPrototypeParameter(Document dom, String funcName, List<String> paramList) {
	    List<Element> prototypeList = new ArrayList<Element>();
	    NodeList nodeList = dom.getElementsByTagName("Prototype");
	    for(int i = 0; i < nodeList.getLength(); i++) {
	        prototypeList.add((Element) nodeList.item(i));
	    }
	    for (Element element : prototypeList) {
			CPrototypeElement ps = new CPrototypeElement(element);
			if (ps.getName().equals(funcName)) {
				Element[] pas = ps.getChildrenNode("Param");
				for (int j = 0; j < pas.length; j++) {
					if (!(paramList.get(j).equals(pas[j].getTextContent()))) {
						return false;
					}
				}
			}
		}		
		return true;
	}

}