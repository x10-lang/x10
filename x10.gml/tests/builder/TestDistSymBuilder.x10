/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011-2015.
 */

import harness.x10Test;

import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.builder.distblock.DistMatrixBuilder;
import x10.matrix.builder.distblock.DistSymMatrixBuilder;

/**
 * This class contains test cases for dense matrix addition, scaling, and negation operations.
 */
public class TestDistSymBuilder extends x10Test {
	public val M:Long;
	public val nzd:Double;

	public def this(m:Long, z:Double) {
		M = m;
		nzd = z;
	}

    public def run():Boolean {
		Console.OUT.println("Distributed symmetric builder tests on "+
							M+"x"+ M + " matrices");
		var ret:Boolean = true;
		ret &= (testDense());
		ret &= (testSparse());

		return ret;
	}

	public def testDense():Boolean{
		Console.OUT.println("Dist symmetric dense init test");
		val nblk = Place.numPlaces();
		val dbld = DistSymMatrixBuilder.make(M, nblk);
		val dmat = dbld.allocAllDenseBlocks().initRandom(nzd, (r:Long,c:Long)=>1.0+r+2*c).toMatrix();

		var ret:Boolean = dbld.checkSymmetric();
		
		if (!ret)
			Console.OUT.println("--------Dist symmetric dense matrix init test failed!--------");
	
		return ret;
	}

	public def testSparse():Boolean{
		Console.OUT.println("Dist symmetric sparse random initialization method test");
		val nblk = Place.numPlaces();
		val sbld = DistSymMatrixBuilder.make(M, nblk);
		val dspa = sbld.allocAllSparseBlocks(nzd).initRandom(nzd, (r:Long,c:Long)=>1.0+r+2*c).toMatrix();

		var ret:Boolean = sbld.checkSymmetric();
		
		if (!ret)
			Console.OUT.println("--------Dist symmetric sparse matrix initialization test failed!--------");
		
		return ret;
	}

    public static def main(args:Rail[String]) {
		val m = (args.size > 0) ? Long.parse(args(0)):8;
		val z = (args.size > 1) ? Double.parse(args(1)):0.5;
		new TestDistSymBuilder(m, z).execute();
	}
}
