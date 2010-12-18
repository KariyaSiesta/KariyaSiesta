/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai.concrete;

import org.sapid.checker.ai.AIEnvironment;
import org.sapid.checker.ai.Value;

/**
 * リテラルを表すクラス
 * @author hirumuta
 *
 */
public class LiteralValue implements Value {
	private double d;
	
	public LiteralValue(double d) {
		this.d = d;
	}

	public Value getValue(AIEnvironment env) {
		return this;
	}
	
	public double getLiteral() {
		return this.d;
	}
	
	

}
