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
import x10.constraint.XConstraint;
import x10.constraint.XField;
import x10.constraint.XStringDef;

public class DisEqualsTests extends BaseTest {
	public DisEqualsTests() {
		super("EntailmentTest");
	}
	
	/**
	 * Test v0 != v1 |- v0 != v1
	 * @throws Throwable
	 */
	public void test1() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		c.addDisEquality(v0, v1);
		//System.out.println("c=" + c);
		boolean b = c.entailsDisEquality(v0, v1);
		assertTrue(b);
	}
	
	/**
	 * Test  |/- v0 != v1
	 * @throws Throwable
	 */
	public void test2() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		boolean b = c.entailsDisEquality(v0,v1);
		assertFalse(b);
	}
	
	/**
	 * Test {} |/- v0 != v1
	 * @throws Throwable
	 */
	public void test3() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		
		XConstraint<TestType> d = xcs.makeConstraint(ts);
		d.addDisEquality(v0, v1);
		boolean b = c.entailsEquality(v0,v1);
		assertFalse(b);
	}
	
	/**
	 * v0 != v1, v1=v2 |- v0 !=v2
	 * @throws Throwable
	 */
	public void test4() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		c.addDisEquality(v0, v1);
		c.addEquality(v1, v2);
	
		XConstraint<TestType> d = xcs.makeConstraint(ts);
		d.addDisEquality(v0, v2);
		boolean b = c.entails(d);
		assertTrue(b);
	}
	
	/**
	 * v0 != v1, v1 = v2 |- v1 !=v2
	 * @throws Throwable
	 */
	public void test5() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		c.addDisEquality(v0, v1);
		c.addEquality(v1, v2);
	
		XConstraint<TestType> d = xcs.makeConstraint(ts);
		d.addDisEquality(v1, v2);
		boolean b = c.entails(d);
		assertFalse(b);
	}
	
	/**
	 * v0 != v1, v1 != v2 |- v0 !=v2
	 * @throws Throwable
	 */
	public void test6() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		c.addDisEquality(v0, v1);
		c.addDisEquality(v1, v2);
	
		XConstraint<TestType> d = xcs.makeConstraint(ts);
		d.addDisEquality(v0, v2);
		boolean b = c.entails(d);
		assertFalse(b);
	}
	
	/**
	 * v0 = v1, v1 != v2, v2=v3, v3=v4|- v0 !=v4
	 * @throws Throwable
	 */
	public void test7() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		c.addEquality(v0, v1);
		c.addDisEquality(v1, v2);
		c.addEquality(v2, v3);
		c.addEquality(v3, v4);
	
		
		boolean b = c.entailsDisEquality(v0,v2);
		assertTrue(b);
	}
	// |- 0 != 1
	public void test8() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		boolean b = c.entailsDisEquality(zero,one);
		assertTrue(b);
	}
	
	// |/- 0 != 0
	public void test9() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		boolean b = c.entailsDisEquality(zero,zero);
		assertFalse(b);
	}
	
	//  v != null |- v != null
	public void test10() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		c.addDisEquality(v0,NULL);
		
		boolean b = c.entailsDisEquality(v0, NULL);
		assertTrue(b);
	}

	//  v0 == v1, v0 != v1 |- v2==v3
	public void test11() throws Throwable {

		XConstraint<TestType> c = xcs.makeConstraint(ts);
		c.addDisEquality(v0,v1);
		c.addEquality(v0, v1);
		assertFalse(c.consistent());

	}

	/**
	 * v0.f != v1.f |- v0 != v1
	 * @throws Throwable
	 */
	public void test12() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		XField<TestType,XStringDef<TestType>> f = xcs.<XStringDef<TestType>>makeField(v0, new XStringDef<TestType>("f",objType));
		XField<TestType,XStringDef<TestType>> g = xcs.<XStringDef<TestType>>makeField(v1, new XStringDef<TestType>("f",objType));
		c.addDisEquality(f,g);
		boolean result = c.entailsDisEquality(v0, v1);
		assertTrue(result);
	}

	/**
	 * v0.f == 0, v1.f == 1 |- v0 != v1
	 * @throws Throwable
	 */
	public void test13() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		XField<TestType,XStringDef<TestType>> f = xcs.<XStringDef<TestType>>makeField(v0, new XStringDef<TestType>("f", intType));
		XField<TestType,XStringDef<TestType>> g = xcs.<XStringDef<TestType>>makeField(v1, new XStringDef<TestType>("f", intType));
		c.addEquality(f,xcs.makeLit(intType, 0));
		c.addEquality(g,xcs.makeLit(intType, 1));
		
		boolean result = c.entailsDisEquality(v0, v1);
		assertTrue(result);
	}
	
}
