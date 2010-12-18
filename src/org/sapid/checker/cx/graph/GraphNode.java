/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.graph;

/**
 * ����դΥΡ���
 * @author Toshinori OSUKA
 */
public class GraphNode<T> {
    private T content;
    private static int counter = 0;
    private int number;

    /**
     * ���󥹥ȥ饯��
     * @param content
     */
    public GraphNode(T content) {
        super();
        this.content = content;
        number = counter++;
    }

    /**
     * @return the content
     */
    public T getContent() {
        return content;
    }

    /**
     * �Ρ��ɤ��̤��ֹ�����
     * @return
     */
    public int getNumber() {
        return number;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GraphNode)) {
            return false;
        }
        GraphNode<T> node = (GraphNode<T>) obj;
        return content.equals(node.getContent());
    }
}
