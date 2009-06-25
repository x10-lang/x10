/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Language.
 *
 */

package x10.constraint.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		//$JUnit-BEGIN$
		suite.addTestSuite(EntailmentTest.class);
		suite.addTestSuite(FormulaTest.class);
		suite.addTestSuite(EQVEntailmentTests.class);
		suite.addTestSuite(DisEqualsTests.class);
		//$JUnit-END$
		return suite;
	}

}
