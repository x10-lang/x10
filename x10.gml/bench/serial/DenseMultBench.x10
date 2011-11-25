/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

import x10.io.Console;
import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.DenseMultXTen;
import x10.matrix.VerifyTools;
import x10.matrix.blas.DenseMultBLAS;


/**
   <p>

   <p>
 */
public class DenseMultBench {
	public static def main(args:Array[String](1)) {
		val M = args.size > 0 ?Int.parse(args(0)):50;
		val K = args.size > 1 ?Int.parse(args(1)):M;
		val N = args.size > 2 ?Int.parse(args(2)):M;
		val iter = args.size > 3 ? Int.parse(args(3)):1;
		val ps = args.size > 4 ? Int.parse(args(4)):0;
		val tc = new RunDenseComp(M, K, N, iter);
		tc.run();
	}
}

class RunDenseComp{

	public val iter:Int;
	public val M:Int;
	public val N:Int;
	public val K:Int;

	val A:DenseMatrix(M, K);
	val B:DenseMatrix(K, N);
	val tB:DenseMatrix(N, K);
	val C:DenseMatrix(M, N);
	
	public def this(m:Int, k:Int, n:Int, it:Int) {
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
		Debug.flushln("Start init matrices");
		A.initRandom();
		B.initRandom();
		tB.initRandom();
		
		testX10Mult();
		testX10MultTrans();
	}
    
    public def testX10Mult():Boolean {
    	Console.OUT.printf("\nTest X10 dense multiplication X10 driver: (%dx%d) * (%dx%d)\n",
    			M, K, K, N);
    	
    	C.init(0.1/7);
    	Debug.flushln("Start computation");
    	val stt = Timer.milliTime();
    	for (1..iter) {
    		DenseMultXTen.comp(A, B, C, true);
    	}
    	val avgt= (1.0*Timer.milliTime()-stt) /1000/iter;
    	Console.OUT.printf("Benchmark X10 driver dense mult --- Time:%8.3f Sec, Mfps:%f\n", 
    						avgt, compMFPS(avgt));
    	return true;
    }


    public def testX10MultTrans():Boolean {
    	Console.OUT.printf("\nTest X10 dense multply transpose X10 driver: (%dx%d) * (%dx%d)^T\n",
    			M, K, N, K);

    	C.init(0.1/7);
    	Debug.flushln("Start computation");
    	
    	val stt = Timer.milliTime();
    	for (1..iter) {
    		DenseMultXTen.compMultTrans(A, tB, C, true);
    	}
    	val avgt= (1.0*Timer.milliTime()-stt) /1000/iter;
    	Console.OUT.printf("Benchmark X10 driver dense multTrans --- Time:%8.3f Sec, Mfps:%f\n", 
    						avgt, compMFPS(avgt));
    	return true;
    }
}
