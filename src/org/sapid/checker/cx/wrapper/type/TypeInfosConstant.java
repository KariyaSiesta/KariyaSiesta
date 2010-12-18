package org.sapid.checker.cx.wrapper.type;

public interface TypeInfosConstant {

	static final String TYPE_ID_ATTRIBUTE_NAME = "type_id";
	static final String TYPEINFOS_ELEMENT_NAME = "TypeInfos";
	static final String TYPEINFO_ELEMENT_NAME = "TypeInfo";
	static final String TYPEREF_ELEMENT_NAME = "typeRef";
	
	static final String ID_ATTRIBUTE_NAME = "id";
	static final String TEXT_ATTRIBUTE_NAME = "text";
	static final String CONST_ATTRIBUTE_NAME = "const";
	static final String VOLATILE_ATTRIBUTE_NAME = "volatile";
	static final String REF_ATTRIBUTE_NAME = "ref";
	static final String NAME_ATTRIBUTE_NAME = "name";
	static final String ARRAYSIZE_ATTRIBUTE_NAME = "array_size";
	
	static final String SORT_ATTRIBUTE_NAME = "sort";
	static final String SORT_ATTRIBUTE_VALUE_STANDARD = "standard";
	static final String SORT_ATTRIBUTE_VALUE_POINTER = "pointer";
	static final String SORT_ATTRIBUTE_VALUE_ARRAY = "array";
	static final String SORT_ATTRIBUTE_VALUE_TYPEDEF = "typedef";
	static final String SORT_ATTRIBUTE_VALUE_FUNCTION = "function";
	static final String SORT_ATTRIBUTE_VALUE_STRUCT = "struct";
	static final String SORT_ATTRIBUTE_VALUE_UNION = "union";
	static final String SORT_ATTRIBUTE_VALUE_ENUM = "enum";
	static final String SORT_ATTRIBUTE_VALUE_OPTIONAL_PARAMETER = "...";
	static final String SORT_ATTRIBUTE_VALUE_ARGUMENT = "argument";
	static final String SORT_ATTRIBUTE_VALUE_MEMBER = "member";

	static final String TYPE_ATTRIBUTE_NAME = "type";
	static final String TYPE_ATTRIBUTE_VALUE_VOID = "void";
	static final String TYPE_ATTRIBUTE_VALUE_INT = "int";
	static final String TYPE_ATTRIBUTE_VALUE_CHAR = "char";
	static final String TYPE_ATTRIBUTE_VALUE_FLOAT = "float";
	static final String TYPE_ATTRIBUTE_VALUE_DOUBLE = "double";
	static final String TYPE_ATTRIBUTE_VALUE_UNSPECIFIED = "unspecified";
	
	static final String SIGN_ATTRIBUTE_NAME = "sign";
	
	static final String SIZE_ATTRIBUTE_NAME = "size";
	static final String SIZE_ATTRIBUTE_VALUE_SHORT = "short";
	static final String SIZE_ATTRIBUTE_VALUE_STANDARD = "standard";
	static final String SIZE_ATTRIBUTE_VALUE_LONG = "long";
	static final String SIZE_ATTRIBUTE_VALUE_LONGLONG = "long_long";
	
	static final String BOOLEAN_ATTRIBUTE_VALUE_TRUE = "true";
	static final String BOOLEAN_ATTRIBUTE_VALUE_FALSE = "false";
	static final String BOOLEAN_ATTRIBUTE_VALUE_UNSPECIFIED = "unspecified";
	
}
