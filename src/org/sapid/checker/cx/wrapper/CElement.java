/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * CX-model のラッパークラス すべてのラッパー要素はこのクラスを継承する
 * @author Toshinori OSUKA
 */
public class CElement {
    /** DOM のノード */
    protected Element elem;

    /**
     * コンストラクタ
     * @param elem
     */
    public CElement(Element elem) {
        super();
        this.elem = elem;
    }

    /**
     * DOM ノードを取得する
     * @return
     */
    public Element getElem() {
        return elem;
    }

    /**
     * Id を取得する
     * @return 無い場合は null を返す
     */
    public String getId() {
        return elem.getAttribute("id");
    }

    /**
     * 子要素のうち一番最初に現れる指定された名前の要素を返す
     * @param nodeName
     * @return
     */
    public Element getFirstChildNode(String nodeName) {
        NodeList nl = elem.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            if (nodeName.equals(nl.item(i).getNodeName())) {
                return (Element) nl.item(i);
            }
        }
        return null;
    }
    
    /**
     * 子要素のうち一番最後に現れる指定された名前の要素を返す
     * @param nodeName
     * @return
     */
    public Element getLastChildNode(String nodeName) {
    	NodeList childNodeList = this.elem.getChildNodes();
    	
    	for (int i = childNodeList.getLength() - 1; i >= 0; i--) {
    		Node childNode = childNodeList.item(i);
    		String childNodeName = childNode.getNodeName();
    		
    		if (childNodeName.equals(nodeName)) {
    			return (Element) childNode;
    		}
    	}
    	
    	return null;
    }

    /**
     * 子要素のうち一番最初に現れる指定された名前と sort を持つ要素を返す
     * @param nodeName
     * @return
     */
    public Element getFirstChildNodeSort(String nodeName, String sort) {
        NodeList nl = elem.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            Element e = (Element) nl.item(i);
            if (nodeName.equals(e.getNodeName())
                    && sort.equals(e.getAttribute("sort"))) {
                return (Element) nl.item(i);
            }
        }
        return null;
    }

    /**
     * 指定された名前を持つ子要素をすべて返す
     * @param nodeName
     * @return
     */
    public Element[] getChildrenNode(String nodeName) {
        List<Element> list = new ArrayList<Element>();
        NodeList nl = elem.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            if (nodeName.equals(nl.item(i).getNodeName())) {
                list.add((Element) nl.item(i));
            }
        }
        return (Element[]) list.toArray(new Element[list.size()]);
    }

    /**
     * 指定された id を持つ要素のうち最初に見つかったものを返す 見つからなければ null を返す
     * @param id
     * @return
     */
    public Element getElementById(String id) {
        NodeList nl = elem.getElementsByTagName("*");
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            Element e = (Element) nl.item(i);
            if (id.equals(e.getAttribute("id"))) {
                return e;
            }
        }
        return null;
    }

    /**
     * DOM ノードの要素名を返す
     * @return
     */
    public String getNodeName() {
        return elem.getNodeName();
    }

    /**
     * すべての Function 要素を返す 1つも無い場合には長さ0の配列を返す
     * @return
     */
    public CFunctionElement[] getFunctions() {
        List<CFunctionElement> list = new ArrayList<CFunctionElement>();
        NodeList nl = elem.getElementsByTagName("Function");
        for (int i = 0; i < nl.getLength(); i++) {
            list.add(new CFunctionElement((Element) nl.item(i)));
        }
        return (CFunctionElement[]) list.toArray(new CFunctionElement[list
                .size()]);
    }

    /**
     * すべての Stmt 要素を返す 1つも無い場合には長さ0の配列を返す
     * @return
     */
    public CStatementElement[] getStatments() {
        List<CStatementElement> list = new ArrayList<CStatementElement>();
        NodeList nl = elem.getElementsByTagName("Stmt");
        for (int i = 0; i < nl.getLength(); i++) {
            list.add(new CStatementElement((Element) nl.item(i)));
        }
        return (CStatementElement[]) list.toArray(new CStatementElement[list
                .size()]);
    }

    /**
     * すべての Expr 要素を文書順に返す 1つも無い場合には長さ0の配列を返す
     * @return
     */
    public CExpressionElement[] getExpressions() {
        List<CExpressionElement> list = new ArrayList<CExpressionElement>();
        NodeList nl = elem.getElementsByTagName("Expr");
        for (int i = 0; i < nl.getLength(); i++) {
            list.add(new CExpressionElement((Element) nl.item(i)));
        }
        return (CExpressionElement[]) list.toArray(new CExpressionElement[list
                .size()]);
    }

    /**
     * すべての宣言を返す
     * @return
     */
    public CDeclarationElement[] getDeclarations() {
        List<CDeclarationElement> list = new ArrayList<CDeclarationElement>();
        NodeList nl = elem.getElementsByTagName("*");
        for (int i = 0; i < nl.getLength(); i++) {
            if (CDeclarationElement.isDeclaration(nl.item(i))) {
                list.add(new CDeclarationElement((Element) nl.item(i)));
            }
        }
        return (CDeclarationElement[]) list
                .toArray(new CDeclarationElement[list.size()]);
    }

    /**
     * すべての typedef を返す
     * @return
     */
    public CTypedeclElement[] getTypeDeclarations() {
        List<CTypedeclElement> list = new ArrayList<CTypedeclElement>();
        NodeList nl = elem.getElementsByTagName("Typedecl");
        for (int i = 0; i < nl.getLength(); i++) {
            list.add(new CTypedeclElement((Element) nl.item(i)));
        }
        return (CTypedeclElement[]) list.toArray(new CTypedeclElement[list
                .size()]);
    }

    /**
     * すべての 変数宣言 (Local と Global) を返す
     * @return
     */
    public CDeclarationElement[] getVarialbeDeclarations() {
        List<CDeclarationElement> list = new ArrayList<CDeclarationElement>();
        NodeList nl = elem.getElementsByTagName("Global");
        for (int i = 0; i < nl.getLength(); i++) {
            list.add(new CGlobalElement((Element) nl.item(i)));
        }
        nl = elem.getElementsByTagName("Local");
        for (int i = 0; i < nl.getLength(); i++) {
            list.add(new CDeclarationElement((Element) nl.item(i)));
        }
        return (CDeclarationElement[]) list
                .toArray(new CDeclarationElement[list.size()]);
    }

    /**
     * すべての Global を返す<br>
     * int i,j,k; はまとめてひとつの Global が返るので注意
     * @return
     */
    public CGlobalElement[] getGlobalDeclarations() {
        List<CDeclarationElement> list = new ArrayList<CDeclarationElement>();
        NodeList nl = elem.getElementsByTagName("Global");
        for (int i = 0; i < nl.getLength(); i++) {
            list.add(new CGlobalElement((Element) nl.item(i)));
        }
        return (CGlobalElement[]) list.toArray(new CGlobalElement[list.size()]);
    }

    /**
     * すべての Label 要素を返す
     * @return
     */
    public CLabelElement[] getLabels() {
        List<CLabelElement> list = new ArrayList<CLabelElement>();
        NodeList nl = elem.getElementsByTagName("Label");
        for (int i = 0; i < nl.getLength(); i++) {
            list.add(new CLabelElement((Element) nl.item(i)));
        }
        return (CLabelElement[]) list.toArray(new CLabelElement[list.size()]);
    }
    
    /**
     * すべての litera 要素を返す
     * @return
     */
    public CLiteralElement[] getLiterals() {
        List<CLiteralElement> list = new ArrayList<CLiteralElement>();
        NodeList nl = elem.getElementsByTagName("literal");
        for (int i = 0; i < nl.getLength(); i++) {
            list.add(new CLiteralElement((Element) nl.item(i)));
        }
        return (CLiteralElement[]) list.toArray(new CLiteralElement[list.size()]);
    }

    /**
     * Statement の種類を返す sort がなければ null を返す
     * @return
     */
    public String getSort() {
        return elem.getAttribute("sort");
    }

    /**
     * スコープを取得する<br>
     * 要素が属する関数かファイル<br>
     * 現在の仕様ではファイルをまたぐ解析はしないのでグローバルスコープは存在しない<br>
     * C90 を対象とするのでブロックスコープも対応としない
     * @return
     */
    public CElement getScope() {
        Element tmp = (Element) elem.getParentNode();
        do {
            if ("File".equals(tmp.getNodeName())) {
                return new CFileElement(tmp);
            } else if (("Function").equals(tmp.getNodeName())) {
                return new CFunctionElement(tmp);
            }
        } while ((tmp = (Element) tmp.getParentNode()) != null);
        return null;
    }

    /**
     * 子要素の Statement と Local を取得する<br>
     * 出現順序を保持する
     * @return
     */
    public Element[] getChildStatementsAndLocals() {
        List<Element> list = new ArrayList<Element>();
        NodeList nl = elem.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            if ("Stmt".equals(nl.item(i).getNodeName())
                    || "Local".equals(nl.item(i).getNodeName())) {
                list.add((Element) nl.item(i));
            }
        }
        return (Element[]) list.toArray(new Element[list.size()]);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CElement)) {
            return false;
        }
        return elem.isSameNode(((CElement) obj).getElem());
    }

    @Override
    public int hashCode() {
        return elem.hashCode();
    }

    /**
     * ノード名とtextContent が一致するノードをリストで取得する<br>
     * 無ければ長さ0の配列が返る
     * @param nodeName
     * @param text
     * @return
     */
    public Element[] getChildNodesByNodeNameAndText(String nodeName, String text) {
        List<Element> list = new ArrayList<Element>();
        NodeList nl = elem.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (nodeName.equals(n.getNodeName())
                    && text.equals(n.getTextContent())) {
                list.add((Element) n);
            }
        }
        return (Element[]) list.toArray(new Element[list.size()]);
    }

}
