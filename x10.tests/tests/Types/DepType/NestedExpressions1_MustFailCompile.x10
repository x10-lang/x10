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

//OPTIONS: -STATIC_CHECKS

import harness.x10Test;

/**
 * Checking that the type-checker can correctly handle boolean expressions as the values
 * boolean properties.
 */
public class NestedExpressions1_MustFailCompile  extends x10Test {
    class C(a:boolean) {
        static type C(b:boolean) = C{self.a==b};
        def n(u:boolean) {
            val x = new C(true);
            val y = new C(u);
            // this must fail. x is f type C(x.a), or even C(true), not C(x.a&&y.a).
            val xya = x.a && y.a;
            val z1: C(xya) = x; // ERR
        }
    }
    public def run() = true;

    public static def main(Rail[String])  {
        new NestedExpressions1_MustFailCompile().execute();
    }
}
