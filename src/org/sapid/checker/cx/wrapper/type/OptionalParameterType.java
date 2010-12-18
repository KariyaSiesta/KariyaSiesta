package org.sapid.checker.cx.wrapper.type;

import org.w3c.dom.Element;

public class OptionalParameterType extends Type {

	OptionalParameterType(Element typeInfoElement) {
		super(typeInfoElement);
	}

	
	@Override
	public Sort getSort() {
		return Type.Sort.OPTIONAL_PARAMETER;
	}

	@Override
	public String getText() {
		return "...";
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || ! (o instanceof OptionalParameterType)) {
			return false;
		}
		
		OptionalParameterType another = (OptionalParameterType) o;
		
		return this.isConst() == another.isConst() && this.isVolatile() == another.isVolatile();
	}
	
	@Override
	public boolean isCompatibleWith(Type anotherType) {
		// TODO 常にtrueを返すべきか？
		return this.equals(anotherType);
	}

}
