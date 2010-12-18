/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * �����ɽ������(Global, Local, Param, Function, Prototype, Argument, Member, Typedecl
 * ������) �������Ǥ� CX-model �ˤϸ���ʤ�
 * @author Toshinori OSUKA
 */
public class CDeclarationElement extends CElement {
    private final static List<String> nodeNames = Arrays
            .asList(new String[] { "Global", "Local", "Param", "Function",
                    "Prototype", "Argument", "Member", "Typedecl", "Tag" });

    /**
     * ���󥹥ȥ饯��
     * @param elem
     */
    public CDeclarationElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * �����̾�����֤�
     * @return
     */
    public String getName() {
        Element ident = getFirstChildNode("ident");
        if (ident == null) {
            throw new AssertionError();
        }
        return ident.getTextContent();
    }

    /**
     * ����η����֤� �ʤ���� ��ʸ�� ���֤�
     * @return
     */
    public String getType() {
        Element type = getFirstChildNode("Type");
        if (type == null) {
            return "";
        }
        return type.getFirstChild().getTextContent();
    }

    /**
     * ������(static)���֤� �����Ҥ��ʤ���� ��ʸ�� ���֤�
     * @return
     */
    public String getStorage() {
        Element type = getFirstChildNodeSort("kw", "storage");
        if (type == null) {
            return "";
        }
        return type.getTextContent();
    }

    /**
     * ident ���Ǥ��֤�
     * @return
     */
    public Element getIdent() {
        return getFirstChildNode("ident");
    }

    /**
     * Node ��������ɤ������ǧ
     * @param node
     * @return
     */
    public static boolean isDeclaration(Node node) {
        return nodeNames.contains(node.getNodeName());
    }
}
