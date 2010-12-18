/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.sapid.checker.cx.wrapper.type.Type;
import org.sapid.checker.cx.wrapper.type.TypeFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Expr sort="VarRef" ����<br>
 * �ѿ��ڤ�<b>�ؿ�</b>�λ��Ȥ�ɽ��
 * @author Toshinori OSUKA
 */
public class CVariableReference extends CExpressionElement {
    /**
     * ���󥹥ȥ饯��
     * @param elem
     */
    public CVariableReference(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * �ؿ��ؤλ��Ȥξ��� true
     * @return
     */
    public boolean isFunction() {
        if ("Expr".equals(elem.getParentNode().getNodeName())) {
            CExpressionElement expr = new CExpressionElement((Element) elem
                    .getParentNode());
            if (expr.getSortEnum() == CExpressionElement.Sort.CALL) {
                return true;
            }
        }
        return false;
    }

    /**
     * �����ʬ���������<br>
     * �ե����볰�����������ʤɸ��Ĥ���ʤ����ˤ� null ���֤�
     * @return
     */
    public CDeclarationElement getDeclaration() {
        String defid = getFirstChildNode("ident").getAttribute("defid");
        CFileElement cfile = new CFileElement(elem.getOwnerDocument());
        Element decl = cfile.getElementById(defid);
        if (decl == null) {
            // TODO int a,b,c �ΤȤ���õ��
            return null;
        }
        return new CDeclarationElement(decl);
    }

    /**
     * defid ���֤� ���Ĥ���ʤ����϶�ʸ�����֤�
     * @return
     */
    public String getDefinitionId() {
        Element firstChild = getFirstChildNode("ident");
        if (firstChild == null) {
            if ((firstChild = getFirstChildNode("literal")) == null) {
                return "";
            }
        }
        return firstChild.getAttribute("defid");
    }
    
    /**
     * �����֤� ���Ĥ���ʤ�����null���֤�
     * @return
     */
    public Type getTypeInfo() {
    	String typeID = getFirstChildNode("ident").getAttribute("type_id");
    	NodeList typeInfosNodeList = this.getElem().getOwnerDocument().getDocumentElement().getElementsByTagName("TypeInfos");
    	
    	if (! typeID.isEmpty() && typeInfosNodeList.getLength() != 0) {
    		return TypeFactory.createType((Element) typeInfosNodeList.item(0), typeID);
    	} else {
    		return null;
    	}
    }
}
