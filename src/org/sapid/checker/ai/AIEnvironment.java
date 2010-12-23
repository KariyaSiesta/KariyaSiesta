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
 * 変数と関数の保持する環境を表わすクラス
 * 
 * @author hirumuta
 * 
 */
public class AIEnvironment {
	
	public AIEnvironment() {
		this.result_set = new HashSet<Element>();
	}
	
	// 関数の id と，AIFunction への参照
	public HashMap<String, AIFunction> funcs;

	// 変数の id と，Variable への参照
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
