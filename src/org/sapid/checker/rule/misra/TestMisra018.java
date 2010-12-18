/* 
 * Program:             $RCSfile: TestMisra018.java,v $  $Revision: 60.2 $
 * 
 *
 * Author:              S.Yamamoto  2010/06/23
 *
 * (C) Copyright:       S.Yamamoto  2010
 *                      This file is a product of the project Sapid.
 */

/* 
 * $Id: TestMisra018.java,v 60.2 2010/06/28 06:14:16 yamamoto Exp yamamoto $
 */

package org.sapid.checker.rule.misra;

import java.util.List;

import junit.framework.TestCase;

import org.sapid.checker.core.CheckerClass;
import org.sapid.checker.core.Result;

public class TestMisra018 extends TestMisra {
    public TestMisra018(String name) {
	super(name);
	RULE_ID = 18;
    }

    protected void setUp() throws Exception {
        setUp(RULE_ID);
    }

    public void testCheck() {
        CheckerClass misra = newMisraInstance(RULE_ID);
        List<Result> results = misra.check(file, null);

        Result [] r = {
            mkResult(-1, -2, -3, -4, -5, -6),
        };
        checkResults(r, results);
    }
}
