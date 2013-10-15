/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.builder.distblock.DistMatrixBuilder;

/**
 * This class contains test cases for dense matrix addition, scaling, and negation operations.
 */
public class TestDistBuilder {

    public static def main(args:Rail[String]) {
		val m = (args.size > 0) ? Long.parse(args(0)):8;
		val n = (args.size > 1) ? Long.parse(args(1)):9;
		val z = (args.size > 2) ? Double.parse(args(2)):0.5;
		val testcase = new BuilderTest(m, n, z);
		testcase.run();
	}
}

class BuilderTest {
	public val M:Long;
	public val N:Long;
	public val nzd:Double;

	public def this(m:Long, n:Long, z:Double) {
		M = m;
		N = n;
		nzd = z;
	}

    public def run (): void {
		Console.OUT.println("Starting distributed block matrix builder tests on "+
							M+"x"+ N + " matrices");
		var ret:Boolean = true;
		ret &= (testInit());

		if (ret)
			Console.OUT.println("Test passed!");
		else
			Console.OUT.println("----------------Test failed!----------------");
	}

	public def testInit():Boolean{
		var ret:Boolean = true;
		Console.OUT.println("Starting dist matrix builder");
		val nblk = Place.MAX_PLACES;
		val dmat = DistBlockMatrix.make(M,M, nblk, nblk); 
		val dbld = new DistMatrixBuilder(dmat);
		dbld.allocAllDenseBlocks().initRandom(nzd, (r:Long,c:Long)=>1.0+r+2*c);
		 
		if (ret)
			Console.OUT.println("Dist dense matrix builder test passed!");
		else
			Console.OUT.println("--------Dist dense matrix builder test failed!--------");
	
		return ret;
	}
}
