/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

/**
 * Function 要素
 * @author Toshinori OSUKA
 */
public class CFunctionElement extends CDeclarationElement {
    /**
     * コンストラクタ
     * @param elem
     */
    public CFunctionElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * 引数のリストを返す
     * @return
     */
    public CParameterElement[] getParams() {
        List<CParameterElement> list = new ArrayList<CParameterElement>();
        Element[] params = getChildrenNode("Param");
        for (int i = 0; i < params.length; i++) {
            list.add(new CParameterElement(params[i]));
        }
        return (CParameterElement[]) list.toArray(new CParameterElement[list
                .size()]);
    }
    
    /**
     * 定義のブロック文を返す
     * @return 定義のブロック文 ただし見つからなかった場合はnull
     */
    public CStatementElement getContentBlockStatement() {
    	Element[] childElements = this.getChildStatementsAndLocals();
    	
    	if (childElements.length == 1) {
    		Element childElement = childElements[0];
    		
    		if (CStatementElement.isStatement(childElement)) {
    			CStatementElement childStatement = new CStatementElement(childElement);
    			
    			if (childStatement.isBlockStatement()) {
    				return childStatement;
    			}
    		}
    	}
    	
    	return null;
    }

}
