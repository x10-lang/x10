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

import harness.x10Test;
import x10.compiler.tests.*;

/**
 * See XTENLANG-2389.
 *
 * @author vj 5/2011
 */
public class DynamicOuterTests extends x10Test {
    static class A (i:Int) {
        class X(j:ULong) {
            def m(Int{self==A.this.i}){}
            def n(a:A, x:A{self.i==a.i}.X{self.j==0xd00d1eabadbaff1eUL}){}
        }
    }

    public def run(): boolean {
        val a = new A(3n);
        val x = a.new X(0xd00d1eabadbaff1eUL);
        x.m(a.i);
        x.m(3n);
        // @ERR { x.m(4n);  }
        var b:A=a;
        x.n(b, x); // ERR: Warning: Generated a dynamic check for the method call.
        return true;
    }

    public static def main(Rail[String]) {
        new DynamicOuterTests().execute();
    }
}
