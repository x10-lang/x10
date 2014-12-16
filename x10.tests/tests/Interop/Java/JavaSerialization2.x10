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

// MANAGED_X10_ONLY

public class JavaSerialization2 extends x10Test {
        
    static def test1():void {
        val e = new java.util.ArrayList();
        at (Place.places().next(here)) {
            val tmp = e;
        }
        Console.OUT.println("test1 passed");
    }

    static class C extends java.util.ArrayList {}
    static def test2a():void {
        val e = new C();
        at (Place.places().next(here)) {
            val tmp = e;
        }
        Console.OUT.println("test2a passed");
    }

    static def test2b():void {
        val e = new java.util.ArrayList(){};
        at (Place.places().next(here)) {
            val tmp = e;
        }
        Console.OUT.println("test2b passed");
    }
        
    public def run(): Boolean = {
        test1();
        test2a();
        test2b();
        return true;
    }

    public static def main(args: Rail[String]) {
        new JavaSerialization2().execute();
    }

}
