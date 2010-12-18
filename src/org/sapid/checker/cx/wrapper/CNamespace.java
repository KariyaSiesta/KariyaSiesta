/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.w3c.dom.Element;

/**
 * �͡��ॹ�ڡ�����ɽ�����륯�饹<br>
 * �͡��ॹ�ڡ����Ȥ�Ʊ���������פ�Ʊ�����̻Ҥ�ȤäƤ���̤������֤Ǥ���<br>
 * C ����Υ͡��ॹ�ڡ����� 4�Ĥμ��ब����<br>
 * <ul>
 * <li>̾��̾ (Label)</li>
 * <li>��¤�Ρ������εڤ�����ΤΥ���</li>
 * <li>��¤�����϶����ΤΥ��С���¤�����ϥ��Ф��Ф����̸ĤΥ͡��ॹ�ڡ��������</li>
 * <li>����¾���٤Ƥμ��̻�</li>
 * </ul>
 * @author Toshinori OSUKA
 */
public class CNamespace {
    public final int NAMESPACE_LABEL = 0;
    public final int NAMESPACE_TAG = 1;
    public final int NAMESPACE_MEMBER = 2;
    public final int NAMESPACE_REGULAR = 3;

    /** ident ���Ǥؤλ��� */
    Element ident;
    /** �������פλ��� */
    private CElement scope;
    /** �͡��ॹ�ڡ����μ��� */
    private int sort;

    /**
     * ���󥹥ȥ饯��<br>
     * �������Τϥ�٥롤�����ڤӡ�����פ� ident �Τ�<br>
     * �ֻ��ȡפ������ä��Ȥ���ư����ݾ㤷�ʤ�
     * @param ident ����
     */
    public CNamespace(Element ident) {
        super();
        this.ident = ident;

        Element parent = (Element) ident.getParentNode();
        if ("Label".equals(parent.getNodeName())) {
            sort = NAMESPACE_LABEL;
            scope = new CLabelElement(parent).getScope();
        } else if ("Tag".equals(parent.getNodeName())) {
            sort = NAMESPACE_TAG;
            scope = new CTagElement(parent).getScope();
        } else if ("Member".equals(parent.getNodeName())) {
            sort = NAMESPACE_MEMBER;
            scope = new CTagElement((Element) parent.getParentNode());
        } else {
            sort = NAMESPACE_REGULAR;
            scope = new CDeclarationElement(parent).getScope();
        }
        // TODO ���Ȥ������ä��Ȥ�������ޤǼ��˹Ԥ�
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CNamespace)) {
            return false;
        }
        CNamespace ns = (CNamespace) obj;
        return scope.getElem().isSameNode(ns.getScope().getElem())
                && (sort == ns.getSort());
    }

    /**
     * �͡��ॹ�ڡ����μ�����������<br>
     * NAMESPACE_LABEL �ʤ�
     * @return
     */
    public int getSort() {
        return sort;
    }

    /**
     * �͡��ॹ�ڡ����δ��Ȥʤ륹�����פ��������
     * @return
     */
    public CElement getScope() {
        return scope;
    }

}
