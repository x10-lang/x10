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

// MANAGED_X10_ONLY

public class JavaComparable1 extends x10Test {

    static class C[T] implements Comparable[T] {
        public def compareTo(T) = 0n;
    }

    public def run(): Boolean = {
        val i = new Comparable[UInt]() {
            public def compareTo(UInt) = 0n;
        };
        val ir = i.compareTo(1UN);
        val c = new C[UInt]();
        val cr = c.compareTo(2UN);
        return true;
    }

    public static def main(args: Rail[String]) {
        new JavaComparable1().execute();
    }
}
