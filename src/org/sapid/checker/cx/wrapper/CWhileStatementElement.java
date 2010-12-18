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
     * ��������������<br>
     * while ($i < 10$){}<br>
     * ̵������ null
     * @deprecated {@link #getConditionExpression()}���֤�����
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
     * �֥�å����������<br>
     * while (i < 10){$$}<br>
     * ̵�����϶��������֤�
     */
    @Override
    public Element[] getTrueBlock() {
        // TODO Auto-generated method stub
        return super.getTrueBlock();
    }

    /**
     * While ʸ���ɤ���
     * @param node
     * @return
     */
    public static boolean isWhileStatement(Node node) {
        return "Stmt".equals(node.getNodeName())
                && "While".equals(((Element) node).getAttribute("sort"));
    }

}
