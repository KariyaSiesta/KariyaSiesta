/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;

/**
 * ネームスペースを表現するクラス<br>
 * ネームスペースとは同じスコープで同じ識別子を使っても区別される空間である<br>
 * C 言語のネームスペースは 4つの種類がある<br>
 * <ul>
 * <li>名札名 (Label)</li>
 * <li>構造体，共用体及び列挙体のタグ</li>
 * <li>構造体又は共用体のメンバ．構造体等はメンバに対する別個のネームスペースを持つ</li>
 * <li>その他すべての識別子</li>
 * </ul>
 * @author Toshinori OSUKA
 */
public class CNamespace {
    public final int NAMESPACE_LABEL = 0;
    public final int NAMESPACE_TAG = 1;
    public final int NAMESPACE_MEMBER = 2;
    public final int NAMESPACE_REGULAR = 3;

    /** ident 要素への参照 */
    Element ident;
    /** スコープの参照 */
    private CElement scope;
    /** ネームスペースの種類 */
    private int sort;

    /**
     * コンストラクタ<br>
     * 受け取るのはラベル，タグ及び「宣言」の ident のみ<br>
     * 「参照」を受け取ったときの動作は保障しない
     * @param ident 要素
     */
    public CNamespace(Element ident) {
        super();
        this.ident = ident;

        Element parent = (Element) ident.getParentNode();
        if ("Label".equals(parent.getNodeName())) {
            sort = NAMESPACE_LABEL;
            scope = new CLabelElement(parent).getScope();
        } else if ("Tag".equals(parent.getNodeName())) {
            sort = NAMESPACE_TAG;
            scope = new CTagElement(parent).getScope();
        } else if ("Member".equals(parent.getNodeName())) {
            sort = NAMESPACE_MEMBER;
            scope = new CTagElement((Element) parent.getParentNode());
        } else {
            sort = NAMESPACE_REGULAR;
            scope = new CDeclarationElement(parent).getScope();
        }
        // TODO 参照を受け取ったときに宣言まで取りに行く
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CNamespace)) {
            return false;
        }
        CNamespace ns = (CNamespace) obj;
        return scope.getElem().isSameNode(ns.getScope().getElem())
                && (sort == ns.getSort());
    }

    /**
     * ネームスペースの種類を取得する<br>
     * NAMESPACE_LABEL など
     * @return
     */
    public int getSort() {
        return sort;
    }

    /**
     * ネームスペースの基準となるスコープを取得する
     * @return
     */
    public CElement getScope() {
        return scope;
    }

}
