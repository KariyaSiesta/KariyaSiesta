/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai;

/**
 * ��ü���Ǥη׻���̤�ɽ�����󥿡��ե�����
 * @author hirumuta
 *
 */
public interface Result extends Value {
	Value getValue(AIEnvironment env);
	
}
