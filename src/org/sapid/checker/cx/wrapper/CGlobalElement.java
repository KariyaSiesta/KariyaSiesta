/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Global 要素
 * @author Toshinori OSUKA
 */
public class CGlobalElement extends CVariableDeclarationElement {
    /**
     * コンストラクタ
     * @param elem
     */
    public CGlobalElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * 参照箇所を返す
     * @return
     */
    public CVariableReference[] getReferences() {
        List<CVariableReference> list = new ArrayList<CVariableReference>();
        String id = ident.getAttribute("defid");
        CFileElement cfile = new CFileElement(elem.getOwnerDocument());
        CExpressionElement[] exprs = cfile.getExpressions();
        for (int i = 0; i < exprs.length; i++) {
            if (!exprs[i].isVarRef()) {
                continue;
            }
            String defid = exprs[i].getFirstChildNode("ident").getAttribute(
                    "defid");
            if (id.equals(defid)) {
                list.add(new CVariableReference(exprs[i].getElem()));
            }
        }
        return (CVariableReference[]) list.toArray(new CVariableReference[list
                .size()]);
    }

    /**
     * Global 要素か
     * @param node
     * @return
     */
    public static boolean isGlobal(Node node) {
        return "Global".equals(node.getNodeName());
    }

}
