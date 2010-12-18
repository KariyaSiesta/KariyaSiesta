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
import org.sapid.checker.cx.graph.path.PathGraph;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CFunctionElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;

/**
 * MISRA-C �롼�� �ƥ�ץ졼��
 * @author Eiji Hirumuta
 */
public class Misra00 implements CheckerClass {
    /** �롼��Υ�٥� */
    private final static int LEVEL = 1;

    /** �롼��Υ�å����� */
    private final static String MESSAGE = "MISRA-C Rule 00";

    /** ������� */
    List<Result> results = new ArrayList<Result>();

    /** ��ȿ�Ȥ��Ƹ��Ф���Ρ��ɤν��� */
    Set<Element> problemNodes = new HashSet<Element>();

    /*
     * �ե�����Υ롼������å����˸ƤФ��
     * @return results
     */
    public List<Result> check(IFile file, CheckRule rule) {
    	
        CFileElement cfile = new CFileElement(file.getDOM());
        System.out.println("PathGraph Test");
        CFunctionElement[] functions = cfile.getFunctions();
        //CStatementElement[] statements = cfile.getStatments();
        //CExpressionElement[] expressions = cfile.getExpressions();
        for (int i = 0; i < functions.length; i++) {
            System.out.println("******");
        	System.out.println(functions[i].getElem().getTextContent());
        	// �ѥ����Ϥ�Ԥ��Ȥ���ɬ��
            PathGraph graph = new PathGraph(functions[i]);
            
            //List<List<GraphNode<Element>>> paths = graph.toPathList();
            /*for (Iterator<List<GraphNode<Element>>> itr = paths.iterator(); itr
                    .hasNext();) {
                List<GraphNode<Element>> path = itr.next();
                checkPath(path);
            }*/
            graph.print();
            // ��ʣ���륨����Ȥ� problemNodes ���ɲ�
            //System.out.println(expressions[i].getElem().getTextContent());
    		problemNodes.add(functions[i].getElem());
        }
        /* ���з�̤��֤��ͤ��ɲ� */
        for (Iterator<Element> itr = problemNodes.iterator(); itr.hasNext();) {
            results.add(new Result(null, new NodeOffsetUtil(itr.next())
                    .getRange(), LEVEL, MESSAGE));
        }
        return results;
    }

    
    

    /*
     * �ѥ������å�
     * @param path
     
    private void checkPath(List<GraphNode<Element>> path) {
    	for (Iterator<GraphNode<Element>> itr = path.iterator(); itr.hasNext();) {
    		//GraphNode<Element> node = itr.next();
    		//System.out.println(node.getContent().getNodeName());
    		//System.out.println(node.getContent().getTextContent());
    	}
    }
    */

}