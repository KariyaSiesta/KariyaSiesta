package org.sapid.checker.cx.wrapper.type;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TypeFactory {
	
	public static Type createType(Element typeInfosElement, String id) {
		if ( ! typeInfosElement.getTagName().equals(TypeInfosConstant.TYPEINFOS_ELEMENT_NAME)) {
			throw new IllegalArgumentException();
		}
		
		NodeList typeInfoNodeList = typeInfosElement.getElementsByTagName(TypeInfosConstant.TYPEINFO_ELEMENT_NAME);
		for (int i = 0; i < typeInfoNodeList.getLength(); i++) {
			Element typeInfoElement = (Element) typeInfoNodeList.item(i);
			
			if (typeInfoElement.getAttribute(TypeInfosConstant.ID_ATTRIBUTE_NAME).equals(id)) {
				return TypeFactory.createType(typeInfoElement);
			}
		}
		
		return null;
	}

	static Type createType(Element typeInfoElement) {
		if (! typeInfoElement.getTagName().equals(TypeInfosConstant.TYPEINFO_ELEMENT_NAME)
				|| ! typeInfoElement.hasAttribute(TypeInfosConstant.SORT_ATTRIBUTE_NAME)) {
			throw new IllegalArgumentException();
		}
		
		Type type;
		
		String sortString = typeInfoElement.getAttribute(TypeInfosConstant.SORT_ATTRIBUTE_NAME);
		Type.Sort sort = Type.Sort.fromString(sortString);
		switch (sort) {
		case STANDARD:
			type = new StandardType(typeInfoElement);
			break;
		case POINTER:
			type = new PointerType(typeInfoElement);
			break;
		case ARRAY:
			type = new ArrayType(typeInfoElement);
			break;
		case TYPEDEF:
			type = new TypedefType(typeInfoElement);
			break;
		case FUNCTION:
			type = new FunctionType(typeInfoElement);
			break;
		case OPTIONAL_PARAMETER:
			type = new OptionalParameterType(typeInfoElement);
			break;
		case STRUCT:
		case UNION:
		case ENUM:
			type = new TagType(typeInfoElement);
			break;
		default:
			type = null;
			//throw new IllegalArgumentException();
		}
		
		return type;
	}
	
}
