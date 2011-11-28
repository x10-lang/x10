/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.io.Console;
import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.blas.DenseMultBLAS;
import x10.matrix.block.Grid;

import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;
import x10.matrix.dist.DupDenseMatrix;

import x10.matrix.dist.DistMultDupToDist;
//import x10.matrix.dist.DistMultDistToDup;

/**
   This class contains test cases for dense matrix multiplication.
   <p>

   <p>
 */

public class MatVecMult{
	
    public static def main(args:Array[String](1)) {
    	
    	val M   = args.size > 0 ?Int.parse(args(0)):100;
    	val nnz = args.size > 1 ?Double.parse(args(1)):0.5;
    	val it  = args.size > 2 ?Int.parse(args(2)):3;
    	val vrf = args.size > 3 ?Int.parse(args(3)):0;
   	
		val testcase = new DVMultRowwise(M, nnz, it, vrf);
		testcase.run();
	}
}

class DVMultRowwise {
	val it:Int;
	val vrf:Int;
	
	//--------------
	val M:Int;
	val partA:Grid;
	val partP:Grid;
	
	val dstA:DistSparseMatrix(M,M);
	val dupV:DupDenseMatrix(dstA.N,1);
	val dstP:DistDenseMatrix(dstA.M,1);
	val P:DenseMatrix(dstA.M,1);
	
	//---------------------
	public var st:Double;
	public var ed:Double;
	public var cmpt:Double = 0.0;
	public var comt:Double = 0.0;

    public def this(m:Int, nnz:Double, i:Int, v:Int) {
    	M=m;
    	it = i; vrf=v;
    	
    	val numP = Place.numPlaces();//Place.MAX_PLACES;
    	Console.OUT.printf("\nTest Dist sparse mult Dup dense over %d places\n", numP);
 
    	partA = new Grid(M, M, numP, 1);
    	dstA  = DistSparseMatrix.make(partA, nnz) as DistSparseMatrix(M,M);
    	dstA.initRandom(nnz);
    	dstA.printRandomInfo();
    	Console.OUT.flush();
    	
    	dupV = DupDenseMatrix.makeRand(M, 1);
    	dupV.initRandom();

    	partP = new Grid(M, 1, numP, 1);
    	dstP  = DistDenseMatrix.make(partP) as DistDenseMatrix(M,1);
    	P	  = DenseMatrix.make(dstP.M, dstP.N);
	}
	
	public def run(): void {
		var ret:Boolean = true;
 		// Set the matrix function
		runMultParallel();
		if (vrf > 0)
			runVerify();

		if (ret)
			Console.OUT.println("Dist Dup multiplication Test passed!");
		else
			Console.OUT.println("--------Dist-Dup multiplication Test failed!--------");
	}
	//------------------------------------------------
	//------------------------------------------------
	public def runMultParallel():void {
		var ct:Long=0;
		st = Timer.milliTime();		
		for (1..it) {
			/* Timer */ ct = Timer.milliTime();
			DistMultDupToDist.comp(dstA, dupV, dstP, false);
			/* Timer */ cmpt += Timer.milliTime()-ct;
			
			/* Timer */ ct = Timer.milliTime();
			dstP.copyTo(dupV);
			/* Timer */ comt += Timer.milliTime() -ct;
		}
		ed = Timer.milliTime();
		Console.OUT.printf("\nDone Dist*Dup->Dist MatVecMult for %d iteration\n", it);

		val tt = (ed-st) / it;
		comt = comt/it;
		cmpt = comt/it;
		Console.OUT.printf("MatVecMult Time:%9.3f ms, communication:%8.3f computation:%8.3f\n", 
				tt, comt, cmpt);		
	}

	public def runVerify():Boolean {
		Console.OUT.printf("Starting converting sparse matrix to dense\n");
		val ma = dstA.toDense() as DenseMatrix(M,M);
		val mb = dupV.getMatrix() as DenseMatrix(M,1);
		val mc = DenseMatrix.make(ma.M, mb.N) as DenseMatrix(M,1);
		Console.OUT.printf("Starting verification on dense matrix\n");
		
		for (1..it) {
			DenseMultBLAS.comp(ma, mb, mc, false);
			mc.copyTo(mb);
		}
		
		val ret = mc.equals(dupV.local() as Matrix(mc.M, mc.N));
		if (ret)
			Console.OUT.println("Dist*Dup->Dist MatVecMult test passed!");
		else
			Console.OUT.println("-----Dist*Dup->Dist MatVecMult test failed!-----");
		return ret;
	}
}
