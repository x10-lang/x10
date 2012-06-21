/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.io.Console;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.TriDense;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.builder.SparseCSCBuilder;
import x10.matrix.builder.SymSparseBuilder;

import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.builder.distblock.DistMatrixBuilder;
import x10.matrix.builder.distblock.DistSymMatrixBuilder;

/**
   This class contails test cases for dense matrix addition, scaling, and negative operations.
   <p>

   <p>
 */
public class TestDistSymBuilder {

    public static def main(args:Array[String](1)) {
		val m = (args.size > 0) ? Int.parse(args(0)):8;
		val z = (args.size > 1) ? Double.parse(args(1)):0.5;
		val testcase = new SymTest(m, z);
		testcase.run();
	}
}


class SymTest {

	public val M:Int;
	public val nzd:Double;

	public def this(m:Int, z:Double) {
		M = m;
		nzd = z;
	}

    public def run (): void {
		Console.OUT.println("Starting distributed symmetric builder tests on "+
							M+"x"+ M + " matrices");
		var ret:Boolean = true;
 		// Set the matrix function
		ret &= (testDense());
		ret &= (testSparse());
		//ret &= (testInit());

		if (ret)
			Console.OUT.println("Test passed!");
		else
			Console.OUT.println("----------------Test failed!----------------");
	}

    
	public def testDense():Boolean{

		Console.OUT.println("Starting distr symmetric dense init test");
		val nblk = Place.MAX_PLACES;
		val dbld = DistSymMatrixBuilder.make(M, nblk);
		val dmat = dbld.allocAllDenseBlocks().initRandom(nzd, (r:Int,c:Int)=>1.0+r+2*c).toMatrix();
		dmat.printMatrix();

		var ret:Boolean = dbld.checkSymmetric();
		
		if (ret)
			Console.OUT.println("Dist symmetric dense matrix init test passed!");
		else
			Console.OUT.println("--------Dist symmetric dense matrix init test failed!--------");
	
		return ret;
	}


	public def testSparse():Boolean{

		Console.OUT.println("Starting dist symmetric sparse random initialization method test");
		val nblk = Place.MAX_PLACES;
		val sbld = DistSymMatrixBuilder.make(M, nblk);
		val dspa = sbld.allocAllSparseBlocks(nzd).initRandom(nzd, (r:Int,c:Int)=>1.0+r+2*c).toMatrix();
		dspa.printMatrix();

		var ret:Boolean = sbld.checkSymmetric();
		
		if (ret)
			Console.OUT.println("Dist symmetric sparse matrix initialization test passed!");
		else
			Console.OUT.println("--------Dist symmetric sparse matrix initialization test failed!--------");
		
		return ret;
	}
		
}
