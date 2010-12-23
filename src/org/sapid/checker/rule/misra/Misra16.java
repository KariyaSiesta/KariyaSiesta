/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.rule.misra;

import java.util.ArrayList;
import java.util.List;

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.Result;
import org.sapid.checker.cx.wrapper.CExpressionElement;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.type.StandardType;
import org.sapid.checker.cx.wrapper.type.Type;
import org.sapid.checker.cx.wrapper.type.TypedefType;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;

/**
 * MISRA-C ルール 16
 * @author Nobuyuki UEHARA
 */
public class Misra16 implements CheckerClass {
	
    /** ルールのレベル */
    private final static int LEVEL = 1;

    /** ルールのメッセージ */
    private final static String MESSAGE = "MISRA-C Rule 16";

    
	@Override
	public List<Result> check(IFile file, CheckRule rule) {
		List<Result> violations = new ArrayList<Result>();
		
		CFileElement cFile = new CFileElement(file.getDOM());
		for (CExpressionElement expression : cFile.getExpressions()) {
			Result violation = this.check(expression);
			
			if (violation != null) {
				violations.add(violation);
			}
		}
		
		return violations;
	}

	private Result check(CExpressionElement expression) {
		Element operatorElement = expression.getFirstChildNode("op");
		if (operatorElement == null) {
			return null;
		}
		
		String operatorString = operatorElement.getTextContent();
		if (operatorString.equals("&=") || operatorString.equals("^=")
				|| operatorString.equals("|=") || operatorString.equals("<<=")
				|| operatorString.equals(">>=") || operatorString.equals("<<")
				|| operatorString.equals(">>") || operatorString.equals("~")
				|| operatorString.equals("^") || operatorString.equals("|")
				|| (operatorString.equals("&") && operatorElement.getPreviousSibling() != null)) {
			
			for (Element childExpressionElement : expression.getChildrenNode("Expr")) {
				CExpressionElement childExpression = new CExpressionElement(childExpressionElement);
				
				Type childExpressionType = childExpression.getTypeInfo();
				if (childExpressionType == null) {
					return null;
				}
				while (childExpressionType.getSort() == Type.Sort.TYPEDEF) {
					childExpressionType = ((TypedefType)childExpressionType).getTrueType();
				}
				
				if (childExpressionType.getSort() == Type.Sort.STANDARD) {
					StandardType.Sort sort = ((StandardType) childExpressionType).getType();
					
					if (sort == StandardType.Sort.FLOAT || sort == StandardType.Sort.DOUBLE) {
						return new Result(null, new NodeOffsetUtil(expression.getElem()).getRange(),
								Misra16.LEVEL, Misra16.MESSAGE);
					}
				}
			}
		}
		
		return null;
	}
	
}
