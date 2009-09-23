package x10.constraint.tests;

import junit.framework.TestCase;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;

public class CyclicTest extends TestCase {
	public CyclicTest() {
		super("CyclicTest");
	}
	XTerm zero = XTerms.makeLit(new Integer(0));
	XTerm one = XTerms.makeLit(new Integer(1));
	XTerm two = XTerms.makeLit(new Integer(2));
	XVar v0 = XTerms.makeLocal(XTerms.makeName("v0"));
	XVar v1 = XTerms.makeLocal(XTerms.makeName("v1"));
	XVar v2 = XTerms.makeLocal(XTerms.makeName("v2"));
	XVar v0a = XTerms.makeField(v0, XTerms.makeName("a"));
	XVar v1b = XTerms.makeField(v1, XTerms.makeName("b"));
	
	/**
	 * v0=v1,v1=v2 |- v0=v2
	 * @throws Throwable
	 */
	public void test1() throws Throwable {
		XConstraint c = new XConstraint_c();
		c.addBinding(v0a, v1);
		c.addBinding(v1b, v0);
		System.out.println("c is|" + c+ "|");
		assertTrue(c.consistent());
	}
}