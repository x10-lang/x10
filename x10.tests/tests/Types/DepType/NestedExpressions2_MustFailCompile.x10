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

//OPTIONS: -STATIC_CHECKS

import harness.x10Test;

/**
 * Checking that the type-checker can correctly handle boolean expressions as the values
 * boolean properties. Check that an expression of type C(x.a&&y.a) can
 * be assigned to a variable of type C{self.a==(y.a&&x.a)}.
 */
public class NestedExpressions2_MustFailCompile extends x10Test {
    class C(a:boolean) {
        static type C(b:boolean) = C{self.a==b};
        def this(b:boolean):C{self.a==b}{property(b);}
        def and(x:C, y:C): C{self.a== (x.a && y.a)} = new C(x.a&&y.a); // ERR
        def n() {
            val x = new C(true);
            val y = new C(true);
            val z: C{self.a==(y.a&&x.a)} = and(x,y); // ERR
        }
    }
    public def run() = true;

    public static def main(Rail[String])  {
        new NestedExpressions2_MustFailCompile().execute();
    }
}
