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
import x10.matrix.builder.TriSparseBuilder;

/**
   <p>

   <p>
 */
public class TestTriSparse{

    public static def main(args:Rail[String]) {
		val m = (args.size > 0) ? Int.parse(args(0)):5;
		val d = (args.size > 1) ? Double.parse(args(1)):1.0;
		val testcase = new TriSpaTest(m, d);
		testcase.run();
	}
}


class TriSpaTest {

	public val M:Int;
	public val nzd:Double;

	public def this(m:Int, nd:Double) {
		M = m;
		nzd = nd;
	}

    public def run (): void {
		Console.OUT.println("Starting Triangular sparse matrix clone/add/sub/scaling tests on "+
							M+"x"+ M + " matrices");
		var ret:Boolean = true;
 		// Set the matrix function
		ret &= (testUpper());
		ret &= (testLower());
		ret &= (testInit());

		if (ret)
			Console.OUT.println("Test passed!");
		else
			Console.OUT.println("----------------Test failed!----------------");
	}

    
	public def testUpper():Boolean{

		Console.OUT.println("Starting Triangular sparse upper part make test");
		val uplo = true;
		val ss = SparseCSCBuilder.make(M,M).initRandom(nzd, (r:Int,c:Int)=>1.0+r+c*M).toSparseCSC();
		//ss.printMatrix("Source sparse");
		val bdr  = TriSparseBuilder.make(true, M).init(ss);
		//bdr.print();
		val up   = bdr.toSparseCSC(); 
		//up.printMatrix("Sparse upper tri");
		val dm = ss.toDense();
		val du = TriDense.make(uplo, dm);
		//du.printMatrix("Dense upper tri");
			
		var ret:Boolean = up.equals(du as Matrix(up.M,up.N));
		
		if (ret)
			Console.OUT.println("Triangular sparse matrix upper  test passed!");
		else
			Console.OUT.println("--------Triangular sparse Mtrix upper part test failed!--------");
		return ret;
	}
	
	public def testLower():Boolean{

		Console.OUT.println("Starting Triangular sparse lower part make test");
		val uplo = false;
		//val ss = SparseCSCBuilder.make(M,M).initRandom(nzd, (r:Int,c:Int)=>1.0+r+c*M).toSparseCSC();
		//ss.printMatrix("Lower tri matrix");
		val bdr = TriSparseBuilder.make(uplo, M).initRandom(nzd);
		//bdr.print();
		val lo = bdr.toSparseCSC(); 
		//lo.print("Sparse lower part tri");
		val dm = lo.toDense();
		//dm.printMatrix("Dense lower tri");
		
		var ret:Boolean = lo.equals(dm as Matrix(lo.M,lo.N));

		if (ret)
			Console.OUT.println("Triangular sparse matrix lower make test passed!");
		else
			Console.OUT.println("--------Triangular sparse Mtrix lower part make test failed!--------");
		return ret;
	}
	
	public def testInit():Boolean{

		Console.OUT.println("Starting Triangular sparse lower initialization test");
		val uptri = false;
		val ss = SparseCSC.make(M, M, nzd).initRandom();
		ss.printRandomInfo();
		val lo = TriSparseBuilder.make(uptri, M).initRandom(nzd).toSparseCSC(); 
		lo.print("Sparse lower part tri");
		lo.printRandomInfo();
		lo.printMatrix();
		return true;
	}
}
