/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Local or Global ����
 * @author Toshinori OSUKA
 */
public class CVariableDeclarationElement extends CDeclarationElement {
    protected Element ident;

    public CVariableDeclarationElement(Element elem) {
        super(elem);
        ident = getFirstChildNode("ident");
    }

    /**
     * int i,j; �ΤȤ��˲���Ū�� j �Τ���β���Ū�� Local ���Ǥ��ä��֤�<br>
     * ����̵���Ȥ��ˤ� null ���֤�
     * @return
     */
    public CVariableDeclarationElement getNext() {
        Node tmp = ident.getNextSibling();
        while (tmp != null) {
            if ("ident".equals(tmp.getNodeName())) {
                break;
            }
            tmp = tmp.getNextSibling();
        }
        if (tmp == null) {
            return null;
        }
        CVariableDeclarationElement newElement;
        if (CLocalElement.isLocal(elem)) {
            newElement = new CLocalElement(elem);
        } else {
            newElement = new CGlobalElement(elem);
        }
        newElement.setIdent((Element) tmp);
        return newElement;
    }

    /**
     * ������� or �������ƥ����֤�<br>
     * ���Τˤ� = �θ��Υ��ڡ����Ǥʤ�����<br>
     * ̵�����ˤ� null ���֤�<br>
     * TODO ����������������Τ�ľ��
     * @return
     */
    public Element getInitializeElement() {
        Node tmp = ident.getNextSibling();
        while (tmp != null) {
            if ("op".equals(tmp.getNodeName())
                    && "=".equals(tmp.getTextContent())) {
                break;
            }
            tmp = tmp.getNextSibling();
        }
        if (tmp == null) {
            return null;
        }
        tmp = tmp.getNextSibling();
        while (tmp != null) {
            if (!"sp".equals(tmp.getNodeName())) {
                return (Element) tmp;
            }
            tmp = tmp.getNextSibling();
        }
        return null;
    }

    @Override
    public String getId() {
        return ident.getAttribute("defid");
    }

    @Override
    public String getName() {
        return ident.getTextContent();
    }

    /**
     * �оݤȤ��� ident �򥻥åȤ���
     * @param ident
     */
    protected void setIdent(Element ident) {
        this.ident = ident;
    }
}
