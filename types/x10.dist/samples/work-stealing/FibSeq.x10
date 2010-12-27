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

/**
 * Fibonacci, sequential version
 */

class FibSeq {
    static def fib(n:Int):Int {
        val t1:Int;
        val t2:Int;
        if (n < 2) return 1;
        /* finish */ {
            /* async */ t1 = fib(n-1);
            t2 = fib(n-2);
        }
        return t1 + t2;
    }

    public static def main(args:Array[String](1)) {
        val n = args.size > 0 ? Int.parse(args(0)) : 10;
        var avgDur:Double = 0;
        for (var i:Int = 0; i < 10; ++i) {
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
