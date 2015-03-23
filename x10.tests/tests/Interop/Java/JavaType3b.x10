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
import x10.interop.Java;

// MANAGED_X10_ONLY

// SKIP_MANAGED_X10 : XTENLANG-3318 : Managed X10 loses generic RTT info for Comparable

public class JavaType3b extends x10Test {

    def test():Boolean {
        var fails:Long = 0;
        val a:Any = Java.newArray[Comparable[Int]](1n);
        
        // implementation limintation: x.l.Comparable[T] is mapped to j.l.Comparable<T> thus no runtime type for T.
        val s = a.typeName();
        if (!"x10.interop.Java.array[x10.lang.Comparable[x10.lang.Int]]".equals(s)) {
            Console.OUT.println("Failed: s = " + s + ". This is an implementation limitation.");
            ++fails;
        }
        
        val b1 = a instanceof Java.array[Comparable[Int]];
        if (!b1) {
            Console.OUT.println("Failed: b1 = " + b1);
            ++fails;
        }
        
        // implementation limintation: x.l.Comparable[T] is mapped to j.l.Comparable<T> thus no runtime type for T.
        val b2 = a instanceof Java.array[Comparable[String]];
        if (b2) {
            Console.OUT.println("Failed: b2 = " + b2 + ". This is an implementation limitation.");
            ++fails;
        }
        
        return fails == 0;
    }

    public def run(): Boolean {
        var ok:Boolean = true;
        ok &= test();
        return ok;
    }

    public static def main(args: Rail[String]) {
        new JavaType3b().execute();
    }
}
