/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai;

/**
 * Interpreter パターンにおける終端要素を表わすインターフェース
 * @author hirumuta
 *
 */
public interface Value extends AbstractExpression {
	Value getValue(AIEnvironment env);

}
