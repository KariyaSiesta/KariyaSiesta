/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * flow 要素
 * @author Eiji Hirumuta
 */
public class CFlowElement extends CElement {
    /**
     * コンストラクタ
     * @param elem
     */
    public CFlowElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    public String getFlowId() {
    	return elem.getAttribute("id");
    }
    
    public String getFlowIdStmt() {
    	return elem.getAttribute("stmt_id");
    }

    public String getFlowIdNext() {
    	return elem.getAttribute("next");
    }
    
    public String getFlowSort() {
    	return elem.getAttribute("sort");
    }
    
    public String getFlowIdExpr() {
    	return elem.getAttribute("expr_id");
    }
    
    public String getFlowIdDepend() {
    	return elem.getAttribute("dep_id");
    }
    
    /*
     * next 属性の id を持つ CStatementElement を取得する．見つからなければ null を返す
     * @return
     */
    public CStatementElement getCStatementElementByNext(CFileElement cfile) {
		String next = elem.getAttribute("next");
		CStatementElement s = new CStatementElement(cfile.getElementById(next));
		return s; 
    }

    /*
     * expr_id 属性の id を持つ CExpressionElement を取得する．見つからなければ null を返す
     * @return
     */
    public CExpressionElement getCExpressionElementByExprId(CFileElement cfile) {
    	if (elem.getAttribute("expr_id") != "") {
    		String expr_Id = elem.getAttribute("expr_id");
    		CExpressionElement e = new CExpressionElement(cfile.getElementById(expr_Id));
    		return e; 
    	}
    	return null;
    }
    
    /*
     * dep_id 属性の id を持つ Element を取得する．(CIdent 要素がないため) 見つからなければ null を返す
     * @return
     */
    public Element getElementByDepId(CFileElement cfile) {
    	if (elem.getAttribute("dep_id") != "") {
    		CExpressionElement e = getCExpressionElementByExprId(cfile);
    		Element ident = e.getFirstChildNode("ident");
    		return ident;
    	}
    	return null;
    }
    
    public CStatementElement getFlowCStatementElement() {
    	Node tmp = elem.getNextSibling();
    	while ((tmp != null) &&!(tmp.getNodeName().equals("Stmt"))) {
    		tmp = tmp.getNextSibling();
    		
    	}
		if (tmp == null) return null;
    	CStatementElement s = new CStatementElement((Element)tmp);
    	return s;
    }

}
