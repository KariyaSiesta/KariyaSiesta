/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai.concrete;

import org.sapid.checker.ai.AIEnvironment;
import org.sapid.checker.ai.NonterminalExpression;
import org.sapid.checker.ai.Result;
import org.sapid.checker.ai.Value;

/**
 * @author hirumuta
 *
 */
public class ConcreteResult implements Result {
	private NonterminalExpression expr;

	/**
	 * 
	 */
	public ConcreteResult(NonterminalExpression expr) {
		this.expr = expr;

	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.Result#getValue()
	 */
	public Value getValue(AIEnvironment env) {
		return expr.interpret(env).getValue(env);
	}

}
