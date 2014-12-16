package x10.cconstraints.test;

import junit.framework.Test;
import junit.framework.TestSuite;
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		//$JUnit-BEGIN$
		suite.addTestSuite(BindingTest.class);
		suite.addTestSuite(NestedExTest.class);
		suite.addTestSuite(QualifiedVarTests.class);
		//$JUnit-END$
		return suite;
	}

}
