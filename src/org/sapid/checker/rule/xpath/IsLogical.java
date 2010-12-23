package org.sapid.checker.rule.xpath;

import java.util.List;

import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

import org.sapid.checker.cx.wrapper.CExpressionElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 式（Expr要素）が論理演算式かどうかを調べるXPath関数
 * @author uehara
 */
public class IsLogical implements XPathFunction {

	private static IsLogical instance;
	
	
	private IsLogical() {
	}
	
	
	public static IsLogical getInstance() {
		if (instance == null) {
			instance = new IsLogical();
		}
		
		return instance;
	}
	
	
	@SuppressWarnings("rawtypes")
	@Override
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
		
		CExpressionElement argumentElement = new CExpressionElement((Element) argumentNode);
		
		Element operatorElement = argumentElement.getFirstChildNode("op");
		if (operatorElement != null) {
			String operatorText = operatorElement.getTextContent();
			
			return "!".equals(operatorText) || "&&".equals(operatorText) || "||".equals(operatorText);
		}
		
		return false;
	}
	
}
