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
 * 宣言を表す要素(Global, Local, Param, Function, Prototype, Argument, Member, Typedecl
 * あたり) この要素は CX-model には現れない
 * @author Toshinori OSUKA
 */
public class CDeclarationElement extends CElement {
    private final static List<String> nodeNames = Arrays
            .asList(new String[] { "Global", "Local", "Param", "Function",
                    "Prototype", "Argument", "Member", "Typedecl", "Tag" });

    /**
     * コンストラクタ
     * @param elem
     */
    public CDeclarationElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * 宣言の名前を返す
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
     * 宣言の型を返す なければ 空文字 を返す
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
     * 修飾子(static)を返す 修飾子がなければ 空文字 を返す
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
     * ident 要素を返す
     * @return
     */
    public Element getIdent() {
        return getFirstChildNode("ident");
    }

    /**
     * Node が宣言かどうかを確認
     * @param node
     * @return
     */
    public static boolean isDeclaration(Node node) {
        return nodeNames.contains(node.getNodeName());
    }
}
