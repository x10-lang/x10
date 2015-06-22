/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011-2014.
 */

import harness.x10Test;

import x10.compiler.Ifndef;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;
import x10.matrix.sparse.SparseCSC;

import x10.matrix.block.Grid;
import x10.matrix.block.SparseBlockMatrix;
import x10.matrix.comm.MatrixBcast;
import x10.matrix.comm.MatrixGather;
import x10.matrix.comm.MatrixRingCast;
import x10.matrix.comm.MatrixScatter;
import x10.matrix.dist.DistSparseMatrix;
import x10.matrix.dist.DupDenseMatrix;
import x10.matrix.dist.DupSparseMatrix;

/**
 * This class contains test cases for dense and sparse matrix broadcast and other collective functions.
 */
public class TestSparseColl extends x10Test {

    static def ET(a:Double)= a as ElemType;
    static def ET(a:Float)= a as ElemType;
	public val M:Long;
	public val N:Long;
	public val nzdensity:Float;

	public val numplace:Long;
	public val gpart:Grid;
	public val gpartRow:Grid;
	
	public var syncTime:Long = 0;
	public var gatherTime:Long = 0;
	public var allgatherTime:Long = 0;
	public var reduceTime:Long = 0;
	
    public def this(m:Long, n:Long, nzd:Float) {
		M=m; N=n;
		nzdensity = nzd;

		numplace =  Place.numPlaces();
		gpart    =  Grid.make(M, N);   //square-like partition
		gpartRow =  new Grid(M, N*numplace, 1, numplace); //Single row block partition
	}
	
    public def run():Boolean {
		var ret:Boolean = true;
	@Ifndef("MPI_COMMU") { // TODO Deadlocks!
  		ret &= (testSparseBcast());
 		ret &= (testSparseRingCast());
		ret &= (testSparseGather());
		ret &= (testSparseGatherRowBs());
		ret &= (testSparseScatter());
		ret &= (testSparseScatterRowBs());
// 		ret &= (testAllgather());
// 		ret &= (testReduce());
// 		ret &= (testAllReduce());
    }
        return ret;
	}

	public def testSparseBcast():Boolean {
		var ret:Boolean=true;
		Console.OUT.printf("\nTest sparse matrix bcast of over %d places\n", numplace);

		val dupSM = DupSparseMatrix.make(M,N, nzdensity);
		val spa   = dupSM.local();
		spa.initRandom(nzdensity);

		MatrixBcast.bcast(dupSM.dupMs);

		ret =dupSM.syncCheck();
		if (!ret)
			Console.OUT.println("-----Test sparse matrix bcast failed!-----");
		return ret;
	}

	public def testSparseRingCast():Boolean {
		var ret:Boolean = true;
		Console.OUT.printf("\nTest sparse matrix ring cast over %d places\n", numplace);

		val dupSM = DupSparseMatrix.make(M,N, nzdensity);
		val spaA  = dupSM.local();
		spaA.initRandom();

		MatrixRingCast.rcast(dupSM.dupMs);
		
		ret =dupSM.syncCheck();
		
		val dupDM = DupDenseMatrix.make(M,N);
		dupSM.copyTo(dupDM);

		ret &= dupDM.syncCheck();
		
		if (!ret)
			Console.OUT.println("-----Test sparse matrix ring cast failed!-----");
		return ret;
 	}

 	public def testSparseGather():Boolean {
 		var ret:Boolean = true;
 		Console.OUT.printf("\nTest block gather of dist sparse matrix over %d places\n", numplace);

 		val dstSM = DistSparseMatrix.make(gpart, nzdensity);
 		dstSM.initRandom();
		val blkSM = SparseBlockMatrix.make(gpart, nzdensity);

		MatrixGather.gather(dstSM.distBs, blkSM.listBs);
		
		ret = dstSM.equals(blkSM as Matrix(gpart.M, gpart.N));

		if (ret) {
			val dm = DenseMatrix.make(gpart.M, gpart.N);
			blkSM.copyTo(dm);
			ret &= dm.equals(blkSM as Matrix(gpart.M, gpart.N));
		}
 		if (!ret)
 			Console.OUT.println("-----Test gather for dist sparse matrix failed!-----");
 		return ret;
 	}

	public def testSparseGatherRowBs():Boolean {
		var ret:Boolean = true;
		Console.OUT.printf("\nTest row-block gather of dist sparse matrix over %d places\n", numplace);

		val distSM = DistSparseMatrix.make(gpartRow, nzdensity);
		distSM.initRandom();

		val SM = SparseCSC.make(gpartRow.M, gpartRow.N, nzdensity);

		MatrixGather.gatherRowBs(gpartRow, distSM.distBs, SM);

		ret = distSM.equals(SM);

		if (!ret)
			Console.OUT.println("-----Test row-gather for dist sparse matrix failed!-----");
		return ret;
	}

 	public def testSparseScatter():Boolean {
 		var ret:Boolean = true;
 		Console.OUT.printf("\nTest block scatter of sparse block matrix to %d places\n", numplace);

 		val dstSM = DistSparseMatrix.make(gpart, nzdensity);
		val blkSM = SparseBlockMatrix.make(gpart, nzdensity);
 		blkSM.initRandom();

		MatrixScatter.scatter(blkSM.listBs, dstSM.distBs);
		
		ret = dstSM.equals(blkSM as Matrix(gpart.M, gpart.N));

 		if (!ret)
 			Console.OUT.println("-----Test scatter for dist sparse matrix failed!-----");
 		return ret;
 	}

	public def testSparseScatterRowBs():Boolean {
		var ret:Boolean = true;
		Console.OUT.printf("\nTest single-row blocks scatter sparse block matrix over %d places\n", 
						   numplace);

		val SM = SparseCSC.make(gpartRow.M, gpartRow.N, nzdensity);
		val distSM = DistSparseMatrix.make(gpartRow, nzdensity);
		SM.initRandom();

		MatrixScatter.scatterRowBs(gpartRow, SM, distSM.distBs);

		ret = distSM.equals(SM);

		if (!ret)
			Console.OUT.println("-----Test single-row block scatter for sparse matrix failed!-----");
		return ret;
	}

// 	public def testAllgather():Boolean {
// 		var ret:Boolean = true;
// 		Console.OUT.printf("\nTest allgather of dist matrix over %d places\n", numplace);

// 		val distDA = DistDenseMatrix.make(gpartCol);
// 		distDA.initRandom();

// 		val dupDA = DupDenseMatrix.make(gpartCol.M,gpartCol.N);
		
// 		distDA.toDupDenseSync(dupDA);
	
// 		val da = dupDA.local();
// 		ret = dupDA.syncCheck() && distDA.equals(da);
// 		if (!ret)
// 			Console.OUT.println("-----Test allgather for dist matrix failed!-----");
// 		return ret;

// 	}
		
// 	public def testReduce(): Boolean {
// 		var ret:Boolean = true;
// 		Console.OUT.printf("\nTest reduce  of dup matrix over %d places\n", numplace);

// 		val dupDA = DupDenseMatrix.make(M,N);
// 		dupDA.initRandom();
// 		val denDA = dupDA.getMatrix().clone();

// 		val tmpDA = DupDenseMatrix.make(M,N);
		
// 		dupDA.reduceSum(tmpDA);
// 		denDA.scale(numplace as Double);
// 		ret = denDA.equals(dupDA.local() as Matrix(denDA.M, denDA.N));

// 		if (!ret)
// 				Console.OUT.println("-----Test reduce for dup matrix failed!-----");
// 		return ret;
// 	}

		
// 	public def testAllReduce(): Boolean {
// 		var ret:Boolean = true;
// 		Console.OUT.printf("\nTest all reduce of dup matrix over %d places\n", numplace);

// 		val dupDA = DupDenseMatrix.make(M,N);
// 		dupDA.initRandom();
// 		val denDA = dupDA.getMatrix().clone();

// 		val tmpDA = DupDenseMatrix.make(M,N);
		
// 		dupDA.allReduceSum(tmpDA);

// 		denDA.scale(numplace);
// 		ret = denDA.equals(dupDA.local() as Matrix(denDA.M, denDA.N));
// 		if (ret && dupDA.syncCheck())
// 			Console.OUT.println("Test reduce for dup matrix test passed!");
// 		else
// 			Console.OUT.println("-----Test reduce for dup matrix failed!-----");
	
// 		return ret;
// 	}

// 	public def testRingCast(): Boolean {

// 		Console.OUT.printf("\nTest ring cast of dup matrix over %d places\n", numplace);
// 		var ret:Boolean = true;
// 		var mpicom:MatrixCommMPI;
// 		var x10com:MatrixCommCopy;	

// 		val dupDA = DupDenseMatrix.make(M,N);
// 		val denA  = dupDA.local();
// 		denA.initRandom();

// 		val plist = new Array[Int](numplace, (i:Int)=>i);
		
// 		@Ifdef("MPI_COMMU") {
// 			{
// 				mpicom = new MatrixCommMPI(dupDA.dist);
// 				mpicom.ringCast(dupDA, plist);
// 			}
// 		}

// 		@Ifndef("MPI_COMMU") {
// 			{
// 				x10com  = new MatrixCommCopy();
// 				x10com.ringCast(dupDA, plist);
// 			}
// 		}

  		
// 		ret = dupDA.syncCheck();
// 		if (!ret)
// 			Console.OUT.println("-----Test ring cast for dup matrix failed!-----");
// 		return ret;
// 	}

    public static def main(args:Rail[String]) {
		val m = args.size > 0 ? Long.parse(args(0)):4;
		val n = args.size > 1 ? Long.parse(args(1)):m+1;
		val d = args.size > 2 ? Float.parse(args(2)):0.9f;

		new TestSparseColl(m, n, d).execute();
	}
}
