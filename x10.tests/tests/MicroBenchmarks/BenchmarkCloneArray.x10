/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2011.
 */

import harness.x10Test;
import x10.array.Array_1;

/**
 * Tests base performance of "at" closures with arrays 
 * of different base types (including cloning the array)
 * @author milthorpe 06/2011
 */
public class BenchmarkCloneArray extends x10Test {
    static val REPS = 100;
    public val N : Int;
    public def this(N : Int) {
        this.N = N;
    }
    public def run(): Boolean {
        val a = new Array_1[Char](N+1);
        for (i in 0..N) {
            a(i) = 'a';
        }
        val N = this.N;

        var start : Long = System.nanoTime();
        for (i in 0..REPS) {
            at (here) {
                val y = a(0);
            }
        }
        var stop : Long = System.nanoTime();
        Console.OUT.printf("clone Array[Char]: %g ms\n", ((stop-start) as Double) / REPS / 1e6);

        val b = new Array_1[Int](N+1);
        for (i in 0n..N) {
            b(i) = i;
        }

        start = System.nanoTime();
        for (i in 0..REPS) {
            at (here) {
                val y = b(0);
            }
        }
        stop = System.nanoTime();
        Console.OUT.printf("clone Array[Int]: %g ms\n", ((stop-start) as Double) / REPS / 1e6);

        val c = new Array_1[Double](N+1);
        for (i in 0..N) {
            c(i) = i as Double;
        }

        start = System.nanoTime();
        for (i in 0..REPS) {
            at (here) {
                val y = c(0);
            }
        }
        stop = System.nanoTime();
        Console.OUT.printf("clone Array[Double]: %g ms\n", ((stop-start) as Double) / REPS / 1e6);

        val d = new Array_1[Complex](N+1);
        for (i in 0..N) {
            d(i) = Complex.ONE;
        }

        start = System.nanoTime();
        for (i in 0..REPS) {
            at (here) {
                val y = d(0);
            }
        }
        stop = System.nanoTime();
        Console.OUT.printf("clone Array[Complex]: %g ms\n", ((stop-start) as Double) / REPS / 1e6);

        return true;
    }

    public static def main(var args: Rail[String]): void {
        var n : Int = 10000n;
        if (args.size > 0) {
            n = Int.parseInt(args(0));
        }
        new BenchmarkCloneArray(n).execute();
    }

}
