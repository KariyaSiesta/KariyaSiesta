/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;

/**
 * Define ����
 * @author Toshinori OSUKA
 */
public class CDefineElement extends CElement {
    /** macroPattern ���Ǥ�̾�� */
    protected final String MACRO_PATTERN = "macroPattern";
    /** macroBody ���Ǥ�̾�� */
    protected final String MACRO_BODY = "macroBody";
    
    /**
     * ���󥹥ȥ饯��
     * @param elem
     */
    public CDefineElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * macroPattern ���ǤΥƥ����Ȥ��֤�
     * @return
     */
    public String getMacroPattern() {
        return getFirstChildNode(MACRO_PATTERN).getTextContent();
    }
    
    /**
     * macroPattern ���Ǥ� Id °�����֤�
     * @return
     */
    public String getMacroPatternId() {
        return getFirstChildNode(MACRO_PATTERN).getAttribute("id");
    }
    
    /**
     * macroBody ���ǤΥƥ����Ȥ��֤�
     * �ʤ���� ��ʸ�� ���֤�
     * @return
     */
    public String getMacroBody() {
        Element macroBody = getFirstChildNode(MACRO_BODY);
        if (macroBody == null) {
            return "";
        }
        return macroBody.getTextContent();
    }

}
