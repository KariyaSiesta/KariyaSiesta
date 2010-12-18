/* 
 * Program:             $RCSfile: TestMisraAll.java,v $  $Revision: 1.4 $
 * 
 *
 * Author:              S.Yamamoto  2010/06/23
 *
 * (C) Copyright:       S.Yamamoto  2010
 *                      This file is a product of the project Sapid.
 */

/* 
 * $Id: TestMisraAll.java,v 1.4 2010/06/26 06:30:21 yamamoto Exp yamamoto $
 */

package org.sapid.checker.rule.misra;

import junit.framework.*;

import org.sapid.checker.rule.misra.*;

public class TestMisraAll extends TestMisra {
    TestMisraAll(String name) {
	super(name);
    }

    protected void setUp(String methodName) throws Exception {
	setUp(methodName);
    }

    public static Test suite() {
	TestSuite suite = new TestSuite();

        suite.addTestSuite(TestMisra005.class);
        suite.addTestSuite(TestMisra012.class);
        suite.addTestSuite(TestMisra016.class);
        suite.addTestSuite(TestMisra017.class);
        suite.addTestSuite(TestMisra018.class);
        suite.addTestSuite(TestMisra022.class);
        suite.addTestSuite(TestMisra024.class);
        suite.addTestSuite(TestMisra030.class);
        suite.addTestSuite(TestMisra032.class);
        suite.addTestSuite(TestMisra037.class);
        suite.addTestSuite(TestMisra047.class);
        suite.addTestSuite(TestMisra052.class);
        suite.addTestSuite(TestMisra053.class);
        suite.addTestSuite(TestMisra066.class);
        suite.addTestSuite(TestMisra067.class);
        suite.addTestSuite(TestMisra072.class);
        suite.addTestSuite(TestMisra074.class);
        suite.addTestSuite(TestMisra078.class);
        suite.addTestSuite(TestMisra079.class);
        suite.addTestSuite(TestMisra080.class);
        suite.addTestSuite(TestMisra081.class);
        suite.addTestSuite(TestMisra082.class);
        suite.addTestSuite(TestMisra083.class);
        suite.addTestSuite(TestMisra088.class);
        suite.addTestSuite(TestMisra096.class);
        suite.addTestSuite(TestMisra108.class);
        suite.addTestSuite(TestMisra123.class);

	return suite;
    }
}
