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

import x10.compiler.Foreach;

/**
 * Benchmarks naive matrix multiplication
 * @author milthorpe 07/2013
 */
public class MatMul(N:Long) {
    private static ITERS = 100;

    public def this(N:Long) {
        property(N);
    }

    private def matmul(a:Rail[Double], b:Rail[Double], c:Rail[Double]) {
/* original
        val indices = new DenseIterationSpace_2(0, 0, (N-1), (N-1));
        for ([i,j] in indices) {
            var x:Double = 0.0;
            for (k in 0..(N-1)) {
                x += a(i+k*N) * b(k+j*N);
            }
            c(i+j*N) = x;
        }
*/
        // NOTE: indices are in reversed order for column-major format
        val body = (j:Long, i:Long) => {
            var x:Double = 0.0;
            for (k in 0..(N-1)) {
                x += a(i+k*N) * b(k+j*N);
            }
            c(i+j*N) = x;
        };

/*
        // sequential
        for (j in 0..(N-1)) {
            for (i in 0..(N-1)) {
                body(j,i);
            }
        }
*/

        //Foreach.basic(0, N-1, 0, N-1, body);
        //Foreach.block(0, N-1, 0, N-1, body);
        //Foreach.cyclic(0, N-1, 0, N-1, body);
        Foreach.bisect(0, N-1, 0, N-1, body);
    }

	public def testAll() {
        Console.OUT.println("X10 matrix multiplication");
        var start:Long;
        var stop:Long;

        val a = new Rail[Double](N*N, (i:Long) => i as Double);
        val b = new Rail[Double](N*N, (i:Long) => i as Double);
        val c = new Rail[Double](N*N, (i:Long) => i as Double);
        start = System.nanoTime();
        for (iter in 1..ITERS) {
            matmul(a, b, c);
        }
        stop = System.nanoTime();
        Console.OUT.printf("matrix size: %d X10_NTHREADS: %d time per iteration: %g ms\n", N, x10.xrx.Runtime.NTHREADS, ((stop-start) as Double) / 1e6 / ITERS);
	}

	public static def main(args:Rail[String]): void {
        var size:Long = 500;
        var print:Boolean = false;
        if (args.size > 0) {
            size = Long.parse(args(0));
        }
		new MatMul(size).testAll();
	}
}
