/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 制御構造を持つ Stmt 要素<br>
 * If / for / while /switch / do はこのクラスを継承する
 * @author Toshinori OSUKA
 */
public abstract class CControlStatementElement extends CStatementElement {

    public CControlStatementElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * sort に応じて インスタンスを生成する<br>
     * node が制御文でない場合は null を返す
     * @param node
     * @return
     */
    public static CControlStatementElement getInstance(Node node) {
        CControlStatementElement stmt = null;
        if (CForStatementElement.isForStatement(node)) {
            stmt = new CForStatementElement((Element) node);
        } else if (CWhileStatementElement.isWhileStatement(node)) {
            stmt = new CWhileStatementElement((Element) node);
        } else if (CIfStatementElement.isIfStatement(node)) {
            stmt = new CIfStatementElement((Element) node);
        } else if (CSwitchStatementElement.isSwitchStatement(node)) {
            stmt = new CSwitchStatementElement((Element) node);
        } else if (CDoStatementElement.isDoStatement(node)) {
            stmt = new CDoStatementElement((Element) node);
        }
        return stmt;
    }

    /**
     * 条件文を取得する<br>
     * @return
     * @deprecated {@link #getConditionExpression()}に置き換え
     */
    @Deprecated
    public CStatementElement getConditionStatement() {
        // 最初の "(" と ")" の間
        Element lparen = getChildNodesByNodeNameAndText("op", "(")[0];
        Element rparen = getChildNodesByNodeNameAndText("op", ")")[0];
        return getInnerStatement(lparen, rparen);
    }
    
    /**
     * 条件式を取得する
     * @return 条件式 ただし見つからない場合はnull
     */
    abstract public CExpressionElement getConditionExpression();

    /**
     * 分岐が true だったときに実行される命令のリストを取得する
     * @return
     */
    public Element[] getTrueBlock() {
        // ) 以降の 命令全部
        Element paren = getChildNodesByNodeNameAndText("op", ")")[0];
        List<Element> list = new ArrayList<Element>();
        Node tmp = paren.getNextSibling();
        while (tmp != null) {
            list.add((Element) tmp);
            tmp = tmp.getNextSibling();
        }
        return (Element[]) list.toArray(new Element[list.size()]);
    }

    /**
     * 指定された Element の間にある Stmt を取得する
     * @param from
     * @param to
     * @return
     */
    protected CStatementElement getInnerStatement(Element from, Element to) {
        Node tmp = from.getNextSibling();
        while (!tmp.isSameNode(to)) {
            if (CStatementElement.isStatement(tmp)) {
                return new CStatementElement((Element) tmp);
            }
            tmp = tmp.getNextSibling();
        }
        return null;
    }

    /**
     * 制御文かどうか
     * @param node
     * @return
     */
    public static boolean isControlStatement(Node node) {
        return CIfStatementElement.isIfStatement(node)
                || CSwitchStatementElement.isSwitchStatement(node)
                || CForStatementElement.isForStatement(node)
                || CWhileStatementElement.isWhileStatement(node)
                || CDoStatementElement.isDoStatement(node);
    }

}
