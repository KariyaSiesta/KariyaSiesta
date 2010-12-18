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
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CFunctionElement;
import org.sapid.checker.cx.wrapper.CParameterElement;
import org.sapid.checker.cx.wrapper.CPrototypeElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * MISRA-C �롼�� �ƥ�ץ졼��
 * @author Eiji Hirumuta
 */
public class Misra72 implements CheckerClass {
    /** �롼��Υ�٥� */
    private final static int LEVEL = 1;

    /** �롼��Υ�å����� */
    private final static String MESSAGE = "MISRA-C Rule 72";

    /** ������� */
    List<Result> results = new ArrayList<Result>();

    /** ��ȿ�Ȥ��Ƹ��Ф���Ρ��ɤν��� */
    Set<Element> problemNodes = new HashSet<Element>();

    /*
     * �ե�����Υ롼������å����˸ƤФ��
     * @return results
     */
    public List<Result> check(IFile file, CheckRule rule) {
        List<Element> prototypeList = new ArrayList<Element>();
        NodeList nodeList =  file.getDOM().getElementsByTagName("Prototype");
        for(int i = 0; i < nodeList.getLength(); i++) {
            prototypeList.add((Element) nodeList.item(i));
        }

        for (Element element : prototypeList) {
        	// prototype�����������η������
        	String protoType = "";
        	List<String> protoparamsType = new ArrayList<String>();
        	CPrototypeElement prototype = new CPrototypeElement(element);
        	Element[] params = prototype.getChildrenNode("Param");
        	for (int j = 0; j < params.length; j++) {
				CParameterElement parameter = new CParameterElement(params[j]);
				protoType = prototype.getType();
				protoparamsType.add(parameter.getType());
			}

        	// prototype�����
        	String defType = ((CFunctionElement)prototype.getDefinition()).getType();
        	List<String> defparamsType = new ArrayList<String>();
        	CParameterElement[] paramsdef = ((CFunctionElement)prototype.getDefinition()).getParams();
        	for (int j = 0; j < paramsdef.length; j++) {
        		defparamsType.add(paramsdef[j].getType());
			}
        	
        	// �����ְ�äƤ��ʤ���Ĵ�٤�
        	if (!(protoType.equals(defType))) {
        		problemNodes.add(prototype.getElem());
        	}
        	for (int j = 0; j < defparamsType.size(); j++) {
            	if (!(defparamsType.get(j).equals(protoparamsType.get(j)))) {
            		problemNodes.add(prototype.getElem());
            	}
			}
        }
        
        for (Iterator<Element> itr = problemNodes.iterator(); itr.hasNext();) {
            results.add(new Result(null, new NodeOffsetUtil(itr.next())
                    .getRange(), LEVEL, MESSAGE));
        }
        return results;
    }
    
}