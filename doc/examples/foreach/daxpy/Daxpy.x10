/*
 * This file is part of ANUChem.
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2013.
 *  (C) Copyright IBM Corporation 2014.
 */

// import x10.compiler.Foreach;
import x10.util.foreach.*;

/**
 * Benchmarks simple DAXPY operation
 * @author milthorpe 07/2013
 */
public class Daxpy(N:Long) {
    private static ITERS = 1000;

    public def this(N:Long) {
        property(N);
    }

    private def daxpy(alpha:Double, x:Rail[Double], y:Rail[Double]) {
        val start = 0;
        val end = x.size-1;

/*
        for (i in start..end) {
            x(i) = alpha * x(i) + y(i);
        }
*/

        // val body = (i:Long) => {
        //     x(i) = alpha * x(i) + y(i);
        // };

/*
        // sequential
        for (i in start..end) {
            body(i);
        }
*/
        //Foreach.basic(start, end, body);
        // Foreach.block(start, end, body);
        //Foreach.cyclic(start, end, body);
        //Foreach.bisect(start, end, body);

	Block.for(i:Long in start..end) {
	    x(i) = alpha * x(i) + y(i);
	}

    }

	public def testAll() {
        Console.OUT.println("X10 DAXPY");
        var start:Long;
        var stop:Long;

        val alpha = 2.5;
        val x = new Rail[Double](N, (i:Long) => i as Double);
        val y = new Rail[Double](N, (i:Long) => i as Double);
        start = System.nanoTime();
        for (iter in 1..ITERS) {
            daxpy(alpha, x, y);
        }
        stop = System.nanoTime();
        Console.OUT.printf("vector size: %d X10_NTHREADS: %d time per iteration: %g ms\n", N, x10.xrx.Runtime.NTHREADS, ((stop-start) as Double) / 1e6 / ITERS);
	}

	public static def main(args:Rail[String]): void {
        var size:Long = 100000;
        var print:Boolean = false;
        if (args.size > 0) {
            size = Long.parse(args(0));
        }
		new Daxpy(size).testAll();
	}
}

