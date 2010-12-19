package org.sapid.checker.rule.xpath;

import java.util.List;

import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

import org.sapid.checker.cx.wrapper.CExpressionElement;
import org.sapid.checker.cx.wrapper.type.ArrayType;
import org.sapid.checker.cx.wrapper.type.PointerType;
import org.sapid.checker.cx.wrapper.type.Type;
import org.sapid.checker.cx.wrapper.type.TypedefType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PointingLevelGetter implements XPathFunction {

	private static PointingLevelGetter instance;
	
	
	private PointingLevelGetter() {
	}
	
	
	public static PointingLevelGetter getInstance() {
		if (instance == null) {
			instance = new PointingLevelGetter();
		}
		
		return instance;
	}
	
	
	@SuppressWarnings("rawtypes")
	@Override
	public Integer evaluate(List args) throws XPathFunctionException {
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
		if (! argumentElement.getNodeName().equals("Expr")) {
			throw new XPathFunctionException("Wrong name of argument element:" + argumentElement.getNodeName());
		}
		
		CExpressionElement expression = new CExpressionElement(argumentElement);
		Type type = expression.getTypeInfo();
		if (type == null) {
			return -1;
		} else {
			return this.getPointingLevel(type);
		}
	}

	private int getPointingLevel(Type type) {
		if (type == null) {
			return 0;
		} else if (type.getSort() == Type.Sort.TYPEDEF) {
			TypedefType typedefType = (TypedefType) type;
			
			return this.getPointingLevel(typedefType.getTrueTypeRecursively());
		} else if (type.getSort() == Type.Sort.POINTER) {
			PointerType pointerType = (PointerType) type;
			
			return 1 + this.getPointingLevel(pointerType.getPointeeType());
		} else if (type.getSort() == Type.Sort.ARRAY) {
			ArrayType arrayType = (ArrayType) type;
			
			if (arrayType.getArraySize() == 0) {
				return 1 + this.getPointingLevel(arrayType.getPointeeType());
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}
	
}
