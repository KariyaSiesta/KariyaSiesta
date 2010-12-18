package org.sapid.checker.rule.xpath;

import java.util.List;

import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

import org.sapid.checker.cx.wrapper.CExpressionElement;
import org.sapid.checker.cx.wrapper.type.StandardType;
import org.sapid.checker.cx.wrapper.type.Type;
import org.sapid.checker.cx.wrapper.type.TypedefType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class IsLossy implements XPathFunction {

	private static IsLossy instance;
	
	
	private IsLossy() {
	}
	
	
	public static IsLossy getInstance() {
		if (instance == null) {
			instance = new IsLossy();
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
		
		Object argument0 = args.get(0);
		Object argument1 = args.get(1);
		if (! (argument0 instanceof NodeList)) {
			throw new XPathFunctionException("Wrong type of argument:" + argument0.getClass());
		} else if (! (argument1 instanceof NodeList)) {
			throw new XPathFunctionException("Wrong type of argument:" + argument1.getClass());
		}
		
		NodeList nodeList0 = (NodeList) argument0;
		NodeList nodeList1 = (NodeList) argument1;
		if (nodeList0.getLength() != 1) {
			throw new XPathFunctionException("Wrong number of argument nodes:" + nodeList0.getLength());
		} else if (nodeList1.getLength() != 1) {
			throw new XPathFunctionException("Wrong number of argument nodes:" + nodeList1.getLength());
		}
		
		Node node0 = nodeList0.item(0);
		Node node1 = nodeList1.item(0);
		if (node0.getNodeType() != Node.ELEMENT_NODE) {
			throw new XPathFunctionException("Wrong type of argument node:" + node0.getNodeType());
		} else if (node1.getNodeType() != Node.ELEMENT_NODE) {
			throw new XPathFunctionException("Wrong type of argument node:" + node1.getNodeType());
		}
		
		Element element0 = (Element) node0;
		Element element1 = (Element) node1;
		if (! element0.getTagName().equals("Expr")) {
			throw new XPathFunctionException("Wrong name of argument element:" + element0.getTagName());
		} else if (! element1.getTagName().equals("Expr")) {
			throw new XPathFunctionException("Wrong name of argument element:" + element1.getTagName());
		}
		
		CExpressionElement expression0 = new CExpressionElement(element0);
		CExpressionElement expression1 = new CExpressionElement(element1);
		
		Type type0 = expression0.getTypeInfo();
		Type type1 = expression1.getTypeInfo();
		if (type0 == null) {
			throw new XPathFunctionException("Unknown type of argument expression:" + expression0.getElem().getTextContent());
		} else if (type1 == null) {
			throw new XPathFunctionException("Unknown type of argument expression:" + expression1.getElem().getTextContent());
		}
		
		if (type0.getSort() == Type.Sort.TYPEDEF) {
			type0 = ((TypedefType) type0).getTrueTypeRecursively();
		}
		if (type1.getSort() == Type.Sort.TYPEDEF) {
			type1 = ((TypedefType) type1).getTrueTypeRecursively();
		}
		
		if (type0.getSort() == Type.Sort.STANDARD && type1.getSort() == Type.Sort.STANDARD) {
			return ((StandardType) type0).isLossy((StandardType) type1);
		} else {
			return false;
		}
	}

}
