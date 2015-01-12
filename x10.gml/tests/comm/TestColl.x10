/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011-2015.
 */

import harness.x10Test;

import x10.regionarray.DistArray;
import x10.compiler.Ifndef;

import x10.matrix.Matrix;
import x10.matrix.util.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.block.Grid;
import x10.matrix.block.DenseBlockMatrix;
import x10.matrix.comm.MatrixBcast;
import x10.matrix.comm.MatrixGather;
import x10.matrix.comm.MatrixRingCast;
import x10.matrix.comm.MatrixScatter;
import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DupDenseMatrix;

/**
   This class contains test cases for dense and sparse matrix broadcast and other collective functions.
 */
public class TestColl extends x10Test {
	public val M:Long;
	public val N:Long;

	public val numplace:Long;
	public val gpart:Grid;
	public val gpartRow:Grid;
	
	public var syncTime:Long = 0;
	public var gatherTime:Long = 0;
	public var allgatherTime:Long = 0;
	public var reduceTime:Long = 0;
	
    public def this(m:Long, n:Long) {
		M=m; N=n;

		numplace =  Place.numPlaces();
		gpart    =  Grid.make(M, N);   //square-like partition
		gpartRow =  new Grid(M, N, 1, numplace); //Single row block partition
	}
	
    public def run():Boolean {
		var ret:Boolean = true;
	@Ifndef("MPI_COMMU") { // TODO Deadlocks!
 		ret &= (testBcast());
 		//ret &= (testRingCast());
 		ret &= (testGather());
 		ret &= (testGatherRowBs());
 		ret &= (testScatter());
 		ret &= (testScatterRowBs());
		//ret &= (testAllgather());
 		ret &= (testReduce());
 		ret &= (testAllReduce());
	}
        return ret;
    }

	public def testBcast():Boolean {
		var ret:Boolean=true;
		Console.OUT.printf("\nTest dense matrix bcast over %d places\n", numplace);

		val dupDA = DupDenseMatrix.make(M,N);
		val denA  = dupDA.local();
		denA.initRandom();

		MatrixBcast.bcast(dupDA.dupMs);
	
		ret =dupDA.syncCheck();
		if (!ret)
			Console.OUT.println("-----Test dense matrix bcast failed!-----");
		return ret;
	}


	public def testRingCast():Boolean {
		var ret:Boolean = true;
		Console.OUT.printf("\nTest dense matrix ring cast over %d places\n", numplace);

		val dupmat = DupDenseMatrix.make(M,N);
		val denA  = dupmat.local();
		denA.initRandom();

		MatrixRingCast.rcast(dupmat.dupMs);
		
		ret =dupmat.syncCheck();
		if (!ret)
			Console.OUT.println("-----Test dense matrix ring cast failed!-----");
		return ret;
 	}

 	public def testGather():Boolean {
 		var ret:Boolean = true;
 		Console.OUT.printf("\nTest block gather of dist dense matrix over %d places\n", numplace);

 		val dstDM = DistDenseMatrix.make(gpart);
 		dstDM.initRandom();
		val blkDM = DenseBlockMatrix.make(gpart);

		MatrixGather.gather(dstDM.distBs, blkDM.listBs);
		
		ret = dstDM.equals(blkDM as Matrix(gpart.M, gpart.N));

		if (ret) {
			val dm = DenseMatrix.make(gpart.M, gpart.N);
			blkDM.copyTo(dm);
			ret &= dm.equals(blkDM as Matrix(gpart.M, gpart.N));
		}
 		if (!ret)
 			Console.OUT.println("-----Test gather for dist dense matrix failed!-----");
 		return ret;
 	}

	public def testGatherRowBs():Boolean {
		var ret:Boolean = true;
		Console.OUT.printf("\nTest single-row block gather of dist dense matrix over %d places\n", 
						   numplace);

		val distDM = DistDenseMatrix.make(gpartRow);
		distDM.initRandom();

		val DM = DenseMatrix.make(gpartRow.M, gpartRow.N);

		MatrixGather.gatherRowBs(gpartRow, distDM.distBs, DM);

		ret = distDM.equals(DM);

		if (!ret)
			Console.OUT.println("-----Test single-row block gather for dist dense matrix failed!-----");
		return ret;
	}

 	public def testScatter():Boolean {
 		var ret:Boolean = true;
 		Console.OUT.printf("\nTest block scatter of dense block matrix to %d places\n", numplace);

 		val dstDM = DistDenseMatrix.make(gpart);
		val blkDM = DenseBlockMatrix.make(gpart);
 		blkDM.initRandom();

		MatrixScatter.scatter(blkDM.listBs, dstDM.distBs);
		
		ret = dstDM.equals(blkDM as Matrix(gpart.M, gpart.N));

 		if (!ret)
 			Console.OUT.println("-----Test scatter for dist dense matrix failed!-----");
 		return ret;
 	}

	public def testScatterRowBs():Boolean {
		var ret:Boolean = true;
		Console.OUT.printf("\nTest single-row blocks scatter dense block matrix over %d places\n", 
						   numplace);

		val DM = DenseMatrix.make(gpartRow.M, gpartRow.N);
		val distDM = DistDenseMatrix.make(gpartRow);
		DM.initRandom();

		MatrixScatter.scatterRowBs(gpartRow, DM, distDM.distBs);

		ret = distDM.equals(DM);

		if (!ret)
			Console.OUT.println("-----Test single-row block scatter for dense matrix failed!-----");
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
		
	public def testReduce(): Boolean {
		var ret:Boolean = true;
		Console.OUT.printf("\nTest reduce  of dup matrix over %d places\n", numplace);

		val dupDA = DupDenseMatrix.make(M,N);
		dupDA.initRandom();
		val denDA = dupDA.getMatrix().clone();

		val tmpDA = DupDenseMatrix.make(M,N);
		dupDA.reduceSum();
		
		//MatrixReduce.reduceSum(dupDA.dupMs, tmpDA.dupMs);

		denDA.scale(numplace as Double);
		ret = denDA.equals(dupDA.local() as Matrix(denDA.M, denDA.N));

		if (!ret)
    		Console.OUT.println("-----Test reduce for dup matrix failed!-----");
		return ret;
	}

		
	public def testAllReduce(): Boolean {
		var ret:Boolean = true;
		Console.OUT.printf("\nTest all reduce of dup matrix over %d places\n", numplace);

		val dupDA = DupDenseMatrix.make(M,N);
		dupDA.initRandom();
		val denDA = dupDA.local().clone();

		val tmpDA = DupDenseMatrix.make(M,N);
		dupDA.allReduceSum();
		//MatrixReduce.allReduceSum(dupDA.dupMs, tmpDA.dupMs);
		denDA.scale(numplace);
		
		ret = denDA.equals(dupDA.local() as Matrix(denDA.M, denDA.N));
		if (!ret || !dupDA.syncCheck())
			Console.OUT.println("-----Test reduce for dup matrix failed!-----");
	
		return ret;
	}

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
		val m = args.size > 0 ? Long.parse(args(0)):30;
		val n = args.size > 1 ? Long.parse(args(1)):m+1;

		new TestColl(m, n).execute();
	}
}
