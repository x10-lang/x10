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

// SKIP_MANAGED_X10 :  XTENLANG-2761
// SKIP_NATIVE_X10 :  XTENLANG-2761

public class TestPropertyConstraintInheritance extends x10Test {
    public static class A(rank:Long) {
        public def this() {
            property(1);
        }
        public operator this(i0:Long){rank==1}:Long {
            return i0+1;
        }
    }

    public static class B extends A {
        public def this() {
            super();
        }
    }

    public def run(): Boolean {
        val b = new B();
        val x = b(0);
        return true;
    }

    public static def main(args:Rail[String]) {
        new TestPropertyConstraintInheritance().execute();
    }
}
