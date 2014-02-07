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
import x10.compiler.Native;

/**
 * Purpose of test: Interfaces may be implemented with native methods
 */
public class ImplementWithNative extends x10Test {
    static interface I {
 	def foo(int):int;
 	def bar(int):int;
    }

    static class C1 implements I {
        @Native("c++", "(#a)+10")
        @Native("java", "(#a)+10")
        public native def foo(a:int):int;

        @Native("c++", "(#a)+100")
        @Native("java", "(#a)+100")
        public native def bar(a:int):int;
    }

    static abstract class P implements I {
        @Native("c++", "(#a)+20")
        @Native("java", "(#a)+20")
        public native def foo(a:int):int;
    }

    static class C2 extends P {
        @Native("c++", "(#a)+200")
        @Native("java", "(#a)+200")
        public native def bar(a:int):int;
    }

    static struct S implements I {
        @Native("c++", "(#a)+30")
        @Native("java", "(#a)+30")
        public native def foo(a:int):int;

        @Native("c++", "(#a)+300")
        @Native("java", "(#a)+300")
        public native def bar(a:int):int;
    }

    public def run(): boolean {	
        val c1:I = new C1();
        val c2:I = new C2();
        val s:I = new S();

        chk(c1.foo(c1.bar(1n)) == 111n);
        chk(c2.foo(c2.bar(2n)) == 222n);
        chk(s.foo(s.bar(3n)) == 333n);

        return true;
    }

    public static def main(Rail[String]) {
        new ImplementWithNative().execute();
    }
}
