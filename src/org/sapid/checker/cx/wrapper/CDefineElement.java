/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;

/**
 * Define 要素
 * @author Toshinori OSUKA
 */
public class CDefineElement extends CElement {
    /** macroPattern 要素の名前 */
    protected final String MACRO_PATTERN = "macroPattern";
    /** macroBody 要素の名前 */
    protected final String MACRO_BODY = "macroBody";
    
    /**
     * コンストラクタ
     * @param elem
     */
    public CDefineElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * macroPattern 要素のテキストを返す
     * @return
     */
    public String getMacroPattern() {
        return getFirstChildNode(MACRO_PATTERN).getTextContent();
    }
    
    /**
     * macroPattern 要素の Id 属性を返す
     * @return
     */
    public String getMacroPatternId() {
        return getFirstChildNode(MACRO_PATTERN).getAttribute("id");
    }
    
    /**
     * macroBody 要素のテキストを返す
     * なければ 空文字 を返す
     * @return
     */
    public String getMacroBody() {
        Element macroBody = getFirstChildNode(MACRO_BODY);
        if (macroBody == null) {
            return "";
        }
        return macroBody.getTextContent();
    }

}
