/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.constraints.tests;

//import polyglot.ext.x10.types.X10TypeMixin;
import junit.framework.TestCase;
import x10.constraint.XConstraint;
import x10.constraint.XField;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;

public class DisEqualsTests extends TestCase {
	public DisEqualsTests() {
		super("EntailmentTest");
	}
	XTerm zero = XTerms.makeLit(new Integer(0));
	XTerm one = XTerms.makeLit(new Integer(1));
	XTerm two = XTerms.makeLit(new Integer(2));
	XTerm NULL = XTerms.makeLit(null);
	XVar v0 = XTerms.makeUQV();
	XVar v1 = XTerms.makeUQV();
	XVar v2 = XTerms.makeUQV();
	XVar v3 = XTerms.makeUQV();
	XVar v4 = XTerms.makeUQV();
	XVar v5 = XTerms.makeUQV();
	
	/**
	 * Test v0 != v1 |- v0 != v1
	 * @throws Throwable
	 */
	public void test1() throws Throwable {
		XConstraint c = new XConstraint();
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
		XConstraint c = new XConstraint();
		boolean b = c.disEntails(v0,v1);
		assertFalse(b);
	}
	
	/**
	 * Test {} |/- v0 != v1
	 * @throws Throwable
	 */
	public void test3() throws Throwable {
		XConstraint c = new XConstraint();
		
		XConstraint d = new XConstraint();
		d.addDisBinding(v0, v1);
		boolean b = c.entails(v0,v1);
		assertFalse(b);
	}
	
	/**
	 * v0 != v1, v1=v2 |- v0 !=v2
	 * @throws Throwable
	 */
	public void test4() throws Throwable {
		XConstraint c = new XConstraint();
		c.addDisBinding(v0, v1);
		c.addBinding(v1, v2);
	
		XConstraint d = new XConstraint();
		d.addDisBinding(v0, v2);
		boolean b = c.entails(d);
		assertTrue(b);
	}
	
	/**
	 * v0 != v1, v1 = v2 |- v1 !=v2
	 * @throws Throwable
	 */
	public void test5() throws Throwable {
		XConstraint c = new XConstraint();
		c.addDisBinding(v0, v1);
		c.addBinding(v1, v2);
	
		XConstraint d = new XConstraint();
		d.addDisBinding(v1, v2);
		boolean b = c.entails(d);
		assertFalse(b);
	}
	
	/**
	 * v0 != v1, v1 != v2 |- v0 !=v2
	 * @throws Throwable
	 */
	public void test6() throws Throwable {
		XConstraint c = new XConstraint();
		c.addDisBinding(v0, v1);
		c.addDisBinding(v1, v2);
	
		XConstraint d = new XConstraint();
		d.addDisBinding(v0, v2);
		boolean b = c.entails(d);
		assertFalse(b);
	}
	
	/**
	 * v0 = v1, v1 != v2, v2=v3, v3=v4|- v0 !=v4
	 * @throws Throwable
	 */
	public void test7() throws Throwable {
		XConstraint c = new XConstraint();
		c.addBinding(v0, v1);
		c.addDisBinding(v1, v2);
		c.addBinding(v2, v3);
		c.addBinding(v3, v4);
	
		
		boolean b = c.disEntails(v0,v2);
		assertTrue(b);
	}
	// |- 0 != 1
	public void test8() throws Throwable {
		XConstraint c = new XConstraint();
		boolean b = c.disEntails(zero,one);
		assertTrue(b);
	}
	
	// |/- 0 != 0
	public void test9() throws Throwable {
		XConstraint c = new XConstraint();
		boolean b = c.disEntails(zero,zero);
		assertFalse(b);
	}
	
	//  v != null |- v != null
	public void test10() throws Throwable {
		XConstraint c = new XConstraint();
		c.addDisBinding(v0,NULL);
		
		boolean b = c.disEntails(v0, NULL);
		assertTrue(b);
	}

	//  v0 == v1, v0 != v1 |- v2==v3
	public void test11() throws Throwable {

		XConstraint c = new XConstraint();
		c.addDisBinding(v0,v1);
		c.addBinding(v0, v1);
		assertFalse(c.consistent());

	}
	/**
	 * v0.f != v1.f |- v0 != v1
	 * @throws Throwable
	 */
	public void test12() throws Throwable {
		XConstraint c = new XConstraint();
		Object field = new Object();
		XField<Object> f = new XField<Object>(v0, field);
		XField<Object> g = new XField<Object>(v1, field);
		c.addDisBinding(f,g);
		boolean result = c.disEntails(v0, v1);
		assertTrue(result);

	}
	/**
	 * v0.f == 0, v1.f == 1 |- v0 != v1
	 * @throws Throwable
	 */
	public void test13() throws Throwable {
		XConstraint c = new XConstraint();
		XField<Object> f = new XField<Object>(v0, "0");
		XField<Object> g = new XField<Object>(v1, "0");
		c.addBinding(f,XTerms.makeLit(0));
		c.addBinding(g,XTerms.makeLit(1));
		
		boolean result = c.disEntails(v0, v1);
		assertTrue(result);

	}



	
}
