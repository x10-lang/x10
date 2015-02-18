/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.util.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.blas.DenseMatrixBLAS;
import x10.matrix.block.Grid;

import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DistVector;
import x10.matrix.distblock.DupVector;


/**
 * Test benchmark Matrix-Vector multiplication.
 * Matrix is partitioned in blocks and has horizontal distribution, while vector is replicated in all places.
 * The result is stored in distributed vector.
 */
public class DistDupToDist{
    public static def main(args:Rail[String]) {
    	val M   = args.size > 0 ? Long.parse(args(0)):100;
    	val bM  = args.size > 1 ? Long.parse(args(1)):-1;
    	val nzd = args.size > 2 ? Double.parse(args(2)):0.5;
    	val it  = args.size > 3 ? Long.parse(args(3)):3;
    	val vrf = args.size > 4 ? Long.parse(args(4)):0;
   	
		val testcase = new DistDupToDist(M, bM, nzd, it, vrf);
		testcase.run();
    }
    
	val it:Long;
	val vrf:Long;

	val M:Long;
	val bM:Long;
	
	val dstrA:DistBlockMatrix(M,M);
	val dupV:DupVector(M);
	val dstrP:DistVector(M);
	val P:Vector(M);
	
    public def this(m:Long, b:Long, nnz:Double, i:Long, v:Long) {
    	val pM = Place.numPlaces();
    	M=m;
    	it = i; vrf=v;
    	bM = b<0?pM:b;
    	
    	dstrA = (nnz > 0.99) ? DistBlockMatrix.makeDense(M, M, bM, 1, pM, 1): 
    		DistBlockMatrix.makeSparse(M, M, bM, 1, pM, 1, nnz);
    	dupV  = DupVector.make(M);
    	dstrP = DistVector.make(M, dstrA.getAggRowBs());
    	P     = Vector.make(M);
    	
    	Console.OUT.printf("\nTest MatVec mult. Matrix(%d,%d) partitioned in (%dx%d) blocks, distr in (%dx%d) places\n",
    			M, M, pM, 1, pM, 1);
    	if (nnz > 0.99) 
    		Console.OUT.println("Matrix has dense blocks");
    	else
    		Console.OUT.println("Matrix has sparse blocks, sparsity:"+nnz);
    	dstrA.initRandom();
    	dupV.local().initRandom();
    }
	
	public def run(): void {
		val stt = Timer.milliTime();
		dupV.sync();
		for (1..it)
			dstrP.mult(dstrA, dupV);
		dstrP.copyTo(P);
		val tt  = (Timer.milliTime() - stt) as Double;
		
		if (vrf > 0) {
			Console.OUT.println("Start verification\n"); Console.OUT.flush();
			val dA = dstrA.toDense();
			val dV = dupV.local(); 
			val dP = Vector.make(M);

			dP.mult(dA, dV);
			
			if (dstrP.equals(dP)) {
				Console.OUT.println("Verification of DistMatrix * DupVecto -> DistVector pass");
			} else {
				Console.OUT.println("Verification failed in  DistMatrix * DupVecto -> DistVector pass");
			}
		}
		val cmpTime:Double = dstrP.getCalcTime() as Double;
		val cmmTime:Double = dstrP.getCommTime() + dupV.getCommTime();
		
		Console.OUT.printf("MatVec DistDupToDist total: %.3f ms, calc time: %.3f ms, comm time: %.3f ms\n",
				tt/it, cmpTime/it, cmmTime/it);
	}
}

