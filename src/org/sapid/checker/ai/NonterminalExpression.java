/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai;

/**
 * Interpreter パターンにおける非終端要素を表すインターフェース
 * 
 * @author hirumuta
 * 
 */
public interface NonterminalExpression extends AbstractExpression {
	Value interpret(AIEnvironment env);

}
