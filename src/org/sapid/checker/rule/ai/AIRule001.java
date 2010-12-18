/*
 * Copyright(c) 2009 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.rule.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sapid.checker.ai.AIBuilder;
import org.sapid.checker.ai.AIDirector;
import org.sapid.checker.ai.AIEnvironment;
import org.sapid.checker.ai.AIFunction;
import org.sapid.checker.ai.concrete.Check0ConcreteBuilder;
import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.Result;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CFunctionElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;

/**
 * 0������å���Ԥ�
 * 
 * @author Eiji Hirumuta
 */
public class AIRule001 implements CheckerClass {
	/** �롼��Υ�٥� */
	private final static int LEVEL = 1;

	/** �롼��Υ�å����� */
	private final static String MESSAGE = "AI Rule 001";

	/** ������� */
	List<Result> results = new ArrayList<Result>();

	/** ��ȿ�Ȥ��Ƹ��Ф���Ρ��ɤν��� */
	Set<Element> problemNodes = new HashSet<Element>();

	/*
	 * �ե�����Υ롼������å����˸ƤФ��
	 * 
	 * @return results
	 */
	public List<Result> check(IFile file, CheckRule rule) {
		CFileElement cfile = new CFileElement(file.getDOM());
		CFunctionElement[] cfuncs = cfile.getFunctions();
		
		/* ��ݲ��ե졼������ɬ�פʼ�³�� */
		//AIBuilder builder = new ConcreteBuilder();
		AIBuilder builder = new Check0ConcreteBuilder();
		AIDirector director = new AIDirector(builder);
		AIFunction aifunc = null;
		AIEnvironment env = new AIEnvironment();
		HashMap<String, AIFunction> func_map = new HashMap<String, AIFunction>();
		
		// System.out.println("START");
		
		for (int i = 0; i < cfuncs.length; i++) {
			List<String> args = new ArrayList<String>();
			aifunc = director.construct(cfuncs[i]);
			// set args
			if (aifunc.getFuncid().equals("s33554433")) {
				args.add("0");
			} 
			aifunc.setArgs(args);
			func_map.put(aifunc.getFuncid(), aifunc);
		}

		// �ؿ��ơ��֥��Ķ�����Ͽ
		env.funcs = func_map;
		// System.out.println("Interpret-----");
		
		// ����¹�
		aifunc.interpret(env);
		
		// ɸ�����
		// env.printVars();
		
        Iterator<Element> it = env.result_set.iterator();
        while (it.hasNext()) {
        	problemNodes.add(it.next());
        }
		
		// System.out.println("END");
		
		// ��ʣ���륨����Ȥ� problemNodes ���ɲ�
		// problemNodes.add(res.getElem());

		/* ���з�̤��֤��ͤ��ɲ� */
		for (Iterator<Element> itr = problemNodes.iterator(); itr.hasNext();) {
			results.add(new Result(null, new NodeOffsetUtil(itr.next())
					.getRange(), LEVEL, MESSAGE));
		}
		return results;
	}

}