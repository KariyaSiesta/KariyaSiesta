/* 
 * Program:             $RCSfile: TestMisra005.java,v $  $Revision: 60.12 $
 * 
 *
 * Author:              S.Yamamoto  2010/06/23
 *
 * (C) Copyright:       S.Yamamoto  2010
 *                      This file is a product of the project Sapid.
 */

/* 
 * $Id: TestMisra005.java,v 60.12 2010/06/26 05:32:37 yamamoto Exp yamamoto $
 */

package org.sapid.checker.rule.misra;

import java.util.List;

import junit.framework.TestCase;

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.Result;

public class TestMisra005 extends TestMisra {
    public TestMisra005(String name) {
	super(name);
	RULE_ID = 5;
    }

    protected void setUp() throws Exception {
        setUp(RULE_ID);
    }

    public void testCheck() {
        CheckerClass misra = newMisraInstance(RULE_ID);
        List<Result> results = misra.check(file, null);

        Result [] r = {
            mkResult(4, 5, 0, 0, 102, 3),
            mkResult(5, 6, 0, 0, 133, 3),
            mkResult(6, 7, 0, 0, 164, 4),
            mkResult(8, 9, 0, 0, 197, 5),
        };
        checkResults(r, results);
    }
}
