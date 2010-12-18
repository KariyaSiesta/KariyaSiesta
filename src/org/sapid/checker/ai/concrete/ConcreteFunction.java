/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai.concrete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sapid.checker.ai.AIEnvironment;
import org.sapid.checker.ai.AIFunction;
import org.sapid.checker.ai.NonterminalExpression;
import org.sapid.checker.ai.Value;
import org.sapid.checker.ai.Variable;

/**
 * @author hirumuta
 * 
 */
public class ConcreteFunction extends AIFunction {
	private String func_id;
	private NonterminalExpression[] exprs;
	private List<ConcreteVariable> args;

	/**
	 * @param func_id
	 * @param exprs
	 */
	public ConcreteFunction(String func_id, NonterminalExpression[] exprs,
			List<Variable> args) {
		super(func_id, exprs, args);
		this.func_id = func_id;
		this.exprs = exprs;
		// TODO
		this.args = new ArrayList<ConcreteVariable>();
		for (int i = 0; i < args.size(); i++) {
			ConcreteVariable cvar = new ConcreteVariable(
					args.get(i).getDefid(), args.get(i).getName());
			this.args.add(cvar);
		}

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
		// set arguments to environment
		HashMap<String, Variable> vars_map = new HashMap<String, Variable>();
		for (ConcreteVariable cvar : this.args) {
			vars_map.put(cvar.getDefid(), cvar);
		}
		env.vars = vars_map;

		if (this.exprs == null) {
			return null;
		}

		for (int i = 0; i < this.exprs.length; i++) {
			this.exprs[i].interpret(env);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sapid.checker.ai.AIFunction#setArgs(java.util.List)
	 */
	@Override
	public void setArgs(List<String> args) throws RuntimeException {
		if (this.args.size() != args.size()) {
			throw new RuntimeException(); //TODO
		}
		for (int i = 0; i < this.args.size(); i++) {
			this.args.get(i).setValue(
					new LiteralValue(Double.parseDouble(args.get(i))));
		}

	}

	public String getFuncid() {
		return this.func_id;
	}

}
