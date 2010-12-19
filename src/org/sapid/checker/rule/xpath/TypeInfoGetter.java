package org.sapid.checker.rule.xpath;

import java.util.List;

import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

import org.sapid.checker.cx.wrapper.CExpressionElement;
import org.sapid.checker.cx.wrapper.type.Type;
import org.sapid.checker.cx.wrapper.type.TypeInfosConstant;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TypeInfoGetter implements XPathFunction {
	
	private static TypeInfoGetter instance;
	

	private TypeInfoGetter() {
	}
	
	
	public static TypeInfoGetter getInstance() {
		if (TypeInfoGetter.instance == null) {
			TypeInfoGetter.instance = new TypeInfoGetter();
		}
		
		return TypeInfoGetter.instance;
	}
	
	
	@SuppressWarnings("rawtypes")
	@Override
	public Element evaluate(List args) throws XPathFunctionException {
		if (args == null) {
			throw new XPathFunctionException("Null argument");
		}
		
		if (args.size() != 1) {
			throw new XPathFunctionException("Wrong number of arguments:" + args.size());
		}
		
		Object argument = args.get(0);
		if (! (argument instanceof NodeList)) {
			throw new XPathFunctionException("Wrong type of arguments:" + argument);
		}
		
		NodeList argumentNodeList = (NodeList) argument;
		if (argumentNodeList.getLength() != 1) {
			throw new XPathFunctionException("Wrong number of argument nodes:" + argumentNodeList.getLength());
		}
		
		Node argumentNode = argumentNodeList.item(0);
		if (argumentNode.getNodeType() != Node.ELEMENT_NODE) {
			throw new XPathFunctionException("Wrong type of argument node:" + argumentNode.getNodeType());
		}
		
		Element argumentElement = (Element) argumentNode;
		if (! argumentElement.getNodeName().equals("Expr")) {
			throw new XPathFunctionException("Wrong name of argument element:" + argumentElement.getNodeName());
		}
		
		return this.getTypeInfoElement(argumentElement);
	}

	private Element getTypeInfoElement(Element element) {
		Element typeInfosElement = this.getTypeInfosElement(element);
		String typeID = this.getTypeID(element);
		
		if (typeInfosElement == null || typeID == null) {
			return null;
		}
		
		NodeList typeInfoElementList = typeInfosElement.getElementsByTagName(TypeInfosConstant.TYPEINFO_ELEMENT_NAME);
		for (int i = 0; i < typeInfoElementList.getLength(); i++) {
			Element typeInfoElement = (Element) typeInfoElementList.item(i);
			String typeInfoElementID = typeInfoElement.getAttribute(TypeInfosConstant.ID_ATTRIBUTE_NAME);
			
			if (typeID.equals(typeInfoElementID)) {
				return typeInfoElement;
			}
		}
		
		return null;
	}
	
	private String getTypeID(Element element) {
		CExpressionElement expression = new CExpressionElement(element);
		Type type = expression.getTypeInfo();
		
		if (type == null) {
			return null;
		} else {
			return type.getId();
		}
	}
	
	private Element getTypeInfosElement(Element element) {
		Document document = element.getOwnerDocument();
		NodeList typeInfosElementList = document.getElementsByTagName(TypeInfosConstant.TYPEINFOS_ELEMENT_NAME);
		
		if (typeInfosElementList.getLength() != 1) {
			return null;
		} else {
			return (Element) typeInfosElementList.item(0);
		}
	}
	
}
