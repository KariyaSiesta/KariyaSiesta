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
 * Expr sort="VarRef" 要素<br>
 * 変数及び<b>関数</b>の参照を表す
 * @author Toshinori OSUKA
 */
public class CVariableReference extends CExpressionElement {
    /**
     * コンストラクタ
     * @param elem
     */
    public CVariableReference(Element elem) {
        super(elem);
        // TODO Auto-generated constructor stub
    }

    /**
     * 関数への参照の場合は true
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
     * 宣言部分を取得する<br>
     * ファイル外に宣言ある場合など見つからない場合には null を返す
     * @return
     */
    public CDeclarationElement getDeclaration() {
        String defid = getFirstChildNode("ident").getAttribute("defid");
        CFileElement cfile = new CFileElement(elem.getOwnerDocument());
        Element decl = cfile.getElementById(defid);
        if (decl == null) {
            // TODO int a,b,c のときも探す
            return null;
        }
        return new CDeclarationElement(decl);
    }

    /**
     * defid を返す 見つからない場合は空文字を返す
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
     * 型を返す 見つからない場合はnullを返す
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
