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
import x10.compiler.NonEscaping;

/**
 * @author bdlucas 12/2008
 Yoav: not relevant under the new object init semantics.
 */

class XTENLANG_198 extends x10Test {

    static class A {
        @NonEscaping final def foo(): String = "A";
        val x:String;
        def this() {
            x = foo();
        }
    }
    
    public def run():boolean {
        x10.io.Console.OUT.println("new A().x: " + (new A().x));
        return new A().x.equals("A");
    }

    public static def main(Rail[String]) {
        new XTENLANG_198().execute();
    }
}
