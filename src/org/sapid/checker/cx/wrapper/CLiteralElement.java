/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * literal ����
 * @author Toshinori OSUKA
 */
public class CLiteralElement extends CElement {

    public CLiteralElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * ʸ�����ƥ�뤫 "abc"
     * @return
     */
    public boolean isString() {
        return elem.getTextContent().endsWith("\"");
    }

    /**
     * ʸ����ƥ�뤫 'a'
     * @return
     */
    public boolean isChar() {
        return elem.getTextContent().endsWith("\'");
    }

    /**
     * ������ƥ�뤫 1U
     * @return
     */
    public boolean isNumber() {
        return !isString() && !isChar();
    }
    
    /**
     * �Ρ��ɤ� textContent ���֤�
     * @return
     */
    public String getTextContent() {
        return elem.getTextContent();
    }

    /**
     * ���ꤵ�줿�Ρ��ɤ� literal ���Ǥ�
     * @param node
     * @return
     */
    public static boolean isLiteral(Node node) {
        return "literal".equals(node.getTextContent());
    }

}
