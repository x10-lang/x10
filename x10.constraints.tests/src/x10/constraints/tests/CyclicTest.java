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
import x10.constraint.XField;
import x10.constraint.XStringDef;

public class CyclicTest extends BaseTest {
	public CyclicTest() {
		super("CyclicTest");
	}

	
	/**
	 * v0.a==v1, v1.a==v0 must be inconsistent
	 * @throws Throwable
	 */
	public void test1() throws Throwable {
		XField<TestType,XStringDef<TestType>> v0a = xcs.makeField(v0, new XStringDef<TestType>("a", objType));
		XField<TestType,XStringDef<TestType>> v1b = xcs.makeField(v1, new XStringDef<TestType>("b", objType));

		XConstraint<TestType> c = xcs.makeConstraint(ts);
		c.addEquality(v0a, v1);
		System.out.println("c is|" + c+ "|");
		c.addEquality(v1b, v0);
		System.out.println("c is|" + c+ "|");
		assertFalse(c.consistent());
	}
}
