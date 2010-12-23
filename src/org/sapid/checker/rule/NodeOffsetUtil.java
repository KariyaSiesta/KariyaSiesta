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
 * を相互変換する
 * @author Toshinori OSUKA
 */
public class NodeOffsetUtil {
    /** モデル XML */
    private Document document = null;
    /** 対象ノード */
    private Node node = null;
    /** 先頭からのオフセット */
    private int offset = -1;
    /** ノードの範囲 */
    private Range range = null;

    /**
     * コンストラクタ(ノード)
     * @param document
     * @param node
     */
    public NodeOffsetUtil(Document document, Node node) {
        this.document = document;
        this.node = node;
    }
    
    /**
     * コンストラクタ(ノード)
     * @param document
     * @param node
     */
    public NodeOffsetUtil(Node node) {
        this.document = node.getOwnerDocument();
        this.node = node;
    }

    /**
     * コンストラクタ(オフセット)
     * @param document
     * @param offset
     */
    public NodeOffsetUtil(Document document, int offset) {
        this.document = document;
        this.offset = offset;
    }

    /**
     * ファイルの先頭からのバイト数
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
     * Range を取得する
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
     * XPath を取得する
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
     * ノードを取得する
     * @return
     */
    public Node getNode() {
        if (node != null) {
            return node;
        }
        node = getNodeFromOffset(document.getDocumentElement(), getOffset());
        return node;
    }

    /** ファイルの改行コード */
    private String nlop = "\n";

    /**
     * Node から Range を力技で取得
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
     * 指定ノード以下のテキストノードの中身を結合して返す．順番は保障しない．
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
                    // DOM にすると改行コードがすべて LF になるが
                    // Windows で使うと 改行の CRLF が <sp>CR</sp><nl>LF</nl>
                    // となる CX-Model のバグを利用して <sp>CR</sp> が
                    // ひとつでもあればファイルの改行コードを CRLF と判断する
                    // FIXME アドホックすぎるので CX-Model が直り次第ここも直す
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

    /** 読み込んだテキストの合計サイズ */
    private int readsize = 0;

    /**
     * ファイル先頭から読み込んだバイト数が，<br>
     * offset の量を越えた時点で読んでいる構文要素のノードを返す
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
