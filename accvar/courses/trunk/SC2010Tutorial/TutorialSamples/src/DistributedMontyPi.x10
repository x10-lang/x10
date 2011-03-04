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

import x10.io.Console;
import x10.util.Random;

/**
 * Parallel computation of the value of pi via
 * MonteCarlo simulation.
 */
public class DistributedMontyPi {
    public static def main(args:Array[String](1)) {
        val N = args.size > 0 ? Int.parse(args(0)) : 500000;
        val result = GlobalRef[Cell[Double]](new Cell[Double](0));
		finish for (p in Place.places()) async at (p) {
			val r = new Random();
			var myResult:Double = 0;
			for (1..N/Place.MAX_PLACES) {
				val x = r.nextDouble(); 
				val y = r.nextDouble();
				if (x*x + y*y <= 1) myResult++;
			}
			val ans = myResult;
			at (result) atomic result()() += ans;
		}
		val pi = 4*(result()())/N;
        Console.OUT.println("The value of pi is "+pi);
    }
}
