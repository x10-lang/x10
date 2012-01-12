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
import x10.matrix.sparse.SparseCSC;
import x10.matrix.block.MatrixBlock;
import x10.matrix.block.Grid;
import x10.matrix.distblock.DistBlockMatrix;		

import x10.matrix.comm.BlockBcast;


/**
   This class contains test cases P2P communication for matrix over different places.
   <p>

   <p>
 */

public class TestBlockColl{
    public static def main(args:Array[String](1)) {
		val m = args.size > 0 ?Int.parse(args(0)):2;
		val n = args.size > 1 ?Int.parse(args(1)):2;
		val bm= args.size > 2 ?Int.parse(args(2)):2;
		val bn= args.size > 3 ?Int.parse(args(3)):6;
		val d = args.size > 4 ? Double.parse(args(4)):0.80;
		val testcase = new BlockCollTest(m, n, bm, bn, d);
		testcase.run();
	}
}


class BlockCollTest {

	public val M:Int;
	public val N:Int;
	public val nzdensity:Double;
	public val bM:Int;
	public val bN:Int;
	
	public val numplace:Int;

	public val dbmat:DistBlockMatrix;
	public val sbmat:DistBlockMatrix;
	
	public val srcden:DenseMatrix;
	public val dstden:DenseMatrix(srcden.M, srcden.N);
	
	public val srcspa:SparseCSC;
	public val dstspa:SparseCSC;
	
	public val rmtcomm:BlockBcast;


    public def this(m:Int, n:Int, bm:Int, bn:Int, d:Double) {

		M=m; N=n;
		nzdensity = d;
		bM = bm; bN = bn;
		
		dbmat = DistBlockMatrix.makeDense(m*bm, n*bn, bm, bn);
		sbmat = DistBlockMatrix.makeSparse(m*bm, n*bn, bm, bn, nzdensity);
		
		srcden = dbmat.handleBS().getFirst().getMatrix() as DenseMatrix;
		srcden.init((x:Int, y:Int)=>(1.0+x+y));
		dstden = DenseMatrix.make(srcden.M, srcden.N);
		
		srcspa = sbmat.handleBS().getFirst().getMatrix() as SparseCSC;
		srcspa.init((x:Int, y:Int)=>1.0*(x+y)*((x+y)%2));
		dstspa = SparseCSC.make(srcspa.M, srcspa.N, nzdensity);
		
		rmtcomm  = new BlockBcast();
		numplace = Place.numPlaces();
	}
	
	public def run(): void {
 		// Set the matrix function
		var retval:Boolean = true;
		Console.OUT.println("Test dense blocks collective commu in distributed block matrix");

		retval &= testBcast(dbmat);

		Console.OUT.println("");
		Console.OUT.println("Test sparse blocks collective commu in distributed block matrix");
	
		retval &= testBcast(sbmat);
		
		if (retval) 
			Console.OUT.println("Block communication test collective commu passed!");
		else
			Console.OUT.println("------------Block communication test collective commu failed!-----------");
	}
	//------------------------------------------------
	//------------------------------------------------
	public def testBcast(bmat:DistBlockMatrix):Boolean {
		val ret:Boolean;
		var ds:Int = 0;
		
		Console.OUT.println("\nTest Bcast on dist block matrix, each block ("+M+"x"+N+") "+
				"("+bM+","+bN+") blocks over "+ numplace+" placaces");
		val src = bmat.handleBS().getFirst().getMatrix();
		
		src.printMatrix("BCast root");
		var st:Long =  Timer.milliTime();
		ds = rmtcomm.bcast(bmat.handleBS);
		val avgt = 1.0*(Timer.milliTime() - st)/(numplace-1);
		bmat.printMatrix("Bcast resuult");
		
		Console.OUT.printf("Bcast %d bytes : %.3f ms, thput: %2.2f MB/s per iteration\n", 
						   ds*8, avgt, 8000.0*ds/avgt/1024/1024);
		
		ret = dbmat.syncCheck();
		if (ret)
			Console.OUT.println("Bcast dist block matrix passed!");
		else
			Console.OUT.println("--------Bcast block matrix test failed!--------");
		
		return ret;

	}

}