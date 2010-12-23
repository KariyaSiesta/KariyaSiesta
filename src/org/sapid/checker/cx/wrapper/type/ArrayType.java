package org.sapid.checker.cx.wrapper.type;

import org.w3c.dom.Element;

public class ArrayType extends Type {
	
	private String pointeeTypeID;
	private Type pointeeTypeCache;
	private int arraySize;
	

	ArrayType(Element typeInfoElement) {
		super(typeInfoElement);
		
		if (! typeInfoElement.hasAttribute(TypeInfosConstant.REF_ATTRIBUTE_NAME)) {
			throw new IllegalArgumentException();
		}
		
		this.pointeeTypeID = typeInfoElement.getAttribute(TypeInfosConstant.REF_ATTRIBUTE_NAME);
		
		String arraySizeString = typeInfoElement.getAttribute(TypeInfosConstant.ARRAYSIZE_ATTRIBUTE_NAME);
		if (arraySizeString.isEmpty()) {
			this.arraySize = 0;
		} else {
			this.arraySize = Integer.valueOf(arraySizeString);
		}
	}
	
	/**
	 * IDの無い配列型を生成する。
	 * @param pointeeType
	 * @param arraySize
	 */
	public ArrayType(Type pointeeType, int arraySize) {
		super(false, false);
		
		this.pointeeTypeCache = pointeeType;
		this.arraySize = arraySize;
	}
	

	@Override
	public Type.Sort getSort() {
		return Type.Sort.ARRAY;
	}

	@Override
	public String getText() {
		String pointeeTypeText = this.getPointeeType().getText();
		
		if (this.getArraySize() == 0) {
			return pointeeTypeText + "[]";
		} else {
			return pointeeTypeText + "[" + this.getArraySize() + "]";
		}
	}

	public Type getPointeeType() {
		if (this.pointeeTypeCache == null) {
			Element typeInfosElement = (Element) this.getTypeInfoElement().getParentNode();
			this.pointeeTypeCache = TypeFactory.createType(typeInfosElement, this.pointeeTypeID);
		}
		
		return this.pointeeTypeCache;
	}
	
	public int getArraySize() {
		return this.arraySize;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || ! (o instanceof ArrayType)) {
			return false;
		}
		
		ArrayType another = (ArrayType) o;
		return this.isConst() == another.isConst() && this.isVolatile() == another.isVolatile()
				&& this.getPointeeType().equals(another.getPointeeType())
				&& this.getArraySize() == another.getArraySize();
	}
	
	@Override
	public boolean isCompatibleWith(Type anotherType) {
		if (anotherType == null) {
			return false;
		}
		
		if (anotherType.getSort() == Type.Sort.TYPEDEF) {
			anotherType = ((TypedefType) anotherType).getTrueTypeRecursively();
		}
		
		if (anotherType.getSort() != Type.Sort.ARRAY) {
			return false;
		}
		ArrayType t = (ArrayType) anotherType;
		
		// TODO サイズも考慮すべきか？
		return this.getPointeeType().isCompatibleWith(t.getPointeeType());
	}
	
}
