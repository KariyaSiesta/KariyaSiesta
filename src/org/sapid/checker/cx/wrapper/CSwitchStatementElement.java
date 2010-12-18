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
 * Stmt sort="Switch" ����
 * @author Toshinori OSUKA
 */
public class CSwitchStatementElement extends CControlStatementElement {

    public CSwitchStatementElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * ���ʸ��������� switch ($a$) { hoge(); case 1: piyo(); case 2: fuga(); }<br>
     * ̵������ null ���֤�
     * @return
     * @deprecated {@link #getConditionExpression()}���֤�����
     */
    @Override
    @Deprecated
    public CStatementElement getConditionStatement() {
        return super.getConditionStatement();
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
     * ��ȤΥ֥�å�ʸ���֤�
     * @return ��ȤΥ֥�å�ʸ ���������Ĥ���ʤ��ä�����null
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

    /*
     * ���ֽ��� case �ʹߤ��֤�<br> switch ($a$) { hoge(); $case 1: piyo(); case 2:
     * fuga(); $}<br> case ��1�Ĥ�̵�����ˤ� ���������֤�
     */
    // @Override
    // public Element[] getTrueBlock() {
    // List<Element> list = new ArrayList<Element>();
    // Element[] labels = getChildrenNode("Label");
    // Element firstcase = null;
    // for (int i = 0; i < labels.length; i++) {
    // CLabelElement label = new CLabelElement(labels[i]);
    // if (label.isCaseLabel() || label.isDefaultLabel()) {
    // firstcase = labels[i];
    // break;
    // }
    // }
    // if (firstcase == null) {
    // return (Element[]) list.toArray(new Element[list.size()]);
    // }
    //
    // Element[] rbraces = getChildNodesByNodeNameAndText("op", "}");
    // Element rbrace = rbraces[rbraces.length - 1];
    // Node tmp = firstcase;
    // while (!tmp.isSameNode(rbrace)) {
    // list.add((Element) tmp);
    // tmp = tmp.getNextSibling();
    // }
    // return (Element[]) list.toArray(new Element[list.size()]);
    // }
    /**
     * Label ��ľ��ˤ��� Stmt �Υꥹ�Ȥ��֤�<br>
     * @return
     */
    public Element[] getStatementsNextLabel() {
        List<Element> list = new ArrayList<Element>();
        
        for (CLabelElement label : this.getCaseOrDefaultLabels()) {
        	Element tmp = label.getElem();
            while ((tmp = (Element) tmp.getNextSibling()) != null) {
                if (!"Stmt".equals(tmp.getNodeName())) {
                    continue;
                }
                if (!list.contains(tmp)) {
                    list.add(tmp);
                }
                break;
            }
        }
        
        return list.toArray(new Element[0]);
    }
    
    /**
     * Case �� Default �Υ�٥�Υꥹ�Ȥ��֤�
     * @return
     */
    public CLabelElement[] getCaseOrDefaultLabels() {
        List<CLabelElement> list = new ArrayList<CLabelElement>();
        CLabelElement[] labels = getLabels();
        for (int i = 0; i < labels.length; i++) {
            if (labels[i].isCaseLabel() || labels[i].isDefaultLabel()) {
                list.add(labels[i]);
            }
        }
        return (CLabelElement[]) list.toArray(new CLabelElement[list.size()]);
    }

    /**
     * Switch ʸ���ɤ���
     * @param node
     * @return
     */
    public static boolean isSwitchStatement(Node node) {
        return "Stmt".equals(node.getNodeName())
                && "Switch".equals(((Element) node).getAttribute("sort"));
    }

}
