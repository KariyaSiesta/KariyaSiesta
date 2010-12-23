/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.rule.misra;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.Result;
import org.sapid.checker.cx.graph.GraphNode;
import org.sapid.checker.cx.graph.path.PathGraph;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CFunctionElement;
import org.sapid.checker.cx.wrapper.CStatementElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;

/**
 * MISRA-C ルール 83 戻り値の型のチェックはしない
 * @author Toshinori OSUKA
 */
public class Misra83 implements CheckerClass {
    /** ルールのレベル */
    private final static int LEVEL = 1;

    /** ルールのメッセージ */
    private final static String MESSAGE = "MISRA-C Rule 83";

    /** 検査結果 */
    private List<Result> results = new ArrayList<Result>();


    public List<Result> check(IFile file, CheckRule rule) {
        CFileElement cfile = new CFileElement(file.getDOM());

        for (CFunctionElement function : cfile.getFunctions()) {
            this.checkFunction(function);
        }

        return this.results;
    }

    private void checkFunction(CFunctionElement function) {
    	String type = function.getType();
        if (type.equals("void")) {
            return;
        }

        PathGraph graph = new PathGraph(function);
        List<List<GraphNode<Element>>> paths = graph.toPathList();
        for (Iterator<List<GraphNode<Element>>> itr = paths.iterator(); itr.hasNext();) {
            List<GraphNode<Element>> path = itr.next();
            Element last = path.get(path.size() - 1).getContent();

            if (!CStatementElement.isStatement(last)) {
                // そもそも最後がステートメントじゃない
                pushResult(function);
                break;
            }
            CStatementElement lastStatement = new CStatementElement(last);
            if (! lastStatement.isReturnStatement()) {
                // return がない
                pushResult(function);
                break;
            } else if (lastStatement.getFirstChildNode("Expr") == null) {
            	// 式のない return
            	pushResult(function);
            	break;
            }
        }
    }

    /**
     * 結果を追加
     * @param function
     */
    private void pushResult(CFunctionElement function) {
        results.add(new Result(null, new NodeOffsetUtil(function.getIdent())
                .getRange(), LEVEL, MESSAGE));
    }

}
