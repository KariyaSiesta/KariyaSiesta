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
 * Stmt sort="If" 要素
 * @author Toshinori OSUKA
 */
public class CIfStatementElement extends CControlStatementElement {
    /**
     * コンストラクタ
     * @param elem
     */
    public CIfStatementElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    
    @Override
    public CExpressionElement getConditionExpression() {
    	Element firstChildExpressionElement = this.getFirstChildNode("Expr");
    	
    	if (firstChildExpressionElement != null) {
    		return new CExpressionElement(firstChildExpressionElement);
    	} else {
    		return null;
    	}
    }

    /**
     * True 節を取得 <br>
     * 以下の$間が返る <br>
     * if (condition)$ { stmt; } $ else { stmt; }
     * @return
     * @deprecated {@link #getTrueStatement()}に置き換え
     */
    @Override
    @Deprecated
    public Element[] getTrueBlock() {
        List<Element> elements = new ArrayList<Element>();
        Element paren = getChildNodesByNodeNameAndText("op", ")")[0];
        Element[] kwelses = getChildNodesByNodeNameAndText("kw", "else");
        Element kwelse = null;
        if (kwelses.length > 0) {
            kwelse = kwelses[0];
        }
        Node tmp = paren.getNextSibling();
        while (tmp != null) {
            if (kwelse != null && tmp.isSameNode(kwelse)) {
                break;
            }
            elements.add((Element) tmp);
            tmp = tmp.getNextSibling();
        }
        return (Element[]) elements.toArray(new Element[elements.size()]);
    }
    
    /**
     * 条件式の評価結果が非零の場合に実行される文を返す
     * @return 条件式の評価結果が非零の場合に実行される文 ただし見つからなかった場合はnull
     */
    public CStatementElement getTrueStatement() {
    	Element firstStatementElement = this.getFirstChildNode("Stmt");
    	
    	if (firstStatementElement != null) {
    		return new CStatementElement(firstStatementElement);
    	} else {
    		return null;
    	}
    }

    /**
     * False 節を取得 <br>
     * 以下の$間が返る <br>
     * if (condition) { stmt; } else $ { stmt; } $<br>
     * 存在しなければ長さ 0 の配列が返る
     * @return
     * @deprecated {@link #getFalseStatement()}に置き換え
     */
    @Deprecated
    public Element[] getFalseBlock() {
        List<Element> elements = new ArrayList<Element>();
        Element[] kwelses = getChildNodesByNodeNameAndText("kw", "else");
        Element kwelse = null;
        if (kwelses.length > 0) {
            kwelse = kwelses[0];
            Node tmp = kwelse.getNextSibling();
            while (tmp != null) {
                elements.add((Element) tmp);
                tmp = tmp.getNextSibling();
            }
        }
        return (Element[]) elements.toArray(new Element[elements.size()]);
    }

    /**
     * 条件式の評価結果が零の場合に実行される文を返す
     * @return 条件式の評価結果が零の場合に実行される文 ただし見つからなかった場合はnull
     */
    public CStatementElement getFalseStatement() {
    	Element[] childStatementElements = this.getChildrenNode("Stmt");
    	
    	if (childStatementElements.length == 2) {
    		return new CStatementElement(childStatementElements[1]);
    	} else {
    		return null;
    	}
    }

    /**
     * If 文かどうか
     * @param node
     * @return
     */
    public static boolean isIfStatement(Node node) {
        return "Stmt".equals(node.getNodeName())
                && "If".equals(((Element) node).getAttribute("sort"));
    }

}
