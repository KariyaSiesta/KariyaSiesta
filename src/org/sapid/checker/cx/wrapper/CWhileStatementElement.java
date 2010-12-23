/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Stmt sort="While"
 * @author Toshinori OSUKA
 */
public class CWhileStatementElement extends CControlStatementElement {

    public CWhileStatementElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * 条件部を取得する<br>
     * while ($i < 10$){}<br>
     * 無い場合は null
     * @deprecated {@link #getConditionExpression()}に置き換え
     */
    @Override
    @Deprecated
    public CStatementElement getConditionStatement() {
        return super.getConditionStatement();
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
     * ブロックを取得する<br>
     * while (i < 10){$$}<br>
     * 無い場合は空の配列が返る
     */
    @Override
    public Element[] getTrueBlock() {
        // TODO Auto-generated method stub
        return super.getTrueBlock();
    }

    /**
     * While 文かどうか
     * @param node
     * @return
     */
    public static boolean isWhileStatement(Node node) {
        return "Stmt".equals(node.getNodeName())
                && "While".equals(((Element) node).getAttribute("sort"));
    }

}
