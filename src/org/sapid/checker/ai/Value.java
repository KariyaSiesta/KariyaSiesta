/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai;

/**
 * Interpreter �ѥ�����ˤ����뽪ü���Ǥ�ɽ�魯���󥿡��ե�����
 * @author hirumuta
 *
 */
public interface Value extends AbstractExpression {
	Value getValue(AIEnvironment env);

}
