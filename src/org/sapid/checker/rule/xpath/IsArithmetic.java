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
 * ����Expr���ǡˤ����ѱ黻�����ɤ�����Ĵ�٤�XPath�ؿ�
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
		
		return new Boolean(this.checkArithmeticExpression(argumentElement));
	}
	
	/**
	 * element�����ѱ黻���Ǥ��뤫�ݤ���Ƚ�ꤹ�롣
	 * �����Ǥ������ѱ黻���Ȥϡ�Expr���ǤǤ��ꡢ
	 * �Ҥ�op����1�Ĥ�Expr�ޤ���macroCall����2�Ĥ������
	 * ����3�Ĥλ����Ǥ�ʸ�������ˤ�����op���Ǥ�����Ǥ��ꡢ
	 * op���ǤΥƥ����ȥ���ƥ�Ĥ� + - * \/ % += -= *= /= %= �Τ����줫�Ǥ����ΤǤ��롣
	 * 
	 * TODO ���ε�ǽ��CExpressionElement�ˤ���٤�����
	 * 
	 * @param element ���ѱ黻���Ǥ��뤫�ݤ���Ƚ�ꤷ��������
	 * @return element�����ѱ黻���Ǥ��뤫�ݤ�
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
