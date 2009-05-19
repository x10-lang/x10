package x10.constraint.tests;

//import polyglot.ext.x10.types.X10TypeMixin;
import junit.framework.TestCase;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XName;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.constraint.XRef_c;
import x10.constraint.*;

public class DisEqualsTests extends TestCase {
	public DisEqualsTests() {
		super("EntailmentTest");
	}
	XTerm zero = XTerms.makeLit(new Integer(0));
	XTerm one = XTerms.makeLit(new Integer(1));
	XTerm two = XTerms.makeLit(new Integer(2));
	XVar v0 = XTerms.makeLocal(XTerms.makeName("v0"));
	XVar v1 = XTerms.makeLocal(XTerms.makeName("v1"));
	XVar v2 = XTerms.makeLocal(XTerms.makeName("v2"));
	
	/**
	 * Test v0 != v1 |- v0 != v1
	 * @throws Throwable
	 */
	public void test1() throws Throwable {
		XConstraint c = new XConstraint_c();
		c.addDisBinding(v0, v1);
		//System.out.println("c=" + c);
		boolean b = c.disEntails(v0, v1);
		assertTrue(b);
	}
	
	/**
	 * Test  |/- v0 != v1
	 * @throws Throwable
	 */
	public void test2() throws Throwable {
		XConstraint c = new XConstraint_c();
		boolean b = c.disEntails(v0,v1);
		assertFalse(b);
	}
	
	/**
	 * Test {} |/- v0 != v1
	 * @throws Throwable
	 */
	public void test3() throws Throwable {
		XConstraint c = new XConstraint_c();
		
		XConstraint d = new XConstraint_c();
		d.addDisBinding(v0, v1);
		boolean b = c.entails(v0,v1);
		assertFalse(b);
	}
	
	/**
	 * v0 != v1, v1=v2 |- v0 !=v2
	 * @throws Throwable
	 */
	public void test4() throws Throwable {
		XConstraint c = new XConstraint_c();
		c.addDisBinding(v0, v1);
		c.addBinding(v1, v2);
	
		XConstraint d = new XConstraint_c();
		d.addDisBinding(v0, v2);
		boolean b = c.entails(d);
		assertTrue(b);
	}
	
	/**
	 * v0 != v1, v1 = v2 |- v1 !=v2
	 * @throws Throwable
	 */
	public void test5() throws Throwable {
		XConstraint c = new XConstraint_c();
		c.addDisBinding(v0, v1);
		c.addBinding(v1, v2);
	
		XConstraint d = new XConstraint_c();
		d.addDisBinding(v1, v2);
		boolean b = c.entails(d);
		assertFalse(b);
	}
	
	/**
	 * v0 != v1, v1 != v2 |- v0 !=v2
	 * @throws Throwable
	 */
	public void test6() throws Throwable {
		XConstraint c = new XConstraint_c();
		c.addDisBinding(v0, v1);
		c.addDisBinding(v1, v2);
	
		XConstraint d = new XConstraint_c();
		d.addDisBinding(v0, v2);
		boolean b = c.entails(d);
		assertFalse(b);
	}



	
}
