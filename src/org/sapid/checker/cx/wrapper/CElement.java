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
 * CX-model �Υ�åѡ����饹 ���٤ƤΥ�åѡ����ǤϤ��Υ��饹��Ѿ�����
 * @author Toshinori OSUKA
 */
public class CElement {
    /** DOM �ΥΡ��� */
    protected Element elem;

    /**
     * ���󥹥ȥ饯��
     * @param elem
     */
    public CElement(Element elem) {
        super();
        this.elem = elem;
    }

    /**
     * DOM �Ρ��ɤ��������
     * @return
     */
    public Element getElem() {
        return elem;
    }

    /**
     * Id ���������
     * @return ̵������ null ���֤�
     */
    public String getId() {
        return elem.getAttribute("id");
    }

    /**
     * �����ǤΤ������ֺǽ�˸������ꤵ�줿̾�������Ǥ��֤�
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
     * �����ǤΤ������ֺǸ�˸������ꤵ�줿̾�������Ǥ��֤�
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
     * �����ǤΤ������ֺǽ�˸������ꤵ�줿̾���� sort ��������Ǥ��֤�
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
     * ���ꤵ�줿̾������Ļ����Ǥ򤹤٤��֤�
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
     * ���ꤵ�줿 id ��������ǤΤ����ǽ�˸��Ĥ��ä���Τ��֤� ���Ĥ���ʤ���� null ���֤�
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
     * DOM �Ρ��ɤ�����̾���֤�
     * @return
     */
    public String getNodeName() {
        return elem.getNodeName();
    }

    /**
     * ���٤Ƥ� Function ���Ǥ��֤� 1�Ĥ�̵�����ˤ�Ĺ��0��������֤�
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
     * ���٤Ƥ� Stmt ���Ǥ��֤� 1�Ĥ�̵�����ˤ�Ĺ��0��������֤�
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
     * ���٤Ƥ� Expr ���Ǥ�ʸ�����֤� 1�Ĥ�̵�����ˤ�Ĺ��0��������֤�
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
     * ���٤Ƥ�������֤�
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
     * ���٤Ƥ� typedef ���֤�
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
     * ���٤Ƥ� �ѿ���� (Local �� Global) ���֤�
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
     * ���٤Ƥ� Global ���֤�<br>
     * int i,j,k; �ϤޤȤ�ƤҤȤĤ� Global ���֤�Τ����
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
     * ���٤Ƥ� Label ���Ǥ��֤�
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
     * ���٤Ƥ� litera ���Ǥ��֤�
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
     * Statement �μ�����֤� sort ���ʤ���� null ���֤�
     * @return
     */
    public String getSort() {
        return elem.getAttribute("sort");
    }

    /**
     * �������פ��������<br>
     * ���Ǥ�°����ؿ����ե�����<br>
     * ���ߤλ��ͤǤϥե������ޤ������ϤϤ��ʤ��Τǥ����Х륹�����פ�¸�ߤ��ʤ�<br>
     * C90 ���оݤȤ���Τǥ֥�å��������פ��б��Ȥ��ʤ�
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
     * �����Ǥ� Statement �� Local ���������<br>
     * �и�������ݻ�����
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
     * �Ρ���̾��textContent �����פ���Ρ��ɤ�ꥹ�ȤǼ�������<br>
     * ̵�����Ĺ��0�������֤�
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
