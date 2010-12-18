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
import org.sapid.checker.cx.wrapper.type.StandardType;
import org.sapid.checker.cx.wrapper.type.Type;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Document;

/**
 * {@link org.sapid.checker.cx.wrapper.CExpressionElement}をテストするためのルール。
 * @author Nobuyuki UEHARA
 */
public class TestGetTypeInfo2 implements CheckerClass {

    /** ルールのレベル */
    private final static int LEVEL = 1;

    /** ルールのメッセージ */
    private final static String MESSAGE = "TestGetTypeInfo2";
    
    
	@Override
	public List<Result> check(IFile file, CheckRule rule) {
		List<Result> violations = new ArrayList<Result>();
		
		Document document = file.getDOM();
		CFileElement cFile = new CFileElement(document);
		
		for (CExpressionElement expression : cFile.getExpressions()) {
			
			ViolationType violationType = this.check(expression);
			if (violationType != null) {
				violations.add(new Result(null, new NodeOffsetUtil(expression.getElem()).getRange(), TestGetTypeInfo2.LEVEL, TestGetTypeInfo2.MESSAGE + " " + violationType));
			}
		}
		
		return violations;
	}
	
	private ViolationType check(CExpressionElement expression) {
		Type type = expression.getTypeInfo();
		
		if (type == null) {
			return ViolationType.NULL;
		} else if (type instanceof StandardType && ((StandardType) type).getType() == StandardType.Sort.CHAR) {
			return ViolationType.CHAR;
		} else {
			return null;
		}
	}
	
	
	private static enum ViolationType {
		NULL,
		CHAR;
	}

}
