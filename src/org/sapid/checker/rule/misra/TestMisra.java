/* 
 * Program:             $RCSfile: TestMisra.java,v $  $Revision: 60.5 $
 * 
 *
 * Author:              S.Yamamoto  2010/06/23
 *
 * (C) Copyright:       S.Yamamoto  2010
 *                      This file is a product of the project Sapid.
 */

/* 
 * $Id: TestMisra.java,v 60.5 2010/06/28 06:14:14 yamamoto Exp yamamoto $
 */

package org.sapid.checker.rule.misra;

import java.util.List;

import junit.framework.TestCase;

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.IFile;
import org.sapid.checker.core.Result;
import org.sapid.checker.core.Range;
import org.sapid.checker.cx.CFile;

public class TestMisra extends TestCase {
    // Document    dom;
    IFile       file;
    int		RULE_ID;

    static final String MESSAGE = "MISRA-C Rule ";

    public TestMisra(String name) {
	super(name);
    }

    protected void setUp(int n) throws Exception {
        super.setUp();
        if (n < 10) {
            file = new CFile("./mc1_00" + n + ".c");
        } else if (n < 100) {
            file = new CFile("./mc1_0" + n + ".c");
        } else {
            file = new CFile("./mc1_" + n + ".c");
        }
        file.buildDOM();
        // dom = file.getDOM();
    }

    protected CheckerClass newMisraInstance(int n) {
        Class           c = null;
        CheckerClass    o = null;
        String          cname = "org.sapid.checker.rule.misra.Misra" + n;

        try {
            c = Class.forName(cname);
            o = (CheckerClass)c.newInstance();
        } catch (ClassNotFoundException e) {
            // Class is not found.
            System.err.println("TestMisra: ClassNotFoundException.");
            // System.exit(0);
        } catch (Exception e) {
            // InstantiationException or IllegalAccessException.
            System.err.println("TestMisra: InstantiationException or IllegalAccessException.");
            // System.exit(0);
        }
        return o;
    }

    Result mkResult(int startLine, int startColumn, int endLine, int endColumn,
		    int offset, int length, int level) {
	return new Result(null,
			  new Range(startLine, startColumn, endLine, endColumn,
				    offset, length),
			  level, MESSAGE + RULE_ID);
    }

    Result mkResult(int startLine, int startColumn, int endLine, int endColumn,
		    int offset, int length) {
	return mkResult(startLine, startColumn, endLine, endColumn,
			offset, length, 1);
    }

    void checkResults(Result [] r1, List<Result> r2) {
        assertEquals("Invliad number of results:", r1.length, r2.size());
	for (int i = 0; i < r1.length; i++) {
	    checkResult(r1[i], r2.get(i));
	}
    }

    void checkResult(Result r1, Result r2) {
	assertTrue("Invliad id:",
		   ((r1.getId() == null && r2.getId() == null) ||
		    (r1.getId() != null && r2.getId() != null &&
		     r1.getId().equals(r2.getId()))));
	assertEquals("Invliad level:", r1.getLevel(), r2.getLevel());
	assertEquals("Invliad message:", r1.getMessage(), r2.getMessage());
	checkRange(r1.getRange(), r2.getRange());
    }

    void checkRange(Range r1, Range r2) {
	assertEquals("Invalid start line:",
		     r1.getStartLine(), r2.getStartLine());
	assertEquals("Invalid start column:",
		     r1.getStartColumn(), r2.getStartColumn());
	assertEquals("Invalid end line:", r1.getEndLine(), r2.getEndLine());
	assertEquals("Invalid end column:",
		     r1.getEndColumn(), r2.getEndColumn());
	assertEquals("Invalid offset:", r1.getOffset(), r2.getOffset());
	assertEquals("Invalid length:", r1.getLength(), r2.getLength());
    }
}
