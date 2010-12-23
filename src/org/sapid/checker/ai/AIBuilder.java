/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai;

import java.util.List;

import org.w3c.dom.Element;

/**
 * Builder パターンにおける Builder クラスを表すインターフェース
 * @author hirumuta
 *
 */
public interface AIBuilder {
	// arithmetic operator
	NonterminalExpression getAIPlus(Value left_value, Value right_value, Element elem);
	NonterminalExpression getAIMinus(Value left_value, Value right_value, Element elem);
	NonterminalExpression getAIMulti(Value left_value, Value right_value, Element elem);
	NonterminalExpression getAIDivide(Value left_value, Value right_value, Element elem);
	NonterminalExpression getAIRemainder(Value left_value, Value right_value, Element elem);
	
	// comparative operator
	NonterminalExpression getAIEqual(Value left_value, Value right_value, Element elem);
	NonterminalExpression getAIGreater(Value left_value, Value right_value, Element elem);
	NonterminalExpression getAILesser(Value left_value, Value right_value, Element elem);
	
	// logical operator
	NonterminalExpression getAINot(Value value, Element elem);
	NonterminalExpression getAILogicalAND(Value left_value, Value right_value, Element elem);
	NonterminalExpression getAILogicalOR(Value left_value, Value right_value, Element elem);
	
	// assignment operator
	NonterminalExpression getAIAssign(Value left_value, Value right_value, Element elem);
	
	// control structure
	NonterminalExpression getAIIf(Value condition_value,
			NonterminalExpression true_expr, NonterminalExpression false_expr, Element elem);
	NonterminalExpression getAIFor(); // TODO
	NonterminalExpression getAIWhile(); // TODO
	NonterminalExpression getAIDoWhile(); // TODO
	NonterminalExpression getAISwitch(); // TODO
	
	AIFunction getAIFunction(String func_id, NonterminalExpression[] exprs, List<Variable> args, Element elem);
	AIBlock getAIBlock(NonterminalExpression[] exprs, Element elem);
	
	Value getAIValue(double d);
	Value getAIVariable(String defid, String name);
	Value getAIResult(NonterminalExpression expr);

}
