/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai;

/**
 * Interpreter �ѥ�����ˤ�������ü���Ǥ�ɽ�����󥿡��ե�����
 * 
 * @author hirumuta
 * 
 */
public interface NonterminalExpression extends AbstractExpression {
	Value interpret(AIEnvironment env);

}
