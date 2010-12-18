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
import org.sapid.checker.cx.wrapper.CIncludeElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * MISRA-C ルール 88 非標準文字列は#include指令のヘッダファイル名に現れてはならない (", /*, \の3つは除外)
 * @author Eiji Hirumuta
 */
public class Misra88 implements CheckerClass {
    /** ルールのレベル */
    private final static int LEVEL = 1;

    /** ルールのメッセージ */
    private final static String MESSAGE = "MISRA-C Rule 88";

    /** 検査結果 */
    List<Result> results = new ArrayList<Result>();

    /** 違反として検出するノードの集合 */
    Set<Element> problemNodes = new HashSet<Element>();

    /*
     * ファイルのルールチェック時に呼ばれる
     * @return results
     */
    public List<Result> check(IFile file, CheckRule rule) {
        List<Element> includeList = new ArrayList<Element>();
        NodeList nodeList = file.getDOM().getElementsByTagName("Include");
        for(int i = 0; i < nodeList.getLength(); i++) {
            includeList.add((Element) nodeList.item(i));
        }
        for (Element element : includeList) {
			CIncludeElement include = new CIncludeElement(element);
        	if (include.getHFile().contains("'")) {
				problemNodes.add(include.getElem());
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