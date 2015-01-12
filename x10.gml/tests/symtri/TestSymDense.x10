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
import x10.matrix.SymDense;

/**
 * This class contains test cases for dense matrix addition, scaling, and negation operations.
 */
public class TestSymDense extends x10Test {
	public val M:Long;

	public def this(m:Long) {
		M = m;
	}

    public def run():Boolean {
		Console.OUT.println("Symmetric matrix clone/add/sub/scaling tests on "+
							M+"x"+ M + " matrices");
		var ret:Boolean = true;
		ret &= (testClone());
		ret &= (testInit());
		ret &= (testScale());
		ret &= (testAdd());
		ret &= (testAddSub());
		ret &= (testAddAssociative());
		ret &= (testScaleAdd());
		ret &= (testCellMult());
		ret &= (testCellDiv());
		ret &= (testMult());

        return ret;
	}

	public def testClone():Boolean{
		Console.OUT.println("Symmetric Matrix clone test");
		val dm = SymDense.make(M).initRandom();
		val dm1 = dm.clone();
		var ret:Boolean = dm.equals(dm1);

		if (!ret)
			Console.OUT.println("--------Symmetric dense Clone test failed!--------");
		val dmm = dm.toDense();
		ret &= dmm.equals(dm as Matrix(dmm.M, dmm.N));
		if (!ret)
			Console.OUT.println("--------Symmetric dense toDense test failed!--------");

		
		dm(1, 1) = dm(2,2) = 10.0;
		
		if ((dm(1,1)==dm(2,2)) && (dm(1,1)==10.0)) {
			ret &= true;
		} else {
			ret &= false;
			Console.OUT.println("---------- Symmetric Matrix chain assignment test failed!-------");
		}
		return ret;
	}
	
	public def testInit():Boolean {
		Console.OUT.println("Symmetric Matrix initialization test");
		var ret:Boolean = true;
		val sym = SymDense.make(M).init((r:Long, c:Long)=>(1.0+r+c));
		
		for (var c:Long=0; c<M; c++)
			for (var r:Long=0; r<M; r++)
				ret &= (sym(r,c) == 1.0+r+c);
		
		if (!ret)
			Console.OUT.println("--------Symmetric matrix initialization func failed!--------");	
		return ret;
	}
	
	public def testScale():Boolean{
		Console.OUT.println("Symmetric matrix scaling test");
		val dm  = SymDense.make(M).initRandom();
		val dm1 = dm * 2.5;

		dm1.scale(1.0/2.5);
		val ret = dm.equals(dm1);
		if (ret)
			Console.OUT.println("Symmetric Matrix scaling test passed!");
		else
			Console.OUT.println("--------Symmetric matrix Scaling test failed!--------");	
		return ret;
	}

	public def testAdd():Boolean {
		Console.OUT.println("Symmetric matrix addition test");
		val dm:SymDense(M)  = SymDense.make(M).initRandom();
		val dm1:SymDense(M) = -dm;
		val dm0 = dm + dm1;
		var ret:Boolean = dm0.equals(0.0);
		
		val dd:DenseMatrix(M,M) = dm.toDense();
		dm1.cellAddTo(dd);
		ret &= dd.equals(0.0);

		if (!ret)
			Console.OUT.println("--------Symmetric Add: dm + dm*-1 test failed--------");

		return ret;
	}

	public def testAddSub():Boolean {
		Console.OUT.println("Symmetric matrix add-sub test");
		val dm = SymDense.make(M).initRandom();
		val dm1= SymDense.make(M).initRandom();
		val dm2 = dm + dm1;
		
		val dm_c:SymDense(M)  = dm2 - dm1;
		val ret   = dm.equals(dm_c as Matrix(dm.M, dm.N));
		if (!ret)
			Console.OUT.println("--------Symmetric matrix Add-sub test failed!--------");
		
		return ret;
	}

	public def testAddAssociative():Boolean {
		Console.OUT.println("Symmetric matrix associative test");

		val a = SymDense.make(M).initRandom();
		val b = SymDense.make(M).initRandom();
		val c = SymDense.make(M).initRandom();
		val c1 = a + b + c;
		val c2 = a + (b + c);
		val ret = c1.equals(c2);
		if (!ret)
			Console.OUT.println("--------Symmetric add associative test failed!--------");
		return ret;
	}

	public def testScaleAdd():Boolean {
		Console.OUT.println("Symmetric Matrix scaling-add test");

		val a = SymDense.make(M).initRandom();
		val b = SymDense.make(M).initRandom();
		val a1= a * 0.2;
		val a2= a * 0.8;
		val ret = a.equals(a1+a2);
		if (!ret)
			Console.OUT.println("--------Symmetric matrix scaling-add test failed!--------");
		return ret;
	}

	public def testCellMult():Boolean {
		Console.OUT.println("Symmetric Matrix cellwise mult test");

		val a = SymDense.make(M).initRandom();
		val b = SymDense.make(M).initRandom();
		val c = (a + b) * a;
		val d = a * a + b * a;
		var ret:Boolean = c.equals(d);
		
		val db = b.toDense();
		a.cellAddTo(db);
		a.cellMultTo(db);
		//ret &= db.equals(d);
		
		if (!ret)
			Console.OUT.println("--------Symmetric matrix cellwise mult test failed!--------");
		return ret;
	}

	public def testCellDiv():Boolean {
		Console.OUT.println("Symmetric Matrix cellwise mult-div test");

		val a = SymDense.make(M).initRandom();
		val b = SymDense.make(M).initRandom();
		val c = (a + b) * a;
		val d =  c / (a + b);
		val ret = d.equals(a);
		if (!ret)
			Console.OUT.println("--------Symmetric matrix cellwise mult-div test failed!--------");
		return ret;
	}
	
	public def testMult():Boolean {
		var ret:Boolean = true;
		Console.OUT.println("Symmetric-matrix multiply test");
		val a:SymDense(M)     = SymDense.make(M).initRandom();
		val b:DenseMatrix(M,M) = DenseMatrix.make(M,M).initRandom();
		val ad= a.toDense();
		val c = a % b;
		val d = ad % b;
		ret= d.equals(c);
		 
		val e = b % a;
		val ed= b % ad;
		ret &= e.equals(ed);
		
		if (!ret)
			Console.OUT.println("--------Symmetric-matrix multiply test failed!--------");
		return ret;		
	}

    public static def main(args:Rail[String]) {
		val m = (args.size > 0) ? Long.parse(args(0)):4;
		new TestSymDense(m).execute();
	}
}
