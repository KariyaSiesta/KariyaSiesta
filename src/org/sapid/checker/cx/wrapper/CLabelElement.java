/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;

/**
 * Label 要素
 * @author Toshinori OSUKA
 */
public class CLabelElement extends CElement {
    protected final String SORT_NAMED = "Named";
    protected final String SORT_CASE = "Case";
    protected final String SORT_DEFAULT = "Default";

    /**
     * コンストラクタ
     * @param elem
     */
    public CLabelElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * ラベルのテキストを返す
     * @return
     */
    public String getLabelText() {
        return elem.getFirstChild().getTextContent();
    }

    /**
     * ident 要素を返す<br>
     * 名前付けされたラベル用
     * @return
     */
    public Element getIdent() {
        return getFirstChildNode("ident");
    }

    /**
     * ラベルの種類を返す 名前付けされたラベル or case ラベル or default ラベル
     * @return
     */
    public String getSort() {
        Element kw = getFirstChildNode("kw");
        if (kw == null) {
            return SORT_NAMED;
        }
        if ("default".equals(kw.getTextContent())) {
            return SORT_DEFAULT;
        } else if ("case".equals(kw.getTextContent())) {
            return SORT_CASE;
        }
        throw new AssertionError();
    }

    /**
     * 名前つきラベルかどうか
     * @return
     */
    public boolean isNamedLabel() {
        return SORT_NAMED.equals(getSort());
    }

    /**
     * Case ラベルかどうか
     * @return
     */
    public boolean isCaseLabel() {
        return SORT_CASE.equals(getSort());
    }

    /**
     * Case ラベルかどうか
     * @return
     */
    public boolean isDefaultLabel() {
        return SORT_DEFAULT.equals(getSort());
    }
}
