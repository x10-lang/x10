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
 * Matrix is partitioned in blocks and has horizontal distribution, while vector is partioned correspondently.
 * The result is stored in duplication vector, where a reduce operation is performed, and only root (place 0)
 * has the final result.
 */
public class DistDistToDup{
    public static def main(args:Rail[String]) {
    	val M   = args.size > 0 ? Long.parse(args(0)):100;
    	val bM  = args.size > 1 ? Long.parse(args(1)):-1;
    	val nzd = args.size > 2 ? Double.parse(args(2)):0.5;
    	val it  = args.size > 3 ? Long.parse(args(3)):3;
    	val vrf = args.size > 4 ? Long.parse(args(4)):0;
   	
		val testcase = new DistDistToDup(M, bM, nzd, it, vrf);
		testcase.run();
    }
    
	val it:Long;
	val vrf:Long;

	val M:Long;
	val bN:Long;
	
	val dstrA:DistBlockMatrix(M,M);
	val dstrV:DistVector(M);
	val V:Vector(M);
	val dupP:DupVector(M);
	
    public def this(m:Long, b:Long, nnz:Double, i:Long, v:Long) {
    	val pN = Place.numPlaces();
    	M=m;
    	it = i; vrf=v;
    	bN = b<0?pN:b;
    	
    	dstrA = (nnz > 0.99) ? DistBlockMatrix.makeDense(M, M, 1, bN, 1, pN): 
    		DistBlockMatrix.makeSparse(M, M, 1, bN, 1, pN, nnz);
    	dstrV = DistVector.make(M, dstrA.getAggColBs());
    	V     = Vector.make(M);
    	dupP  = DupVector.make(M);    	

    	Console.OUT.printf("\nTest MatVec mult. Matrix(%d,%d) partitioned in (%dx%d) blocks, distr in (%dx%d) places\n",
    			M, M, 1, bN, 1, pN);
    	if (nnz > 0.99)
    		Console.OUT.println("Matrix has dense blocks\n");
    	else
    		Console.OUT.println("Matrix has sparse blocks, sparsity:"+nnz);
    	dstrA.initRandom();
    	V.initRandom();
    }
	
	public def run(): void {
		val stt = Timer.milliTime();
		dstrV.copyFrom(V);
		for (1..it)
			dupP.mult(dstrA, dstrV);
		val tt  = (Timer.milliTime() - stt) as Double;
		
		if (vrf > 0) {
			Console.OUT.println("Start verification\n"); Console.OUT.flush();

			val dA = dstrA.toDense();
			val dP = Vector.make(M);

			dP.mult(dA, V);
			
			if (dP.equals(dupP.local() as Vector(M))) {
				Console.OUT.println("Verification of DistMatrix * DistVector -> DupVector pass");
			} else {
				Console.OUT.println("Verification failed in  DistMatrix * DistVecto -> DupVector pass");
			}
		}
		val cmpTime:Double = dupP.getCalcTime();
		val cmmTime:Double = dupP.getCommTime() + dstrV.getCommTime();
		
		Console.OUT.printf("MatVec DistDistToDup: total: %.3f ms, calc time: %.3f ms, comm time: %.3f ms\n",
				tt/it, cmpTime/it, cmmTime/it);
	}
}

