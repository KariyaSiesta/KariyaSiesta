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
import org.sapid.checker.cx.wrapper.CElement;
import org.sapid.checker.cx.wrapper.CFileElement;
import org.sapid.checker.cx.wrapper.CGlobalElement;
import org.sapid.checker.cx.wrapper.CVariableReference;
import org.sapid.checker.rule.CheckRule;
import org.sapid.checker.rule.NodeOffsetUtil;

/**
 * MISRA-C ルール 22
 * @author Toshinori OSUKA
 */
public class Misra22 implements CheckerClass {
    /** ルールのレベル */
    private final static int LEVEL = 1;

    /** ルールのメッセージ */
    private final static String MESSAGE = "MISRA-C Rule 22";

    /** 検査結果 */
    List<Result> results = new ArrayList<Result>();

    public List<Result> check(IFile file, CheckRule rule) {

        CFileElement cfile = new CFileElement(file.getDOM());

        CGlobalElement[] globals = cfile.getGlobalDeclarations();
        for (int i = 0; i < globals.length; i++) {
            CGlobalElement global = globals[i];
            while (global != null) {
                CVariableReference[] references = globals[i].getReferences();
                Set<CElement> scopes = new HashSet<CElement>();
                for (int j = 0; j < references.length; j++) {
                    CElement scope = references[j].getScope();
                    scopes.add(scope);
                }
                // 1つの関数のみで参照されている場合は NG
                if (scopes.size() == 1) {
                    results.add(new Result(null, new NodeOffsetUtil(globals[i]
                            .getElem()).getRange(), LEVEL, MESSAGE));
                }
                global = (CGlobalElement) global.getNext();
            }
        }

        return results;
    }
}
