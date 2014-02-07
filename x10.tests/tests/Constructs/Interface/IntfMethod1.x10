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

public class IntfMethod1 extends x10Test {
    static interface Comparator[T] {
        def compare(a:T, b:T):Int;
    }
    static class A[T] {
        public def compare(a:T, b:T):Any = null;
    }
    static class B[T] extends A[T] implements Comparator[T] {
        public def compare(a:T, b:T):Int = 0n;
    }
    public def run():Boolean = true;
    public static def main(Rail[String]) {
        new IntfMethod1().execute();
    }
}
