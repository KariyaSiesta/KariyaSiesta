/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai;

/**
 * @author hirumuta
 *
 */
public abstract class AIBlock implements NonterminalExpression {
	
	/**
	 * 
	 */
	public AIBlock() {
	}

	
	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.NonterminalExpression#interpret(org.sapid.checker.ai.AIEnvironment)
	 */
	public abstract Value interpret(AIEnvironment env);


}
