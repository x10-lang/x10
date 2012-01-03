/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

import x10.io.Console;

import x10.matrix.Debug;
import x10.matrix.DenseMatrix;

import x10.matrix.block.Grid;
import x10.matrix.block.DenseBlockMatrix;

import x10.matrix.dist.DistDenseMatrix;


/**
   <p>

   <p>
 */
public class TestDistDense {
	
    public static def main(args:Array[String](1)) {
		val testcase = new TestDD(args);
		testcase.run();
	}
}

class TestDD {
	public val nzp:Double;
	public val M:Int;
	public val N:Int;
	public val K:Int;	

    public def this(args:Array[String](1)) {
		M = args.size > 0 ?Int.parse(args(0)):4;
		nzp = args.size > 1 ?Double.parse(args(1)):0.5;
		N = args.size > 2 ?Int.parse(args(2)):M+1;
		K = args.size > 3 ?Int.parse(args(3)):M+2;	
	}

    public def run (): void {
		Console.OUT.println("Starting dense matrix clone/add/sub/scaling tests");
		Console.OUT.printf("Matrix M:%d K:%d N:%d\n", M, N, K);

		var ret:Boolean = true;
 		// Set the matrix function
		ret &= (testClone());
		ret &= (testInit());
		ret &= (testCopyTo());
		ret &= (testScale());
		ret &= (testAdd());
		ret &= (testAddSub());
		ret &= (testAddAssociative());
		ret &= (testScaleAdd());
		ret &= (testCellMult());
		ret &= (testCellDiv());

		if (ret)
			Console.OUT.println("Test passed!");
		else
			Console.OUT.println("----------------Test failed!----------------");
	}
	public def testClone():Boolean{
		var ret:Boolean = true;
		Console.OUT.println("Starting dist dense Matrix clone test");
		val ddm = DistDenseMatrix.make(M, N);
		Debug.flushln("Starting initialization");
		ddm.initRandom();
		Debug.flushln("Initialization done");

		//dm.printBlock("Dist dense");
		val ddm1 = ddm.clone();
		Debug.flushln("Clone done");
		ret = ddm.equals(ddm1);
		Debug.flushln("Equal test done");
		
		if (ret)
			Console.OUT.println("DistDenseMatrix Clone test passed!");
		else
			Console.OUT.println("--------DistDense Matrix Clone test failed!--------");

                ddm(1, 1) = ddm(2,2) = 10.0;

                if ((ddm(1,1)==ddm(2,2)) && (ddm(1,1)==10.0)) {
                        ret &= true;
                        Console.OUT.println("Dist Dense Matrix chain assignment test passed!");
                } else {
                        ret &= false;
                        Console.OUT.println("----------Dist Dense Matrix chain assignment test failed!-------");
                }

                return ret;
	}
	
	public def testInit():Boolean {
		Console.OUT.println("Starting Dist Dense Matrix initialization test");
		var ret:Boolean = true;
		val ddm = DistDenseMatrix.make(M,N).init((r:Int, c:Int)=>(1.0+r+c));
		for (var c:Int=0; c<M; c++)
			for (var r:Int=0; r<M; r++)
				ret &= (ddm(r,c) == 1.0+r+c);
		
		if (ret)
			Console.OUT.println("Dist Dense matrix initialization func test passed!");
		else
			Console.OUT.println("--------Dist Dense matrix initialization func failed!--------");	
		return ret;
	}
	
	public def testCopyTo():Boolean {
		var ret:Boolean = true;
		Console.OUT.println("Starting dist dense Matrix copyTo test");
		val ddm = DistDenseMatrix.make(M, N);
		val dbm = DenseBlockMatrix.make(ddm.grid);
		val dm  = DenseMatrix.make(M,N);

		Debug.flushln("Starting initialization");
		ddm.initRandom();
		Debug.flushln("Initialization done");

		ddm.copyTo(dbm);
		Debug.flushln("Convert to dense block matrix done");
		ret &= ddm.equals(dbm);
		Debug.flushln("Verify copyTo dense block matrix  done");
		
		dbm.copyTo(dm);
		Debug.flushln("Convert to dense matrix done");
		ret &= ddm.equals(dbm);
		Debug.flushln("Verify copyTo dense matrix done");

		if (ret)
			Console.OUT.println("Dist dense Matrix copyTo test passed!");
		else
			Console.OUT.println("--------Dist dense matrix copyTo test failed!--------");	
		return ret;
	}

	public def testScale():Boolean{
		Console.OUT.println("Starting dist dense matrix scaling test");
		val dm = DistDenseMatrix.make(M, N);
		dm.initRandom();
		val dm1  = dm * 2.5;
		val m = dm.toDense();
		val m1 = m * 2.5;
		val ret = dm1.equals(m1);
		if (ret)
			Console.OUT.println("Dist dense Matrix scaling test passed!");
		else
			Console.OUT.println("--------Dist dense matrix Scaling test failed!--------");	
		return ret;
	}

	public def testAdd():Boolean {
		Console.OUT.println("Starting dist dense matrix addition test");
		val dm = DistDenseMatrix.make(M, N);
		dm.initRandom();
		val dm1 = dm * -1.0;
		val dm0 = dm + dm1;
		val ret = dm0.equals(0.0);
		if (ret)
			Console.OUT.println("DistDenseMatrix Add: dm + dm*-1 test passed");
		else
			Console.OUT.println("--------DistDenseMatrix Add: dm + dm*-1 test failed--------");
		return ret;
	}

	public def testAddSub():Boolean {
		Console.OUT.println("Starting DistDenseMatrix add-sub test");
		val dm = DistDenseMatrix.make(M, N);
		dm.initRandom();
		val dm1= DistDenseMatrix.make(M, N);
		dm.initRandom();
		//sp.print("Input:");
		val dm2= dm  + dm1;
		//sp2.print("Add result:");
		//
		val dm_c  = dm2 - dm1;
		val ret   = dm.equals(dm_c);
		//sp_c.print("Another add result:");
		if (ret)
			Console.OUT.println("DistDenseMatrix Add-sub test passed!");
		else
			Console.OUT.println("--------DistDenseMatrix Add-sub test failed!--------");
		return ret;
	}


	public def testAddAssociative():Boolean {
		Console.OUT.println("Starting dist dense matrix associative test");

		val a = DistDenseMatrix.make(M, N);
		val b = DistDenseMatrix.make(M, N);
		val c = DistDenseMatrix.make(M, N);
		a.initRandom();
		b.initRandom();
		c.initRandom();
		val c1 = a + b + c;
		val c2 = a + (b + c);
		val ret = c1.equals(c2);
		if (ret)
			Console.OUT.println("DistDenseMatrix Add associative test passed!");
		else
			Console.OUT.println("--------DistDenseMatrix Add associative test failed!--------");
		return ret;
	}

	public def testScaleAdd():Boolean {
		Console.OUT.println("Starting dist dense Matrix scaling-add test");

		val a = DistDenseMatrix.make(M, N);
		a.initRandom();

		val m = a.toDense();
		val a1= a * 0.2;
		val a2= 0.8 * a;
		var ret:Boolean = a.equals(a1+a2);
		ret &= a.equals(m);

		if (ret)
			Console.OUT.println("DistDenseMatrix scaling-add test passed!");
		else
			Console.OUT.println("--------DistDenseMatrix scaling-add test failed!--------");
		return ret;
	}

	public def testCellMult():Boolean {
		Console.OUT.println("Starting dist dense Matrix cellwise mult test");

		val a = DistDenseMatrix.make(M, N);
		val b = DistDenseMatrix.make(M, N);
		a.initRandom();
		b.initRandom();

		val c = (a + b) * a;
		val d = a * a + b * a;
		var ret:Boolean = c.equals(d);

		val da= a.toDense();
		val db= b.toDense();
		val dc= (da + db) * da;
		ret &= dc.equals(c);

		if (ret)
			Console.OUT.println("Dist dense Matrix cellwise mult passed!");
		else
			Console.OUT.println("--------Dist dense matrix cellwise mult test failed!--------");
		return ret;
	}

	public def testCellDiv():Boolean {
		Console.OUT.println("Starting DistDenseMatrix cellwise mult-div test");

		val a = DistDenseMatrix.make(M, N);
		val b = DistDenseMatrix.make(M, N);
		a.initRandom();
		b.initRandom();

		val c = (a + b) * a;
		val d =  c / (a + b);
		var ret:Boolean = d.equals(a);

		if (ret)
			Console.OUT.println("Dist dense Matrix cellwise mult-div passed!");
		else
			Console.OUT.println("--------Dist dense matrix cellwise mult-div test failed!--------");
		return ret;
	}



} 
