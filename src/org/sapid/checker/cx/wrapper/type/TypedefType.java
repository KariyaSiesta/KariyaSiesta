package org.sapid.checker.cx.wrapper.type;

import org.w3c.dom.Element;

public class TypedefType extends Type {

	private String trueTypeID;
	private Type trueTypeCache;
	private String name;
	
	
	TypedefType(Element typeInfoElement) {
		super(typeInfoElement);
		
		if (! typeInfoElement.hasAttribute(TypeInfosConstant.REF_ATTRIBUTE_NAME)
				|| ! typeInfoElement.hasAttribute(TypeInfosConstant.NAME_ATTRIBUTE_NAME)) {
			throw new IllegalArgumentException();
		}
		
		this.trueTypeID = typeInfoElement.getAttribute(TypeInfosConstant.REF_ATTRIBUTE_NAME);
		this.name = typeInfoElement.getAttribute(TypeInfosConstant.NAME_ATTRIBUTE_NAME);
	}

	
	@Override
	public Type.Sort getSort() {
		return Type.Sort.TYPEDEF;
	}

	@Override
	public String getText() {
		return this.getName();
	}

	public Type getTrueType() {
		if (this.trueTypeCache == null) {
			Element typeInfosElement = (Element) this.getTypeInfoElement().getParentNode();
			this.trueTypeCache = TypeFactory.createType(typeInfosElement, this.trueTypeID);
		}
		
		return this.trueTypeCache;
	}
	
	public Type getTrueTypeRecursively() {
		Type currentType = this;
		
		while (currentType.getSort() == Type.Sort.TYPEDEF) {
			currentType = ((TypedefType) currentType).getTrueType();
		}
		
		return currentType;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || ! (o instanceof TypedefType)) {
			return false;
		}
		
		TypedefType another = (TypedefType) o;
		
		return this.isConst() == another.isConst() && this.isVolatile() == another.isVolatile()
				&& this.getName().equals(another.getName()) && this.getTrueType().equals(another.getTrueType());
	}
	
	public boolean isCompatibleWith(Type anotherType) {
		if (anotherType == null) {
			return false;
		}
		
		if (anotherType.getSort() == Type.Sort.TYPEDEF) {
			anotherType = ((TypedefType) anotherType).getTrueTypeRecursively();
		}
		
		return this.getTrueTypeRecursively().isCompatibleWith(anotherType);
	}
	
}
