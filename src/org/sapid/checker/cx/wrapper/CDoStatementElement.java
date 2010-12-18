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
 * Stmt sort="Do" ����
 * @author Toshinori OSUKA
 */
public class CDoStatementElement extends CControlStatementElement {

    public CDoStatementElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * ��������֤�<br>
     * do { } while($ i < 10 $);<br>
     * Stmt ��̵�����ˤ� null ���֤�
     * @deprecated {@link #getConditionExpression()}���֤�����
     */
    @Override
    @Deprecated
    public CStatementElement getConditionStatement() {
        // �Ǹ��(�ȺǸ��)�δ�
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
     * �֥�å������֤�<br>
     * do$ { } $while( i < 10 );<br>
     * ̵�����ˤ�Ĺ��0�������֤�
     */
    @Override
    public Element[] getTrueBlock() {
        // do ���� while �ޤǤδ�
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
     * Do ʸ���ɤ���
     * @param node
     * @return
     */
    public static boolean isDoStatement(Node node) {
        return "Stmt".equals(node.getNodeName())
                && "Do".equals(((Element) node).getAttribute("sort"));
    }

}
