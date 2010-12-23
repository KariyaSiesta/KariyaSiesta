/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * File 要素
 * @author Toshinori OSUKA
 */
public class CFileElement extends CElement {
    /**
     * コンストラクタ
     * @param elem
     */
    public CFileElement(Element elem) {
        super(elem);
    }

    /**
     * コンストラクタ
     * @param elem
     */
    public CFileElement(Document doc) {
        super(doc.getDocumentElement());
    }

}
