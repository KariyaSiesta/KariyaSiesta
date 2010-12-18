/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.ai;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.sapid.checker.ai.concrete.LiteralValue;
import org.w3c.dom.Element;

/**
 * �ѿ��ȴؿ����ݻ�����Ķ���ɽ�魯���饹
 * 
 * @author hirumuta
 * 
 */
public class AIEnvironment {
	
	public AIEnvironment() {
		this.result_set = new HashSet<Element>();
	}
	
	// �ؿ��� id �ȡ�AIFunction �ؤλ���
	public HashMap<String, AIFunction> funcs;

	// �ѿ��� id �ȡ�Variable �ؤλ���
	public HashMap<String, Variable> vars;
	
	public HashSet<Element> result_set;

	// for DEBUG in Concrete version
	public void printVars() {
		System.out.println("DEBUG PRINT START:");
		AIEnvironment env = new AIEnvironment();
		Iterator<String> it = vars.keySet().iterator();
		while (it.hasNext()) {
			Object o = it.next();
			System.out.println("id:" + o + " name:" + vars.get(o).getName()
					+ " value:"
					+ ((LiteralValue) vars.get(o).getValue(env)).getLiteral());
		}
		System.out.println("DEBUG PRINT END:");
	}

}
