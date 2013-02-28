/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.io.Console;
import x10.util.Timer;
import x10.array.DistArray;


import x10.matrix.Matrix;
import x10.matrix.Debug;
import x10.matrix.DenseMatrix;

import x10.matrix.block.Grid;
import x10.matrix.block.DenseBlockMatrix;

import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DupDenseMatrix;

import x10.matrix.comm.CommHandle;

/**
   This class contains test cases for dense and sparse matrix broadcast and other collective functions.
   <p>

   <p>
 */

public class TestColl{
    public static def main(args:Rail[String]) {
		val m = args.size > 0 ?Int.parse(args(0)):30;
		val n = args.size > 1 ?Int.parse(args(1)):m+1;

		val testcase = new RunDenseCollTest(m, n);
		testcase.run();
	}
}

class RunDenseCollTest {

	public val M:Int;
	public val N:Int;

	public val numplace:Int;
	public val gpart:Grid;
	public val gpartRow:Grid;
	
	public var syncTime:Long = 0;
	public var gatherTime:Long = 0;
	public var allgatherTime:Long = 0;
	public var reduceTime:Long = 0;
	
	
	val comm:CommHandle;

    public def this(m:Int, n:int) {
		M=m; N=n;

		numplace =  Place.numPlaces();
		gpart    =  Grid.make(M, N);   //square-like partition
		gpartRow =  new Grid(M, N, 1, numplace); //Single row block partition
		
		comm = new CommHandle();
	}
	
	public def run(): void {
		var ret:Boolean = true;
 		// Set the matrix function
 		ret &= (testBcast());
 		//ret &= (testRingCast());
 		ret &= (testGather());
 		ret &= (testGatherRowBs());
 		ret &= (testScatter());
 		ret &= (testScatterRowBs());
		//ret &= (testAllgather());
 		ret &= (testReduce());
 		ret &= (testAllReduce());

		if (ret)
			Console.OUT.println("Test Matrix collective commuication passed!");
		else
			Console.OUT.println("--------Test of Matrix collective communication failed!--------");
		}
	//------------------------------------------------
	//------------------------------------------------
	public def testBcast():Boolean {
		var ret:Boolean=true;
		Console.OUT.printf("\nTest dense matrix bcast of over %d places\n", numplace);

		val dupDA = DupDenseMatrix.make(M,N);
		val denA  = dupDA.local();
		denA.initRandom();

		comm.bcast(dupDA.dupMs);
	
		ret =dupDA.syncCheck();
		if (ret)
			Console.OUT.println("Dense matrix.bcast for dup matrix test passed!");
		else
			Console.OUT.println("-----Test dense matrix bcast failed!-----");
		return ret;
	}

	//------------------------------------------------
	public def testRingCast():Boolean {
		var ret:Boolean = true;
		Console.OUT.printf("\nTest dense matrix ring cast over %d places\n", numplace);

		val dupmat = DupDenseMatrix.make(M,N);
		val denA  = dupmat.local();
		denA.initRandom();

		comm.rcast(dupmat.dupMs);
		
		//dupDA.printAll("All copies:");

		ret =dupmat.syncCheck();
		if (ret)
			Console.OUT.println("Dense matrix ring rast test passed!");
		else
			Console.OUT.println("-----Test dense matrix ring cast failed!-----");
		return ret;
 	}

 	public def testGather():Boolean {
 		var ret:Boolean = true;
 		Console.OUT.printf("\nTest block gather of dist dense matrix over %d places\n", numplace);

 		val dstDM = DistDenseMatrix.make(gpart);
 		dstDM.initRandom();
		val blkDM = DenseBlockMatrix.make(gpart);

		Debug.flushln("Start gathering "+numplace+" places");
		comm.gather(dstDM.distBs, blkDM.listBs);
		Debug.flushln("Done");
		
		ret = dstDM.equals(blkDM as Matrix(gpart.M, gpart.N));
		Debug.flushln("Done with verify");

		if (ret) {
			val dm = DenseMatrix.make(gpart.M, gpart.N);
			Debug.flushln("Start copy from block matrix to dense matrix locally");
			blkDM.copyTo(dm);
			Debug.flushln("Done");
			ret &= dm.equals(blkDM as Matrix(gpart.M, gpart.N));
		}
 		if (ret)
 			Console.OUT.println("Test gather for dist dense matrix test passed!");
 		else
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

		Debug.flushln("Start single-row gather for matrix blocks");
		comm.gatherRowBs(gpartRow, distDM.distBs, DM);
		Debug.flushln("Done");

		ret = distDM.equals(DM);
		Debug.flushln("Done verification");

		if (ret)
			Console.OUT.println("Test single-row blocks gather for dist dense matrix test passed!");
		else
			Console.OUT.println("-----Test single-row block gather for dist dense matrix failed!-----");
		return ret;
	}

 	public def testScatter():Boolean {
 		var ret:Boolean = true;
 		Console.OUT.printf("\nTest block scatter of dense block matrix to %d places\n", numplace);

 		val dstDM = DistDenseMatrix.make(gpart);
		val blkDM = DenseBlockMatrix.make(gpart);
 		blkDM.initRandom();

		Debug.flushln("Start scattering data to "+numplace+" places");
		comm.scatter(blkDM.listBs, dstDM.distBs);
		Debug.flushln("Done");
		
		ret = dstDM.equals(blkDM as Matrix(gpart.M, gpart.N));
		Debug.flushln("Done with verify");

 		if (ret)
 			Console.OUT.println("Test scatter for dist dense matrix test passed!");
 		else
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

		Debug.flushln("Start single-row block scatter dense matrix");
		comm.scatterRowBs(gpartRow, DM, distDM.distBs);
		Debug.flushln("Done");

		ret = distDM.equals(DM);
		Debug.flushln("Done verification");

		if (ret)
			Console.OUT.println("Test single-row blocks scatter for dense matrix test passed!");
		else
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
// 		if (ret)
// 			Console.OUT.println("Test allgather for dist matrix test passed!");
// 		else
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
		
		//comm.reduceSum(dupDA.dupMs, tmpDA.dupMs);

		denDA.scale(numplace as Double);
		ret = denDA.equals(dupDA.local() as Matrix(denDA.M, denDA.N));

		if (ret)
				Console.OUT.println("Test reduce for dup matrix test passed!");
			else
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
		//comm.allReduceSum(dupDA.dupMs, tmpDA.dupMs);
		//dupDA.printAll("Result");
		denDA.scale(numplace);
		//denDA.print("verify");
		
		ret = denDA.equals(dupDA.local() as Matrix(denDA.M, denDA.N));
		if (ret && dupDA.syncCheck())
			Console.OUT.println("Test reduce for dup matrix test passed!");
		else
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
// 		if (ret)
// 			Console.OUT.println("Test ring cast for dup matrix test passed!");
// 		else
// 			Console.OUT.println("-----Test ring cast for dup matrix failed!-----");
// 		return ret;
// 	}
}
