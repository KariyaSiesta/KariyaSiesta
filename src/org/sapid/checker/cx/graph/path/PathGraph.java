/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.graph.path;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sapid.checker.cx.graph.Graph;
import org.sapid.checker.cx.graph.GraphEdge;
import org.sapid.checker.cx.graph.GraphNode;
import org.sapid.checker.cx.wrapper.CDoStatementElement;
import org.sapid.checker.cx.wrapper.CElement;
import org.sapid.checker.cx.wrapper.CExpressionElement;
import org.sapid.checker.cx.wrapper.CForStatementElement;
import org.sapid.checker.cx.wrapper.CFunctionElement;
import org.sapid.checker.cx.wrapper.CIfStatementElement;
import org.sapid.checker.cx.wrapper.CLabelElement;
import org.sapid.checker.cx.wrapper.CLocalElement;
import org.sapid.checker.cx.wrapper.CStatementElement;
import org.sapid.checker.cx.wrapper.CSwitchStatementElement;
import org.sapid.checker.cx.wrapper.CWhileStatementElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * �ѥ���ɽ�����륰���<br>
 * GOTO �ˤ�̤�б� / loop �� 0��ޤ�뤫1��ޤ��Τ�<br>
 * default ��٥�Ϲ�θ���ʤ�<br>
 * �ɤ��ͤ��Ƥ�᥽�å� exit �ΥΡ��ɤ��ߤ���<br>
 * TODO �����ɤ���������ΤǼ�������
 * @author Toshinori OSUKA
 */
public class PathGraph extends Graph<Element> {

    /**
     * �ѥ�����դ�ӥ�ɤ���
     * @param function
     * @return
     */
    public PathGraph(CFunctionElement function) {
        Element[] instructions = function.getContentBlockStatement().getChildStatementsAndLocals();
        if (instructions.length > 0) {
            visitBlock(instructions, null, null, null);
            setRoot(findNode(instructions[0]));
            clean();
            // print();
        }
    }

    private List<GraphNode<Element>> makeChildNodes(Element[] instructions) {
        List<GraphNode<Element>> list = new ArrayList<GraphNode<Element>>();
        for (int i = 0; i < instructions.length; i++) {
            GraphNode<Element> node = new GraphNode<Element>(instructions[i]);
            list.add(node);
        }
        return list;
    }

    protected void visitStatement(GraphNode<Element> stmt,
            GraphNode<Element> next, GraphNode<Element> exit) {
        CStatementElement cstmt = new CStatementElement(stmt.getContent());
        if (CIfStatementElement.isIfStatement(stmt.getContent())) {
            visitIfStatement(stmt, next, exit);
        } else if (CSwitchStatementElement.isSwitchStatement(stmt.getContent())) {
            visitSwitchStatement(stmt, next);
        } else if (CWhileStatementElement.isWhileStatement(stmt.getContent())) {
            visitWhileStatement(stmt, next);
        } else if (CForStatementElement.isForStatement(stmt.getContent())) {
            visitForStatement(stmt, next);
        } else if (CDoStatementElement.isDoStatement(stmt.getContent())) {
            visitDoStatement(stmt, next);
        } else if (cstmt.isBreakStatement() || cstmt.isContinueStatement()) {
        	makeEdge(stmt, exit); // Loop ����ȴ����
        } else if (! cstmt.isReturnStatement()) {
        	makeEdge(stmt, next);
        }
    }

    /**
     * If ʸ
     * @param ifStatementNode
     * @param next
     * @param graph
     */
    protected void visitIfStatement(GraphNode<Element> ifStatementNode,
            GraphNode<Element> next, GraphNode<Element> exit) {
        CIfStatementElement ifStatement = new CIfStatementElement(ifStatementNode.getContent());

        // ��Ｐ
        CExpressionElement conditionExpression = ifStatement.getConditionExpression();
        GraphNode<Element> conditionExpressionNode;
        if (conditionExpression == null) {
            // TODO ���������ƥ��
            conditionExpressionNode = ifStatementNode;
        } else {
            // If ʸ���Τ�����ʸ��
            conditionExpressionNode = new GraphNode<Element>(conditionExpression.getElem());
            makeEdge(ifStatementNode, conditionExpressionNode);
        }
        
        Element[] trueStatements;
        CStatementElement trueStatement = ifStatement.getTrueStatement();
        if (trueStatement == null) {
        	trueStatements = new Element[] {};
        } else {
        	if (trueStatement.isBlockStatement()) {
        		trueStatements = trueStatement.getChildStatementsAndLocals();
        	} else {
        		trueStatements = new Element[] {trueStatement.getElem()};
        	}
        }
        visitBlock(trueStatements, conditionExpressionNode, next, exit);
        
        Element[] falseStatements;
        CStatementElement falseStatement = ifStatement.getFalseStatement();
        if (falseStatement == null) {
        	falseStatements = new Element[] {};
        } else {
        	if (falseStatement.isBlockStatement()) {
        		falseStatements = falseStatement.getChildStatementsAndLocals();
        	} else {
        		falseStatements = new Element[] {falseStatement.getElem()};
        	}
        }
        visitBlock(falseStatements, conditionExpressionNode, next, exit);
    }

    /**
     * TODO default �ΰ�̣���᤹��
     * @param switchStatementNode
     * @param next
     */
    protected void visitSwitchStatement(GraphNode<Element> switchStatementNode,
            GraphNode<Element> next) {
        CSwitchStatementElement switchStatement =
        		new CSwitchStatementElement(switchStatementNode.getContent());

        // Switch ʸ���Τ����Ｐ��
        CExpressionElement conditionExpression = switchStatement.getConditionExpression();
        GraphNode<Element> last;
        if (conditionExpression == null) {
            last = switchStatementNode;
        } else {
            last = new GraphNode<Element>(conditionExpression.getElem());
            makeEdge(switchStatementNode, last);
        }

        // ��٥�Υꥹ��
        CLabelElement[] labels = switchStatement.getCaseOrDefaultLabels();
        if (labels.length == 0) {
            // ��٥뤬�ʤ����
            makeEdge(last, next);
            return;
        }

        Element[] block;
        CStatementElement contentBlockStatement = switchStatement.getContentBlockStatement();
        if (contentBlockStatement == null) {
        	block = new Element[] {};
        } else {
        	block = contentBlockStatement.getChildStatementsAndLocals();
        }
        // �ǽ�Υ�٥�ޤǤϤĤʤ��ʤ�
        List<Element> tmp = new ArrayList<Element>();
        for (int i = 0; i < block.length; i++) {
            if (labels[0].getElem().compareDocumentPosition(block[i]) == Node.DOCUMENT_POSITION_FOLLOWING) {
                tmp.add(block[i]);
            }
        }
        block = (Element[]) tmp.toArray(new Element[tmp.size()]);
        
        // �Ȥꤢ�������̤ˤĤʤ�
        visitBlock(block, last, next, next);

        // Label Jump
        Element[] stmts = switchStatement.getStatementsNextLabel();
        for (int i = 0; i < stmts.length; i++) {
            makeEdge(last, findNode(stmts[i]));
        }
    }

    protected void visitWhileStatement(GraphNode<Element> whileStatementNode,
            GraphNode<Element> next) {
        CWhileStatementElement whileStatement =
        		new CWhileStatementElement(whileStatementNode.getContent());
        CExpressionElement conditionExpression = whileStatement.getConditionExpression();

        GraphNode<Element> condNode;
        if (conditionExpression == null) {
            // TODO ���������ƥ��
            condNode = whileStatementNode;
        } else {
            // while ʸ���Τ�����ʸ��
            condNode = new GraphNode<Element>(conditionExpression.getElem());
            makeEdge(whileStatementNode, condNode);
            makeEdge(condNode, next);
        }
        visitBlock(whileStatement.getTrueBlock(), condNode, next, next);
    }

    protected void visitDoStatement(GraphNode<Element> doStatementNode,
            GraphNode<Element> next) {
        CDoStatementElement doStatement =
        		new CDoStatementElement(doStatementNode.getContent());

        CExpressionElement conditionExpression = doStatement.getConditionExpression();
        GraphNode<Element> condNode;
        if (conditionExpression == null) {
            condNode = null;
        } else {
            condNode = new GraphNode<Element>(conditionExpression.getElem());
        }
        Element[] block = doStatement.getTrueBlock();
        visitBlock(block, doStatementNode, condNode, next);

        // Cond ���鼡��
        makeEdge(condNode, next);

    }

    protected void visitForStatement(GraphNode<Element> forStatementNode,
            GraphNode<Element> next) {
        CForStatementElement forStatement =
        		new CForStatementElement(forStatementNode.getContent());

        CElement current = forStatement.getInitialExpression();
        GraphNode<Element> curNode;
        if (current == null) {
            curNode = forStatementNode;
        } else {
            curNode = new GraphNode<Element>(current.getElem());
            makeEdge(forStatementNode, curNode);
        }

        current = forStatement.getConditionExpression();
        if (current == null) {
        } else {
            GraphNode<Element> condNode = new GraphNode<Element>(current
                    .getElem());
            makeEdge(curNode, condNode);
            makeEdge(curNode, next);
            curNode = condNode;
        }

        current = forStatement.getIncrementalExpression();
        if (current == null) {
        } else {
            GraphNode<Element> incNode = new GraphNode<Element>(current
                    .getElem());
            makeEdge(curNode, incNode);
            curNode = incNode;
        }

        visitBlock(forStatement.getTrueBlock(), curNode, next, next);

    }

    private void visitChildren(GraphNode<Element> next,
            GraphNode<Element> exit, List<GraphNode<Element>> list) {
        for (int j = 0; j < list.size() - 1; j++) {
            if (CStatementElement.isStatement(list.get(j).getContent())) {
                visitStatement(list.get(j), list.get(j + 1), next);
            } else {
                // Local �ʤ鼡�Ȥ��äĤ���
                makeEdge(list.get(j), list.get(j + 1));
            }
        }
        if (CStatementElement.isStatement(list.get(list.size() - 1)
                .getContent())) {
            visitStatement(list.get(list.size() - 1), next, next);
        }
    }

    private void visitBlock(Element[] block, GraphNode<Element> from,
            GraphNode<Element> next, GraphNode<Element> exit) {
        List<Element> instructions = new ArrayList<Element>();
        for (int i = 0; i < block.length; i++) {
            if (CStatementElement.isStatement(block[i])
                    || CLocalElement.isLocal(block[i])) {
                instructions.add(block[i]);
            }
        }
        List<GraphNode<Element>> list = makeChildNodes((Element[]) instructions
                .toArray(new Element[instructions.size()]));

        if (list.size() > 0) {
            // ���ʸ����ǽ�� Stmt ��
            makeEdge(from, list.get(0));
            // �Ҷ�ã�β���
            visitChildren(next, exit, list);
            // for ʸ�κǸ�� Stmt ���� ����ʸ��
            makeEdge(list.get(list.size() - 1), next);
        } else {
            // Block ��̿�᤬�ʤ��Ȥ��Ͼ��ʸ����ľ�ܼ���
            makeEdge(from, next);
        }
    }

    public void print() {
        System.out.println("----- Nodes -----");
        for (Iterator<GraphNode<Element>> iterator = nodes.iterator(); iterator
                .hasNext();) {
            GraphNode<Element> node = iterator.next();
            System.out.print(node.getNumber() + ":\t");
            System.out.println(node.getContent().getTextContent().replaceAll(
                    "\n", ""));
        }
        System.out.println("----- Edges -----");
        for (Iterator<GraphEdge<Element>> iterator = edges.iterator(); iterator
                .hasNext();) {
            GraphEdge<Element> edge = iterator.next();
            System.out.println(edge.getSrcNode().getNumber() + " -> "
                    + edge.getDstNode().getNumber());
        }
    }
}
