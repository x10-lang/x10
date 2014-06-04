/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.DenseMultXTen;
import x10.matrix.blas.DenseMatrixBLAS;

public class DenseBlasBench {
	public static def main(args:Rail[String]) {
		val M = args.size > 0 ? Long.parse(args(0)):50;
		val K = args.size > 1 ? Long.parse(args(1)):M;
		val N = args.size > 2 ? Long.parse(args(2)):M;
		val iter = args.size > 3 ? Long.parse(args(3)):1;
		val ps = args.size > 4 ? Long.parse(args(4)):0;
		val tc = new RunBlasComp(M, K, N, iter);
		tc.run();
	}
}

class RunBlasComp {
	public val iter:Long;
	public val M:Long;
	public val N:Long;
	public val K:Long;

	val A:DenseMatrix(M, K);
	val B:DenseMatrix(K, N);
	val tB:DenseMatrix(N, K);
	val C:DenseMatrix(M, N);
	
	public def this(m:Long, k:Long, n:Long, it:Long) {
		M = m; N = n; K=k; iter=it;
		
		A  = DenseMatrix.make(M, K);
		B  = DenseMatrix.make(K, N);
		tB = DenseMatrix.make(N, K);
		C  = DenseMatrix.make(M, N);
	}
	
	public def compMFPS(t:Double) = 2.0*M*N*K/(1024*1024*t);

    public def run(): void {
		Console.OUT.println("Starting dense multiply benchamrks tests on "+
							M+"x"+K+" * "+K+"x"+N+" matrices");
		A.initRandom();
		B.initRandom();
		tB.initRandom();
		
		testBlasMult();
		testBlasMultTrans();
	}
    
    public def testBlasMult():Boolean {
    	Console.OUT.printf("\nTest BLAS multiplication driver: (%dx%d) * (%dx%d)\n",
    						M, K, K, N);

    	C.init(0.1/7);

    	val stt = Timer.milliTime();
    	for (1..iter) {
    		DenseMatrixBLAS.comp(1.0, A, B, 1.0, C);
    	}
    	val avgt= (1.0*Timer.milliTime()-stt) /1000/iter;
    	Console.OUT.printf("Benchmark BLAS driver dense mult --- Time:%8.3f Sec, Mfps:%f\n", 
    						avgt, compMFPS(avgt));
    	return true;
    }

    public def testBlasMultTrans():Boolean {
    	Console.OUT.printf("\nTest BLAS multiplication driver: (%dx%d) * (%dx%d)\n",
    						M, K, K, N);

    	C.init(0.1/7);

    	val stt = Timer.milliTime();
    	for (1..iter) {
    		DenseMatrixBLAS.compMultTrans(1.0, A, tB, 1.0, C);
    	}
    	val avgt= (1.0*Timer.milliTime()-stt) /1000/iter;
    	Console.OUT.printf("Benchmark BLAS driver multTrans --- Time:%8.3f Sec, Mfps:%f\n", 
    						avgt, compMFPS(avgt));
    	return true;
    }

}
