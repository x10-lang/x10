/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.io.Console;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.SymMatrix;

/**
   This class contails test cases for dense matrix addition, scaling, and negative operations.
   <p>

   <p>
 */
public class TestSymMat{

    public static def main(args:Array[String](1)) {
		val m = (args.size > 0) ? Int.parse(args(0)):4;
		val testcase = new CellWiseSymMatTest(m);
		testcase.run();
	}
}


class CellWiseSymMatTest {

	public val M:Int;

	public def this(m:Int) {
		M = m;
	}

    public def run (): void {
		Console.OUT.println("Starting symmetric matrix clone/add/sub/scaling tests on "+
							M+"x"+ M + " matrices");
		var ret:Boolean = true;
 		// Set the matrix function
		ret &= (testClone());
		ret &= (testScale());
		ret &= (testAdd());
		ret &= (testAddSub());
		ret &= (testAddAssociative());
		ret &= (testScaleAdd());
		ret &= (testCellMult());
		ret &= (testCellDiv());
		ret &= (testMult());

		if (ret)
			Console.OUT.println("Test passed!");
		else
			Console.OUT.println("----------------Test failed!----------------");
	}

    
	public def testClone():Boolean{

		Console.OUT.println("Starting Symmetric Matrix clone test");
		val dm = SymMatrix.make(M).initRandom();
		//dm.print();
		//dm.printMatrix();
		//Console.OUT.println(dm.d.toString());
		val dm1 = dm.clone();
		var ret:Boolean = dm.equals(dm1);

		if (ret)
			Console.OUT.println("Symmetric Matrix Clone test passed!");
		else
			Console.OUT.println("--------Symmetric Matrix Clone test failed!--------");
		//dm.print();
		val dmm = dm.toDense();
		//dmm.print();
		ret &= dmm.equals(dm as Matrix(dmm.M, dmm.N));
		if (ret)
			Console.OUT.println("Symmetric Matrix toDense test passed!");
		else
			Console.OUT.println("--------Symmetric Matrix toDense test failed!--------");

		
		dm(1, 1) = dm(2,2) = 10.0;
		
		if ((dm(1,1)==dm(2,2)) && (dm(1,1)==10.0)) {
			ret &= true;
			Console.OUT.println("Symmetric Matrix chain assignment test passed!");
		} else {
			ret &= false;
			Console.OUT.println("---------- Symmetric Matrix chain assignment test failed!-------");
		}
		
		return ret;
	}
	
	public def testScale():Boolean{
		Console.OUT.println("Starting symmetric matrix scaling test");
		val dm  = SymMatrix.make(M).initRandom();
		val dm1 = dm * 2.5;

		dm1.scale(1.0/2.5);
		val ret = dm.equals(dm1);
		if (ret)
			Console.OUT.println("Symmetrix Matrix scaling test passed!");
		else
			Console.OUT.println("--------Symmetrix matrix Scaling test failed!--------");	
		return ret;
	}

	public def testAdd():Boolean {
		Console.OUT.println("Starting symmetric matrix addition test");
		val dm:SymMatrix(M)  = SymMatrix.make(M).initRandom();
		//dm.print();
		val dm1:SymMatrix(M) = -dm;
		//dm.print();
		//dm1.print();
		val dm0:SymMatrix(M) = dm + dm1;
		//dm0.print();
		var ret:Boolean = dm0.equals(0.0);
		
		val dd:DenseMatrix(M,M) = dm.toDense();
		//dd.print();
		dm1.cellAddTo(dd);
		ret &= dd.equals(0.0);
		//dd.print();

		if (ret)
			Console.OUT.println("Symmetric Add: dm + dm*-1 test passed");
		else
			Console.OUT.println("--------Symmetric Add: dm + dm*-1 test failed--------");

		return ret;
	}

	public def testAddSub():Boolean {
		Console.OUT.println("Starting symmetric matrix add-sub test");
		val dm = SymMatrix.make(M).initRandom();
		val dm1= SymMatrix.make(M).initRandom();
		//sp.print("Input:");
		val dm2 = dm + dm1;
		//sp2.print("Add result:");
		//
		val dm_c:SymMatrix(M)  = dm2 - dm1;
		val ret   = dm.equals(dm_c as Matrix(dm.M, dm.N));
		//sp_c.print("Another add result:");
		if (ret)
			Console.OUT.println("Symmetric matrix Add-sub test passed!");
		else
			Console.OUT.println("--------Symmetric matrix Add-sub test failed!--------");
		
		return ret;
	}

	public def testAddAssociative():Boolean {
		Console.OUT.println("Starting symmetric matrix associative test");

		val a = SymMatrix.make(M).initRandom();
		val b = SymMatrix.make(M).initRandom();
		val c = SymMatrix.make(M).initRandom();
		val c1 = a + b + c;
		val c2 = a + (b + c);
		val ret = c1.equals(c2);
		if (ret)
			Console.OUT.println("Symmetric matric Add associative test passed!");
		else
			Console.OUT.println("--------Symmetric add associative test failed!--------");
		return ret;
	}

	public def testScaleAdd():Boolean {
		Console.OUT.println("Starting symmetric Matrix scaling-add test");

		val a = SymMatrix.make(M).initRandom();
		val b = SymMatrix.make(M).initRandom();
		val a1= a * 0.2;
		val a2= a * 0.8;
		val ret = a.equals(a1+a2);
		if (ret)
			Console.OUT.println("Symmetric Matrix scaling-add test passed!");
		else
			Console.OUT.println("--------Symmetric matrix scaling-add test failed!--------");
		return ret;
	}

	public def testCellMult():Boolean {
		Console.OUT.println("Starting symmetric Matrix cellwise mult test");

		val a = SymMatrix.make(M).initRandom();
		val b = SymMatrix.make(M).initRandom();
		val c = (a + b) * a;
		val d = a * a + b * a;
		var ret:Boolean = c.equals(d);
		
		val db = b.toDense();
		a.cellAddTo(db);
		a.cellMultTo(db);
		//ret &= db.equals(d);
		
		if (ret)
			Console.OUT.println("Symmetric Matrix cellwise mult passed!");
		else
			Console.OUT.println("--------Symmetric matrix cellwise mult test failed!--------");
		return ret;
	}

	public def testCellDiv():Boolean {
		Console.OUT.println("Starting symmetric Matrix cellwise mult-div test");

		val a = SymMatrix.make(M).initRandom();
		val b = SymMatrix.make(M).initRandom();
		val c = (a + b) * a;
		val d =  c / (a + b);
		val ret = d.equals(a);
		if (ret)
			Console.OUT.println("Symmetric Matrix cellwise mult-div passed!");
		else
			Console.OUT.println("--------Symmetric matrix cellwise mult-div test failed!--------");
		return ret;
	}
	
	public def testMult():Boolean {
		var ret:Boolean = true;
		Console.OUT.println("Starting symmetric-matrix multiply test");
		val a:SymMatrix(M)     = SymMatrix.make(M).initRandom();
		val b:DenseMatrix(M,M) = DenseMatrix.make(M,M).initRandom();
		val ad= a.toDense();
		val c = a % b;
		val d = ad % b;
		ret= d.equals(c);
		 
		val e = b % a;
		val ed= b % ad;
		ret &= e.equals(ed);
		
		if (ret)
			Console.OUT.println("Symmetric-Matrix multiply passed!");
		else
			Console.OUT.println("--------Symmetric-matrix multiply test failed!--------");
		return ret;		
		
	}
		
}
