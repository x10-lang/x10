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



import x10.constraint.XConstraint;
import x10.constraint.XEQV;
import x10.constraint.XUQV;

public class MyTest extends BaseTest {
	public MyTest() {
		super("MyTest");
	}
	public void test6() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		XUQV<TestType> v1 = xcs.makeUQV(intType, "v1");
		XUQV<TestType> v2 = xcs.makeUQV(intType, "v2");

		XConstraint<TestType> d = xcs.makeConstraint(ts);
		XEQV<TestType> x1 = xcs.makeEQV(intType);
		d.addEquality(v1,x1);
		d.addEquality(v2,x1);

		//XVar x1 = c0.genEQV(xcs.makeName("x1"), true);
		boolean result = c.entails(d);
		System.out.println("test6:");
		System.out.println("c: " + c);
		System.out.println("d: " + d);
		System.out.println("d.terms(): " + d.terms());
		
		assertFalse(result);
		
	}

}
