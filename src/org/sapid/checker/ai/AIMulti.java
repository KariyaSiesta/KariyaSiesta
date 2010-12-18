/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai;

/**
 * 掛算を表す抽象クラス．コンストラクタのインターフェースを定義する．
 * @author hirumuta
 *
 */
public abstract class AIMulti implements NonterminalExpression {
	
	public AIMulti(Value left_value,Value right_value) {
	}
	
	public abstract Value interpret(AIEnvironment env);

}
