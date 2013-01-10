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
import x10.constraint.XEQV;
import x10.constraint.XField;
import x10.constraint.XStringDef;

public class EntailmentTest extends BaseTest {
	public EntailmentTest() {
		super("EntailmentTest");
	}

	/**
	 * v0=v1,v1=v2 |- v0=v2
	 * @throws Throwable
	 */
	public void test1() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		c.addEquality(v0, v1);
		c.addEquality(v1, v2);
		XConstraint<TestType> d = xcs.makeConstraint(ts);
		d.addEquality(v0, v2);
		System.out.println(c);
		System.out.println(d);
		assertTrue(c.entails(d));
	}
	
	/**
	 * v0=v1 |- exists x. x=v2
	 * @throws Throwable
	 */
	public void test2() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		c.addEquality(v0,v1);
		
		XConstraint<TestType> d = xcs.makeConstraint(ts);
		XEQV<TestType> x = xcs.makeEQV(intType);
		d.addEquality(x, v2);
		
		//XVar x1 = c0.genEQV(xcs.makeUQV("x1"), true);
		//XTerm x1f = xcs.makeField(x1, xcs.makeUQV(new Object(), "f"));
		assertTrue(c.entails(d));
	}
	
	/**
	 * v0=v1 |- exists x, y. x=y
	 * @throws Throwable
	 */
	public void test3() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		c.addEquality(v0,v1);
		
		XConstraint<TestType> d = xcs.makeConstraint(ts);
		XEQV<TestType>  x = xcs.makeEQV(intType);
		XEQV<TestType>  y = xcs.makeEQV(intType);
		d.addEquality(x, y);
		
		//XVar x1 = c0.genEQV(xcs.makeUQV("x1"), true);
		//XTerm x1f = xcs.makeField(x1, xcs.makeUQV(new Object(), "f"));
		assertTrue(c.entails(d));
	}
	
	/**
	 * v0=v1 |- v0.f=v1.f
	 * @throws Throwable
	 */
	public void test4() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		c.addEquality(v0,v1);
		
		XConstraint<TestType> d = xcs.makeConstraint(ts);
		XField<TestType,XStringDef<TestType>> v0f = xcs.makeField(v0, new XStringDef<TestType>("f", intType));
		XField<TestType,XStringDef<TestType>> v1f = xcs.makeField(v1, new XStringDef<TestType>("f", intType));
		d.addEquality(v0f, v1f);
		
		//XVar x1 = c0.genEQV(xcs.makeUQV("x1"), true);
		boolean result = c.entails(d);
		assertTrue(result);
	}
	/**
	 * v0=v0.f |- v1=v2  (antecedent is false)
	 * @throws Throwable
	 */
	public void test5() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		
		XField<TestType,XStringDef<TestType>> v0f = xcs.makeField(v0, new XStringDef<TestType>("f", intType));
			c.addEquality(v0f, v0);
			assertFalse(c.consistent());
		

		XConstraint<TestType> d = xcs.makeConstraint(ts);
		d.addEquality(v1,v2);

		//XVar x1 = c0.genEQV(xcs.makeUQV("x1"), true);
		boolean result = c.entails(d);
		assertTrue(result);
		
	}
	/**
	 *  |/= exists x1. v1=x1, v2=x2
	 * @throws Throwable
	 */
	public void test6() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		

		XConstraint<TestType> d = xcs.makeConstraint(ts);
		XEQV<TestType>  x1 = xcs.makeEQV(intType);
		d.addEquality(v1,x1);
		d.addEquality(v2,x1);

		//XVar x1 = c0.genEQV(xcs.makeUQV("x1"), true);
		boolean result = c.entails(d);
		assertFalse(result);
		
	}
	public void test7() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		

		XConstraint<TestType> d = xcs.makeConstraint(ts);
		XEQV<TestType>  x1 = xcs.makeEQV(intType);
		XEQV<TestType>  x2 = xcs.makeEQV(intType);
		d.addEquality(v1,x1);
		d.addEquality(x2,x1);
		d.addEquality(v2,x2);

		//XVar x1 = c0.genEQV(xcs.makeUQV("x1"), true);
		boolean result = c.entails(d);
		assertFalse(result);
		
	}
	public void test8() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		

		XConstraint<TestType> d = xcs.makeConstraint(ts);
		XEQV<TestType> x1 = xcs.makeEQV(intType);
		XEQV<TestType> x2 = xcs.makeEQV(intType);
		d.addEquality(x2,x1);
		d.addEquality(v1,x1);
		d.addEquality(v2,x2);

		//XVar x1 = c0.genEQV(xcs.makeUQV("x1"), true);
		boolean result = c.entails(d);
		assertFalse(result);
		
	}
	/**
	 *  |- exists x1. x1=v1, x1.f=v2 -- should fail
	 * @throws Throwable
	 */
	public void test9() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		
		XConstraint<TestType> d = xcs.makeConstraint(ts);
		XEQV<TestType>  x1 = xcs.makeEQV(intType);
		XField<TestType,XStringDef<TestType>> x1f = xcs.makeField(x1, new XStringDef<TestType>("f", intType));
		d.addEquality(v1,x1);
		d.addEquality(v2,x1f);

		//XVar x1 = c0.genEQV(xcs.makeUQV("x1"), true);
		boolean result = c.entails(d);
		assertFalse(result);
		
	}
	/**
	 *  |- exists x1. x1=v1, x2=x1.f, x2=v2 -- should fail
	 * @throws Throwable
	 */
	public void test10() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		

		XConstraint<TestType> d = xcs.makeConstraint(ts);
		XEQV<TestType>  x1 = xcs.makeEQV(intType);
		XEQV<TestType>  x2 = xcs.makeEQV(intType);
		XField<TestType,XStringDef<TestType>> x1f = xcs.makeField(x1, new XStringDef<TestType>("f", intType));
		d.addEquality(v1,x1);
		d.addEquality(x2,x1f);
		d.addEquality(x2,v2);

		boolean result = c.entails(d);
		assertFalse(result);
		
	}
	/**
	 *  |- exists x1. x1=v1, x2=v1.f, x2=v2.f -- should fail
	 * @throws Throwable
	 */
	public void test11() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		

		XConstraint<TestType> d = xcs.makeConstraint(ts);
		XEQV<TestType>  x1 = xcs.makeEQV(intType);
		XEQV<TestType>  x2 = xcs.makeEQV(intType);
		XField<TestType,XStringDef<TestType>> v1f = xcs.makeField(v1, new XStringDef<TestType>("f", intType));
		XField<TestType,XStringDef<TestType>> v2f = xcs.makeField(v2, new XStringDef<TestType>("f", intType));
		d.addEquality(v1,x1);
		d.addEquality(x2,v1f);
		d.addEquality(x2,v2f);

		boolean result = c.entails(d);
		assertFalse(result);
		
	}
	/**
	 *   exists x1. x1=v1, x2=v1.f, x2=v2.f |- v1=v2 --- should fail
	 * @throws Throwable
	 */
	public void test12() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		XEQV<TestType>  x1 = xcs.makeEQV(intType);
		XEQV<TestType>  x2 = xcs.makeEQV(intType);
		XField<TestType,XStringDef<TestType>> v1f = xcs.makeField(v1, new XStringDef<TestType>("f", intType));
		XField<TestType,XStringDef<TestType>> v2f = xcs.makeField(v2, new XStringDef<TestType>("f", intType));
		c.addEquality(v1,x1);
		c.addEquality(x2,v1f);
		c.addEquality(x2,v2f);
		
		XConstraint<TestType> d = xcs.makeConstraint(ts);
		d.addEquality(v1,v2);
		
		boolean result = c.entails(d);
		assertFalse(result);
		
	}
	
	/**
	 *   v1=a |- exists x. x=a, x=v1
	 * @throws Throwable
	 */
	public void test13() throws Throwable {
		XConstraint<TestType> c = xcs.makeConstraint(ts);
		c.addEquality(v1,zero);
		c.toString();
		
		XConstraint<TestType> d = xcs.makeConstraint(ts);
		XEQV<TestType>  x1 = xcs.makeEQV(intType);
		d.addEquality(zero,x1);
		d.addEquality(v1,x1);
		
		boolean result = c.entails(d);
		assertTrue(result);
		
	}
	
	/**
         * v0 : _{self=v1} |- v0=v1
         * @throws Throwable
        
        public void test14() throws Throwable {
            final XVar v0 = xcs.makeUQV("v0");
            final XVar v1 = xcs.makeUQV("v1");

            final XConstraint<TestType> s = xcs.makeConstraint(ts);
            s.addEquality(v1, v0);

            XConstraint<TestType> c = xcs.makeConstraint(ts);
            c.addTerm(v0);
            
            XConstraint<TestType> d = xcs.makeConstraint(ts);
            d.addEquality(v0, v1);
            System.out.println();
            System.out.println("EntailmentTest.test14: v0 : _{self=v1} |- v0=v1");
            System.out.println("c:" + c);
            System.out.println("s:" + s);
            System.out.println("d: " + d + " ext:" + d.extConstraints());
            assertTrue(c.entails(d, s));
        }
         */
        /**
         * |- exists x. x.a=x.b
         * @throws Throwable
         */
        public void test15() throws Throwable {
           
            XConstraint<TestType> c = xcs.makeConstraint(ts);
            XConstraint<TestType> d = xcs.makeConstraint(ts);
            XEQV<TestType> X = xcs.makeEQV(intType);
            XField<TestType,XStringDef<TestType>> Xa = xcs.makeField(X, new XStringDef<TestType>("a", objType));
            XField<TestType,XStringDef<TestType>> Xb = xcs.makeField(X, new XStringDef<TestType>("b", objType));
            d.addEquality(Xa, Xb); 
            assertTrue(c.entails(d));
        }

	
}
