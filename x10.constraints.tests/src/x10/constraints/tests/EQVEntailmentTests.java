/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.constraints.tests;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import x10.constraint.XConstraint;
import x10.constraint.XConstraintManager;
import x10.constraint.XField;
import x10.constraint.XTerm;
import x10.constraint.XVar;

public class EQVEntailmentTests extends TestCase {
	public EQVEntailmentTests() {
		super("EQVEntailmentTests");
	}
	 XTerm zero = XConstraintManager.getConstraintSystem().makeLit(new Integer(0));
	 XTerm one = XConstraintManager.getConstraintSystem().makeLit(new Integer(1));

	/**
	 * |- exists X. X.a=X.b
	 * @throws Throwable
	 */
	 public void test1() throws Throwable {
		 System.out.println();
		 System.out.println("test1: |- exists X. X.a=X.b");
         XConstraint c = XConstraintManager.getConstraintSystem().makeConstraint();
         XConstraint d = XConstraintManager.getConstraintSystem().makeConstraint();
         XVar X = XConstraintManager.getConstraintSystem().makeEQV();
         String fieldA = "a";
         String fieldB = "b";
         XField<String> Xa = XConstraintManager.getConstraintSystem().makeField(X, fieldA);
         XField<String> Xb = XConstraintManager.getConstraintSystem().makeField(X, fieldB);
         d.addBinding(Xa, Xb); 
         System.out.println("c:" + c);
         System.out.println("d:" + d);
         System.out.println("d.extConstraints() ():" + d.extConstraints());
         assertTrue(c.entails(d));
     }
	 
	 /**
		 * |- exists X. X.a=0
		 * @throws Throwable
		 */
	 public void test2() throws Throwable {
		 System.out.println();
		 System.out.println("test2: |- exists X. X.a=0");
         XConstraint c = XConstraintManager.getConstraintSystem().makeConstraint();
         XConstraint d = XConstraintManager.getConstraintSystem().makeConstraint();
         XVar X = XConstraintManager.getConstraintSystem().makeEQV();
         XField<String> Xa = XConstraintManager.getConstraintSystem().makeField(X, "a");
         XTerm zero = XConstraintManager.getConstraintSystem().makeLit(new Integer(0));
         d.addBinding(Xa, zero); 
         System.out.println("c:" + c);
         System.out.println("d:" + d);
         System.out.println("d.extConstraints() ():" + d.extConstraints());
         assertTrue(c.entails(d));
     }
	 /**
		 * Y=1 |- exists X. (X.a=0, Y=1)
		 * @throws Throwable
		 */
	 public void test3() throws Throwable {
		 System.out.println();
		 System.out.println("test3: Y=1 |- exists X. (X.a=0,Y=1)");
		 XConstraint c = XConstraintManager.getConstraintSystem().makeConstraint();
		 XConstraint d = XConstraintManager.getConstraintSystem().makeConstraint();
		 XVar Y = XConstraintManager.getConstraintSystem().makeUQV("Y");
		 c.addBinding(Y, one);
		 System.out.println("c:" + c);
		 
		 XVar X = XConstraintManager.getConstraintSystem().makeEQV();
		 XField<String> Xa = XConstraintManager.getConstraintSystem().makeField(X, "a");
		 d.addBinding(Xa, zero); 
		 d.addBinding(Y, one);
		 System.out.println("d:" + d);
		 System.out.println("d.extConstraints() (Y=1):" + d.extConstraints());
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
		 XConstraint c = XConstraintManager.getConstraintSystem().makeConstraint();
		 XVar Y = XConstraintManager.getConstraintSystem().makeUQV("Y");
		 XField<String> Ya = XConstraintManager.getConstraintSystem().makeField(Y, "a");
		 XField<String> Yb = XConstraintManager.getConstraintSystem().makeField(Y, "b");
		 c.addBinding(Ya, zero);
		 c.addBinding(Yb, one);
		 System.out.println("c:" + c);
		 
		 XConstraint d = XConstraintManager.getConstraintSystem().makeConstraint();
		 XVar X = XConstraintManager.getConstraintSystem().makeEQV();
		 XField<String> Yda = XConstraintManager.getConstraintSystem().makeField(Y, "a");
		 XField<String> Ydb = XConstraintManager.getConstraintSystem().makeField(Y, "b");
		 d.addBinding(X, Yda); 
		 d.addBinding(X, Ydb);
		 System.out.println("d:" + d);
		 System.out.println("d.extConstraints() (Y.a=Y.b):" + d.extConstraints());
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
		 XConstraint c = XConstraintManager.getConstraintSystem().makeConstraint();
		 XVar Y = XConstraintManager.getConstraintSystem().makeEQV();
		 XField<String> Ya = XConstraintManager.getConstraintSystem().makeField(Y, "a");
		 XField<String> Yb = XConstraintManager.getConstraintSystem().makeField(Y, "b");
		 c.addBinding(Ya, zero);
		 c.addBinding(Yb, one);
		 System.out.println("c:" + c);
		 
		 XConstraint d = XConstraintManager.getConstraintSystem().makeConstraint();
		 XVar X = XConstraintManager.getConstraintSystem().makeEQV();
		 XField<String> Yda = XConstraintManager.getConstraintSystem().makeField(Y, "a");
		 d.addBinding(X, Yda); 
		 d.addBinding(X, zero);
		 System.out.println("d:" + d);
		 System.out.println("d.extConstraints() (Y.a=0):" + d.extConstraints());
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
		
		 XConstraint c = XConstraintManager.getConstraintSystem().makeConstraint();
		 XVar Y = XConstraintManager.getConstraintSystem().makeEQV();
		 XField<String> Ya = XConstraintManager.getConstraintSystem().makeField(Y, "a");
		 XField<String> Yab = XConstraintManager.getConstraintSystem().makeField(Ya, "b");
		 XField<String> Yb = XConstraintManager.getConstraintSystem().makeField(Y, "b");
		 XField<String> Ybc = XConstraintManager.getConstraintSystem().makeField(Yb, "c");
		 c.addBinding(Yab, zero);
		 c.addBinding(Ybc, one);
		 System.out.println("c:" + c);
		 
		 XConstraint d = XConstraintManager.getConstraintSystem().makeConstraint();
		 XVar X = XConstraintManager.getConstraintSystem().makeEQV();
		 XField<String> Yda = XConstraintManager.getConstraintSystem().makeField(Y, "a");
		 d.addBinding(X, Yda); 
		 XField<String> Xb = XConstraintManager.getConstraintSystem().makeField(X, "b");
		 d.addBinding(Xb, zero);
		 System.out.println("d:" + d);
		 System.out.println("d.extConstraints() (Y.a.b=0):" + d.extConstraints());
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
		
		 XConstraint c = XConstraintManager.getConstraintSystem().makeConstraint();
		 XVar Y = XConstraintManager.getConstraintSystem().makeUQV();
		 XField<String> Ya = XConstraintManager.getConstraintSystem().makeField(Y, "a");
		 XField<String> Yab = XConstraintManager.getConstraintSystem().makeField(Ya, "b");
		 XField<String> Yb = XConstraintManager.getConstraintSystem().makeField(Y, "b");
		 XField<String> Ybc = XConstraintManager.getConstraintSystem().makeField(Yb, "c");
		 c.addBinding(Yab, zero);
		 c.addBinding(Ybc, one);
		 System.out.println("c:" + c);
		 
		 XConstraint d = XConstraintManager.getConstraintSystem().makeConstraint();
		 XVar X = XConstraintManager.getConstraintSystem().makeEQV();
		 XField<String> Yda = XConstraintManager.getConstraintSystem().makeField(Y, "a");
		 d.addBinding(X, Yda); 
		 XField<String> Xb = XConstraintManager.getConstraintSystem().makeField(X, "b");
		 d.addBinding(Xb, one);
		 System.out.println("d:" + d);
		 System.out.println("d.extConstraints() (Y.a.b=1):" + d.extConstraints());

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
		
		 XConstraint c = XConstraintManager.getConstraintSystem().makeConstraint();
		 XVar Y = XConstraintManager.getConstraintSystem().makeEQV();
		 XVar Z = XConstraintManager.getConstraintSystem().makeEQV();
		 XField<String> Ya = XConstraintManager.getConstraintSystem().makeField(Y, "a");
		 XField<String> Zb = XConstraintManager.getConstraintSystem().makeField(Z, "b");
		 c.addBinding(Ya, Z);
		 c.addBinding(Zb, zero);
		 System.out.println("c:" + c);
		 
		 XConstraint d = XConstraintManager.getConstraintSystem().makeConstraint();
		 XVar X = XConstraintManager.getConstraintSystem().makeEQV();
		 XField<String> Yda = XConstraintManager.getConstraintSystem().makeField(Y, "a");
		 d.addBinding(X, Yda); 
		 XField<String> Xb = XConstraintManager.getConstraintSystem().makeField(X, "b");
		 d.addBinding(Xb, zero);
		 System.out.println("d:" + d);
		 System.out.println("d.extConstraints() (Y.a.b=0):" + d.extConstraints());
		 assertTrue(c.entails(d));
	 }
	  public static TestSuite suite() {
			TestSuite suite = new TestSuite(EQVEntailmentTests.class);
			return suite;
		}
}