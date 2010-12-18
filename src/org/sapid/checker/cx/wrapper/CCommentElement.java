/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;

/**
 * comment ����
 * @author Toshinori OSUKA
 */
public class CCommentElement extends CElement {
    /**
     * ���󥹥ȥ饯��
     * @param elem
     */
    public CCommentElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * �����ȤΥƥ����Ȥ��֤�
     * @return
     */
    public String getTextContent() {
        return elem.getTextContent();
    }

}
