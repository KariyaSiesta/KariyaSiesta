/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.rule.misra;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.Result;
import org.sapid.checker.cx.graph.GraphNode;
import org.sapid.checker.cx.graph.path.PathGraph;
import org.sapid.checker.cx.wrapper.CAssignExpressionElement;
import org.sapid.checker.cx.wrapper.CExpressionElement;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CFunctionElement;
import org.sapid.checker.cx.wrapper.CLocalElement;
import org.sapid.checker.cx.wrapper.CStatementElement;
import org.sapid.checker.cx.wrapper.CVariableDeclarationElement;
import org.sapid.checker.cx.wrapper.CVariableReference;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;

/**
 * MISRA-C �롼�� 30
 * @author Toshinori OSUKA
 */
public class Misra30 implements CheckerClass {
    /** �롼��Υ�٥� */
    private final static int LEVEL = 1;

    /** �롼��Υ�å����� */
    private final static String MESSAGE = "MISRA-C Rule 30";

    /** ������� */
    List<Result> results = new ArrayList<Result>();

    /** ��ȿ�Ȥ��Ƹ��Ф���Ρ��ɤν��� */
    Set<Element> problemNodes = new HashSet<Element>();

    public List<Result> check(IFile file, CheckRule rule) {
        CFileElement cfile = new CFileElement(file.getDOM());
        CFunctionElement[] functions = cfile.getFunctions();
        for (int i = 0; i < functions.length; i++) {
            PathGraph graph = new PathGraph(functions[i]);
            List<List<GraphNode<Element>>> paths = graph.toPathList();
            for (Iterator<List<GraphNode<Element>>> itr = paths.iterator(); itr
                    .hasNext();) {
                List<GraphNode<Element>> path = itr.next();
                traversePath(path);
            }
        }
        for (Iterator<Element> itr = problemNodes.iterator(); itr.hasNext();) {
            results.add(new Result(null, new NodeOffsetUtil(itr.next())
                    .getRange(), LEVEL, MESSAGE));
        }
        return results;
    }

    /**
     * �ѥ�������å����ư�ȿ�򸡽Ф���
     * @param path
     */
    private void traversePath(List<GraphNode<Element>> path) {
        // ������줿�����������Ƥʤ��ѿ��� ID �Υꥹ��
        List<String> unassigned = new ArrayList<String>();
        for (Iterator<GraphNode<Element>> itr = path.iterator(); itr.hasNext();) {
            GraphNode<Element> node = itr.next();
            if (CLocalElement.isLocal(node.getContent())) {
                CVariableDeclarationElement local = new CLocalElement(node
                        .getContent());
                while (local != null) {
                    if (local.getInitializeElement() == null) {
                        unassigned.add(local.getId());
                    }
                    local = local.getNext();
                }
            } else {
                CStatementElement stmt = new CStatementElement(node
                        .getContent());
                Element e = stmt.getFirstChildNode("Expr");
                if (e == null) {
                    continue;
                }
                CExpressionElement expr = new CExpressionElement(e);
                parseExpression(expr, unassigned);
            }
        }
    }

    /**
     * Expr ����Ϥ���
     * @param expr
     * @param unassigned
     */
    private void parseExpression(CExpressionElement expr,
            List<String> unassigned) {
        if (expr == null) {
        } else if (expr.isAssign()
                && "=".equals(expr.getFirstChildNode("op").getTextContent())) {
            // ����ʸ�ΤȤ�
            CAssignExpressionElement assign = new CAssignExpressionElement(expr
                    .getElem());
            // ���դ����
            CExpressionElement right = assign.getRightHandExpression();
            parseExpression(right, unassigned);
            // ����
            CExpressionElement left = assign.getLeftHandExpression();
            if (left != null && left.isVarRef()) {
                CVariableReference leftvar = new CVariableReference(left
                        .getElem());
                if (!leftvar.isFunction()) {
                    // ���������Τǥꥹ�Ȥ�����
                    unassigned.remove(leftvar.getDefinitionId());
                }
            }
            assign.getLeftHandExpression();
        } else if (expr.isVarRef()) {
            // ����
            CVariableReference varref = new CVariableReference(expr.getElem());
            if (!varref.isFunction()) {
                // ��������Ƥ��ʤ����֤ǻ��Ѥ����ΤǸ���
                if (unassigned.contains(varref.getDefinitionId())) {
                    problemNodes.add(varref.getElem());
                }
            }
        } else {
            // �Ҷ���õ���˹Ԥ�
            CExpressionElement[] exprs = expr.getExpressions();
            for (int i = 0; i < exprs.length; i++) {
                parseExpression(exprs[i], unassigned);
            }
        }
    }
}
