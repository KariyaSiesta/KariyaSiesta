/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * グラフはノードとエッジの集合を持つ
 * @author Toshinori OSUKA
 */
public class Graph<T> {
    /** ノードの集合 */
    protected List<GraphNode<T>> nodes = new ArrayList<GraphNode<T>>();
    /** エッジの集合 */
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
     * ノードを追加する
     * @param node
     */
    public void addNode(GraphNode<T> node) {
        this.nodes.add(node);
    }

    /**
     * エッジを追加する
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
     * 内容が一致するノードを返す<br>
     * 見つからなければ null
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
     * 到達しないノード及びエッジを削除する
     */
    public void clean() {
        if (getRoot() == null) {
            return;
        }

        // エッジの掃除
        List<GraphEdge<T>> visitedEdge = new ArrayList<GraphEdge<T>>();
        traverse(getRoot(), visitedEdge);
        edges = visitedEdge;

        // ノードの掃除
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
     * a->b, b->c, b->d みたいなグラフを<br>
     * a->b->c, a->b->d みたいなリストのリストにする
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
     * グラフからパスに変換
     * @param graph
     * @param node 現在のノード
     * @param path 現在のノードまでのパス
     * @param paths 最終的にできあがるパスの集合
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
     * List をコピーする
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
     * 到達可能なエッジの集合を visited に格納する<br>
     * グラフにループがあるととまらないかも
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
     * 指定したノードに入ってくるエッジのリストを取得する<br>
     * 無ければ長さ0の配列が返る
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
     * 指定したノードから出て行くエッジのリストを取得する<br>
     * 無ければ長さ0の配列が返る
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
     * エッジを追加する<br>
     * ノードが無いものはノードをグラフに追加する<br>
     * 重複チェックやnullチェックを行うので便利<br>
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