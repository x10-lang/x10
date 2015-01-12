/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;

import x10.matrix.DenseMatrix;
import x10.matrix.block.Grid;
import x10.matrix.block.DenseBlockMatrix;

public class TestDenseBlockMatrix extends x10Test {
	public val M:Long;
	public val N:Long;
	public val R:Long;
	public val C:Long;
	public val grid:Grid;

    public def this(args:Rail[String]) {
		M = args.size > 0 ? Long.parse(args(0)):50;
		N = args.size > 1 ? Long.parse(args(1)):(M as Int)+1;
		R = args.size > 2 ? Long.parse(args(2)):3;
		C = args.size > 3 ? Long.parse(args(3)):(R as Int)+1;
		grid = new Grid(M, N, R, C);
	}

    public def run():Boolean {
		Console.OUT.println("Dense block matrix clone/add/sub/scaling tests");
		Console.OUT.printf("Matrix M:%d N:%d Row block:%d Column block:%d\n", M, N, R, C);

		var ret:Boolean = true;
 		ret &= (testClone());
		ret &= (testCopy());
		ret &= (testScale());
		ret &= (testAdd());
		ret &= (testAddSub());
		ret &= (testAddAssociative());
 		ret &= (testScaleAdd());
 		ret &= (testCellMult());
 		ret &= (testCellDiv());

		return ret;
	}

	public def testClone():Boolean{
		var ret:Boolean = true;
		Console.OUT.println("Dense block matrix clone test");
		val dbm = DenseBlockMatrix.make(grid);
		dbm.initRandom();

		val dbm1 = dbm.clone();
		ret = dbm.equals(dbm1);

		if (!ret)
			Console.OUT.println("--------Dense block matrix Clone test failed!--------");
		return ret;
	}

	public def testCopy():Boolean{
		var ret:Boolean = true;
		Console.OUT.println("Dense block matrix copy To/From test");
		val dbm = DenseBlockMatrix.make(grid);
		val dm  = DenseMatrix.make(grid.M, grid.N);

		dbm.initRandom();
		dbm.copyTo(dm);

		ret = dbm.equals(dm);

		val dbm2 = DenseBlockMatrix.make(grid);
		dbm2.copyFrom(dm);

		ret &= dbm2.equals(dm);

		if (!ret)
			Console.OUT.println("--------Dense block matrix copy to/from test failed!--------");

        dbm(1, 1) = dbm(2,2) = 10.0;

        if ((dbm(1,1)==dbm(2,2)) && (dbm(1,1)==10.0)) {
                ret &= true;
        } else {
                ret &= false;
                Console.OUT.println("---------- Dense block Matrix chain assignment test failed!-------");
        }

		return ret;
	}

	public def testScale():Boolean{
		Console.OUT.println("Dense block matrix scaling test");
		val dm = DenseBlockMatrix.make(grid);
		dm.initRandom();
		val dm1  = dm * 2.5;
		val m = dm.toDense();
		m.scale(2.5);
		
		val ret = m.equals(dm1);
		if (!ret)
			Console.OUT.println("--------Dense block matrix Scaling test failed!--------");	
		return ret;
	}

	public def testAdd():Boolean {
		Console.OUT.println("Dense block matrix add test");
		val dm = DenseBlockMatrix.make(grid);
		dm.initRandom();
		val dm1 = dm * -1.0;
		val dm0 = dm + dm1;
		val ret = dm0.equals(0.0);
		if (!ret)
			Console.OUT.println("--------Dense block matrix Add: dm + dm*-1 test failed--------");
		return ret;
	}

	public def testAddSub():Boolean {
		Console.OUT.println("Dense block matrix add-sub test");
		val dm = DenseBlockMatrix.make(grid);
		dm.initRandom();
		val dm1= DenseBlockMatrix.make(grid);
		dm.initRandom();
		val dm2= dm  + dm1;
		val dm_c  = dm2 - dm1;
		val ret   = dm.equals(dm_c);
		if (!ret)
			Console.OUT.println("--------Dense block matrix Add-sub test failed!--------");
		return ret;
	}


	public def testAddAssociative():Boolean {
		Console.OUT.println("Dense block matrix add associative test");

		val a = DenseBlockMatrix.make(grid);
		val b = DenseBlockMatrix.make(grid);
		val c = DenseBlockMatrix.make(grid);
		a.initRandom();
		b.initRandom();
		c.initRandom();
		val c1 = a + b + c;
		val c2 = a + (b + c);
		val ret = c1.equals(c2);
		if (!ret)
			Console.OUT.println("--------Dense block matrix Add associative test failed!--------");
		return ret;
	}

	public def testScaleAdd():Boolean {
		Console.OUT.println("Dense block Matrix scale-add test");

		val a = DenseBlockMatrix.make(grid);
		a.initRandom();

		val m = a.toDense();
		val a1= a * 0.2;
		val a2= 0.8 * a;
		var ret:Boolean = a.equals(a1+a2);
		ret &= a.equals(m);

		if (!ret)
			Console.OUT.println("--------Dense block matrix scale-add test failed!--------");
		return ret;
	}

	public def testCellMult():Boolean {
		Console.OUT.println("Dense block Matrix cellwise mult test");

		val a = DenseBlockMatrix.make(grid);
		val b = DenseBlockMatrix.make(grid);
		a.initRandom();
		b.initRandom();

		val c = (a + b) * a;
		val d = a * a + b * a;
		var ret:Boolean = c.equals(d);

		val da= a.toDense();
		val db= b.toDense();
		val dc= (da + db) * da;
		ret &= dc.equals(c);

		if (!ret)
			Console.OUT.println("--------Dense block matrix cellwise mult test failed!--------");
		return ret;
	}

	public def testCellDiv():Boolean {
		Console.OUT.println("Dense block matrix cellwise mult-div test");

		val a = DenseBlockMatrix.make(grid);
		val b = DenseBlockMatrix.make(grid);
		a.initRandom();
		b.initRandom();

		val c = (a + b) * a;
		val d =  c / (a + b);
		var ret:Boolean = d.equals(a);

		if (!ret)
			Console.OUT.println("--------Dense block matrix cellwise mult-div test failed!--------");
		return ret;
	}

    public static def main(args:Rail[String]) {
		new TestDenseBlockMatrix(args).execute();
	}
} 
