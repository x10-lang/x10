/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

import x10.compiler.Ifndef;

import x10.matrix.util.Debug;
import x10.matrix.block.Grid;
import x10.matrix.block.SparseBlockMatrix;

import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;

public class TestDistSparse extends x10Test {
	public val nzp:Double;
	public val M:Long;
	public val N:Long;

	public val g:Grid;
	public val grow:Grid;

	public def this(args:Rail[String]) {
		M = args.size > 0 ? Long.parse(args(0)):5;
		N = args.size > 1 ? Long.parse(args(1)):(M as Int)+1;
		nzp = args.size > 2 ?Double.parse(args(2)):1.0;

		g   = Grid.make(M,N);
		grow= new Grid(M, N, 1, Place.numPlaces());
	}

	public def run():Boolean {
		var ret:Boolean= true;
		ret &= testClone();
		ret &= testCopy();
		ret &= testGather();

		return ret;
	}

	public def testClone():Boolean {
		Console.OUT.println("Test dist sparse matrix clone");
		var ret:Boolean;
		val m1  = DistSparseMatrix.make(g, nzp);
		m1.initRandom();

		val m2 = m1.clone();

		ret = m1.equals(m2);
		if (!ret)
			Console.OUT.println("--------------Test dist sparse matrix clone failed!--------------");

		m1(1, 1) = m2(2,2) = 10.0;

		if ((m1(1,1)==m2(2,2)) && (m2(2,2)==10.0)) {
			ret &= true;
		} else {
			ret &= false;
			Console.OUT.println("---------- Dist sparse chain assignment test failed!-------");
		}

		return ret;
	}

	public def testCopy():Boolean {
		Console.OUT.println("Test dist sparse matrix copy");
		var ret:Boolean;
		val ds  = DistSparseMatrix.make(g, nzp);
		ds.initRandom();
		val dd  = DistDenseMatrix.make(g);

		ds.copyTo(dd);

		ret = ds.equals(dd);

		if (!ret)
			Console.OUT.println("--------------Test dist sparse matrix copy to failed!--------------");
		return ret;
	}

	public def testGather():Boolean {
		Console.OUT.println("Test dist sparse matrix gather");
		var ret:Boolean = true;
    @Ifndef("MPI_COMMU") { // TODO gather deadlocks!
		val ds  = DistSparseMatrix.make(g, nzp);
		ds.initRandom();

		val sbm  = SparseBlockMatrix.make(g, nzp);

		ds.copyTo(sbm);

		ret = ds.equals(sbm);

		if (!ret)
			Console.OUT.println("--------------Test dist sparse matrix copy to failed!--------------");
    }
		return ret;
	}

    public static def main(args:Rail[String]) {
		new TestDistSparse(args).execute();
	}
}
