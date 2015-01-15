/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.util.Debug;

import x10.matrix.block.Grid;

import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;
import x10.matrix.dist.summa.SummaSparse;

public class DistSparseBench {
	public static def main(args:Rail[String]) {
		val M = args.size > 0 ? Long.parse(args(0)):1000;
		val K = args.size > 1 ? Long.parse(args(1)):1000;
		val N = args.size > 2 ? Long.parse(args(2)):1000;
		val nzD = args.size > 3 ?Float.parse(args(3)):0.1f;
		val iter = args.size > 4 ? Long.parse(args(4)):1;
		val ps = args.size > 5 ? Long.parse(args(5)):0;
		val tc = new RunDistSparseBench(M, K, N, nzD, iter, ps);
		tc.run();
	}
}

class RunDistSparseBench{
	public val M:Long, N:Long, K:Long, iter:Long, nzD:ElemType, pCmp:ElemType;
	public val testps:Long; lastps:Long;
	public val nplace = Place.numPlaces();

	public val aPart:Grid;
	public val bPart:Grid;
	public val btPart:Grid;
	public val cPart:Grid;
	
	val A:DistSparseMatrix(aPart.M, aPart.N);
	val B:DistSparseMatrix(bPart.M, bPart.N);
	val tB:DistSparseMatrix(btPart.M, btPart.N);
	val C:DistDenseMatrix(cPart.M, cPart.N);
	
	public def this(m:Long, k:Long, n:Long, nzd:Float, it:Long, p:Long) {
		M = m; N = n; K=k; iter=it; nzD =nzd; pCmp=nzD*nzD;
		aPart = Grid.make(M, K);
		bPart = Grid.make(K, N);
		btPart = Grid.make(N, K);
		cPart = Grid.make(M, N);
		
		A  = DistSparseMatrix.make(aPart, nzD);
		B  = DistSparseMatrix.make(bPart, nzD);
		tB = DistSparseMatrix.make(btPart, nzD);
		C  = DistDenseMatrix.make(cPart);
		
		if (p != 0L) {
			testps = p;	lastps = p;
		} else {
			testps = 1;	
			val lps = Math.min(aPart.getMinColSize(), bPart.getMinRowSize()); 
			lastps=Math.min(lps, 256L);		}
	}
	public def compMFPS(t:ElemType) = 2.0*pCmp*M*N*K/(t*1000*1000*aPart.size);
	
    public def run(): void {
		Console.OUT.println("Starting sparse matrix  multiply benchamrks tests ov"+
							M+"x"+K+" * "+K+"x"+N+" matrices, sparsity:"+nzD+" over"+
							aPart.numRowBlocks+"x"+aPart.numColBlocks+" places");
		Debug.flushln("Start init matrices");
		A.initRandom();
		B.initRandom();
		tB.initRandom();
		
		testSummaSparseMult();
		testSummaSparseMultTrans();
	}
    
    public def testSummaSparseMult():Boolean {
    	Console.OUT.printf("\nTest dist sparse multiply: (%dx%d) * (%dx%d) over %dx%d places\n",
    			M, K, K, N, aPart.numRowBlocks, aPart.numColBlocks);
    	Debug.flushln("Start computation");
    	
    	for (var ps:Long=testps; ps <=lastps; ps*=2) {
    		C.init(0.1/7);
    		C.distBs(here.id()).calcTime=0; 
    		C.distBs(here.id()).commTime=0; 
    		
    		val stt = Timer.milliTime();
    		for (1..iter) {
    			SummaSparse.mult(ps, 1.0, A, B, C);
    		}
    		val avgt= (1.0*Timer.milliTime()-stt) /1000/iter;
    		val cmut= 1.0*C.getCommTime()/1000/iter;
    		val cmpt= 1.0*C.getCalcTime()/1000/iter;
    		
    		Console.OUT.printf("Benchmark dist sparse mult --- Panelsize:%4d, Time:%9.3f Sec, Mfps:%8.3f per place (cmu:%8.3f cmp:%8.3f)\n", 
    							ps, avgt, compMFPS(avgt), cmut, cmpt);
    	}
    	return true;
    }

    public def testSummaSparseMultTrans():Boolean {
    	Console.OUT.printf("\nTest dist sparse multTrans: (%dx%d) * (%dx%d)^T over %dx%d place\n",
    			M, K, N, K, aPart.numRowBlocks, aPart.numColBlocks);

    	Debug.flushln("Start computation");
    	for (var ps:Long=testps; ps <=lastps; ps*=2) {
    		C.init(0.1/7);
    		C.distBs(here.id()).calcTime=0; 
    		C.distBs(here.id()).commTime=0; 
    		
    		val stt = Timer.milliTime();
    			for (1..iter) {
    				SummaSparse.multTrans(ps, 1.0, A, tB, C);
    			}
    		val avgt= (1.0*Timer.milliTime()-stt) /1000/iter;
    		val cmut= 1.0*C.getCommTime()/1000/iter;
    		val cmpt= 1.0*C.getCalcTime()/1000/iter;

    		Console.OUT.printf("Benchmark dist sparse multTrans --- Panelsize:%4d, Time:%9.3f Sec, Mfps:%8.3f per place (cmu:%8.3f cmp:%8.3f)\n", 
    							ps, avgt, compMFPS(avgt), cmut, cmpt);
    	}
    	return true;
    }
}
