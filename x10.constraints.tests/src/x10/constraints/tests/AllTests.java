/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.constraints.tests;

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
