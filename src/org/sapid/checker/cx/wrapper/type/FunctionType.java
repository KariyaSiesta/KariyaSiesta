package org.sapid.checker.cx.wrapper.type;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FunctionType extends Type {
	
	private String returnTypeID;
	private Type returnTypeCache;
	private List<String> argumentTypeIDs;
	private Type[] argumentTypesCache;
	

	FunctionType(Element typeInfoElement) {
		super(typeInfoElement);
		
		if (! typeInfoElement.hasAttribute(TypeInfosConstant.REF_ATTRIBUTE_NAME)) {
			throw new IllegalArgumentException();
		}
		
		this.returnTypeID = typeInfoElement.getAttribute(TypeInfosConstant.REF_ATTRIBUTE_NAME);
		
		this.argumentTypeIDs = new ArrayList<String>();
		
		NodeList typeRefNodeList = typeInfoElement.getElementsByTagName(TypeInfosConstant.TYPEREF_ELEMENT_NAME);
		for (int i = 0; i < typeRefNodeList.getLength(); i++) {
			Element typeRefElement = (Element) typeRefNodeList.item(i);
			
			if (typeRefElement.getAttribute(TypeInfosConstant.SORT_ATTRIBUTE_NAME).equals(TypeInfosConstant.SORT_ATTRIBUTE_VALUE_ARGUMENT)) {
				this.argumentTypeIDs.add(typeRefElement.getAttribute(TypeInfosConstant.REF_ATTRIBUTE_NAME));
			}
		}
	}
	

	@Override
	public Sort getSort() {
		return Type.Sort.FUNCTION;
	}

	@Override
	public String getText() {
		String text = this.getReturnType().getText();
		
		Type[] argumentTypes = this.getArgumentTypes();
		text += "(";
		for (int i = 0; i < argumentTypes.length; i++) {
			text += argumentTypes[i].getText();
			
			if (i + 1 < argumentTypes.length) {
				text += ",";
			}
		}
		text += ")";
		
		return text;
	}

	public Type getReturnType() {
		if (this.returnTypeCache == null) {
			Element typeInfosElement = (Element) this.getTypeInfoElement().getParentNode();
			this.returnTypeCache = TypeFactory.createType(typeInfosElement, this.returnTypeID);
		}
		
		return this.returnTypeCache;
	}
	
	public Type[] getArgumentTypes() {
		if (this.argumentTypesCache == null) {
			this.argumentTypesCache = new Type[this.argumentTypeIDs.size()];
			
			Element typeInfosElement = (Element) this.getTypeInfoElement().getParentNode();
			
			for (int i = 0; i < this.argumentTypeIDs.size(); i++) {
				String argumentTypeID = this.argumentTypeIDs.get(i);
				Type argumentType = TypeFactory.createType(typeInfosElement, argumentTypeID);
				this.argumentTypesCache[i] = argumentType;
			}
		}
		
		return this.argumentTypesCache;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || ! (o instanceof FunctionType)) {
			return false;
		}
		
		FunctionType another = (FunctionType) o;
		
		if (this.getArgumentTypes().length != another.getArgumentTypes().length) {
			return false;
		}
		
		for (int i = 0; i < this.getArgumentTypes().length; i++) {
			if (! this.getArgumentTypes()[i].equals(another.getArgumentTypes()[i])) {
				return false;
			}
		}
		
		return this.isConst() == another.isConst() && this.isVolatile() == another.isVolatile()
				&& this.getReturnType().equals(another.getReturnType());
	}
	
	@Override
	public boolean isCompatibleWith(Type anotherType) {
		if (anotherType == null) {
			return false;
		}
		
		if (anotherType.getSort() == Type.Sort.TYPEDEF) {
			anotherType = ((TypedefType) anotherType).getTrueTypeRecursively();
		}
		
		if (anotherType.getSort() != Type.Sort.FUNCTION) {
			return false;
		}
		FunctionType t = (FunctionType) anotherType;
		
		if (! this.getReturnType().isCompatibleWith(t.getReturnType())) {
			return false;
		}
		
		if (this.getArgumentTypes().length != t.getArgumentTypes().length) {
			return false;
		}
		
		for (int i = 0; i < this.getArgumentTypes().length; i++) {
			if (! this.getArgumentTypes()[i].isCompatibleWith(t.getArgumentTypes()[i])) {
				return false;
			}
		}
		
		return true;
	}
	
}
