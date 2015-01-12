/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2015.
 */

import harness.x10Test;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.TriDense;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.builder.SparseCSCBuilder;
import x10.matrix.builder.SymSparseBuilder;

/**
 * This class contains test cases for symmetric sparse builders.
 */
public class TestSymSparse extends x10Test {
	public val M:Long;
	public val nzd:Double;

	public def this(m:Long, z:Double) {
		M = m;
		nzd = z;
	}

    public def run():Boolean {
		Console.OUT.println("Symmetric sparse builder tests on "+
							M+"x"+ M + " matrices");
		var ret:Boolean = true;
		ret &= (testMake());

        return ret;
	}

	public def testMake():Boolean{
		Console.OUT.println("Symmetric sparse Matrix random initialization method test");
		val bd = SymSparseBuilder.make(M).initRandom(nzd);
		val ss = bd.toSparseCSC();
		val st = SparseCSCBuilder.make(M,M).initTransposeFrom(ss).toSparseCSC();
		var ret:Boolean = st.equals(ss);
		ret &= bd.checkSymmetric();
		
		if (!ret)
			Console.OUT.println("--------Symmetric sparse matrix initialization test failed!--------");
	
		return ret;
	}

    public static def main(args:Rail[String]) {
		val m = (args.size > 0) ? Long.parse(args(0)):8;
		val z = (args.size > 1) ? Double.parse(args(1)):0.5;
		new TestSymSparse(m, z).execute();
	}
}
