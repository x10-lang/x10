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
 * Tests performance of filling arrays of different base 
 * types with the zero value
 * @author milthorpe 06/2011
 */
public class BenchmarkFillArray extends x10Test {
    static val REPS = 1000;
    public val N : Int;
    public def this(N : Int) {
        this.N = N;
    }
	public def run(): Boolean = {
        val a = new Array_1[Char](N+1);
        var start : Long = System.nanoTime();
        for (i in 0..REPS) {
            a.fill('\0');
        }
        var stop : Long = System.nanoTime();
        Console.OUT.printf("fill Array[Char]: %g ms\n", ((stop-start) as Double) / REPS / 1e6);

        val b = new Array_1[Int](N+1);
        start = System.nanoTime();
        for (i in 0..REPS) {
            b.fill(0n);
        }
        stop = System.nanoTime();
        Console.OUT.printf("fill Array[Int]: %g ms\n", ((stop-start) as Double) / REPS / 1e6);

        val c = new Array_1[Double](N+1);
        start = System.nanoTime();
        for (i in 0..REPS) {
            c.fill(0.0);
        }
        stop = System.nanoTime();
        Console.OUT.printf("fill Array[Double]: %g ms\n", ((stop-start) as Double) / REPS / 1e6);

        val d = new Array_1[Complex](N+1);
        start = System.nanoTime();
        for (i in 0..REPS) {
            d.fill(Complex.ZERO);
        }
        stop = System.nanoTime();
        Console.OUT.printf("fill Array[Complex]: %g ms\n", ((stop-start) as Double) / REPS / 1e6);

        return true;
	}

	public static def main(var args: Rail[String]): void = {
        var n : Int = 100000n;
        if (args.size > 0) {
            n = Int.parseInt(args(0));
        }
	new BenchmarkFillArray(n).execute();
	}

}
