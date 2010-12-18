/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.cx.wrapper;

import org.sapid.checker.cx.wrapper.type.ArrayType;
import org.sapid.checker.cx.wrapper.type.FunctionType;
import org.sapid.checker.cx.wrapper.type.PointerType;
import org.sapid.checker.cx.wrapper.type.StandardType;
import org.sapid.checker.cx.wrapper.type.Type;
import org.sapid.checker.cx.wrapper.type.TypeFactory;
import org.sapid.checker.cx.wrapper.type.TypeInfosConstant;
import org.sapid.checker.cx.wrapper.type.TypedefType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Expr 要素
 * @author Toshinori OSUKA
 */
public class CExpressionElement extends CElement {
    protected final String SORT_EMPTY = "Empty";
    protected final String SORT_BREAK = "Break";
    protected final String SORT_CONTINUE = "Continue";
    protected final String SORT_RETURN = "Return";
    protected final String SORT_GOTO = "Goto";

    protected final String SORT_ARITH = "Arith";
    protected final String SORT_FUNCCALL = "FuncCall";
    protected final String SORT_INITLIST = "InitList";

    protected final String SORT_VARREF = "VarRef";
    protected final String SORT_PAREN = "Paren";
    protected final String SORT_ASSIGN = "Assign";
    protected final String SORT_INC = "INC";
    protected final String SORT_DEC = "DEC";
    protected final String SORT_REF = "Ref";
    protected final String SORT_LITERAL = "Literal";
    
    
    private Sort sortCache;
    private Type typeCache;
    

    public CExpressionElement(Element elem) {
        super(elem);
        
        if (elem == null) {
        	throw new NullPointerException();
        }
        // TODO Auto-generated constructor stub
    }

    /**
     * Expression の種類を取得する TODO 中途半端なので JX-model を参考に書き直す
     * @return
     * 
     * @deprecated {@link #getSortEnum()}の使用を推奨
     */
    @Deprecated
    public String getSort() {
        if ("".equals(elem.getTextContent())) {
            return SORT_EMPTY;
        }

        Element kw = getFirstChildNode("kw");
        if (kw != null) {
            String text = kw.getTextContent();
            if ("break".equals(text)) {
                return SORT_BREAK;
            } else if ("continue".equals(text)) {
                return SORT_CONTINUE;
            } else if ("goto".equals(text)) {
                return SORT_GOTO;
            } else if ("return".equals(text)) {
                return SORT_RETURN;
            } else if ("sizeof".equals(text)) {
                return "SizeOf";
            }
        }

        Element ident = getFirstChildNode("ident");
        if (elem.getChildNodes().getLength() > 0 && ident != null) {
            return SORT_VARREF;
        }

        Element op = getFirstChildNode("op");
        if (op != null) {
            String text = op.getTextContent();
            if ("<".equals(text) || "<=".equals(text) || ">".equals(text)
                    || ">=".equals(text) || "==".equals(text)
                    || "!=".equals(text) || "!".equals(text)
                    || "&&".equals(text) || "||".equals(text)) {
                return "Condition";
            } else if ("++".equals(text)) {
                return SORT_INC;
            } else if ("--".equals(text)) {
                return SORT_DEC;
            } else if ("(".equals(text)) {
                if (elem.getFirstChild() == op) {
                    if ("Type".equals(op.getNextSibling().getNodeName())) {
                        return "Cast";
                    }
                    return SORT_PAREN;
                } else {
                    return SORT_FUNCCALL;
                }
            } else if ("=".equals(text) || "+=".equals(text)
                    || "-=".equals(text) || "*=".equals(text)
                    || "/=".equals(text) || "%=".equals(text)
                    || "&=".equals(text) || "^=".equals(text)
                    || "|=".equals(text) || "<<=".equals(text)
                    || ">>=".equals(text)) {
                return SORT_ASSIGN;
            } else if ("<<".equals(text) || ">>".equals(text)) {
                return "Shift";
            } else if ("+".equals(text) || "-".equals(text) || "*".equals(text)
                    || "/".equals(text) || "%".equals(text)) {
                return SORT_ARITH;
            } else if ("[".equals(text)) {
                return "ArrayAccess";
            } else if ("?".equals(text)) {
                return "Selection";
            } else if ("~".equals(text) || "^".equals(text) || "|".equals(text)) {
                return "Bit";
            } else if ("&".equals(text)) {
                if (elem.getFirstChild() == op) {
                    return "Address";
                }
                return "Bit";
            } else if ("->".equals(text)) {
                return "Pointer";
            } else if (".".equals(text)) {
                return "DOT";
            } else if (",".equals(text)) {
                return "Listed";
            }
        }
        // System.out.println(elem.getTextContent());
        throw new AssertionError(elem.getTextContent());
    }
    
    /**
     * 式の種類を返す
     * @return 式の種類
     */
    public Sort getSortEnum() {
    	if (this.sortCache == null) {
    		this.sortCache = this.getSortEnumWithoutCache();
    	}
    	
    	return this.sortCache;
    }

    private Sort getSortEnumWithoutCache() {
    	if (this.getChildrenNode("macroCall").length > 0) {
    		return Sort.UNKNOWN;
    	}
    	
        if ("".equals(elem.getTextContent())) {
            return Sort.EMPTY;
        }
        
        Element kw = getFirstChildNode("kw");
        if (kw != null) {
            String text = kw.getTextContent();
            if ("sizeof".equals(text)) {
                return Sort.SIZEOF;
            }
        }
        
        Element literal = getFirstChildNode("literal");
        if (literal != null) {
        	return Sort.LITERAL;
        }
        
        int subExpressionCount = this.getChildrenNode("Expr").length;

        Element op = getFirstChildNode("op");
        if (op == null) {
	        Element ident = getFirstChildNode("ident");
	        if (this.getElem().getChildNodes().getLength() > 0 && ident != null) {
	            return Sort.VARIABLE;
	        }
        } else {
            String text = op.getTextContent();
            if ("<".equals(text) || "<=".equals(text) || ">".equals(text) ||
            		">=".equals(text) || "==".equals(text) || "!=".equals(text)) {
            	return Sort.COMPARISON;
            } else if ("!".equals(text) || "&&".equals(text) || "||".equals(text)) {
            	return Sort.LOGICAL;
            } else if ("++".equals(text)) {
                return Sort.INCREMENT;
            } else if ("--".equals(text)) {
                return Sort.DECREMENT;
            } else if ("(".equals(text)) {
                if (elem.getFirstChild() == op) {
                    if (this.getChildrenNode("Type").length > 0) {
                        return Sort.CAST;
                    }
                    return Sort.PARENTHETIC;
                } else {
                    return Sort.CALL;
                }
            } else if ("=".equals(text)) {
            	return Sort.ASSIGN;
            } else if ("+=".equals(text) || "-=".equals(text) ||
            		"*=".equals(text) || "/=".equals(text) || "%=".equals(text)) {
            	return Sort.ARITHMETIC_ASSIGN;
            } else if ("&=".equals(text) || "^=".equals(text) || "|=".equals(text)) {
            	return Sort.BIT_ASSIGN;
            } else if ("<<=".equals(text) || ">>=".equals(text)) {
                return Sort.SHIFT_ASSIGN;
            } else if ("<<".equals(text) || ">>".equals(text)) {
                return Sort.SHIFT;
            } else if ("*".equals(text) && subExpressionCount == 1) {
            	return Sort.REFERENCE;
            } else if ("+".equals(text) || "-".equals(text) ||
            		"*".equals(text) || "/".equals(text) || "%".equals(text)) {
            	if (subExpressionCount == 1) {
            		return Sort.ARITHMETIC1;
            	} else if (subExpressionCount == 2) {
            		return Sort.ARITHMETIC2;
            	}
            } else if ("[".equals(text)) {
                return Sort.ARRAY;
            } else if ("?".equals(text)) {
                return Sort.SELECTION;
            } else if ("&".equals(text)) {
                if (elem.getFirstChild() == op) {
                    return Sort.ADDRESS;
                }
                return Sort.BIT;
            } else if ("~".equals(text) || "^".equals(text) || "|".equals(text)) {
                return Sort.BIT;
            } else if ("->".equals(text)) {
                return Sort.ARROW;
            } else if (".".equals(text)) {
                return Sort.DOT;
            } else if (",".equals(text)) {
                return Sort.LISTED;
            }
        }
        
        // System.err.println(this.getClass().getName() + " " + elem.getTextContent() + " unknown expression sort");
        return Sort.UNKNOWN;
    }

   /**
     * 算術演算（のみ）かどうか
     * @return
     */
    public boolean isArith() {
    	Sort sort = this.getSortEnum();
        return sort == Sort.ARITHMETIC1 || sort == Sort.ARITHMETIC2;
    }

    /**
     * 括弧演算かどうか
     * @return
     */
    public boolean isParen() {
        return this.getSortEnum() == Sort.PARENTHETIC;
    }

    /**
     * 代入（を伴う）演算かどうか
     * @return
     */
    public boolean isAssign() {
    	Sort sort = this.getSortEnum();
        return sort == Sort.ASSIGN || sort == Sort.ARITHMETIC_ASSIGN ||
        		sort == Sort.BIT_ASSIGN || sort == Sort.SHIFT_ASSIGN;
    }

    /**
     * インクリメント演算かどうか
     * @return
     */
    public boolean isIncrement() {
        return this.getSortEnum() == Sort.INCREMENT;
    }

    /**
     * デクリメント演算かどうか
     * @return
     */
    public boolean isDecrement() {
        return this.getSortEnum() == Sort.DECREMENT;
    }

    /**
     * Return するかどうか
     * @return
     */
    @Deprecated
    public boolean isReturn() {
    	return false;
        //return getSort().equals(SORT_RETURN);
    }

    /**
     * 変数参照かどうか
     * @return
     */
    public boolean isVarRef() {
        return this.getSortEnum() == Sort.VARIABLE;
    }
    
    /**
     * 副作用を持つか<br>
     * return なども制御フローを変更するという意味の副作用を持つ
     * @return
     */
     public boolean hasSideEffect() {
        return this.isIncrement() || this.isDecrement() || this.isAssign() || this.getSortEnum() == Sort.CALL;
    }

    /**
     * 与えられたノードが Expr 要素かどうか
     * @param node
     * @return
     */
    public static boolean isExpression(Node node) {
        return "Expr".equals(node.getNodeName());
    }
    
    /**
     * 式の型を返す 判別できなかった場合はnull
     * TODO 多態化
     * @return
     */
    public Type getTypeInfo() {
    	if (this.typeCache == null) {
    		this.typeCache = this.getTypeInfoWithoutCache();
    	}
    	
    	return this.typeCache;
    }

    private Type getTypeInfoWithoutCache() {
    	// For debug.
    	String textContent = this.getElem().getTextContent();
    	// System.out.println(this.getClass().getName() + " typing: " + textContent);
    	
    	NodeList typeInfosNodeList;
    	Element firstSubExpressionElement;
    	CExpressionElement firstSubExpression;
    	
    	Type type;
    	
    	Sort sort = this.getSortEnum();
    	switch (sort) {
    	case UNKNOWN:
    	case EMPTY:
    		type = null;
    		break;
    	case LITERAL:
    		Element literalElement = this.getFirstChildNode("literal");
    		String literalContent = literalElement.getTextContent();
    		if (literalContent.startsWith("\'")) {
    			type = new StandardType(StandardType.Sort.CHAR, StandardType.Sign.UNSPECIFIED, StandardType.Size.NORMAL, false, false);
    		} else if (literalContent.startsWith("L\'") || literalContent.startsWith("l\'")) {
    			// 本当は wchar_t
    			type = new StandardType(StandardType.Sort.CHAR, StandardType.Sign.UNSPECIFIED, StandardType.Size.NORMAL, false, false);
    		} else if (literalContent.startsWith("\"")) {
    			Type pointeeType = new StandardType(StandardType.Sort.CHAR, StandardType.Sign.UNSPECIFIED, StandardType.Size.NORMAL, true, false);
    			type = new ArrayType(pointeeType, 0);
    		} else if (literalContent.startsWith("L\"") || literalContent.startsWith("l\"")) {
    			// 本当は wchar_t[]
    			Type pointeeType = new StandardType(StandardType.Sort.CHAR, StandardType.Sign.UNSPECIFIED, StandardType.Size.NORMAL, true, false);
    			type = new ArrayType(pointeeType, 0);
    		} else {
    			type = StandardType.fromLiteral(textContent);
    		}
    		break;
    	case ARITHMETIC1:
    		type = new CExpressionElement(this.getFirstChildNode("Expr")).getTypeInfo();
    		break;
    	case ARITHMETIC2:
    		Type oneType = new CExpressionElement(this.getFirstChildNode("Expr")).getTypeInfo();
    		if (oneType != null && oneType.getSort() == Type.Sort.TYPEDEF) {
    			oneType = ((TypedefType) oneType).getTrueTypeRecursively();
    		}
    		
    		Type anotherType = new CExpressionElement(this.getLastChildNode("Expr")).getTypeInfo();
    		if (anotherType != null && anotherType.getSort() == Type.Sort.TYPEDEF) {
    			anotherType = ((TypedefType) anotherType).getTrueTypeRecursively();
    		}
    		
    		if (oneType != null && anotherType != null) {
    			if (oneType.getSort() == Type.Sort.STANDARD && anotherType.getSort() == Type.Sort.STANDARD) {
    				type = ((StandardType) oneType).arithmeticConversion((StandardType) anotherType);
    			} else if (oneType.getSort() == Type.Sort.POINTER && anotherType.getSort() != Type.Sort.POINTER) {
    				type = oneType;
    			} else if (oneType.getSort() == Type.Sort.POINTER && anotherType.getSort() == Type.Sort.POINTER) {
        			// 本当は ptrdiff_t
        			type = new StandardType(StandardType.Sort.INT, StandardType.Sign.UNSPECIFIED, StandardType.Size.NORMAL, false, false);
    			} else {
    				type = null;
    			}
    		} else {
    			type = null;
    		}
    		break;
    	case VARIABLE:
    		String typeID = getFirstChildNode("ident").getAttribute(TypeInfosConstant.TYPE_ID_ATTRIBUTE_NAME);
    		typeInfosNodeList = this.getElem().getOwnerDocument().getDocumentElement().getElementsByTagName(TypeInfosConstant.TYPEINFOS_ELEMENT_NAME);
    		
    		type = null;
        	if (! typeID.isEmpty() && typeInfosNodeList.getLength() != 0) {
        		Element typeInfosElement = (Element) typeInfosNodeList.item(0);
        		type = TypeFactory.createType(typeInfosElement, typeID);
        		
        		if (type.getSort() == Type.Sort.FUNCTION) {
        			type = new PointerType(typeInfosElement, type.getId());
        		}
        	}
    		break;
    	case PARENTHETIC:
    		type = new CExpressionElement(this.getFirstChildNode("Expr")).getTypeInfo();
    		break;
    	case ASSIGN:
    	case ARITHMETIC_ASSIGN:
    	case BIT_ASSIGN:
    	case SHIFT_ASSIGN:
    		type = new CExpressionElement(this.getFirstChildNode("Expr")).getTypeInfo();
    		break;
    	case INCREMENT:
    	case DECREMENT:
    		type = new CExpressionElement(this.getFirstChildNode("Expr")).getTypeInfo();
    		break;
    	case BIT:
    		type = new CExpressionElement(this.getFirstChildNode("Expr")).getTypeInfo();
    		break;
    	case SHIFT:
    		type = new CExpressionElement(this.getFirstChildNode("Expr")).getTypeInfo();
    		break;
    	case ARRAY:
    		firstSubExpressionElement = this.getFirstChildNode("Expr");
    		firstSubExpression = new CExpressionElement(firstSubExpressionElement);
    		Type arrayType = firstSubExpression.getTypeInfo();
    		
    		if (arrayType == null) {
    			type = null;
    			break;
    		}
    		
    		if (arrayType.getSort() == Type.Sort.TYPEDEF) {
    			arrayType = ((TypedefType) arrayType).getTrueTypeRecursively();
    		}
    		
    		if (arrayType.getSort() == Type.Sort.ARRAY) {
    			type = ((ArrayType) arrayType).getPointeeType();
    		} else if (arrayType.getSort() == Type.Sort.POINTER) {
    			type = ((PointerType) arrayType).getPointeeType();
    		} else {
    			type = null;
    		}
    		break;
    	case CALL:
    		firstSubExpressionElement = this.getFirstChildNode("Expr");
    		type = null;
    		if (firstSubExpressionElement != null) {
        		firstSubExpression = new CExpressionElement(firstSubExpressionElement);
        		Type firstSubExpressionType = firstSubExpression.getTypeInfo();
        		
        		if (firstSubExpressionType != null && firstSubExpressionType.getSort() == Type.Sort.POINTER) {
        			Type pointeeType = ((PointerType) firstSubExpressionType).getPointeeType();
        			if (pointeeType != null && pointeeType.getSort() == Type.Sort.FUNCTION) {
        				type = ((FunctionType)pointeeType).getReturnType();
        			}
        		}
    		}
    		break;
    	case ADDRESS:
    		typeInfosNodeList = this.getElem().getOwnerDocument().getElementsByTagName(TypeInfosConstant.TYPEINFOS_ELEMENT_NAME);
    		if (typeInfosNodeList.getLength() == 1) {
    			Element typeInfosElement = (Element) typeInfosNodeList.item(0);
	    		Type pointeeType = new CExpressionElement(this.getFirstChildNode("Expr")).getTypeInfo();
	    		type = new PointerType(typeInfosElement, pointeeType.getId());
    		} else {
    			type = null;
    		}
    		break;
    	case REFERENCE:
    		Type referenceType = new CExpressionElement(this.getFirstChildNode("Expr")).getTypeInfo();
    		if (referenceType.getSort() == Type.Sort.POINTER) {
    			PointerType pointerType = (PointerType) referenceType;
    			type = pointerType.getPointeeType();
    		} else if (referenceType.getSort() == Type.Sort.ARRAY) {
    			type = ((ArrayType) referenceType).getPointeeType();
    		} else {
    			type = null;
    		}
    		break;
    	case DOT:
    		type = new CExpressionElement(this.getLastChildNode("Expr")).getTypeInfo();
    		break;
    	case ARROW:
    		type = new CExpressionElement(this.getLastChildNode("Expr")).getTypeInfo();
    		break;
    	case COMPARISON:
    	case LOGICAL:
    		type = new StandardType(StandardType.Sort.INT, StandardType.Sign.UNSPECIFIED, StandardType.Size.NORMAL, false, false);
    		break;
    	case SELECTION:
    		type = new CExpressionElement(this.getLastChildNode("Expr")).getTypeInfo();
    		break;
    	case CAST:
    		typeInfosNodeList = this.getElem().getOwnerDocument().getDocumentElement().getElementsByTagName(TypeInfosConstant.TYPEINFOS_ELEMENT_NAME);
    		String typeIDAttribute = this.getElem().getAttribute(TypeInfosConstant.TYPE_ID_ATTRIBUTE_NAME);
    		if (! typeIDAttribute.isEmpty() && typeInfosNodeList.getLength() > 0) {
    			type = TypeFactory.createType((Element) typeInfosNodeList.item(0), typeIDAttribute);
    		} else {
    			type = null;
    		}
    		break;
    	case SIZEOF:
    		// 本当はsize_t
    		type = new StandardType(StandardType.Sort.INT, StandardType.Sign.UNSPECIFIED, StandardType.Size.NORMAL, false, false);
    		break;
    	case LISTED:
    		type = new CExpressionElement(this.getLastChildNode("Expr")).getTypeInfo();
    		break;
    	default:
    		throw new AssertionError(sort);
    	}
    	// For debug.
    	// System.out.println(textContent + " typed: " + type);
    	
    	return type;
    }
    
    
    /**
     * 式の種類を表す列挙体
     */
    public static enum Sort {
    	EMPTY,
    	LITERAL,
    	ARITHMETIC1,
    	ARITHMETIC2,
    	PARENTHETIC,
    	ASSIGN,
    	ARITHMETIC_ASSIGN,
    	SHIFT_ASSIGN,
    	BIT_ASSIGN,
    	INCREMENT,
    	DECREMENT,
    	VARIABLE,
    	CALL,
    	CAST,
    	SHIFT,
    	BIT,
    	COMPARISON,
    	LOGICAL,
    	ARRAY,
    	SELECTION,
    	ADDRESS,
    	REFERENCE,
    	DOT,
    	ARROW,
    	SIZEOF,
    	LISTED,
    	UNKNOWN
    }
    
}
