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
		TestSuite suite = new TestSuite(EntailmentTest.class);
		//$JUnit-BEGIN$
		//suite.addTest(new FormulaTest());
		//suite.addTest(new EntailmentTest());
		//$JUnit-END$
		return suite;
	}

}
