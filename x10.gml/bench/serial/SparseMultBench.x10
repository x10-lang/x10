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
import x10.matrix.util.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.sparse.SparseMultSparseToDense;

public class SparseMultBench {
	public static def main(args:Rail[String]) {
		val M = args.size > 0 ? Long.parse(args(0)):1000;
		val K = args.size > 1 ? Long.parse(args(1)):M;
		val N = args.size > 2 ? Long.parse(args(2)):M;
		val nzD = args.size > 3 ?Double.parse(args(3)):0.1;
		val iter = args.size > 4 ? Long.parse(args(4)):1;
		val tc = new RunSparseComp(M, K, N, nzD, iter);
		tc.run();
	}
}

class RunSparseComp {
	public val iter:Long;
	public val M:Long;
	public val N:Long;
	public val K:Long;
	public val nzD:Double;
	public val pCmp:Double;

	val A:SparseCSC(M, K);
	val B:SparseCSC(K, N);
	val tB:SparseCSC(N, K);
	val C:DenseMatrix(M, N);
	
	public def this(m:Long, k:Long, n:Long, nzd:Double, it:Long) {
		M = m; N = n; K=k; iter=it; nzD=nzd;
		pCmp = nzd*nzd;
		
		A  = SparseCSC.make(M, K, nzd);
		B  = SparseCSC.make(K, N, nzd);
		tB = SparseCSC.make(N, K, nzd);
		C  = DenseMatrix.make(M, N);
	}

    public def run(): void {
		Console.OUT.println("Starting sparse matrix  multiply benchamrks tests on "+
							M+"x"+K+" * "+K+"x"+N+" matrices, sparsity:"+nzD);
		testX10Mult();
		testX10MultTrans();
	}
    
    public def compMFPS(t:Double) = 2.0*pCmp*M*N*K/(1024*1024*t);

    public def testX10Mult():Boolean {
    	Console.OUT.printf("\nTest X10 sparse multiplication X10 driver: (%dx%d) * (%dx%d)\n",
    			M, K, K, N);
    	
    	Debug.flushln("Start init matrices");
    	A.initRandom();
    	B.initRandom();
    	C.initRandom();
    	Debug.flushln("Start computation");
   	
    	val stt = Timer.milliTime();
    	for (1..iter) {
    		SparseMultSparseToDense.comp(A, B, C, true);
    	}
    	val avgt= (1.0*Timer.milliTime()-stt) /1000/iter;
    	Console.OUT.printf("Benchmark X10 driver sparse mult --- Time:%8.3f Sec, Mfps:%f\n", 
    						avgt, compMFPS(avgt));
    	return true;
    }

    public def testX10MultTrans():Boolean {
    	Console.OUT.printf("\nTest X10 sparse multply transpose X10 driver: (%dx%d) * (%dx%d)^T\n",
    			M, K, N, K);
    	
    	Debug.flushln("Start init matrices");
    	A.initRandom();
    	tB.initRandom();
    	C.initRandom();
    	Debug.flushln("Start computation");
    	
    	val stt = Timer.milliTime();
    	for (1..iter) {
    		SparseMultSparseToDense.compMultTrans(A, tB, C, true);
    	}
    	val avgt= (1.0*Timer.milliTime()-stt) /1000/iter;
    	Console.OUT.printf("Benchmark X10 driver sparse multTrans --- Time:%8.3f Sec, Mfps:%f\n", 
    						avgt, compMFPS(avgt));
    	return true;
    }
}
