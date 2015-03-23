/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014.
 */
import harness.x10Test;

import x10.util.Team;

/**
 * Benchmarks performance of Team.barrier
 */
public class BenchmarkBarrier extends x10Test {
    private static ITERS = 1000;

	public def run(): Boolean {
        finish for (place in Place.places()) at (place) async {
            val start = System.nanoTime();
            for (iter in 1..ITERS) {
                Team.WORLD.barrier();
            }
            val stop = System.nanoTime();

            if (here == Place.FIRST_PLACE) Console.OUT.printf("barrier: %g ms\n", ((stop-start) as Double) / 1e6 / ITERS);
        }

        return true;
	}

	public static def main(var args: Rail[String]): void {
		new BenchmarkBarrier().execute();
	}
}
