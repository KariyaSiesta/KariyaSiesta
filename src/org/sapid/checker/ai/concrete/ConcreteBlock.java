/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai.concrete;

import org.sapid.checker.ai.AIBlock;
import org.sapid.checker.ai.AIEnvironment;
import org.sapid.checker.ai.NonterminalExpression;
import org.sapid.checker.ai.Value;

/**
 * @author hirumuta
 * 
 */
public class ConcreteBlock extends AIBlock {
	private NonterminalExpression[] exprs;

	/**
	 * @param exprs
	 */
	public ConcreteBlock(NonterminalExpression[] exprs) {
		super();
		this.exprs = exprs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sapid.checker.ai.AIFunction#interpret(org.sapid.checker.ai.AIEnvironment
	 * )
	 */
	@Override
	public Value interpret(AIEnvironment env) {
		if (this.exprs == null) {
			return null;
		}
		for (int i = 0; i < this.exprs.length; i++) {
			this.exprs[i].interpret(env);
		}
		return null;
	}

}
