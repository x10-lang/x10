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

import harness.x10Test;
import x10.compiler.tests.*;

/**
 * See XTENLANG-2389.
 *
 * @author vj 5/2011
 */
public class OuterThisTests(i:Int) extends x10Test {
    public static class A {
        property foo()=A.this; // Same as property foo()=this
        class B {
            // ok.Any use x.bar(), for x:B should expand into ax, where ax is the outer A instance for x
            property bar()=A.this;

            // ok.Any use x.foo2(), for x:B should expand into x
            property foo2()=B.this; // ok.
        }

        def bla(a1:A, a2:A{this==a1}) {
            // signature ok. Forces this method can be called on receiver x only if x==a1.

            // ok. This should be an error with STATIC_CHECKS.
            //  The only thing known about a2 is a2:A. We also know
            // this==a1, but this says nothing about a2, per se.
            // This information cannot be used to establish that a2==a1 statically.
            // Without STATIC_CHECKS, the condition a2==a1 should be checked dynamically.
            @ERR val a3:A{self==a1} = a2;

            // ok. This should be an error with STATIC_CHECKS.
            // The only thing known about a2 is a2:A. We also know
            // this==a1, but this says nothing about a2, per se.
            // This information cannot be used to establish that a2 !=a1 statically.
            // Without STATIC_CHECKS, the condition a2 != a1 should be checked dynamically.
            @ERR val a4:A{self!=a1} = a2;
        }

        // ok. the call x.test1(y1,y2) should resolve against this only if
        // it can be established that x:A1,y1:A,y2:A and a1==a2.
        // Why? Because for any x:A, x.foo() expands to x. Hence self.foo()
        // expands to self. Thus the signature normalizes to test1(a1:A, a2:A{self==a1}):void.
        def test1(a1:A, a2:A{self.foo()==a1}) {}


        // ok. the call x.test2(y1,y2) should resolve against this only if
        // it can be established that x==a1, and y1:A and y2:A.
        def test2(a1:A, a2:A{A.this==a1}) {
            // ok. This should be an error with STATIC_CHECKS
            //  With the information this:A, a1:A, a2:A, this==a1, we cannot establish a1==a2.
            // If STATIC_CHECKS is not specified, a1==a2 should be checked dynamically.

            @ERR { test1(a1,a2); }
        }

        // ok. the call x.test3(y1,y2) should resolve against this only if
        // it can be established that x:A, y1:A, y2:B, and the outer A for y2 == y1.
        def test3(a1:A, b2:B{self.bar()==a1}) {}

        // ok. the call x.test4(y1,y2) should resolve against this only if
        // it can be established that x:A, y1:A, y2:B, and x==y1.
        def test4(a:A, b:B{A.this==a}) {

            // ok. the compiler should generate an error with STATIC_CHECKS because
            // outer A for b == this cannot be established knowing that
            // this:A, a:A, b:B, this==a. Without STATIC_CHECKS, the condition
            // a == outer A for b should be checked dynamically,
            @ERR { test3(a,b); }
        }
    }

    class X(j:String) {
        def m(Int{self==OuterThisTests.this.i}){}
    }

    public def run(): boolean = {
        val o = new OuterThisTests(3n);
        val x = o.new X("x");
        x.m(o.i);
        x.m(3n);
        try {
            @ERR { x.m(4n); }
            return false;
        } catch (FailedDynamicCheckException) { }

        return true;
    }

    public static def main(Rail[String]) {
        new OuterThisTests(4n).execute();
    }
}
