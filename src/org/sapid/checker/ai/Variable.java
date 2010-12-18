/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai;

/**
 * 終端要素のうち変数を表すインターフェース
 * @author hirumuta
 *
 */
public interface Variable extends Value {

	Value getValue(AIEnvironment env);
	
	String getDefid();
	void setDefid(String defid);
	
	String getName();
	void setName(String name);
	
}
