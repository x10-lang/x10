/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2015.
 */

import x10.compiler.Foreach;
import x10.compiler.WorkerLocal;

import x10.matrix.Vector;
import x10.matrix.sparse.SparseCSR;

/**
 * GML Sparse Matrix Vector multiplication kernel
 */
public class SpMV(N:Long) {
    private static ITERS = 100;

    public def this(N:Long) {
        property(N);
    }

	/**
	 * Multiply matrix with a segment of vector and store result in a segment of output vector
	 */
	public static def comp(A:SparseCSR, B:Vector, offsetB:Long, C:Vector, offsetC:Long):Vector(C) {
        val body = (row:Long) => {
			val rowA = A.getRow(row);
			for (cidx in 0..(rowA.size()-1)) {
				val col = rowA.getIndex(cidx);
				val v1 = rowA.getValue(cidx);
				C.d(row) += v1 * B(col);
			}
		};

        //Foreach.sequential(0, A.N-1, body);
        Foreach.block(0, A.N-1, body);
        //Foreach.bisect(0, A.N-1, body);
		
		return C;
	}

	public def testAll() {
        Console.OUT.println("X10 Sparse Matrix-Vector multiplication");

        val density = 0.001f;

        val a = SparseCSR.make(N, N, density).initRandom();
        val b = Vector.make(N).initRandom();
        val c = Vector.make(N);

        val start = System.nanoTime();
        for (iter in 1..ITERS) {
            comp(a, b, 0, c, 0);
        }
        val stop = System.nanoTime();
        Console.OUT.printf("matrix size: %d X10_NTHREADS: %d time per iteration: %g ms\n", N, x10.xrx.Runtime.NTHREADS, ((stop-start) as Double) / 1e6 / ITERS);
	}

	public static def main(args:Rail[String]): void {
        var size:Long = 200000;
        if (args.size > 0) {
            size = Long.parse(args(0));
        }
		new SpMV(size).testAll();
	}
}
