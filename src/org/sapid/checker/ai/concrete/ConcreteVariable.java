/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai.concrete;

import org.sapid.checker.ai.AIEnvironment;
import org.sapid.checker.ai.Value;
import org.sapid.checker.ai.Variable;

/**
 * 具体値の変数を表現するクラス
 * @author hirumuta
 *
 */
public class ConcreteVariable implements Variable {
	private String defid;
	private String name;
	private LiteralValue value;

	/**
	 * 
	 */
	public ConcreteVariable(String defid, String name) {
		this.defid = defid;
		this.name = name;

	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.Variable#getDefid()
	 */
	public String getDefid() {
		return this.defid;
		
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.Variable#getName()
	 */
	public String getName() {
		return this.name;
		
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.Variable#getValue()
	 */
	public Value getValue(AIEnvironment env) {
		return this.value;
		
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.Variable#setDefid(java.lang.String)
	 */
	public void setDefid(String defid) {
		this.defid = defid;
		
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.Variable#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
		
	}

	/* (non-Javadoc)
	 * @see org.sapid.checker.ai.Variable#setValue(org.sapid.chechker.ai.Value)
	 */
	public void setValue(LiteralValue value) {
		this.value = value;
		
	}

}
