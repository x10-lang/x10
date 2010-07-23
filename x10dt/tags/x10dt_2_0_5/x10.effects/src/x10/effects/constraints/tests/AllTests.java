package x10.effects.constraints.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		//$JUnit-BEGIN$
		suite.addTestSuite(BaseTests.class);
		
		//$JUnit-END$
		return suite;
	}

}
