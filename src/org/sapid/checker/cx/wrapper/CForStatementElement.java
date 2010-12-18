/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Stmt sort="For"
 * @author Toshinori OSUKA
 */
public class CForStatementElement extends CControlStatementElement {

    public CForStatementElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * for の初期化文を取得する<br>
     * for ($i=0,j=0$;i<10;i++){}<br>
     * 存在しない場合は null を返す
     * @return
     * @deprecated {@link #getInitialExpression()}に置き換え
     */
    @Deprecated
    public CStatementElement getInitialStatement() {
        // 最初の ( から ; までにある Stmt を返す
        Element paren = getChildNodesByNodeNameAndText("op", "(")[0];
        Element semi = getChildNodesByNodeNameAndText("op", ";")[0];
        return getInnerStatement(paren, semi);
    }
    
    /**
     * 初期化式を取得する
     * @return 初期化式 ただし見つからない場合はnull
     */
    public CExpressionElement getInitialExpression() {
    	Element[] childExpressionElements = this.getChildrenNode("Expr");
    	
    	if (childExpressionElements.length >= 1) {
    		return new CExpressionElement(childExpressionElements[0]);
    	} else {
    		return null;
    	}
    }

    /**
     * for の条件文を取得する<br>
     * for (i=0,j=0;$i<10$;i++){}<br>
     * 存在しない場合は null を返す
     * @return
     * @deprecated {@link #getConditionExpression()}に置き換え
     */
    @Override
    @Deprecated
    public CStatementElement getConditionStatement() {
        // 一個目の; から二個目の ; までにある Stmt を返す
        Element[] semis = getChildNodesByNodeNameAndText("op", ";");
        return getInnerStatement(semis[0], semis[1]);
    }

    @Override
    public CExpressionElement getConditionExpression() {
    	Element[] childExpressionElements = this.getChildrenNode("Expr");
    	
    	if (childExpressionElements.length >= 2) {
    		return new CExpressionElement(childExpressionElements[1]);
    	} else {
    		return null;
    	}
    }

    /**
     * for の増加文を取得する<br>
     * for (i=0,j=0;i<10;$i++$){}<br>
     * 存在しない場合は null を返す
     * @return
     * @deprecated {@link #getIncrementalExpression()}に置き換え
     */
    @Deprecated
    public CStatementElement getIncrementalStatement() {
        // 二個目の ; から ) までにある Stmt を返す
        Element paren = getChildNodesByNodeNameAndText("op", ";")[1];
        Element semi = getChildNodesByNodeNameAndText("op", ")")[0];
        return getInnerStatement(paren, semi);
    }

    /**
     * 増加式を取得する
     * @return 増加式 ただし見つからない場合はnull
     */
    public CExpressionElement getIncrementalExpression() {
    	Element[] childExpressionElements = this.getChildrenNode("Expr");
    	
    	if (childExpressionElements.length >= 3) {
    		return new CExpressionElement(childExpressionElements[2]);
    	} else {
    		return null;
    	}
    }

    /**
     * For 文かどうか
     * @param node
     * @return
     */
    public static boolean isForStatement(Node node) {
        return "Stmt".equals(node.getNodeName())
                && "For".equals(((Element) node).getAttribute("sort"));
    }

    /**
     * for のブロックを取得する<br>
     * for (i=0,j=0;i<10;i++)${}$<br>
     * 存在しない場合は 長さ 0 の配列 を返す
     * @return
     */
    @Override
    public Element[] getTrueBlock() {
        return super.getTrueBlock();
    }

}
