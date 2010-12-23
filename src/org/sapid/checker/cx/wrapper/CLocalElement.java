/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Local 要素
 * @author Toshinori OSUKA
 */
public class CLocalElement extends CVariableDeclarationElement {
    /**
     * コンストラクタ
     * @param elem
     */
    public CLocalElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * Local 要素か
     * @param node
     * @return
     */
    public static boolean isLocal(Node node) {
        return "Local".equals(node.getNodeName());
    }

}
