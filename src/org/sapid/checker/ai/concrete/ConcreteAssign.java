/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai.concrete;

import org.sapid.checker.ai.AIEnvironment;
import org.sapid.checker.ai.AIPlus;
import org.sapid.checker.ai.Value;

/**
 * 具体値による代入を表現するクラス
 * @author hirumuta
 *
 */
public class ConcreteAssign extends AIPlus {
	private Value left_value;
	private Value right_value;

	/**
	 * @param left_value
	 * @param right_value
	 */
	public ConcreteAssign(Value left_value, Value right_value) {
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
		ConcreteVariable variable_left = (ConcreteVariable) left_value;
		// for right
		String right_classname = this.right_value.getClass().getSimpleName();
		if (right_classname.equals("ConcreteVariable")) {
			ConcreteVariable var = (ConcreteVariable) this.right_value;
			this.right_value = env.vars.get(var.getDefid());
		}
		
		LiteralValue literal_right = (LiteralValue) right_value.getValue(env);
		
		// set to environment
		if (env.vars.containsKey(variable_left.getDefid())) {
			ConcreteVariable res = (ConcreteVariable) env.vars.get(variable_left.getDefid());
			res.setValue(literal_right);
		} else {
			variable_left.setValue(literal_right);
			env.vars.put(variable_left.getDefid(), variable_left);
		}
		
		return null;
	}

}
