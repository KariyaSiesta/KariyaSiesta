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
import org.sapid.checker.cx.graph.path.PathGraph;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CFunctionElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;

/**
 * MISRA-C ルール テンプレート
 * @author Eiji Hirumuta
 */
public class Misra00 implements CheckerClass {
    /** ルールのレベル */
    private final static int LEVEL = 1;

    /** ルールのメッセージ */
    private final static String MESSAGE = "MISRA-C Rule 00";

    /** 検査結果 */
    List<Result> results = new ArrayList<Result>();

    /** 違反として検出するノードの集合 */
    Set<Element> problemNodes = new HashSet<Element>();

    /*
     * ファイルのルールチェック時に呼ばれる
     * @return results
     */
    public List<Result> check(IFile file, CheckRule rule) {
    	
        CFileElement cfile = new CFileElement(file.getDOM());
        System.out.println("PathGraph Test");
        CFunctionElement[] functions = cfile.getFunctions();
        //CStatementElement[] statements = cfile.getStatments();
        //CExpressionElement[] expressions = cfile.getExpressions();
        for (int i = 0; i < functions.length; i++) {
            System.out.println("******");
        	System.out.println(functions[i].getElem().getTextContent());
        	// パス解析を行うときに必要
            PathGraph graph = new PathGraph(functions[i]);
            
            //List<List<GraphNode<Element>>> paths = graph.toPathList();
            /*for (Iterator<List<GraphNode<Element>>> itr = paths.iterator(); itr
                    .hasNext();) {
                List<GraphNode<Element>> path = itr.next();
                checkPath(path);
            }*/
            graph.print();
            // 重複するエレメントは problemNodes に追加
            //System.out.println(expressions[i].getElem().getTextContent());
    		problemNodes.add(functions[i].getElem());
        }
        /* 検出結果を返り値に追加 */
        for (Iterator<Element> itr = problemNodes.iterator(); itr.hasNext();) {
            results.add(new Result(null, new NodeOffsetUtil(itr.next())
                    .getRange(), LEVEL, MESSAGE));
        }
        return results;
    }

    
    

    /*
     * パスチェック
     * @param path
     
    private void checkPath(List<GraphNode<Element>> path) {
    	for (Iterator<GraphNode<Element>> itr = path.iterator(); itr.hasNext();) {
    		//GraphNode<Element> node = itr.next();
    		//System.out.println(node.getContent().getNodeName());
    		//System.out.println(node.getContent().getTextContent());
    	}
    }
    */

}