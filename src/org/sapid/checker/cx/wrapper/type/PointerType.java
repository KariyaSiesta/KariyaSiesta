package org.sapid.checker.cx.wrapper.type;

import org.w3c.dom.Element;

public class PointerType extends Type {

	private String pointeeTypeID;
	private Type pointeeTypeCache;
	private Element typeInfosElement;
	
	
	PointerType(Element typeInfoElement) {
		super(typeInfoElement);
		
		if (! typeInfoElement.hasAttribute(TypeInfosConstant.REF_ATTRIBUTE_NAME)) {
			throw new IllegalArgumentException();
		}
		
		this.pointeeTypeID = typeInfoElement.getAttribute(TypeInfosConstant.REF_ATTRIBUTE_NAME);
	}
	
	/**
	 * ID������ʤ��ݥ��󥿷����������롣 &hoge �Τ褦�ʼ��η���ɽ�����˻Ȥ���
	 */
	public PointerType(Element typeInfosElement, String pointeeTypeID) {
		super(false, false);
		
		this.pointeeTypeID = pointeeTypeID;
		this.typeInfosElement = typeInfosElement;
	}
	

	@Override
	public Type.Sort getSort() {
		return Type.Sort.POINTER;
	}

	@Override
	public String getText() {
		String pointeeTypeText = this.getPointeeType().getText();
		return pointeeTypeText + "*";
	}

	/**
	 * �ݥ���Ȥ��뷿��������롥
	 * @return �ݥ���Ȥ��뷿�����η���CX-modelʸ���̵��null���֤����Ȥ����롥
	 */
	public Type getPointeeType() {
		if (this.pointeeTypeCache == null) {
			Element typeInfoElement = this.getTypeInfoElement();
			
			Element typeInfosElement;
			if (typeInfoElement != null) {
				typeInfosElement = (Element) typeInfoElement.getParentNode();
			} else {
				typeInfosElement = this.typeInfosElement;
			}
			
			this.pointeeTypeCache = TypeFactory.createType(typeInfosElement, this.pointeeTypeID);
		}
		
		return this.pointeeTypeCache;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || ! (o instanceof PointerType)) {
			return false;
		}
		
		PointerType another = (PointerType) o;
		
		return this.isConst() == another.isConst() && this.isVolatile() == another.isVolatile()
				&& this.getPointeeType().equals(another.getPointeeType());
	}
	
	@Override
	public boolean isCompatibleWith(Type anotherType) {
		if (anotherType == null) {
			return false;
		}
		
		if (anotherType.getSort() == Type.Sort.TYPEDEF) {
			anotherType = ((TypedefType) anotherType).getTrueTypeRecursively();
		}
		
		if (anotherType.getSort() != Type.Sort.POINTER) {
			return false;
		}
		PointerType t = (PointerType) anotherType;
		
		Type thisPointeeType = this.getPointeeType();
		if (thisPointeeType == null) {
			return false;
		} else {
			return thisPointeeType.isCompatibleWith(t.getPointeeType());
		}
	}
	
}
