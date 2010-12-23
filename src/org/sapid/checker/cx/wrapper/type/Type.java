package org.sapid.checker.cx.wrapper.type;

import org.w3c.dom.Element;

public abstract class Type {
	
	private Element typeInfoElement;
	private String id;
	private boolean isConst;
	private boolean isVolatile;
	
		
	Type(Element typeInfoElement) {
		if (! typeInfoElement.getTagName().equals(TypeInfosConstant.TYPEINFO_ELEMENT_NAME)
				|| ! typeInfoElement.hasAttribute(TypeInfosConstant.ID_ATTRIBUTE_NAME)
				|| ! typeInfoElement.hasAttribute(TypeInfosConstant.SORT_ATTRIBUTE_NAME)) {
			throw new IllegalArgumentException();
		}
		
		this.typeInfoElement = typeInfoElement;
		this.id = typeInfoElement.getAttribute(TypeInfosConstant.ID_ATTRIBUTE_NAME);
		this.isConst = typeInfoElement.getAttribute(TypeInfosConstant.CONST_ATTRIBUTE_NAME).equals(TypeInfosConstant.BOOLEAN_ATTRIBUTE_VALUE_TRUE);
		this.isVolatile = typeInfoElement.getAttribute(TypeInfosConstant.VOLATILE_ATTRIBUTE_NAME).equals(TypeInfosConstant.BOOLEAN_ATTRIBUTE_VALUE_TRUE);
	}
	
	Type(boolean isConst, boolean isVolatile) {
		this.isConst = isConst;
		this.isVolatile = isVolatile;
	}

	
	public Element getTypeInfoElement() {
		return this.typeInfoElement;
	}
	
	public String getId() {
		return this.id;
	}

	public abstract Type.Sort getSort();
	
	/*
	 * TODO
	 * 型修飾が行われ得ない型もあったので
	 * このメソッドはTypeクラスに持たせるべきではなかった
	 */
	public boolean isConst() {
		return this.isConst;
	}

	public boolean isVolatile() {
		return this.isVolatile;
	}
	
	public abstract String getText();
	
	public abstract boolean isCompatibleWith(Type anotherType);
	
	
	public static enum Sort {
		
		STANDARD(TypeInfosConstant.SORT_ATTRIBUTE_VALUE_STANDARD),
		POINTER(TypeInfosConstant.SORT_ATTRIBUTE_VALUE_POINTER),
		ARRAY(TypeInfosConstant.SORT_ATTRIBUTE_VALUE_ARRAY),
		TYPEDEF(TypeInfosConstant.SORT_ATTRIBUTE_VALUE_TYPEDEF),
		FUNCTION(TypeInfosConstant.SORT_ATTRIBUTE_VALUE_FUNCTION),
		STRUCT(TypeInfosConstant.SORT_ATTRIBUTE_VALUE_STRUCT),
		UNION(TypeInfosConstant.SORT_ATTRIBUTE_VALUE_UNION),
		ENUM(TypeInfosConstant.SORT_ATTRIBUTE_VALUE_ENUM),
		OPTIONAL_PARAMETER(TypeInfosConstant.SORT_ATTRIBUTE_VALUE_OPTIONAL_PARAMETER);
		
		
		private String string;
		
		
		private Sort(String string) {
			this.string = string;
		}
		
		
		@Override
		public String toString() {
			return this.string;
		}
		
		
		public static Sort fromString(String string) {
			Sort sort = null;
			
			for (Sort value : values()) {
				if (string.equals(value.toString())) {
					sort = value;
				}
			}
			
			return sort;
		}
		
	}
	
}
