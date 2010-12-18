/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * literal 要素
 * @author Toshinori OSUKA
 */
public class CLiteralElement extends CElement {

    public CLiteralElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * 文字列リテラルか "abc"
     * @return
     */
    public boolean isString() {
        return elem.getTextContent().endsWith("\"");
    }

    /**
     * 文字リテラルか 'a'
     * @return
     */
    public boolean isChar() {
        return elem.getTextContent().endsWith("\'");
    }

    /**
     * 数字リテラルか 1U
     * @return
     */
    public boolean isNumber() {
        return !isString() && !isChar();
    }
    
    /**
     * ノードの textContent を返す
     * @return
     */
    public String getTextContent() {
        return elem.getTextContent();
    }

    /**
     * 指定されたノードが literal 要素か
     * @param node
     * @return
     */
    public static boolean isLiteral(Node node) {
        return "literal".equals(node.getTextContent());
    }

}
