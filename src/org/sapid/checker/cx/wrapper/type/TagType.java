package org.sapid.checker.cx.wrapper.type;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TagType extends Type {
	
	private Type.Sort sort;
	private String name;
	private List<String> memberTypeIDs;
	private Type[] memberTypesCache;

	
	TagType(Element typeInfoElement) {
		super(typeInfoElement);
		
		if (! typeInfoElement.hasAttribute(TypeInfosConstant.VOLATILE_ATTRIBUTE_NAME)) {
			throw new IllegalArgumentException();
		}
		
		this.sort = Type.Sort.fromString(typeInfoElement.getAttribute(TypeInfosConstant.SORT_ATTRIBUTE_NAME));
		this.name = typeInfoElement.getAttribute(TypeInfosConstant.VOLATILE_ATTRIBUTE_NAME);
		
		this.memberTypeIDs = new ArrayList<String>();
		
		NodeList typeRefNodeList = typeInfoElement.getElementsByTagName(TypeInfosConstant.TYPEREF_ELEMENT_NAME);
		for (int i = 0; i < typeRefNodeList.getLength(); i++) {
			Element typeRefElement = (Element) typeRefNodeList.item(i);
			
			if (typeRefElement.getAttribute(TypeInfosConstant.SORT_ATTRIBUTE_NAME).equals(TypeInfosConstant.SORT_ATTRIBUTE_VALUE_MEMBER)) {
				this.memberTypeIDs.add(typeRefElement.getAttribute(TypeInfosConstant.REF_ATTRIBUTE_NAME));
			}
		}
	}
	

	@Override
	public Type.Sort getSort() {
		return this.sort;
	}

	@Override
	public String getText() {
		String text;
		
		switch (this.getSort()) {
		case STRUCT:
			text = "struct";
			break;
		case UNION:
			text = "union";
			break;
		case ENUM:
			text = "enum";
			break;
		default:
			throw new IllegalStateException();
		}
		
		if (! this.getName().isEmpty()) {
			text += " " + this.getName();
		}
		
		return text;
	}
	
	public String getName() {
		return this.name;
	}

	public Type[] getMemberTypes() {
		if (this.memberTypesCache == null) {
			this.memberTypesCache = new Type[this.memberTypeIDs.size()];
			
			Element typeInfosElement = (Element) this.getTypeInfoElement().getParentNode();
			
			for (int i = 0; i < this.memberTypeIDs.size(); i++) {
				String argumentTypeID = this.memberTypeIDs.get(i);
				Type argumentType = TypeFactory.createType(typeInfosElement, argumentTypeID);
				this.memberTypesCache[i] = argumentType;
			}
		}
		
		return this.memberTypesCache;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || ! (o instanceof TagType)) {
			return false;
		}
		
		TagType another = (TagType) o;
		
		if (this.getMemberTypes().length != another.getMemberTypes().length) {
			return false;
		}
		
		for (int i = 0; i < this.getMemberTypes().length; i++) {
			if (! this.getMemberTypes()[i].equals(another.getMemberTypes()[i])) {
				return false;
			}
		}
		
		return this.isConst() == another.isConst() && this.isVolatile() == another.isVolatile()
				&& this.getSort() == another.getSort() && this.getName().equals(another.getName());
	}
	
	@Override
	public boolean isCompatibleWith(Type anotherType) {
		if (anotherType == null) {
			return false;
		}
		
		if (anotherType.getSort() == Type.Sort.TYPEDEF) {
			anotherType = ((TypedefType) anotherType).getTrueTypeRecursively();
		}
		
		Type.Sort anotherSort = anotherType.getSort();
		if (anotherSort != Type.Sort.STRUCT && anotherSort != Type.Sort.UNION && anotherSort != Type.Sort.ENUM) {
			return false;
		}
		TagType t = (TagType) anotherType;
		
		if (this.getSort() != anotherSort) {
			return false;
		}
		
		Type[] thisMemberTypes = this.getMemberTypes();
		Type[] anotherMemberTypes = t.getMemberTypes();
		
		if (thisMemberTypes.length != anotherMemberTypes.length) {
			return false;
		}
		
		// TODO メンバ名まで考慮すべき？
		for (int i = 0; i < thisMemberTypes.length; i++) {
			if (! thisMemberTypes[i].isCompatibleWith(anotherMemberTypes[i])) {
				return false;
			}
		}
		
		return true;
	}
	
}
