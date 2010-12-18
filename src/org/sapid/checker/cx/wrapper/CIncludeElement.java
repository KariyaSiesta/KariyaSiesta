/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;

/**
 * Include мваг
 * @author Toshinori OSUKA
 */
public class CIncludeElement extends CElement {

    public CIncludeElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }
    
    public String getHFile() {
        return getFirstChildNode("hfile").getTextContent();
    }

}
