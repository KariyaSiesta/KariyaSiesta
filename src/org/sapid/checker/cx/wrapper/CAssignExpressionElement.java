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
 * 代入を表す Expr 要素
 * @author Owner
 */
public class CAssignExpressionElement extends CExpressionElement {

    public CAssignExpressionElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * 左辺の要素を返す<br>
     * @return
     */
    public Element[] getLeftHandElements() {
        List<Element> list = new ArrayList<Element>();
        Element op = getFirstChildNode("op");
        Node n = elem.getFirstChild();
        while (n != null && !n.isSameNode(op)) {
            list.add((Element) n);
            n = n.getNextSibling();
        }
        return (Element[]) list.toArray(new Element[list.size()]);
    }

    /**
     * 右辺の要素を返す<br>
     * @return
     */
    public Element[] getRightHandElements() {
        List<Element> list = new ArrayList<Element>();
        Node n = getFirstChildNode("op").getNextSibling();
        while (n != null) {
            list.add((Element) n);
            n = n.getNextSibling();
        }
        return (Element[]) list.toArray(new Element[list.size()]);
    }

    /**
     * 左辺の Expr を返す<br>
     * 見つからない場合（リテラルとか)はnull
     * @return
     */
    public CExpressionElement getLeftHandExpression() {
        Element[] elements = getLeftHandElements();
        for (int i = 0; i < elements.length; i++) {
            if (CExpressionElement.isExpression(elements[i])) {
                return new CExpressionElement(elements[i]);
            }
        }
        return null;
    }

    /**
     * 左辺の Expr を返す<br>
     * 見つからない場合（リテラルとか)はnull
     * @return
     */
    public CExpressionElement getRightHandExpression() {
        Element[] elements = getRightHandElements();
        for (int i = 0; i < elements.length; i++) {
            if (CExpressionElement.isExpression(elements[i])) {
                return new CExpressionElement(elements[i]);
            }
        }
        return null;
    }
}
