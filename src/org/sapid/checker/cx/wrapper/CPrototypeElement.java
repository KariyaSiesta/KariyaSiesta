/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;

/**
 * Prototype 要素
 * @author Toshinori OSUKA
 */
public class CPrototypeElement extends CDeclarationElement {
    /**
     * コンストラクタ
     * @param elem
     */
    public CPrototypeElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * 関数の定義を取得する<br>
     * ファイル内に存在しなければ null
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
