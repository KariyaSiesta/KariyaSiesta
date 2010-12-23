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
 * Stmt sort="Do" 要素
 * @author Toshinori OSUKA
 */
public class CDoStatementElement extends CControlStatementElement {

    public CDoStatementElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * 条件部を返す<br>
     * do { } while($ i < 10 $);<br>
     * Stmt が無い場合には null を返す
     * @deprecated {@link #getConditionExpression()}に置き換え
     */
    @Override
    @Deprecated
    public CStatementElement getConditionStatement() {
        // 最後の(と最後の)の間
        Element[] lparens = getChildNodesByNodeNameAndText("op", "(");
        Element[] rparens = getChildNodesByNodeNameAndText("op", ")");
        return getInnerStatement(lparens[lparens.length - 1],
                rparens[rparens.length - 1]);
    }

    @Override
    public CExpressionElement getConditionExpression() {
    	Element firstChildExpressionElement = this.getFirstChildNode("Expr");
    	
    	if (firstChildExpressionElement != null) {
    		return new CExpressionElement(firstChildExpressionElement);
    	} else {
    		return null;
    	}
    }

    /**
     * ブロック部を返す<br>
     * do$ { } $while( i < 10 );<br>
     * 無い場合には長さ0の配列が返る
     */
    @Override
    public Element[] getTrueBlock() {
        // do から while までの間
        Element kwdo = getChildNodesByNodeNameAndText("kw", "do")[0];
        Element kwwhile = getChildNodesByNodeNameAndText("kw", "while")[0];
        List<Element> list = new ArrayList<Element>();
        Node tmp = kwdo.getNextSibling();
        while (!tmp.isSameNode(kwwhile)) {
            list.add((Element) tmp);
            tmp = tmp.getNextSibling();
        }
        return (Element[]) list.toArray(new Element[list.size()]);
    }

    /**
     * Do 文かどうか
     * @param node
     * @return
     */
    public static boolean isDoStatement(Node node) {
        return "Stmt".equals(node.getNodeName())
                && "Do".equals(((Element) node).getAttribute("sort"));
    }

}
