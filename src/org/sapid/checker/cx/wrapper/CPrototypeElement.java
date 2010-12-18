/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;

/**
 * Prototype ����
 * @author Toshinori OSUKA
 */
public class CPrototypeElement extends CDeclarationElement {
    /**
     * ���󥹥ȥ饯��
     * @param elem
     */
    public CPrototypeElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * �ؿ���������������<br>
     * �ե��������¸�ߤ��ʤ���� null
     * @return
     */
    public CElement getDefinition() {
        String defid = elem.getAttribute("defid");
        CFileElement cfile = new CFileElement(elem.getOwnerDocument()
                .getDocumentElement());
        Element function = cfile.getElementById(defid);
        if (function == null) {
            return null;
        }
        return new CFunctionElement(function);
    }

}
