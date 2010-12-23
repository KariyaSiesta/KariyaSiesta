/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

/**
 * Tag 要素
 * @author Toshinori OSUKA
 */
public class CTagElement extends CElement {
    /**
     * コンストラクタ
     * @param elem
     */
    public CTagElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * すべての Member を返す
     * @return
     */
    public CMemberElement[] getMembers() {
        List<CMemberElement> list = new ArrayList<CMemberElement>();
        Element[] nl = getChildrenNode("Member");
        for (int i = 0; i < nl.length; i++) {
            list.add(new CMemberElement(nl[i]));
        }
        return (CMemberElement[]) list.toArray(new CMemberElement[list.size()]);
    }

}
