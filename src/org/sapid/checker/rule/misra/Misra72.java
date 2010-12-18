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
import org.sapid.checker.cx.wrapper.CPrototypeElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * MISRA-C ルール テンプレート
 * @author Eiji Hirumuta
 */
public class Misra72 implements CheckerClass {
    /** ルールのレベル */
    private final static int LEVEL = 1;

    /** ルールのメッセージ */
    private final static String MESSAGE = "MISRA-C Rule 72";

    /** 検査結果 */
    List<Result> results = new ArrayList<Result>();

    /** 違反として検出するノードの集合 */
    Set<Element> problemNodes = new HashSet<Element>();

    /*
     * ファイルのルールチェック時に呼ばれる
     * @return results
     */
    public List<Result> check(IFile file, CheckRule rule) {
        List<Element> prototypeList = new ArrayList<Element>();
        NodeList nodeList =  file.getDOM().getElementsByTagName("Prototype");
        for(int i = 0; i < nodeList.getLength(); i++) {
            prototypeList.add((Element) nodeList.item(i));
        }

        for (Element element : prototypeList) {
        	// prototype宣言から引数の型を取得
        	String protoType = "";
        	List<String> protoparamsType = new ArrayList<String>();
        	CPrototypeElement prototype = new CPrototypeElement(element);
        	Element[] params = prototype.getChildrenNode("Param");
        	for (int j = 0; j < params.length; j++) {
				CParameterElement parameter = new CParameterElement(params[j]);
				protoType = prototype.getType();
				protoparamsType.add(parameter.getType());
			}

        	// prototypeの定義
        	String defType = ((CFunctionElement)prototype.getDefinition()).getType();
        	List<String> defparamsType = new ArrayList<String>();
        	CParameterElement[] paramsdef = ((CFunctionElement)prototype.getDefinition()).getParams();
        	for (int j = 0; j < paramsdef.length; j++) {
        		defparamsType.add(paramsdef[j].getType());
			}
        	
        	// 型が間違っていないか調べる
        	if (!(protoType.equals(defType))) {
        		problemNodes.add(prototype.getElem());
        	}
        	for (int j = 0; j < defparamsType.size(); j++) {
            	if (!(defparamsType.get(j).equals(protoparamsType.get(j)))) {
            		problemNodes.add(prototype.getElem());
            	}
			}
        }
        
        for (Iterator<Element> itr = problemNodes.iterator(); itr.hasNext();) {
            results.add(new Result(null, new NodeOffsetUtil(itr.next())
                    .getRange(), LEVEL, MESSAGE));
        }
        return results;
    }
    
}