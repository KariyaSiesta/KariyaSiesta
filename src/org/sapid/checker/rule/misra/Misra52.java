/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.rule.misra;

import java.util.ArrayList;
import java.util.List;

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.Result;
import org.sapid.checker.cx.graph.path.PathGraph;
import org.sapid.checker.cx.wrapper.CDeclarationElement;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CFunctionElement;
import org.sapid.checker.cx.wrapper.CStatementElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;
import org.w3c.dom.Element;

/**
 * MISRA-C Rule 52
 * @author Toshinori OSUKA
 */
public class Misra52 implements CheckerClass {

    /** �롼��Υ�٥� */
    private final static int LEVEL = 1;

    /** �롼��Υ�å����� */
    private final static String MESSAGE = "MISRA-C Rule 52";

    /** ������� */
    List<Result> results = new ArrayList<Result>();

    public List<Result> check(IFile file, CheckRule rule) {
        CFileElement cfile = new CFileElement(file.getDOM());

        // �Ρ��ɤ�¸�ߤ��ʤ���Τ�õ��
        CFunctionElement[] functions = cfile.getFunctions();
        for (int i = 0; i < functions.length; i++) {
            PathGraph graph = new PathGraph(functions[i]);
            List<Element> instructions = new ArrayList<Element>();

            // ʸ
            CStatementElement[] stmts = functions[i].getStatments();
            for (int j = 0; j < stmts.length; j++) {
            	if (! stmts[j].isBlockStatement()) {
            		instructions.add(stmts[j].getElem());
            	}
            }
            // ���
            CDeclarationElement[] locals = functions[i]
                    .getVarialbeDeclarations();
            for (int j = 0; j < locals.length; j++) {
                instructions.add(locals[j].getElem());
            }
            // �����å�
            for (int j = 0; j < instructions.size(); j++) {
                if (graph.findNode(instructions.get(j)) != null) {
                    continue;
                }
                results.add(new Result(null, new NodeOffsetUtil(instructions
                        .get(j)).getRange(), LEVEL, MESSAGE));
            }
        }
        return results;
    }

}
