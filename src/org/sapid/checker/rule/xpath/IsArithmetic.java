package org.sapid.checker.rule.xpath;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 式（Expr要素）が算術演算式かどうかを調べるXPath関数
 * @author uehara
 */
public class IsArithmetic implements XPathFunction {

	private static IsArithmetic instance;
	
	private static Set<String> arithmeticOperators;
	
	
	static {
		arithmeticOperators = new HashSet<String>();
		arithmeticOperators.add("+");
		arithmeticOperators.add("-");
		arithmeticOperators.add("*");
		arithmeticOperators.add("/");
		arithmeticOperators.add("%");
		arithmeticOperators.add("+=");
		arithmeticOperators.add("-=");
		arithmeticOperators.add("*=");
		arithmeticOperators.add("/=");
		arithmeticOperators.add("%=");
	}
	
	
	private IsArithmetic() {
	}
	
	
	public static IsArithmetic getInstance() {
		if (instance == null) {
			instance = new IsArithmetic();
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
		
		Element argumentElement = (Element) argumentNode;
		
		return new Boolean(this.checkArithmeticExpression(argumentElement));
	}
	
	/**
	 * elementが算術演算式であるか否かを判定する。
	 * ここでいう算術演算式とは、Expr要素であり、
	 * 子にop要素1つとExprまたはmacroCall要素2つを持ち、
	 * この3つの子要素の文書内順序においてop要素が中央であり、
	 * op要素のテキストコンテンツが + - * \/ % += -= *= /= %= のいずれかであるものである。
	 * 
	 * TODO この機能はCExpressionElementにあるべきか？
	 * 
	 * @param element 算術演算式であるか否かを判定したい要素
	 * @return elementが算術演算式であるか否か
	 */
	private boolean checkArithmeticExpression(Element element) {
		if (! element.getTagName().equals("Expr")) {
			return false;
		}
		
		Element operatorElement = null;
		List<Element> operandElements = new ArrayList<Element>(); 
		
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				String childTagName = childElement.getTagName();
				
				if (childTagName.equals("op") && arithmeticOperators.contains(childElement.getTextContent())) {
					operatorElement = childElement;
				} else if (childTagName.equals("Expr") || childTagName.equals("macroCall")) {
					operandElements.add(childElement);
				}
			}
		}
		
		if (operatorElement == null || operandElements.size() != 2) {
			return false;
		}
		
		Element operandElement1 = operandElements.get(0);
		Element operandElement2 = operandElements.get(1);
		
		short operandElement1Position = operatorElement.compareDocumentPosition(operandElement1);
		short operandElement2Position = operatorElement.compareDocumentPosition(operandElement2);
		
		boolean isArighmeticExpression =
			operandElement1Position == Node.DOCUMENT_POSITION_PRECEDING
			&& operandElement2Position == Node.DOCUMENT_POSITION_FOLLOWING
			|| operandElement1Position == Node.DOCUMENT_POSITION_FOLLOWING
			&& operandElement2Position == Node.DOCUMENT_POSITION_PRECEDING;
		
		return isArighmeticExpression;
	}
	
}
