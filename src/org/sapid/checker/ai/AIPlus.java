/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai;

/**
 * 足算を表す抽象クラス．コンストラクタのインターフェースを定義する．
 * @author hirumuta
 *
 */
public abstract class AIPlus implements NonterminalExpression {
	
	public AIPlus(Value left_value,Value right_value) {
	}
	
	public abstract Value interpret(AIEnvironment env);

}
