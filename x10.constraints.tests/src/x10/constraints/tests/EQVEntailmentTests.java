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

import junit.framework.TestSuite;
import x10.constraint.XConstraint;
import x10.constraint.XEQV;
import x10.constraint.XField;
import x10.constraint.XStringDef;
import x10.constraint.XUQV;

public class EQVEntailmentTests extends BaseTest {
	public EQVEntailmentTests() {
		super("EQVEntailmentTests");
	}

	/**
	 * |- exists X. X.a=X.b
	 * @throws Throwable
	 */
	 public void test1() throws Throwable {
		 System.out.println();
		 System.out.println("test1: |- exists X. X.a=X.b");
         XConstraint<TestType> c = xcs.makeConstraint(ts);
         XConstraint<TestType> d = xcs.makeConstraint(ts);
         XEQV<TestType> X = xcs.makeEQV(intType);
         XStringDef<TestType> fieldA = new XStringDef<TestType>("a", intType);
         XStringDef<TestType> fieldB = new XStringDef<TestType>("b", intType);
         XField<TestType,XStringDef<TestType>> Xa = xcs.makeField(X, fieldA);
         XField<TestType,XStringDef<TestType>> Xb = xcs.makeField(X, fieldB);
         d.addEquality(Xa, Xb); 
         System.out.println("c:" + c);
         System.out.println("d:" + d);
         System.out.println("d.terms() ():" + d.terms());
         assertTrue(c.entails(d));
     }
	 
	 /**
		 * |- exists X. X.a=0
		 * @throws Throwable
		 */
	 public void test2() throws Throwable {
		 System.out.println();
		 System.out.println("test2: |- exists X. X.a=0");
         XConstraint<TestType> c = xcs.makeConstraint(ts);
         XConstraint<TestType> d = xcs.makeConstraint(ts);
         XEQV<TestType> X = xcs.makeEQV(intType);
         XField<TestType,XStringDef<TestType>> Xa = xcs.makeField(X, new XStringDef<TestType>("a", intType));
         d.addEquality(Xa, zero); 
         System.out.println("c:" + c);
         System.out.println("d:" + d);
         System.out.println("d.terms() ():" + d.terms());
         assertTrue(c.entails(d));
     }
	 /**
		 * Y=1 |- exists X. (X.a=0, Y=1)
		 * @throws Throwable
		 */
	 public void test3() throws Throwable {
		 System.out.println();
		 System.out.println("test3: Y=1 |- exists X. (X.a=0,Y=1)");
		 XConstraint<TestType> c = xcs.makeConstraint(ts);
		 XConstraint<TestType> d = xcs.makeConstraint(ts);
		 XUQV<TestType> Y = xcs.makeUQV(intType, "Y");
		 c.addEquality(Y, one);
		 System.out.println("c:" + c);
		 
		 XEQV<TestType> X = xcs.makeEQV(intType);
		 XField<TestType,XStringDef<TestType>> Xa = xcs.makeField(X, new XStringDef<TestType>("a", intType));
		 d.addEquality(Xa, zero); 
		 d.addEquality(Y, one);
		 System.out.println("d:" + d);
		 System.out.println("d.terms() (Y=1):" + d.terms());
		 assertTrue(c.entails(d));
	 }
	 
	 /**  d has an implied equality between c terms. Check that that equality
	  * is checked during entailment, even as X is ignored.
		 * Y.a=0, Y.b=1 |/- exists X. (X=Y.a, X=Y.b)
		 * @throws Throwable
		 */
	 public void test4() throws Throwable {
		 System.out.println();
		 System.out.println("test4: Y.a=0, Y.b=1 |/- exists X. X=Y.a,X=Y.b)");
		 XConstraint<TestType> c = xcs.makeConstraint(ts);
		 XUQV<TestType> Y = xcs.makeUQV(intType, "Y");
		 XField<TestType,XStringDef<TestType>> Ya = xcs.makeField(Y, new XStringDef<TestType>("a", intType));
		 XField<TestType,XStringDef<TestType>> Yb = xcs.makeField(Y, new XStringDef<TestType>("b", intType));
		 c.addEquality(Ya, zero);
		 c.addEquality(Yb, one);
		 System.out.println("c:" + c);
		 
		 XConstraint<TestType> d = xcs.makeConstraint(ts);
		 XEQV<TestType> X = xcs.makeEQV(intType);
		 XField<TestType,XStringDef<TestType>> Yda = xcs.makeField(Y, new XStringDef<TestType>("a", intType));
		 XField<TestType,XStringDef<TestType>> Ydb = xcs.makeField(Y, new XStringDef<TestType>("b", intType));
		 d.addEquality(X, Yda); 
		 d.addEquality(X, Ydb);
		 System.out.println("d:" + d);
		 System.out.println("d.terms() (Y.a=Y.b):" + d.terms());
		 assertFalse(c.entails(d));
	 }
	 
	 /**  d has an implied equality between c terms. Check that that equality
	  * is checked during entailment, even as X is ignored.
		 * Y.a=0, Y.b=1 |- exists X. (X=Y.a, X=0)
		 * @throws Throwable
		 */
	 public void test5() throws Throwable {
		 System.out.println();
		 System.out.println("test5: Y.a=0, Y.b=1 |- exists X. X=Y.a,X=0)");
		 XConstraint<TestType> c = xcs.makeConstraint(ts);
		 XEQV<TestType> Y = xcs.makeEQV(intType);
		 XField<TestType,XStringDef<TestType>> Ya = xcs.makeField(Y, new XStringDef<TestType>("a", intType));
		 XField<TestType,XStringDef<TestType>> Yb = xcs.makeField(Y, new XStringDef<TestType>("b", intType));
		 c.addEquality(Ya, zero);
		 c.addEquality(Yb, one);
		 System.out.println("c:" + c);
		 
		 XConstraint<TestType> d = xcs.makeConstraint(ts);
		 XEQV<TestType> X = xcs.makeEQV(intType);
		 XField<TestType,XStringDef<TestType>> Yda = xcs.makeField(Y, new XStringDef<TestType>("a", intType));
		 d.addEquality(X, Yda); 
		 d.addEquality(X, zero);
		 System.out.println("d:" + d);
		 System.out.println("d.terms() (Y.a=0):" + d.terms());
		 assertTrue(c.entails(d));
	 }
	 /**  d has an implied equality between c terms. Check that that equality
	  * is checked during entailment, even as X is ignored.
		 * Y.a.b=0, Y.b.c=1 |- exists X. (X=Y.a, X.b=0)
		 * @throws Throwable
		 */
	 public void test6() throws Throwable {
		 System.out.println();
		 System.out.println("test6: Y.a.b=0, Y.b.c=1 |- exists X. X=Y.a,X.b=0)");
		
		 XConstraint<TestType> c = xcs.makeConstraint(ts);
		 XEQV<TestType> Y = xcs.makeEQV(intType);
		 XField<TestType,XStringDef<TestType>> Ya = xcs.makeField(Y, new XStringDef<TestType>("a", intType));
		 XField<TestType,XStringDef<TestType>> Yab = xcs.makeField(Ya, new XStringDef<TestType>("b", intType));
		 XField<TestType,XStringDef<TestType>> Yb = xcs.makeField(Y, new XStringDef<TestType>("b", intType));
		 XField<TestType,XStringDef<TestType>> Ybc = xcs.makeField(Yb, new XStringDef<TestType>("c", intType));
		 c.addEquality(Yab, zero);
		 c.addEquality(Ybc, one);
		 System.out.println("c:" + c);
		 
		 XConstraint<TestType> d = xcs.makeConstraint(ts);
		 XEQV<TestType> X = xcs.makeEQV(intType);
		 XField<TestType,XStringDef<TestType>> Yda = xcs.makeField(Y, new XStringDef<TestType>("a", intType));
		 d.addEquality(X, Yda); 
		 XField<TestType,XStringDef<TestType>> Xb = xcs.makeField(X, new XStringDef<TestType>("b", intType));
		 d.addEquality(Xb, zero);
		 System.out.println("d:" + d);
		 System.out.println("d.terms() (Y.a.b=0):" + d.terms());
		 assertTrue(c.entails(d));
	 }
	 /**  d has an implied equality between c terms. Check that that equality
	  * is checked during entailment, even as X is ignored.
		 * Y.a.b=0, Y.b.c=1 |/- exists X. (X=Y.a, X.b=1)
		 * @throws Throwable
		 */
	 public void test7() throws Throwable {
		 System.out.println();
		 System.out.println("test7: Y.a.b=0, Y.b.c=1 |/- exists X. X=Y.a,X.b=1)");
		
		 XConstraint<TestType> c = xcs.makeConstraint(ts);
		 XUQV<TestType> Y = xcs.makeUQV(intType, "Y");
		 XField<TestType,XStringDef<TestType>> Ya = xcs.makeField(Y, new XStringDef<TestType>("a",intType));
		 XField<TestType,XStringDef<TestType>> Yab = xcs.makeField(Ya, new XStringDef<TestType>("b",intType));
		 XField<TestType,XStringDef<TestType>> Yb = xcs.makeField(Y, new XStringDef<TestType>("b",intType));
		 XField<TestType,XStringDef<TestType>> Ybc = xcs.makeField(Yb, new XStringDef<TestType>("c",intType));
		 c.addEquality(Yab, zero);
		 c.addEquality(Ybc, one);
		 System.out.println("c:" + c);
		 
		 XConstraint<TestType> d = xcs.makeConstraint(ts);
		 XEQV<TestType> X = xcs.makeEQV(intType);
		 XField<TestType,XStringDef<TestType>> Yda = xcs.makeField(Y, new XStringDef<TestType>("a", intType));
		 d.addEquality(X, Yda); 
		 XField<TestType,XStringDef<TestType>> Xb = xcs.makeField(X, new XStringDef<TestType>("b", intType));
		 d.addEquality(Xb, one);
		 System.out.println("d:" + d);
		 System.out.println("d.terms() (Y.a.b=1):" + d.terms());

		 assertFalse(c.entails(d));
	 }
	 
	 /**  d has an implied equality between c terms. Check that that equality
	  * is checked during entailment, even as X is ignored.
		 * Y.a==Z, Z.b==0 |- exists X. (X==Y.a, X.b==0)
		 * @throws Throwable
		 */
	 public void test8() throws Throwable {
		 String cString = "Y.a==Z, Z.b==0";
		 String dString = "exists X. X==Y.a, X.b==0";
		 System.out.println();
		 System.out.println("test8: " + cString + " |- " + dString);
		
		 XConstraint<TestType> c = xcs.makeConstraint(ts);
		 XEQV<TestType> Y = xcs.makeEQV(intType);
		 XEQV<TestType> Z = xcs.makeEQV(intType);
		 XField<TestType,XStringDef<TestType>> Ya = xcs.makeField(Y, new XStringDef<TestType>("a", intType));
		 XField<TestType,XStringDef<TestType>> Zb = xcs.makeField(Z, new XStringDef<TestType>("b", intType));
		 c.addEquality(Ya, Z);
		 c.addEquality(Zb, zero);
		 System.out.println("c:" + c);
		 
		 XConstraint<TestType> d = xcs.makeConstraint(ts);
		 XEQV<TestType> X = xcs.makeEQV(intType);
		 XField<TestType,XStringDef<TestType>> Yda = xcs.makeField(Y, new XStringDef<TestType>("a", intType));
		 d.addEquality(X, Yda); 
		 XField<TestType,XStringDef<TestType>> Xb = xcs.makeField(X, new XStringDef<TestType>("b", intType));
		 d.addEquality(Xb, zero);
		 System.out.println("d:" + d);
		 System.out.println("d.terms() (Y.a.b=0):" + d.terms());
		 assertTrue(c.entails(d));
	 }
	  public static TestSuite suite() {
			TestSuite suite = new TestSuite(EQVEntailmentTests.class);
			return suite;
		}
}