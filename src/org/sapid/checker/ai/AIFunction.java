/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai;

import java.util.List;


/**
 * @author hirumuta
 *
 */
public abstract class AIFunction extends AIBlock {

	/**
	 * @param exprs
	 */
	public AIFunction(String func_id, NonterminalExpression[] exprs, List<Variable> args) {
	}
	
	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.NonterminalExpression#interpret(org.sapid.checker.ai.AIEnvironment)
	 */
	public abstract Value interpret(AIEnvironment env);
	
	
	public abstract void setArgs(List<String> args);
	
	
	public abstract String getFuncid();

	
}
