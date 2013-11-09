/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2010.
 */
import harness.x10Test;
import x10.regionarray.*;

/**
 * Tests base performance of finish/ateach
 * @author milthorpe 09/2010
 */
public class BenchmarkAteach extends x10Test {

	public def run(): Boolean = {
        val a = DistArray.make[Long](Dist.makeUnique());

        val start = System.nanoTime();
        finish {
            ateach (p in a.dist) {
                a(p) = here.id;
            }
        }
        val stop = System.nanoTime();

        Console.OUT.printf("ateach: %g ms\n", ((stop-start) as Double) / 1e6);
        return true;
	}

	public static def main(var args: Rail[String]): void = {
		new BenchmarkAteach().execute();
	}

}
