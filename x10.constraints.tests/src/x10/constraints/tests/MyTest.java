/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.constraints.tests;



import junit.framework.TestCase;
import x10.constraint.XConstraint;
import x10.constraint.XConstraintManager;
import x10.constraint.XVar;

public class MyTest extends TestCase {
	public MyTest() {
		super("MyTest");
	}
	public void test6() throws Throwable {
		XConstraint c = XConstraintManager.getConstraintSystem().makeConstraint();
		XVar v1 = XConstraintManager.getConstraintSystem().makeUQV("v1");
		XVar v2 = XConstraintManager.getConstraintSystem().makeUQV("v2");

		XConstraint d = XConstraintManager.getConstraintSystem().makeConstraint();
		XVar x1 = XConstraintManager.getConstraintSystem().makeEQV();
		d.addBinding(v1,x1);
		d.addBinding(v2,x1);

		//XVar x1 = c0.genEQV(XConstraintManager.getConstraintSystem().makeName("x1"), true);
		boolean result = c.entails(d);
		System.out.println("test6:");
		System.out.println("c: " + c);
		System.out.println("d: " + d);
		System.out.println("d.extConstraints(): " + d.extConstraints());
		
		assertFalse(result);
		
	}

}
