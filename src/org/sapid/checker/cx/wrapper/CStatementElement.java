/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Stmt 要素
 * @author Toshinori OSUKA
 */
public class CStatementElement extends CElement {
    /**
     * コンストラクタ
     * @param elem
     */
    public CStatementElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * 副作用を持つか<br>
     * 式のひとつでも副作用をも持てば OK
     * @return
     */
    public boolean hasSideEffect() {
        CExpressionElement[] exprs = getExpressions();
        for (int i = 0; i < exprs.length; i++) {
            if (exprs[i].hasSideEffect()) {
                return true;
            }
        }
        
        return this.isReturnStatement() || this.isGotoStatement() ||
        		this.isBreakStatement() || this.isContinueStatement();
    }

    public static boolean isStatement(Node node) {
        return "Stmt".equals(node.getNodeName());
    }

    /**
     * return 文かどうか
     * @return
     */
    public boolean isReturnStatement() {
        return this.hasKeyword("return");
    }
    
    /**
     * break 文かどうか
     * @return
     */
    public boolean isBreakStatement() {
        return this.hasKeyword("break");
    }
    
    /**
     * continue 文かどうか
     * @return
     */
    public boolean isContinueStatement() {
        return this.hasKeyword("continue");
    }
    
    /**
     * goto 文かどうか
     * @return
     */
    public boolean isGotoStatement() {
        return this.hasKeyword("goto");
    }
    
    /**
     * ブロック文かどうか
     * @return
     */
    public boolean isBlockStatement() {
    	return this.getElem().getAttribute("sort").equals("Block");
    }
    
    private boolean hasKeyword(String keyword) {
        Element kwChild = this.getFirstChildNode("kw");
        return kwChild != null && kwChild.getTextContent().equals(keyword);
    }

}
