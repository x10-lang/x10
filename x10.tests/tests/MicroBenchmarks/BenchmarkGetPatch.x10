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

import x10.array.DenseIterationSpace_2;
import x10.array.DenseIterationSpace_3;
import x10.array.DistArray_Block_2;
import x10.array.DistArray_Block_3;

public class BenchmarkGetPatch(N:Long) extends x10Test {
    static ITERS = 1000;

    public def this(N:Long) {
        property(N);
    }

    public def run(): Boolean {
        benchmark2D();
        benchmark3D();

        return true;
    }

    public def benchmark2D() {
        val a = new DistArray_Block_2[Double](N, N, (i:Long, j:Long)=>(10*i+j) as Double);
        val localIndices = a.localIndices();
        val rightEdge = new DenseIterationSpace_2(localIndices.max0, localIndices.min1, localIndices.max0, localIndices.max1);

        var start : Long = System.nanoTime();
        for (t in 1..ITERS) {
            val patch = new Rail[Double](rightEdge.size());
            var patchIndex:Long = 0;
            for ([i,j] in rightEdge) {
                patch(patchIndex++) = a(i,j);
            }
        }
        var stop : Long = System.nanoTime();

        Console.OUT.printf("DistArray_Block_2 hand-written get patch: %g us\n", ((stop-start) as Double) / (1e3*ITERS));

        start = System.nanoTime();
        for (t in 1..ITERS) {
            val patch = a.getPatch(rightEdge);
        }
        stop = System.nanoTime();

        Console.OUT.printf("DistArray_Block_2.getPatch(): %g us\n", ((stop-start) as Double) / (1e3*ITERS));
    }

    public def benchmark3D() {
        val sqrtN = Math.sqrt(N) as Long;
        val a = new DistArray_Block_3[Double](N, sqrtN, sqrtN, (i:Long, j:Long, k:Long)=>(100*i+10*j+k) as Double);
        val localIndices = a.localIndices();
        val rightFace = new DenseIterationSpace_3(localIndices.max0, localIndices.min1, localIndices.min2, localIndices.max0, localIndices.max1, localIndices.max2);

        var start : Long = System.nanoTime();
        for (t in 1..ITERS) {
            val patch = new Rail[Double](rightFace.size());
            var patchIndex:Long = 0;
            for ([i,j,k] in rightFace) {
                patch(patchIndex++) = a(i,j,k);
            }
        }
        var stop : Long = System.nanoTime();

        Console.OUT.printf("DistArray_Block_3 hand-written get patch: %g us\n", ((stop-start) as Double) / (1e3*ITERS));

        start = System.nanoTime();
        for (t in 1..ITERS) {
            val patch = a.getPatch(rightFace);
        }
        stop = System.nanoTime();

        Console.OUT.printf("DistArray_Block_3.getPatch(): %g us\n", ((stop-start) as Double) / (1e3*ITERS));
    }

    public static def main(args:Rail[String]) {
        var N:Long = 100;
        if (args.size > 0) {
            N = Long.parse(args(0));
        }
        new BenchmarkGetPatch(N).execute();
    }
}
