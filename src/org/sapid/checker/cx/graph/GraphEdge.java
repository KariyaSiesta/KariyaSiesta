/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.graph;

/**
 * ����դΥ��å�
 * @author Toshinori OSUKA
 */
public class GraphEdge<T> {
    private GraphNode<T> srcNode;
    private GraphNode<T> dstNode;

    /**
     * ���󥹥ȥ饯��
     * @param srcNode ���å��κ���
     * @param dstNode ���å�����
     */
    public GraphEdge(GraphNode<T> srcNode, GraphNode<T> dstNode) {
        super();
        this.srcNode = srcNode;
        this.dstNode = dstNode;
    }

    /**
     * @return the srcNode
     */
    public GraphNode<T> getSrcNode() {
        return srcNode;
    }

    /**
     * @return the dstNode
     */
    public GraphNode<T> getDstNode() {
        return dstNode;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GraphEdge)) {
            return false;
        }
        GraphEdge<T> edge = (GraphEdge<T>) obj;
        return srcNode.equals(edge.getSrcNode())
                && dstNode.equals(edge.getDstNode());
    }

}
