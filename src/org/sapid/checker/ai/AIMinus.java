/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai;

/**
 * 引算を表す抽象クラス．コンストラクタのインターフェースを定義する．
 * @author hirumuta
 *
 */
public abstract class AIMinus implements NonterminalExpression {
	
	public AIMinus(Value left_value,Value right_value) {
	}
	
	public abstract Value interpret(AIEnvironment env);

}
