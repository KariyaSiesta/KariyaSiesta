package org.sapid.checker.rule.xpath;

import java.util.List;

import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

import org.sapid.checker.cx.wrapper.type.FunctionType;
import org.sapid.checker.cx.wrapper.type.StandardType;
import org.sapid.checker.cx.wrapper.type.Type;
import org.sapid.checker.cx.wrapper.type.TypeFactory;
import org.sapid.checker.cx.wrapper.type.TypeInfosConstant;
import org.sapid.checker.cx.wrapper.type.TypedefType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class IsVoidFunction implements XPathFunction {
	
	private static IsVoidFunction instance;
	
	
	private IsVoidFunction() {
	}
	
	
	public static IsVoidFunction getInstance() {
		if (instance == null) {
			instance = new IsVoidFunction();
		}
		
		return instance;
	}
	

	@Override
	@SuppressWarnings("unchecked")
	public Boolean evaluate(List args) throws XPathFunctionException {
		if (args == null) {
			throw new XPathFunctionException("Null argument");
		}
		
		if (args.size() != 1) {
			throw new XPathFunctionException("Wrong number of arguments:" + args.size());
		}
		
		Object argument = args.get(0);
		if (! (argument instanceof NodeList)) {
			throw new XPathFunctionException("Wrong type of argument:" + argument.getClass());
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
		if (! argumentElement.getNodeName().equals("Function")) {
			throw new XPathFunctionException("Wrong name of argument element:" + argumentElement.getNodeName());
		}
		
		Element typeInfosElement = this.getTypeInfosElement(argumentElement);
		if (typeInfosElement == null) {
			return false;
		}
		
		Element functionIdentifierElement = this.getIdentifierElement(argumentElement);
		if (functionIdentifierElement == null) {
			return false;
		}
		
		String typeID = functionIdentifierElement.getAttribute(TypeInfosConstant.TYPE_ID_ATTRIBUTE_NAME);
		if (typeID.isEmpty()) {
			return false;
		}
		
		Type type = TypeFactory.createType(typeInfosElement, typeID);
		if (type == null) {
			return false;
		}
		
		if (type.getSort() != Type.Sort.FUNCTION) {
			return false;
		}
		
		Type returnType = ((FunctionType) type).getReturnType();
		if (returnType == null) {
			return false;
		}
		
		if (returnType.getSort() == Type.Sort.TYPEDEF) {
			returnType = ((TypedefType) returnType).getTrueTypeRecursively();
		}
		
		if (returnType.getSort() != Type.Sort.STANDARD) {
			return false;
		}
		
		return ((StandardType) returnType).getType() == StandardType.Sort.VOID;
	}

	private Element getIdentifierElement(Element functionElement) {
		NodeList childNodeList = functionElement.getChildNodes();
		
		for (int i = 0; i < childNodeList.getLength(); i++) {
			Node childNode = childNodeList.item(i);
			
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				
				if (childElement.getTagName().equals("ident")) {
					return childElement;
				}
			}
		}
		
		return null;
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
