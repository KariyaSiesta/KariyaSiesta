/*
 * Copyright(c) 2008 Nagoya University
 *  All Rights Reserved
 */
package org.sapid.checker.rule.misra;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.Result;
import org.sapid.checker.cx.wrapper.CDeclarationElement;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;

/**
 * MISRA-C Rule 17
 * @author Toshinori OSUKA
 */
public class Misra17 implements CheckerClass {
    private Set<String> typeNames = new HashSet<String>();
    private List<Result> results = new ArrayList<Result>();
    /** �롼��Υ�٥� */
    private final static int LEVEL = 1;

    /** �롼��Υ�å����� */
    private final static String MESSAGE = "MISRA-C Rule 17";

    public List<Result> check(IFile file, CheckRule rule) {
        CFileElement cfile = new CFileElement(file.getDOM());
        CDeclarationElement[] decls = cfile.getDeclarations();
        // 1-pass �ܤ� typedecf �μ����Ƚ�ʣ�����å�
        for (int i = 0; i < decls.length; i++) {
            if ("Typedecl".equals(decls[i].getNodeName())) {
                detect(decls[i]);
                typeNames.add(decls[i].getName());
            }
        }
        // 2-pass �ܤ��ѿ��Υ����å�
        // 2-pass ���ʤ����ѿ�����˽и����������н�Ǥ��ʤ�
        for (int i = 0; i < decls.length; i++) {
            if ("Global".equals(decls[i].getNodeName())
                    || "Local".equals(decls[i].getNodeName())) {
                detect(decls[i]);
            }
        }
        return results;
    }

    /**
     * ����
     * @param decl
     */
    private void detect(CDeclarationElement decl) {
        if (typeNames.contains(decl.getName())) {
            results.add(new Result(null, new NodeOffsetUtil(decl.getElem())
                    .getRange(), LEVEL, MESSAGE));
        }
    }
}
