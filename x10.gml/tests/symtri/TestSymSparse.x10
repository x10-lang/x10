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


/**
   This class contails test cases for dense matrix addition, scaling, and negative operations.
   <p>

   <p>
 */
public class TestSymSparse{

    public static def main(args:Rail[String]) {
		val m = (args.size > 0) ? Int.parse(args(0)):8;
		val z = (args.size > 1) ? Double.parse(args(1)):0.5;
		val testcase = new SymSpaTest(m, z);
		testcase.run();
	}
}


class SymSpaTest {

	public val M:Int;
	public val nzd:Double;

	public def this(m:Int, z:Double) {
		M = m;
		nzd = z;
	}

    public def run (): void {
		Console.OUT.println("Starting symmetric sparse builder tests on "+
							M+"x"+ M + " matrices");
		var ret:Boolean = true;
 		// Set the matrix function
		ret &= (testMake());
		//ret &= (testInit());

		if (ret)
			Console.OUT.println("Test passed!");
		else
			Console.OUT.println("----------------Test failed!----------------");
	}

    
	public def testMake():Boolean{

		Console.OUT.println("Starting Symmetric sparse Matrix random initialization method test");
		val bd = SymSparseBuilder.make(M).initRandom(nzd);
		val ss = bd.toSparseCSC();
		ss.printMatrix();
		//dm.printMatrix();
		//Console.OUT.println(dm.d.toString());
		val st = SparseCSCBuilder.make(M,M).initTransposeFrom(ss).toSparseCSC();
		var ret:Boolean = st.equals(ss);
		ret &= bd.checkSymmetric();
		
		if (ret)
			Console.OUT.println("Symmetric sparse matrix initialization test passed!");
		else
			Console.OUT.println("--------Symmetric sparse matrix initialization test failed!--------");
	
		return ret;
	}
	
		
}
