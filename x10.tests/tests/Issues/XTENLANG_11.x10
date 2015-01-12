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

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_11 extends x10Test {

    class H {}
    
    class A[T] {
        public def add(v: T): void { throw new Exception(); }
        public def iterator(): Iterator[T] { throw new Exception(); }
    }
    
    public def foo(hl: A[H]) {
        val it  = hl.iterator();
        hl.add(it.next());
    }

    public def run()=true;

    public static def main(Rail[String]) {
        new XTENLANG_11().execute();
    }
}
