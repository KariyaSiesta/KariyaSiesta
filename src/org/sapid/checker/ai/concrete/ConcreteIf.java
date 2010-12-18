/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai.concrete;

import org.sapid.checker.ai.AIEnvironment;
import org.sapid.checker.ai.AIIf;
import org.sapid.checker.ai.NonterminalExpression;
import org.sapid.checker.ai.Value;

/**
 * @author hirumuta
 * 
 */
public class ConcreteIf extends AIIf {
	private Value condition_value;
	private NonterminalExpression true_expr;
	private NonterminalExpression false_expr;

	/**
	 * @param condition_expr
	 * @param true_expr
	 * @param false_expr
	 */
	public ConcreteIf(Value condition_value,
			NonterminalExpression true_expr, NonterminalExpression false_expr) {
		super(condition_value, true_expr, false_expr);
		this.condition_value = condition_value;
		this.true_expr = true_expr;
		this.false_expr = false_expr;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sapid.checker.ai.AIIf#interpret(org.sapid.checker.ai.AIEnvironment)
	 */
	@Override
	public Value interpret(AIEnvironment env) {
		// for condition_expr
		ConcreteVariable cvar = (ConcreteVariable) this.condition_value;
		if (env.vars.containsKey(cvar.getDefid())) {
			cvar = (ConcreteVariable) env.vars.get(cvar.getDefid());
		}
		
		LiteralValue literalvalue = (LiteralValue) cvar.getValue(env);
		Double d = literalvalue.getLiteral();
		
		Value res = null;
		
		if (false_expr == null) {
			if (d != 0.0) {
				res = this.true_expr.interpret(env);
			}			
		} else {
			if (d != 0.0) {
				res = this.true_expr.interpret(env);
			} else {
				res = this.false_expr.interpret(env);
			}
		}
		
		//TODO
		return res;
	}

}
