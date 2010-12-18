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
 * Stmt sort="If" ����
 * @author Toshinori OSUKA
 */
public class CIfStatementElement extends CControlStatementElement {
    /**
     * ���󥹥ȥ饯��
     * @param elem
     */
    public CIfStatementElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
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
     * True ������ <br>
     * �ʲ���$�֤��֤� <br>
     * if (condition)$ { stmt; } $ else { stmt; }
     * @return
     * @deprecated {@link #getTrueStatement()}���֤�����
     */
    @Override
    @Deprecated
    public Element[] getTrueBlock() {
        List<Element> elements = new ArrayList<Element>();
        Element paren = getChildNodesByNodeNameAndText("op", ")")[0];
        Element[] kwelses = getChildNodesByNodeNameAndText("kw", "else");
        Element kwelse = null;
        if (kwelses.length > 0) {
            kwelse = kwelses[0];
        }
        Node tmp = paren.getNextSibling();
        while (tmp != null) {
            if (kwelse != null && tmp.isSameNode(kwelse)) {
                break;
            }
            elements.add((Element) tmp);
            tmp = tmp.getNextSibling();
        }
        return (Element[]) elements.toArray(new Element[elements.size()]);
    }
    
    /**
     * ��Ｐ��ɾ����̤�����ξ��˼¹Ԥ����ʸ���֤�
     * @return ��Ｐ��ɾ����̤�����ξ��˼¹Ԥ����ʸ ���������Ĥ���ʤ��ä�����null
     */
    public CStatementElement getTrueStatement() {
    	Element firstStatementElement = this.getFirstChildNode("Stmt");
    	
    	if (firstStatementElement != null) {
    		return new CStatementElement(firstStatementElement);
    	} else {
    		return null;
    	}
    }

    /**
     * False ������ <br>
     * �ʲ���$�֤��֤� <br>
     * if (condition) { stmt; } else $ { stmt; } $<br>
     * ¸�ߤ��ʤ����Ĺ�� 0 �������֤�
     * @return
     * @deprecated {@link #getFalseStatement()}���֤�����
     */
    @Deprecated
    public Element[] getFalseBlock() {
        List<Element> elements = new ArrayList<Element>();
        Element[] kwelses = getChildNodesByNodeNameAndText("kw", "else");
        Element kwelse = null;
        if (kwelses.length > 0) {
            kwelse = kwelses[0];
            Node tmp = kwelse.getNextSibling();
            while (tmp != null) {
                elements.add((Element) tmp);
                tmp = tmp.getNextSibling();
            }
        }
        return (Element[]) elements.toArray(new Element[elements.size()]);
    }

    /**
     * ��Ｐ��ɾ����̤���ξ��˼¹Ԥ����ʸ���֤�
     * @return ��Ｐ��ɾ����̤���ξ��˼¹Ԥ����ʸ ���������Ĥ���ʤ��ä�����null
     */
    public CStatementElement getFalseStatement() {
    	Element[] childStatementElements = this.getChildrenNode("Stmt");
    	
    	if (childStatementElements.length == 2) {
    		return new CStatementElement(childStatementElements[1]);
    	} else {
    		return null;
    	}
    }

    /**
     * If ʸ���ɤ���
     * @param node
     * @return
     */
    public static boolean isIfStatement(Node node) {
        return "Stmt".equals(node.getNodeName())
                && "If".equals(((Element) node).getAttribute("sort"));
    }

}
