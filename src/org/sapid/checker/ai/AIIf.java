/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai;

/**
 * @author hirumuta
 * 
 */
public abstract class AIIf implements NonterminalExpression {

	/**
	 * 
	 */
	public AIIf(Value condition_value,
			NonterminalExpression true_expr, NonterminalExpression false_expr) {
		// TODO Auto-generated constructor stub
	}

	public abstract Value interpret(AIEnvironment env);

}
