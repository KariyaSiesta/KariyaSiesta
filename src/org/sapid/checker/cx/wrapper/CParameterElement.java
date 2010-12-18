/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;

/**
 * Param Í×ÁÇ
 * @author Toshinori OSUKA
 */
public class CParameterElement extends CDeclarationElement {

    public CParameterElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * Àë¸À¤Î·¿¤òÊÖ¤¹
     * @return
     */
    public String getType() {
        String type = super.getType();
        if (type.length() > 0) {
            return type;
        }
        Element arg = new CFunctionElement((Element) elem.getParentNode())
                .getElementById(elem.getAttribute("id"));
        if (arg == null) {
            throw new AssertionError();
        }
        return new CDeclarationElement(arg).getType();
    }
}
