/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.rule.ojl;

import java.util.ArrayList;
import java.util.List;

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.Result;
import org.sapid.checker.cx.wrapper.CExpressionElement;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CFunctionElement;
import org.sapid.checker.cx.wrapper.CStatementElement;
import org.sapid.checker.cx.wrapper.type.FunctionType;
import org.sapid.checker.cx.wrapper.type.StandardType;
import org.sapid.checker.cx.wrapper.type.Type;
import org.sapid.checker.cx.wrapper.type.TypeFactory;
import org.sapid.checker.cx.wrapper.type.TypeInfosConstant;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * {@link org.sapid.checker.cx.wrapper.CExpressionElement}をテストするためのルール。
 * 関数宣言での戻り値の型と、実際にreturn文で返している式の型が一致しない場合に、
 * そのreturn文を違反として検出する。
 * @author Nobuyuki UEHARA
 */
public class TestGetTypeInfo implements CheckerClass {

    /** ルールのレベル */
    private final static int LEVEL = 1;

    /** ルールのメッセージ */
    private final static String MESSAGE = "TestGetTypeInfo";

    private final static String MESSAGE_UNKNOWN_FUNCTION_TYPE = "TestGetTypeInfo 関数の戻り値の型を判別できませんでした";

    private final static String MESSAGE_EXPRESSION_NOT_FOUND = "TestGetTypeInfo returnされる式を見つけられませんでした";

    
    private Element typeInfosElement;
    
    
	@Override
	public List<Result> check(IFile file, CheckRule rule) {
		List<Result> violations = new ArrayList<Result>();
		
		Document document = file.getDOM();
		Element fileElement = (Element) document.getElementsByTagName("File").item(0);
		this.typeInfosElement = (Element) document.getElementsByTagName(TypeInfosConstant.TYPEINFOS_ELEMENT_NAME).item(0);
		
		if (this.typeInfosElement == null) {
			return violations;
		}
		
		CFileElement cFile = new CFileElement(fileElement);
		
		for (CFunctionElement function : cFile.getFunctions()) {
			
			List<Result> violationsInOneFunction = this.check(function);
			if (violationsInOneFunction != null) {
				violations.addAll(violationsInOneFunction);
			}
		}
		
		return violations;
	}
	
	private List<Result> check(CFunctionElement function) {
		List<Result> violations = new ArrayList<Result>();
		
		Type returnType = this.getReturnType(function);
		if (returnType == null) {
			violations.add(new Result(null, new NodeOffsetUtil(function.getElem()).getRange(), TestGetTypeInfo.LEVEL, TestGetTypeInfo.MESSAGE_UNKNOWN_FUNCTION_TYPE));
			return violations;
		}
		
		for (CStatementElement statement : function.getStatments()) {
			
			if (statement.isReturnStatement()) {
				CExpressionElement returnExpression = this.getReturnExpression(statement);
				
				if (returnExpression == null) {
					violations.add(new Result(null, new NodeOffsetUtil(statement.getElem()).getRange(), TestGetTypeInfo.LEVEL, TestGetTypeInfo.MESSAGE_EXPRESSION_NOT_FOUND));
					continue;
				}
				
				Type returnExpressionType = returnExpression.getTypeInfo();
				
				if (! returnType.equals(returnExpressionType)) {
					if (returnType instanceof StandardType && ((StandardType) returnType).isCompatibleWith(returnExpressionType)
							|| returnExpressionType instanceof StandardType && ((StandardType) returnExpressionType).isCompatibleWith(returnType)) {
					} else {
						violations.add(new Result(null, new NodeOffsetUtil(returnExpression.getElem()).getRange(), TestGetTypeInfo.LEVEL, TestGetTypeInfo.MESSAGE + " decl:" + returnType + " expr:" + returnExpressionType));
					}
				}
			}
		}
		
		return violations;
	}

	private Type getReturnType(CFunctionElement function) {
		Element identElement = function.getIdent();
		
		if (! identElement.hasAttribute(TypeInfosConstant.TYPE_ID_ATTRIBUTE_NAME)) {
			return null;
		}
		
		String typeIDAttributeValue = identElement.getAttribute(TypeInfosConstant.TYPE_ID_ATTRIBUTE_NAME);
		FunctionType functionType = (FunctionType) TypeFactory.createType(this.typeInfosElement, typeIDAttributeValue);
		if (functionType == null) {
			return null;
		}
		
		return functionType.getReturnType();
	}
	
	private CExpressionElement getReturnExpression(CStatementElement returnStatement) {
		for (CExpressionElement expression : returnStatement.getExpressions()) {
			if (! expression.isReturn()) {
				return expression;
			}
		}
		
		return null;
	}
	
}
