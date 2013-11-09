/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2012.
 */
import harness.x10Test;

import x10.util.ArrayList;
import x10.util.Random;

/**
 * Performance test of ArrayList sort
 * @author milthorpe 05/2012
 */
public class BenchmarkArrayListSort extends x10Test {
    public val N : Int;
    public def this(N : Int) {
        this.N = N;
    }
	public def run(): Boolean = {
        val r = new Random();
        val a = new ArrayList[Double]();
        for (i in 1..N) {
            a.add(r.nextDouble());
        }

        var start : Long = System.nanoTime();
        var max : Double = -1.0;
        for (i in 1..10) {
            a.sort();
        }
        var stop : Long = System.nanoTime();
        Console.OUT.printf("sort ArrayList of size %d: %g ms\n", N, ((stop-start) as Double) / 1e6);

        // functional check
        var current:Double = -Double.MAX_VALUE;
        for (i in 0..(N-1)) {
            chk(current <= a(i));
            current = a(i);
        }

        return true;
	}

	public static def main(var args: Rail[String]): void = {
        var n : Int = 1000n;
        if (args.size > 0) {
            n = Int.parseInt(args(0));
        }
		new BenchmarkArrayListSort(n).execute();
	}

}
