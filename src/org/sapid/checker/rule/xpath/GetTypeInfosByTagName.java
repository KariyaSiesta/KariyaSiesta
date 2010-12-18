package org.sapid.checker.rule.xpath;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

import org.sapid.checker.cx.wrapper.type.Type;
import org.sapid.checker.cx.wrapper.type.TypeInfosConstant;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GetTypeInfosByTagName implements XPathFunction {

	private static GetTypeInfosByTagName instance;
	
	
	private GetTypeInfosByTagName() {
	}
	
	
	public static GetTypeInfosByTagName getInstance() {
		if (instance == null) {
			instance = new GetTypeInfosByTagName();
		}
		
		return instance;
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public NodeList evaluate(List args) throws XPathFunctionException {
		if (args == null) {
			throw new XPathFunctionException("Null argument");
		}
		
		if (args.size() != 2) {
			throw new XPathFunctionException("Wrong number of arguments:" + args.size());
		}
		
		Object argumentA = args.get(0);
		Object argumentB = args.get(1);
		
		String argumentString = this.objectToString(argumentA);
		
		NodeList argumentNodeList = (NodeList) argumentB;
		if (argumentNodeList.getLength() != 1) {
			throw new XPathFunctionException("Wrong number of argument nodes:" + argumentNodeList.getLength());
		}
		
		Node argumentNode = argumentNodeList.item(0);
		if (argumentNode.getNodeType() != Node.ELEMENT_NODE) {
			throw new XPathFunctionException("Wrong type of argument node:" + argumentNode.getNodeType());
		}
		
		Element argumentElement = (Element) argumentNode;
		if (! argumentElement.getNodeName().equals(TypeInfosConstant.TYPEINFOS_ELEMENT_NAME)) {
			throw new XPathFunctionException("Wrong name of argument element:" + argumentElement.getNodeName());
		}
		
		return this.getTypeInfosByTagName(argumentString, argumentElement);
	}
	
	private String objectToString(Object object) throws XPathFunctionException {
		if (object instanceof String) {
			return (String) object;
		}
		
		if (object instanceof NodeList) {
			NodeList nodeList = (NodeList) object;
			
			StringBuilder builder = new StringBuilder();
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				
				switch(node.getNodeType()) {
				case Node.ELEMENT_NODE:
					builder.append(node.getTextContent());
					break;
				case Node.ATTRIBUTE_NODE:
				case Node.TEXT_NODE:
					builder.append(node.getNodeValue());
					break;
				}
			}
			
			return builder.toString();
		}
		
		if (object instanceof Number) {
			return object.toString();
		}
		
		throw new XPathFunctionException("Wrong type of argument:" + object.getClass());
	}

	private NodeList getTypeInfosByTagName(String tagName, Element typeInfosElement) {
		final List<Node> typeInfos = new ArrayList<Node>();
		
		NodeList typeInfoNodeList = typeInfosElement.getChildNodes();
		for (int i = 0; i < typeInfoNodeList.getLength(); i++) {
			Node typeInfoNode = typeInfoNodeList.item(i);
			
			if (typeInfoNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Element typeInfoElement = (Element) typeInfoNode;
			if (! typeInfoElement.getTagName().equals(TypeInfosConstant.TYPEINFO_ELEMENT_NAME)) {
				continue;
			}
			
			String sortString = typeInfoElement.getAttribute(TypeInfosConstant.SORT_ATTRIBUTE_NAME);
			Type.Sort sort = Type.Sort.fromString(sortString);
			if (sort == Type.Sort.STRUCT || sort == Type.Sort.UNION || sort == Type.Sort.ENUM) {
				
				String name = typeInfoElement.getAttribute(TypeInfosConstant.NAME_ATTRIBUTE_NAME);
				if (name.equals(tagName)) {
					typeInfos.add(typeInfoNode);
				}
			}
		}
		
		return new NodeList() {

			@Override
			public int getLength() {
				return typeInfos.size();
			}

			@Override
			public Node item(int index) {
				return typeInfos.get(index);
			}
			
		};
	}

}
