package org.sapid.checker.rule.xpath;

import java.util.List;

import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Matches implements XPathFunction {

	private static Matches instance;
	
	
	private Matches() {
	}
	
	
	public static Matches getInstance() {
		if (instance == null) {
			instance = new Matches();
		}
		
		return instance;
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public Boolean evaluate(List args) throws XPathFunctionException {
		if (args == null) {
			throw new XPathFunctionException("Null argument");
		}
		
		if (args.size() != 2) {
			throw new XPathFunctionException("Wrong number of arguments:" + args.size());
		}
		
		String string0 = this.objectToString(args.get(0));
		String string1 = this.objectToString(args.get(1));
		
		// System.out.println(string0 + "\t" + string1);
		return string0.matches(string1);
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

}
