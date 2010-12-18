/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Local ����
 * @author Toshinori OSUKA
 */
public class CLocalElement extends CVariableDeclarationElement {
    /**
     * ���󥹥ȥ饯��
     * @param elem
     */
    public CLocalElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * Local ���Ǥ�
     * @param node
     * @return
     */
    public static boolean isLocal(Node node) {
        return "Local".equals(node.getNodeName());
    }

}
