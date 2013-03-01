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

import harness.x10Test;
import x10.compiler.Native;

/**
 * Purpose of test: Can override/inherit @Native methods.
 */
public class OverrideNative extends x10Test {

    static class Parent {
        public def foo(a:int):int = a + 10;
    
        @Native("c++", "(#1)+100")
        @Native("java", "(#a)+100")
        public native def bar(a:int):int;
    }

    static class Child extends Parent {

        @Native("c++", "(#1)+200")
        @Native("java", "(#a)+200")
        public native def foo(a:int):int;
    
        public def bar(a:int):int = a + 20;
    }

    public def run(): boolean {	
        val p:Parent = new Parent();
        val c1:Parent = new Child();
        val c2:Child = new Child();

        chk(p.foo(1) == 11);
        chk(p.bar(1) == 101);

        chk(c1.foo(1) == 201);
        chk(c1.bar(1) == 21);

        chk(c2.foo(1) == 201);
        chk(c2.bar(1) == 21);

        return true;
    }

    public static def main(Rail[String]) {
        new OverrideNative().execute();
    }
}
