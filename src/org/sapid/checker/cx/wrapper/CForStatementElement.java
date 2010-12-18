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
     * for �ν����ʸ���������<br>
     * for ($i=0,j=0$;i<10;i++){}<br>
     * ¸�ߤ��ʤ����� null ���֤�
     * @return
     * @deprecated {@link #getInitialExpression()}���֤�����
     */
    @Deprecated
    public CStatementElement getInitialStatement() {
        // �ǽ�� ( ���� ; �ޤǤˤ��� Stmt ���֤�
        Element paren = getChildNodesByNodeNameAndText("op", "(")[0];
        Element semi = getChildNodesByNodeNameAndText("op", ";")[0];
        return getInnerStatement(paren, semi);
    }
    
    /**
     * ����������������
     * @return ������� ���������Ĥ���ʤ�����null
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
     * for �ξ��ʸ���������<br>
     * for (i=0,j=0;$i<10$;i++){}<br>
     * ¸�ߤ��ʤ����� null ���֤�
     * @return
     * @deprecated {@link #getConditionExpression()}���֤�����
     */
    @Override
    @Deprecated
    public CStatementElement getConditionStatement() {
        // ����ܤ�; ��������ܤ� ; �ޤǤˤ��� Stmt ���֤�
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
     * for ������ʸ���������<br>
     * for (i=0,j=0;i<10;$i++$){}<br>
     * ¸�ߤ��ʤ����� null ���֤�
     * @return
     * @deprecated {@link #getIncrementalExpression()}���֤�����
     */
    @Deprecated
    public CStatementElement getIncrementalStatement() {
        // ����ܤ� ; ���� ) �ޤǤˤ��� Stmt ���֤�
        Element paren = getChildNodesByNodeNameAndText("op", ";")[1];
        Element semi = getChildNodesByNodeNameAndText("op", ")")[0];
        return getInnerStatement(paren, semi);
    }

    /**
     * ���ü����������
     * @return ���ü� ���������Ĥ���ʤ�����null
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
     * For ʸ���ɤ���
     * @param node
     * @return
     */
    public static boolean isForStatement(Node node) {
        return "Stmt".equals(node.getNodeName())
                && "For".equals(((Element) node).getAttribute("sort"));
    }

    /**
     * for �Υ֥�å����������<br>
     * for (i=0,j=0;i<10;i++)${}$<br>
     * ¸�ߤ��ʤ����� Ĺ�� 0 ������ ���֤�
     * @return
     */
    @Override
    public Element[] getTrueBlock() {
        return super.getTrueBlock();
    }

}
