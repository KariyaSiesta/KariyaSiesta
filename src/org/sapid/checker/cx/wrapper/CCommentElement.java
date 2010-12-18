/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;

/**
 * comment 要素
 * @author Toshinori OSUKA
 */
public class CCommentElement extends CElement {
    /**
     * コンストラクタ
     * @param elem
     */
    public CCommentElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * コメントのテキストを返す
     * @return
     */
    public String getTextContent() {
        return elem.getTextContent();
    }

}
