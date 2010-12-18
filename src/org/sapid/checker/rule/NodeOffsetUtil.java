/*
 * Copyright(c) 2008 Aisin Comcruise
 *  All Rights Reserved
 */
package org.sapid.checker.rule;

import org.sapid.checker.core.Range;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Node <-> Offset <-> Range -> XPath<br />
 * ������Ѵ�����
 * @author Toshinori OSUKA
 */
public class NodeOffsetUtil {
    /** ��ǥ� XML */
    private Document document = null;
    /** �оݥΡ��� */
    private Node node = null;
    /** ��Ƭ����Υ��ե��å� */
    private int offset = -1;
    /** �Ρ��ɤ��ϰ� */
    private Range range = null;

    /**
     * ���󥹥ȥ饯��(�Ρ���)
     * @param document
     * @param node
     */
    public NodeOffsetUtil(Document document, Node node) {
        this.document = document;
        this.node = node;
    }
    
    /**
     * ���󥹥ȥ饯��(�Ρ���)
     * @param document
     * @param node
     */
    public NodeOffsetUtil(Node node) {
        this.document = node.getOwnerDocument();
        this.node = node;
    }

    /**
     * ���󥹥ȥ饯��(���ե��å�)
     * @param document
     * @param offset
     */
    public NodeOffsetUtil(Document document, int offset) {
        this.document = document;
        this.offset = offset;
    }

    /**
     * �ե��������Ƭ����ΥХ��ȿ�
     * @return
     */
    public int getOffset() {
        if (offset != -1) {
            return offset;
        }
        range = getRange();
        offset = range.getOffset();
        return offset;
    }

    /**
     * Range ���������
     * @return
     */
    public Range getRange() {
        if (range != null) {
            return range;
        }
        range = getRangeFromNode(getNode());
        return range;
    }

    /**
     * XPath ���������
     * @return
     */
    public String getXPath() {
        Node node = getNode();
        Node tmp = node;
        String xpath = "[text()=\"" + tmp.getTextContent() + "\"]";
        xpath = node.getNodeName() + xpath;
        while (!((tmp = tmp.getParentNode()) == null || tmp
                .isSameNode(document))) {
            xpath = tmp.getNodeName() + "/" + xpath;
        }
        xpath = "/" + xpath;
        return xpath;
    }

    /**
     * �Ρ��ɤ��������
     * @return
     */
    public Node getNode() {
        if (node != null) {
            return node;
        }
        node = getNodeFromOffset(document.getDocumentElement(), getOffset());
        return node;
    }

    /** �ե�����β��ԥ����� */
    private String nlop = "\n";

    /**
     * Node ���� Range ���ϵ��Ǽ���
     * @param node
     * @return
     */
    private Range getRangeFromNode(Node node) {
        String node_text = getTextContentR(node);

        String prev_source = "";
        Node tmp = node;

        do {
            while ((tmp = tmp.getPreviousSibling()) != null) {
                prev_source = getTextContentR(tmp) + prev_source;
            }
            node = node.getParentNode();
            tmp = node;
        } while (node != null);
        int start_line = prev_source.split("\n").length;
        int end_line = start_line + node_text.split("\n").length - 1;
        return new Range(start_line, 0, end_line, 0, prev_source.length(),
                node_text.length());
    }

    /**
     * ����Ρ��ɰʲ��Υƥ����ȥΡ��ɤ���Ȥ��礷���֤������֤��ݾ㤷�ʤ���
     * @param node
     * @return
     */
    private String getTextContentR(Node node) {
        String ret = "";
        NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() == Node.TEXT_NODE) {
                String textContent = n.getTextContent();
                if ("\n".equals(textContent)
                        && "sp".equals(n.getParentNode().getNodeName())) {
                    // DOM �ˤ���Ȳ��ԥ����ɤ����٤� LF �ˤʤ뤬
                    // Windows �ǻȤ��� ���Ԥ� CRLF �� <sp>CR</sp><nl>LF</nl>
                    // �Ȥʤ� CX-Model �ΥХ������Ѥ��� <sp>CR</sp> ��
                    // �ҤȤĤǤ⤢��Хե�����β��ԥ����ɤ� CRLF ��Ƚ�Ǥ���
                    // FIXME ���ɥۥå�������Τ� CX-Model ��ľ�꼡�褳����ľ��
                    nlop = "\r\n";
                }
                if ("comment".equals(n.getParentNode().getNodeName())) {
                    ret += textContent.replaceAll("\n", nlop);
                } else {
                    ret += textContent;
                }
            } else {
                ret += getTextContentR(n);
            }
        }
        return ret;
    }

    /** �ɤ߹�����ƥ����Ȥι�ץ����� */
    private int readsize = 0;

    /**
     * �ե�������Ƭ�����ɤ߹�����Х��ȿ�����<br>
     * offset ���̤�ۤ����������ɤ�Ǥ��빽ʸ���ǤΥΡ��ɤ��֤�
     * @param node
     * @param offset
     * @return
     */
    private Node getNodeFromOffset(Node node, int offset) {
        NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() != Node.TEXT_NODE) {
                Node result = getNodeFromOffset(n, offset);
                if (result != null) {
                    return result;
                }
            } else {
                readsize += n.getTextContent().length();
                if (readsize > offset) {
                    return node;
                }
            }
        }
        return null;
    }

}
