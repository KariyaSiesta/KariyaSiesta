package org.sapid.checker.rule.xpath;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

import org.sapid.checker.cx.wrapper.CExpressionElement;
import org.sapid.checker.cx.wrapper.type.PointerType;
import org.sapid.checker.cx.wrapper.type.StandardType;
import org.sapid.checker.cx.wrapper.type.TagType;
import org.sapid.checker.cx.wrapper.type.Type;
import org.sapid.checker.cx.wrapper.type.TypedefType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CanBeFloating implements XPathFunction {
	
	private static CanBeFloating instance;
	
	
	private CanBeFloating() {
	}
	
	
	public static CanBeFloating getInstance() {
		if (instance == null) {
			instance = new CanBeFloating();
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
		if (! argumentElement.getNodeName().equals("Expr")) {
			throw new XPathFunctionException("Wrong name of argument element:" + argumentElement.getNodeName());
		}
		
		CExpressionElement expression = new CExpressionElement(argumentElement);
		
		// is floating ?
		Type type = expression.getTypeInfo();
		if (this.isFloating(type)) {
			return true;
		}
		
		// can be floating ?
		Type ownerType;
		CExpressionElement.Sort sort = expression.getSortEnum();
		if (sort == CExpressionElement.Sort.DOT) {
			Element firstChildExpressionElement = expression.getFirstChildNode("Expr");
			if (firstChildExpressionElement == argumentElement) {
				return false;
			}
			
			// TODO ロジックに自信無し
			if (this.evaluate(firstChildExpressionElement)) {
				return true;
			}
			
			ownerType = new CExpressionElement(firstChildExpressionElement).getTypeInfo();
		} else if (sort == CExpressionElement.Sort.ARROW) {
			Element firstChildExpressionElement = expression.getFirstChildNode("Expr");
			if (firstChildExpressionElement == argumentElement) {
				return false;
			}
			
			Type pointerType = new CExpressionElement(firstChildExpressionElement).getTypeInfo();
			if (pointerType == null) {
				return false;
			} else if (pointerType.getSort() == Type.Sort.TYPEDEF) {
				pointerType = ((TypedefType) pointerType).getTrueTypeRecursively();
			}
			
			if (pointerType.getSort() == Type.Sort.POINTER) {
				ownerType = ((PointerType) pointerType).getPointeeType();
			} else {
				return false;
			}
		} else {
			return false;
		}
		
		if (ownerType == null) {
			return false;
		} else if (ownerType.getSort() == Type.Sort.TYPEDEF) {
			ownerType = ((TypedefType) ownerType).getTrueTypeRecursively();
		}
		
		if (ownerType.getSort() != Type.Sort.UNION) {
			return false;
		}
		Type[] memberTypes = ((TagType) ownerType).getMemberTypes();
		
		for (Type memberType : memberTypes) {
			if (this.isFloating(memberType)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isFloating(Type type) {
		if (type == null) {
			return false;
		}
		
		if (type.getSort() == Type.Sort.TYPEDEF) {
			type = ((TypedefType) type).getTrueTypeRecursively();
		}
		
		if (type.getSort() == Type.Sort.STANDARD) {
			StandardType.Sort sort = ((StandardType) type).getType();
			
			if (sort == StandardType.Sort.FLOAT || sort == StandardType.Sort.DOUBLE) {
				return true;
			}
		}
		
		return false;
	}

	private boolean evaluate(final Element element) throws XPathFunctionException {
		List<NodeList> list = new ArrayList<NodeList>();
		list.add(new NodeList() {
			public int getLength() {
				return 1;
			}
			
			public Node item(int i) {
				return element;
			}
		});
		
		return this.evaluate(list);
	}
	
}
