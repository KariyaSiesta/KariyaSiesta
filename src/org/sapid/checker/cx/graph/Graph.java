/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ����դϥΡ��ɤȥ��å��ν�������
 * @author Toshinori OSUKA
 */
public class Graph<T> {
    /** �Ρ��ɤν��� */
    protected List<GraphNode<T>> nodes = new ArrayList<GraphNode<T>>();
    /** ���å��ν��� */
    protected List<GraphEdge<T>> edges = new ArrayList<GraphEdge<T>>();

    protected GraphNode<T> root = null;

    /**
     * @return the nodes
     */
    public List<GraphNode<T>> getNodes() {
        return nodes;
    }

    /**
     * @param nodes the nodes to set
     */
    public void setNodes(List<GraphNode<T>> nodes) {
        this.nodes = nodes;
    }

    /**
     * @return the edges
     */
    public List<GraphEdge<T>> getEdges() {
        return edges;
    }

    /**
     * @param edges the edges to set
     */
    public void setEdges(List<GraphEdge<T>> edges) {
        this.edges = edges;
    }

    /**
     * �Ρ��ɤ��ɲä���
     * @param node
     */
    public void addNode(GraphNode<T> node) {
        this.nodes.add(node);
    }

    /**
     * ���å����ɲä���
     * @param edge
     */
    public void addEdge(GraphEdge<T> edge) {
        if (!edges.contains(edge)) {
            this.edges.add(edge);
        }
    }

    /**
     * @return the root
     */
    public GraphNode<T> getRoot() {
        return root;
    }

    /**
     * @param root the root to set
     */
    public void setRoot(GraphNode<T> root) {
        this.root = root;
    }

    /**
     * ���Ƥ����פ���Ρ��ɤ��֤�<br>
     * ���Ĥ���ʤ���� null
     * @param content
     * @return
     */
    public GraphNode<T> findNode(T content) {
        for (Iterator<GraphNode<T>> itr = nodes.iterator(); itr.hasNext();) {
            GraphNode<T> node = itr.next();
            if (node.getContent().equals(content)) {
                return node;
            }
        }
        return null;
    }

    /**
     * ��ã���ʤ��Ρ��ɵڤӥ��å���������
     */
    public void clean() {
        if (getRoot() == null) {
            return;
        }

        // ���å����ݽ�
        List<GraphEdge<T>> visitedEdge = new ArrayList<GraphEdge<T>>();
        traverse(getRoot(), visitedEdge);
        edges = visitedEdge;

        // �Ρ��ɤ��ݽ�
        List<GraphNode<T>> visitedNode = new ArrayList<GraphNode<T>>();
        for (Iterator<GraphEdge<T>> itr = visitedEdge.iterator(); itr.hasNext();) {
            GraphEdge<T> e = itr.next();
            if (!visitedNode.contains(e.getDstNode())) {
                visitedNode.add(e.getDstNode());
            }
            if (!visitedNode.contains(e.getSrcNode())) {
                visitedNode.add(e.getSrcNode());
            }
        }
        nodes = visitedNode;
    }

    /**
     * a->b, b->c, b->d �ߤ����ʥ���դ�<br>
     * a->b->c, a->b->d �ߤ����ʥꥹ�ȤΥꥹ�Ȥˤ���
     * @return
     */
    public List<List<GraphNode<T>>> toPathList() {
        List<List<GraphNode<T>>> paths = new ArrayList<List<GraphNode<T>>>();
        if (getRoot() != null) {
            makePath(getRoot(), new ArrayList<GraphNode<T>>(), paths);
        }
        return paths;
    }

    /**
     * ����դ���ѥ����Ѵ�
     * @param graph
     * @param node ���ߤΥΡ���
     * @param path ���ߤΥΡ��ɤޤǤΥѥ�
     * @param paths �ǽ�Ū�ˤǤ�������ѥ��ν���
     */
    private void makePath(GraphNode<T> node, List<GraphNode<T>> path,
            List<List<GraphNode<T>>> paths) {
        List<GraphNode<T>> newList = copyList(path);
        newList.add(node);
        List<GraphEdge<T>> edges = getOutgoingEdge(node);
        if (edges.size() > 0) {
            for (Iterator<GraphEdge<T>> itr = edges.iterator(); itr.hasNext();) {
                GraphEdge<T> edge = (GraphEdge<T>) itr.next();
                makePath(edge.getDstNode(), newList, paths);
            }
        } else {
            paths.add(newList);
        }
    }

    /**
     * List �򥳥ԡ�����
     * @param list
     * @return
     */
    private List<GraphNode<T>> copyList(List<GraphNode<T>> list) {
        List<GraphNode<T>> newList = new ArrayList<GraphNode<T>>();
        for (Iterator<GraphNode<T>> itr = list.iterator(); itr.hasNext();) {
            newList.add(itr.next());
        }
        return newList;
    }

    /**
     * ��ã��ǽ�ʥ��å��ν���� visited �˳�Ǽ����<br>
     * ����դ˥롼�פ�����ȤȤޤ�ʤ�����
     * @param node
     * @param visited
     */
    private void traverse(GraphNode<T> node, List<GraphEdge<T>> visited) {
        List<GraphEdge<T>> dsts = getOutgoingEdge(node);
        for (Iterator<GraphEdge<T>> itr = dsts.iterator(); itr.hasNext();) {
            GraphEdge<T> graphEdge = itr.next();
            if (visited.contains(graphEdge)) {
                continue;
            }
            visited.add(graphEdge);
            traverse(graphEdge.getDstNode(), visited);
        }
    }

    /**
     * ���ꤷ���Ρ��ɤ����äƤ��륨�å��Υꥹ�Ȥ��������<br>
     * ̵�����Ĺ��0�������֤�
     * @param node
     * @return
     */
    public List<GraphEdge<T>> getIncomingEdge(GraphNode<T> node) {
        List<GraphEdge<T>> list = new ArrayList<GraphEdge<T>>();
        for (Iterator<GraphEdge<T>> itr = edges.iterator(); itr.hasNext();) {
            GraphEdge<T> edge = itr.next();
            if (edge.getDstNode().equals(node)) {
                list.add(edge);
            }
        }
        return list;
    }

    /**
     * ���ꤷ���Ρ��ɤ���ФƹԤ����å��Υꥹ�Ȥ��������<br>
     * ̵�����Ĺ��0�������֤�
     * @param node
     * @return
     */
    public List<GraphEdge<T>> getOutgoingEdge(GraphNode<T> node) {
        List<GraphEdge<T>> list = new ArrayList<GraphEdge<T>>();
        for (Iterator<GraphEdge<T>> itr = edges.iterator(); itr.hasNext();) {
            GraphEdge<T> edge = itr.next();
            if (edge.getSrcNode().equals(node)) {
                list.add(edge);
            }
        }
        return list;
    }

    /**
     * ���å����ɲä���<br>
     * �Ρ��ɤ�̵����ΤϥΡ��ɤ򥰥�դ��ɲä���<br>
     * ��ʣ�����å���null�����å���Ԥ��Τ�����<br>
     * @param src
     * @param dst
     */
    public void makeEdge(GraphNode<T> src, GraphNode<T> dst) {
        if (src == null || dst == null) {
            return;
        }
        if (!nodes.contains(src)) {
            nodes.add(src);
        }
        if (!nodes.contains(dst)) {
            nodes.add(dst);
        }
        GraphEdge<T> edge = new GraphEdge<T>(src, dst);
        if (!edges.contains(edge)) {
            edges.add(edge);
        }
    }

}