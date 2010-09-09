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

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_198 extends x10Test {

    static class A {
        proto def foo(): String = "A";
        val x:String;
        def this() {
            x = foo();
        }
    }
    
    static class B extends A {
        proto def foo(): String = "B";
    }
    
    public def run():boolean {
        x10.io.Console.OUT.println("new B().x: " + (new B().x));
        return new B().x.equals("B");
    }

    public static def main(Rail[String]) {
        new XTENLANG_198().execute();
    }
}
