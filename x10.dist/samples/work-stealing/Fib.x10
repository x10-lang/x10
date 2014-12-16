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

/**
 * Fibonacci, parallel version
 */

class Fib {
    static def fib(n:Long):Long {
        val t1:Long;
        val t2:Long;
        if (n < 2) return 1;
        finish {
            async t1 = fib(n-1);
            t2 = fib(n-2);
        }
        return t1 + t2;
    }

    public static def main(args:Rail[String]) {
        val n = args.size > 0 ? Long.parse(args(0)) : 10;
        var avgDur:Double = 0;
        for (1..10) {
            val startTime = System.nanoTime();
            val r = fib(n);
            val duration = ((System.nanoTime() - startTime) as Double)/1e9;
            avgDur += duration;
            Console.OUT.println("Fib("+ n +")=" + r.toString());
            Console.OUT.printf("Time: %7.3f\n", duration);
        }
        Console.OUT.printf("------------------- Average Time: %7.3f\n", avgDur / 10);
    }
}
