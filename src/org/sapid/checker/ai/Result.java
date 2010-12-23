/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai;

/**
 * 非終端要素の計算結果を表すインターフェース
 * @author hirumuta
 *
 */
public interface Result extends Value {
	Value getValue(AIEnvironment env);
	
}
