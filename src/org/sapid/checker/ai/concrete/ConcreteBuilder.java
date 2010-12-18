/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai.concrete;

import java.util.List;

import org.sapid.checker.ai.AIBlock;
import org.sapid.checker.ai.AIBuilder;
import org.sapid.checker.ai.AIFunction;
import org.sapid.checker.ai.NonterminalExpression;
import org.sapid.checker.ai.Value;
import org.sapid.checker.ai.Variable;
import org.w3c.dom.Element;

/**
 * @author hirumuta
 *
 */
public class ConcreteBuilder implements AIBuilder {

	/**
	 * 
	 */
	public ConcreteBuilder() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAIAssign(org.sapid.checker.ai.Value, org.sapid.checker.ai.Value)
	 */
	public NonterminalExpression getAIAssign(Value left_value, Value right_value, Element elem) {
		return new ConcreteAssign(left_value, right_value);
	}
	
	public AIBlock getAIBlock(NonterminalExpression[] exprs, Element elem) {
		return new ConcreteBlock(exprs);
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAIDivide(org.sapid.checker.ai.Value, org.sapid.checker.ai.Value)
	 */
	public NonterminalExpression getAIDivide(Value left_value, Value right_value, Element elem) {
		return new ConcreteDivide(left_value, right_value);
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAIDoWhile()
	 */
	public NonterminalExpression getAIDoWhile() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAIEqual(org.sapid.checker.ai.Value, org.sapid.checker.ai.Value)
	 */
	public NonterminalExpression getAIEqual(Value left_value, Value right_value, Element elem) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAIFor()
	 */
	public NonterminalExpression getAIFor() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAIFunction(java.lang.String, org.sapid.checker.ai.NonterminalExpression[], java.util.List)
	 */
	public AIFunction getAIFunction(String func_id,
			NonterminalExpression[] exprs, List<Variable> args, Element elem) {
		return new ConcreteFunction(func_id, exprs, args);
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAIGreater(org.sapid.checker.ai.Value, org.sapid.checker.ai.Value)
	 */
	public NonterminalExpression getAIGreater(Value left_value,
			Value right_value, Element elem) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAIIf(org.sapid.checker.ai.NonterminalExpression, org.sapid.checker.ai.NonterminalExpression, org.sapid.checker.ai.NonterminalExpression)
	 */
	public NonterminalExpression getAIIf(Value condition_value,
			NonterminalExpression true_expr, NonterminalExpression false_expr, Element elem) {
		return new ConcreteIf(condition_value, true_expr, false_expr);
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAILesser(org.sapid.checker.ai.Value, org.sapid.checker.ai.Value)
	 */
	public NonterminalExpression getAILesser(Value left_value, Value right_value, Element elem) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAILogicalAND(org.sapid.checker.ai.Value, org.sapid.checker.ai.Value)
	 */
	public NonterminalExpression getAILogicalAND(Value left_value,
			Value right_value, Element elem) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAILogicalOR(org.sapid.checker.ai.Value, org.sapid.checker.ai.Value)
	 */
	public NonterminalExpression getAILogicalOR(Value left_value,
			Value right_value, Element elem) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAIMinus(org.sapid.checker.ai.Value, org.sapid.checker.ai.Value)
	 */
	public NonterminalExpression getAIMinus(Value left_value, Value right_value, Element elem) {
		return new ConcreteMinus(left_value, right_value);
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAIMulti(org.sapid.checker.ai.Value, org.sapid.checker.ai.Value)
	 */
	public NonterminalExpression getAIMulti(Value left_value, Value right_value, Element elem) {
		return new ConcreteMulti(left_value, right_value);
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAINot(org.sapid.checker.ai.Value)
	 */
	public NonterminalExpression getAINot(Value value, Element elem) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAIPlus(org.sapid.checker.ai.Value, org.sapid.checker.ai.Value)
	 */
	public NonterminalExpression getAIPlus(Value left_value, Value right_value, Element elem) {
		return new ConcretePlus(left_value, right_value);
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAIRemainder(org.sapid.checker.ai.Value, org.sapid.checker.ai.Value)
	 */
	public NonterminalExpression getAIRemainder(Value left_value,
			Value right_value, Element elem) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAIResult(org.sapid.checker.ai.NonterminalExpression)
	 */
	public Value getAIResult(NonterminalExpression expr) {
		return new ConcreteResult(expr);
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAISwitch()
	 */
	public NonterminalExpression getAISwitch() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAIValue(double)
	 */
	public Value getAIValue(double d) {
		return new LiteralValue(d);
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAIVariable(java.lang.String, java.lang.String)
	 */
	public Value getAIVariable(String defid, String name) {
		return new ConcreteVariable(defid, name);
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIBuilder#getAIWhile()
	 */
	public NonterminalExpression getAIWhile() {
		// TODO Auto-generated method stub
		return null;
	}

}
