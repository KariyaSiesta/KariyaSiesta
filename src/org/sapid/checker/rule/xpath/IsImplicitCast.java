package org.sapid.checker.rule.xpath;

import java.util.List;

import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

import org.sapid.checker.cx.wrapper.CExpressionElement;
import org.sapid.checker.cx.wrapper.type.FunctionType;
import org.sapid.checker.cx.wrapper.type.PointerType;
import org.sapid.checker.cx.wrapper.type.Type;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 式（Expr要素）が暗黙的にキャストされるかどうかを調べるXPath関数
 * @author uehara
 */
public class IsImplicitCast implements XPathFunction {

	private static IsImplicitCast instance;
	
	
	private IsImplicitCast() {
	}
	
	
	public static IsImplicitCast getInstance() {
		if (instance == null) {
			instance = new IsImplicitCast();
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
		if (! argumentElement.getTagName().equals("Expr")) {
			return false;
		}
		
		CExpressionElement expression = new CExpressionElement(argumentElement);
		
		Node parentNode = argumentElement.getParentNode();
		if (argumentNode.getNodeType() != Node.ELEMENT_NODE) {
			return false;
		}
		
		Element parentElement = (Element) parentNode;
		if (! parentElement.getTagName().equals("Expr")) {
			return false;
		}
		
		CExpressionElement parentExpression = new CExpressionElement(parentElement);
		
		CExpressionElement.Sort parentSort = parentExpression.getSortEnum();
		if (parentSort == CExpressionElement.Sort.ASSIGN || parentSort == CExpressionElement.Sort.ARITHMETIC_ASSIGN ||
				parentSort == CExpressionElement.Sort.BIT_ASSIGN || parentSort == CExpressionElement.Sort.SHIFT_ASSIGN) {
			return ! this.checkCompatibility(expression, parentExpression);
		} else if (parentSort == CExpressionElement.Sort.ARITHMETIC1 || parentSort == CExpressionElement.Sort.ARITHMETIC2) {
			return ! this.checkCompatibility(expression, parentExpression);
		} else if (parentSort == CExpressionElement.Sort.CALL) {
    		Type firstSubExpressionType = this.getFunctionType(parentExpression);
    		if (firstSubExpressionType == null || firstSubExpressionType.getSort() != Type.Sort.POINTER) {
    			return false;
    		}
    		Type pointeeType = ((PointerType) firstSubExpressionType).getPointeeType();
    		if (pointeeType == null || pointeeType.getSort() != Type.Sort.FUNCTION) {
    			return false;
    		}
    		FunctionType functionType = (FunctionType) pointeeType;
    		Type[] argumentTypes = functionType.getArgumentTypes();
    		
    		int parameterIndex = this.getParameterIndex(parentExpression, expression);
    		if (parameterIndex < 0 || parameterIndex >= argumentTypes.length) {
    			return false;
    		}
    		
			Type argumentType = argumentTypes[parameterIndex];
			Type parameterType = expression.getTypeInfo();
			if (argumentType == null || parameterType == null) {
				return false;
			}
			
			return ! argumentType.isCompatibleWith(parameterType);
		} else {
			return false;
		}
	}
	
	private boolean checkCompatibility(CExpressionElement expressionA, CExpressionElement expressionB) {
		Type typeA = expressionA.getTypeInfo();
		Type typeB = expressionB.getTypeInfo();
		
		if (typeA == null || typeB == null) {
			return false;
		} else {
			return typeA.isCompatibleWith(typeB);
		}
	}
	
	private Type getFunctionType(CExpressionElement callExpression) {
		Element firstSubExpressionElement = callExpression.getFirstChildNode("Expr");
		CExpressionElement firstSubExpression = new CExpressionElement(firstSubExpressionElement);
		return firstSubExpression.getTypeInfo();
	}
	
	private int getParameterIndex(CExpressionElement callExpression, CExpressionElement parameterExpression) {
		Element callElement = callExpression.getElem();
		Element parameterElement = parameterExpression.getElem();
		
		int currentIndex = -1;
		
		NodeList callChildren = callElement.getChildNodes();
		for (int i = 0; i < callChildren.getLength(); i++) {
			Node callChildNode = callChildren.item(i);
			
			if (callChildNode.getNodeType() == Node.ELEMENT_NODE) {
				Element callChildElement = (Element) callChildNode;
				
				if (callChildElement.getTagName().equals("op")) {
					if (callChildElement.getTextContent().equals(",") || callChildElement.getTextContent().equals("(")) {
						currentIndex++;
					}
				} else if (callChildElement == parameterElement) {
					return currentIndex;
				}
			}
		}
		
		return -1;
	}
		
}
