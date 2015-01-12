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
//OPTIONS: -WORK_STEALING=true


import harness.x10Test;
/*
 * Collecting-Finish Fib
 */

class CollectingFinish1 extends x10Test {
    
    static struct Reducer implements Reducible[Long] {
        public def zero()=0;
        public operator this(a:Long,b:Long)=a+b;
    }
    
    static def fib(n:Long):Long {

        if (n < 2) return 1;
        val x = finish(Reducer()) {
            async {
                val t1 = fib(n-1);
                offer t1;
            }
            val t2 = fib(n-2);
            offer t2;
        };
        return x;
    }
    
    public def run():boolean {
        val r = fib(20);
        return (r == 10946);
    }
    
    public static def main(Rail[String]) {
        new CollectingFinish1().execute();
    }
    
}
