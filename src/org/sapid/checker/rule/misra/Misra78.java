/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.rule.misra;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.Result;
import org.sapid.checker.cx.wrapper.CExpressionElement;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CPrototypeElement;
import org.sapid.checker.cx.wrapper.CVariableReference;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * MISRA-C �롼�� 78 �ؿ��˰����Ϥ��������ο��ϴؿ��ץ�ȥ����פȰ��פ��Ƥ��ʤ���Фʤ�ʤ�
 * @author Eiji Hirumuta
 */
public class Misra78 implements CheckerClass {
	/** �롼��Υ�٥� */
	private final static int LEVEL = 1;

	/** �롼��Υ�å����� */
	private final static String MESSAGE = "MISRA-C Rule 78";

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
		
		/* Get number of parameters that function call have */
		CExpressionElement[] e = cfile.getExpressions();
		Set<Node> haveCheckedNodeSet = new HashSet<Node>();
		for (int j = 0; j < e.length; j++) {
		    CVariableReference varRef = new CVariableReference(e[j].getElem());
		    if (varRef.isFunction()) {
		        if (haveCheckedNodeSet.contains(varRef.getElem().getParentNode())) {
                    continue;
                }
	            haveCheckedNodeSet.add(varRef.getElem().getParentNode());

	            /* params is number of parameters that function call have */
		        int params = 0;
		        Element tmp;
		        tmp = varRef.getElem();
		        
		        while ((tmp = (Element) tmp.getNextSibling()) != null) {
		            String nodename = tmp.getNodeName();
		            if ((nodename != "op")&&(nodename != "sp")) {
		                params++;
		            }
		        }
		        
		        /* Compare params with number of parameters that Prototype definition have */
		        if (!(params == getPrototypeParameterNumber(file.getDOM(), varRef.getDefinitionId()))) {
		            problemNodes.add(varRef.getElem());
		        }
		    }
		}
		
		// problemNodes.add(expressions[i].getElem());
		for (Iterator<Element> itr = problemNodes.iterator(); itr.hasNext();) {
			results.add(new Result(null, new NodeOffsetUtil(itr.next())
					.getRange(), LEVEL, MESSAGE));
		}
		return results;
	}

	/*
	 * Compare id with Prototype definition
	 * @return number of parameters that prototype definition have  
	 */
	private int getPrototypeParameterNumber(Document dom, String id) {
		int protoparams = 0;
		List<Element> prototypeList = new ArrayList<Element>();
		NodeList nodeList = dom.getElementsByTagName("Prototype");
		for(int i = 0; i < nodeList.getLength(); i++) {
		    prototypeList.add((Element) nodeList.item(i));
		}
		for (Element element : prototypeList) {
			CPrototypeElement prototype = new CPrototypeElement(element);
			
			if (id.equals(prototype.getElem().getAttribute("defid"))) {
				Element[] paramElements = prototype.getChildrenNode("Param");
				protoparams =  paramElements.length;
			}
		}
		return protoparams;
	}

}