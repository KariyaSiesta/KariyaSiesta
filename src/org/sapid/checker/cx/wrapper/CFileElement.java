/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * File ����
 * @author Toshinori OSUKA
 */
public class CFileElement extends CElement {
    /**
     * ���󥹥ȥ饯��
     * @param elem
     */
    public CFileElement(Element elem) {
        super(elem);
    }

    /**
     * ���󥹥ȥ饯��
     * @param elem
     */
    public CFileElement(Document doc) {
        super(doc.getDocumentElement());
    }

}
