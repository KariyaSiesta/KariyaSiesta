/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Local or Global 要素
 * @author Toshinori OSUKA
 */
public class CVariableDeclarationElement extends CDeclarationElement {
    protected Element ident;

    public CVariableDeclarationElement(Element elem) {
        super(elem);
        ident = getFirstChildNode("ident");
    }

    /**
     * int i,j; のときに仮想的な j のための仮想的な Local 要素を作って返す<br>
     * 次が無いときには null を返す
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
     * 初期化式 or 初期化リテラルを返す<br>
     * 正確には = の後ろのスペースでない要素<br>
     * 無い場合には null を返す<br>
     * TODO ソースが汚すぎるので直す
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
     * 対象とする ident をセットする
     * @param ident
     */
    protected void setIdent(Element ident) {
        this.ident = ident;
    }
}
