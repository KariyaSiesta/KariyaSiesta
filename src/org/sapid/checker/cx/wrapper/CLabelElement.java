/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;

/**
 * Label ����
 * @author Toshinori OSUKA
 */
public class CLabelElement extends CElement {
    protected final String SORT_NAMED = "Named";
    protected final String SORT_CASE = "Case";
    protected final String SORT_DEFAULT = "Default";

    /**
     * ���󥹥ȥ饯��
     * @param elem
     */
    public CLabelElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * ��٥�Υƥ����Ȥ��֤�
     * @return
     */
    public String getLabelText() {
        return elem.getFirstChild().getTextContent();
    }

    /**
     * ident ���Ǥ��֤�<br>
     * ̾���դ����줿��٥���
     * @return
     */
    public Element getIdent() {
        return getFirstChildNode("ident");
    }

    /**
     * ��٥�μ�����֤� ̾���դ����줿��٥� or case ��٥� or default ��٥�
     * @return
     */
    public String getSort() {
        Element kw = getFirstChildNode("kw");
        if (kw == null) {
            return SORT_NAMED;
        }
        if ("default".equals(kw.getTextContent())) {
            return SORT_DEFAULT;
        } else if ("case".equals(kw.getTextContent())) {
            return SORT_CASE;
        }
        throw new AssertionError();
    }

    /**
     * ̾���Ĥ���٥뤫�ɤ���
     * @return
     */
    public boolean isNamedLabel() {
        return SORT_NAMED.equals(getSort());
    }

    /**
     * Case ��٥뤫�ɤ���
     * @return
     */
    public boolean isCaseLabel() {
        return SORT_CASE.equals(getSort());
    }

    /**
     * Case ��٥뤫�ɤ���
     * @return
     */
    public boolean isDefaultLabel() {
        return SORT_DEFAULT.equals(getSort());
    }
}
