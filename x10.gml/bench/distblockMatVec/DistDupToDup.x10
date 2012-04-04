/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.io.Console;
import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.MathTool;
import x10.matrix.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.blas.DenseMatrixBLAS;
import x10.matrix.block.Grid;

import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DistVector;
import x10.matrix.distblock.DupVector;


/**
 * Test benchmark Matrix-Vector multiplication.
 * Matrix is partitioned in blocks and has horizontal distribution, while vector is duplicated in all places.
 * The result is stored in duplication vector, where a reduce operation is performed, and only root (place 0)
 * has the final result.
 */
public class DistDupToDup{
	
    public static def main(args:Array[String](1)) {
    	
    	val M   = args.size > 0 ?Int.parse(args(0)):100;
    	val bM  = args.size > 1 ?Int.parse(args(1)):-1;
    	val nzd = args.size > 2 ?Double.parse(args(2)):0.5;
    	val it  = args.size > 3 ?Int.parse(args(3)):3;
    	val vrf = args.size > 4 ?Int.parse(args(4)):0;
    	
   	
		val testcase = new DistDupToDup(M, bM, nzd, it, vrf);
		testcase.run();
    }
    
	val it:Int;
	val vrf:Int;
	
	//--------------
	val M:Int;
	val bN:Int;
	val bM:Int;
	
	val dstrA:DistBlockMatrix(M,M);
	val dupV:DupVector(M);
	val dupP:DupVector(M);
	

    public def this(m:Int, b:Int, nnz:Double, i:Int, v:Int) {
    	val pM = MathTool.sqrt(Place.MAX_PLACES);
    	val pN = Place.MAX_PLACES/pM;
    	M=m;
    	it = i; vrf=v;
    	bM = b<0?pM:b;
    	bN = b<0?pN:b;
    	 	
    	
    	dstrA = (nnz > 0.99) ? DistBlockMatrix.makeDense(M, M, bM, bN, pM, pN): 
    		DistBlockMatrix.makeSparse(M, M, bM, bN, pM, pN, nnz);
    	dupV = DupVector.make(M);
    	dupP = DupVector.make(M);    	

    	Console.OUT.printf("\nTest MatVec mult. Matrix(%d,%d) partitioned in (%dx%d) blocks, distr in (%dx%d) places\n",
    			M, M, bM, bN, pM, pN);
    	if (nnz > 0.99) 
    		Console.OUT.println("Matrix has dense blocks\n");
    	else
    		Console.OUT.println("Matrix has sparse blocks, sparsity:"+nnz);
    	dstrA.initRandom();
    	dupV.local().initRandom();
    }
	
	public def run(): void {
		
		val stt = Timer.milliTime();
		dupV.sync();
		for (1..it)
			dupP.mult(dstrA, dupV);
		val tt  = (Timer.milliTime() - stt) as Double;
		
		if (vrf > 0) {
			Console.OUT.println("Start verification\n"); Console.OUT.flush();
			val dA = dstrA.toDense();
			val dV = dupV.local(); 
			val dP = Vector.make(M);

			dP.mult(dA, dV);
			
			if (dP.equals(dupP.local() as Vector(M))) {
				Console.OUT.println("Verification of DistMatrix * DistVecto -> DupVector pass");
			} else {
				Console.OUT.println("Verification failed in  DistMatrix * DistVecto -> DupVector pass");
			}
		}
		val cmpTime:Double = dupP.getCalcTime() as Double;
		val cmmTime:Double = dupP.getCommTime() + dupV.getCommTime();
		
		Console.OUT.printf("MatVec DistDupToDup total: %.3f ms, calc time: %f ms, comm time: %f ms\n",
				tt/it, cmpTime/it, cmmTime/it);
	}
}

