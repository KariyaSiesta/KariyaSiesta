/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai.concrete;

import org.sapid.checker.ai.AIEnvironment;
import org.sapid.checker.ai.AIPlus;
import org.sapid.checker.ai.Value;

/**
 * 具体値による足算を表現するクラス
 * @author hirumuta
 *
 */
public class ConcretePlus extends AIPlus {
	private Value left_value;
	private Value right_value;

	/**
	 * @param left_value
	 * @param right_value
	 */
	public ConcretePlus(Value left_value, Value right_value) {
		super(left_value, right_value);
		this.left_value = left_value;
		this.right_value = right_value;
		
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.AIPlus#interpret(org.sapid.checker.ai.AIEnvironment)
	 */
	@Override
	public Value interpret(AIEnvironment env) {
		// for left
		String left_classname = this.left_value.getClass().getSimpleName();
		if (left_classname.equals("ConcreteVariable")) {
			ConcreteVariable var = (ConcreteVariable) this.left_value;
			this.left_value = env.vars.get(var.getDefid());
		}
		// for right
		String right_classname = this.right_value.getClass().getSimpleName();
		if (right_classname.equals("ConcreteVariable")) {
			ConcreteVariable var = (ConcreteVariable) this.right_value;
			this.right_value = env.vars.get(var.getDefid());
		}
		
		LiteralValue literal_left = (LiteralValue) left_value.getValue(env);
		LiteralValue literal_right = (LiteralValue) right_value.getValue(env);
		
		double left = literal_left.getLiteral();
		double right = literal_right.getLiteral();
		
		double res = left + right;
		
		return new LiteralValue(res);
	}

}
