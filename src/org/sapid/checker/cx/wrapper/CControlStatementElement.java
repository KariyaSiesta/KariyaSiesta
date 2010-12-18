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
 * ���湽¤����� Stmt ����<br>
 * If / for / while /switch / do �Ϥ��Υ��饹��Ѿ�����
 * @author Toshinori OSUKA
 */
public abstract class CControlStatementElement extends CStatementElement {

    public CControlStatementElement(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * sort �˱����� ���󥹥��󥹤���������<br>
     * node ������ʸ�Ǥʤ����� null ���֤�
     * @param node
     * @return
     */
    public static CControlStatementElement getInstance(Node node) {
        CControlStatementElement stmt = null;
        if (CForStatementElement.isForStatement(node)) {
            stmt = new CForStatementElement((Element) node);
        } else if (CWhileStatementElement.isWhileStatement(node)) {
            stmt = new CWhileStatementElement((Element) node);
        } else if (CIfStatementElement.isIfStatement(node)) {
            stmt = new CIfStatementElement((Element) node);
        } else if (CSwitchStatementElement.isSwitchStatement(node)) {
            stmt = new CSwitchStatementElement((Element) node);
        } else if (CDoStatementElement.isDoStatement(node)) {
            stmt = new CDoStatementElement((Element) node);
        }
        return stmt;
    }

    /**
     * ���ʸ���������<br>
     * @return
     * @deprecated {@link #getConditionExpression()}���֤�����
     */
    @Deprecated
    public CStatementElement getConditionStatement() {
        // �ǽ�� "(" �� ")" �δ�
        Element lparen = getChildNodesByNodeNameAndText("op", "(")[0];
        Element rparen = getChildNodesByNodeNameAndText("op", ")")[0];
        return getInnerStatement(lparen, rparen);
    }
    
    /**
     * ��Ｐ���������
     * @return ��Ｐ ���������Ĥ���ʤ�����null
     */
    abstract public CExpressionElement getConditionExpression();

    /**
     * ʬ���� true ���ä��Ȥ��˼¹Ԥ����̿��Υꥹ�Ȥ��������
     * @return
     */
    public Element[] getTrueBlock() {
        // ) �ʹߤ� ̿������
        Element paren = getChildNodesByNodeNameAndText("op", ")")[0];
        List<Element> list = new ArrayList<Element>();
        Node tmp = paren.getNextSibling();
        while (tmp != null) {
            list.add((Element) tmp);
            tmp = tmp.getNextSibling();
        }
        return (Element[]) list.toArray(new Element[list.size()]);
    }

    /**
     * ���ꤵ�줿 Element �δ֤ˤ��� Stmt ���������
     * @param from
     * @param to
     * @return
     */
    protected CStatementElement getInnerStatement(Element from, Element to) {
        Node tmp = from.getNextSibling();
        while (!tmp.isSameNode(to)) {
            if (CStatementElement.isStatement(tmp)) {
                return new CStatementElement((Element) tmp);
            }
            tmp = tmp.getNextSibling();
        }
        return null;
    }

    /**
     * ����ʸ���ɤ���
     * @param node
     * @return
     */
    public static boolean isControlStatement(Node node) {
        return CIfStatementElement.isIfStatement(node)
                || CSwitchStatementElement.isSwitchStatement(node)
                || CForStatementElement.isForStatement(node)
                || CWhileStatementElement.isWhileStatement(node)
                || CDoStatementElement.isDoStatement(node);
    }

}
