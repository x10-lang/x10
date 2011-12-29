/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.io.Console;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.TriMatrix;

/**
   This class contails test cases for dense matrix addition, scaling, and negative operations.
   <p>

   <p>
 */
public class TestTriMat{

    public static def main(args:Array[String](1)) {
		val m = (args.size > 0) ? Int.parse(args(0)):5;
		val testcase = new CellWiseTriMatTest(m);
		testcase.run();
	}
}


class CellWiseTriMatTest {

	public val M:Int;

	public def this(m:Int) {
		M = m;
	}

    public def run (): void {
		Console.OUT.println("Starting Triangular matrix clone/add/sub/scaling tests on "+
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
		ret &= (testSolve());

		if (ret)
			Console.OUT.println("Test passed!");
		else
			Console.OUT.println("----------------Test failed!----------------");
	}

    
	public def testClone():Boolean{

		Console.OUT.println("Starting Triangular Matrix clone test");
		val dm = TriMatrix.make(M).init(1.0);
		
		val dm1 = dm.clone();
		var ret:Boolean = dm.equals(dm1);

		if (ret)
			Console.OUT.println("Triangular Matrix Clone test passed!");
		else
			Console.OUT.println("--------Triangular Matrix Clone test failed!--------");
		//dm.print();
		val dmm = dm.toDense();
		//dmm.print();
		ret &= dmm.equals(dm as Matrix(dmm.M, dmm.N));
		if (ret)
			Console.OUT.println("Triangular Matrix toDense test passed!");
		else
			Console.OUT.println("--------Triangular Matrix toDense test failed!--------");

		
		dm(1, 1) = dm(2,2) = 10.0;
		
		if ((dm(1,1)==dm(2,2)) && (dm(1,1)==10.0)) {
			ret &= true;
			Console.OUT.println("Triangular Matrix chain assignment test passed!");
		} else {
			ret &= false;
			Console.OUT.println("---------- Triangular Matrix chain assignment test failed!-------");
		}
		
		return ret;
	}
	
	public def testScale():Boolean{
		Console.OUT.println("Starting Triangular matrix scaling test");
		val dm  = TriMatrix.make(M).initRandom();
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
		Console.OUT.println("Starting Triangular matrix addition test");
		val dm:TriMatrix(M)  = TriMatrix.make(M).initRandom();
		//dm.print();
		val dm1:TriMatrix(M) = -dm;
		//dm.print();
		//dm1.print();
		val dm0:TriMatrix(M) = dm + dm1;
		//dm0.print();
		var ret:Boolean = dm0.equals(0.0);
		
		val dd = dm.toDense();
		//dd.print();
		dm1.cellAddTo(dd);
		ret &= dd.equals(0.0);
		//dd.print();

		if (ret)
			Console.OUT.println("Triangular Add: dm + dm*-1 test passed");
		else
			Console.OUT.println("--------Triangular Add: dm + dm*-1 test failed--------");

		return ret;
	}

	public def testAddSub():Boolean {
		Console.OUT.println("Starting Triangular matrix add-sub test");
		val dm = TriMatrix.make(M).initRandom();
		val dm1= TriMatrix.make(M).initRandom();
		//sp.print("Input:");
		val dm2 = dm + dm1;
		//sp2.print("Add result:");
		//
		val dm_c:TriMatrix(M)  = dm2 - dm1;
		val ret   = dm.equals(dm_c as Matrix(dm.M, dm.N));
		//sp_c.print("Another add result:");
		if (ret)
			Console.OUT.println("Triangular matrix Add-sub test passed!");
		else
			Console.OUT.println("--------Triangular matrix Add-sub test failed!--------");
		
		return ret;
	}

	public def testAddAssociative():Boolean {
		Console.OUT.println("Starting Triangular matrix associative test");

		val a = TriMatrix.make(M).initRandom();
		val b = TriMatrix.make(M).initRandom();
		val c = TriMatrix.make(M).initRandom();
		val c1 = a + b + c;
		val c2 = a + (b + c);
		val ret = c1.equals(c2);
		if (ret)
			Console.OUT.println("Triangular matric Add associative test passed!");
		else
			Console.OUT.println("--------Triangular add associative test failed!--------");
		return ret;
	}

	public def testScaleAdd():Boolean {
		Console.OUT.println("Starting Triangular Matrix scaling-add test");

		val a = TriMatrix.make(M).initRandom();
		val b = TriMatrix.make(M).initRandom();
		val a1= a * 0.2;
		val a2= a * 0.8;
		val ret = a.equals(a1+a2);
		if (ret)
			Console.OUT.println("Triangular Matrix scaling-add test passed!");
		else
			Console.OUT.println("--------Triangular matrix scaling-add test failed!--------");
		return ret;
	}

	public def testCellMult():Boolean {
		Console.OUT.println("Starting Triangular Matrix cellwise mult test");

		val a = TriMatrix.make(M).initRandom();
		val b = TriMatrix.make(M).initRandom();
		val c = (a + b) * a;
		val d = a * a + b * a;
		var ret:Boolean = c.equals(d);
		
		val db = b.toDense();
		a.cellAddTo(db);
		a.cellMultTo(db);
		ret &= db.equals(d);
		
		if (ret)
			Console.OUT.println("Triangular Matrix cellwise mult passed!");
		else
			Console.OUT.println("--------Triangular matrix cellwise mult test failed!--------");
		return ret;
	}

	public def testCellDiv():Boolean {
		Console.OUT.println("Starting Triangular Matrix cellwise mult-div test");

		val a = TriMatrix.make(M).initRandom();
		val b = TriMatrix.make(M).initRandom();
		val c = (a + b) * a;
		val d =  c / (a + b);
		val ret = d.equals(a);
		if (ret)
			Console.OUT.println("Triangular Matrix cellwise mult-div passed!");
		else
			Console.OUT.println("--------Triangular matrix cellwise mult-div test failed!--------");
		return ret;
	}
	
	public def testMult():Boolean {
		var ret:Boolean = true;
		Console.OUT.println("Starting triangular-matrix multiply test");
		val a:TriMatrix(M)     = TriMatrix.make(M).init(1);//Random();
		val b:DenseMatrix(M,M) = DenseMatrix.make(M,M).init(1);//Random();
		val ad= a.toDense();
		//a.print();
		val c = a % b;
		//c.print();
		val d = ad % b;
		ret= d.equals(c);
		
		val e = b % a;
		//e.print();
		val ed= b % ad;
		//ed.print();
		ret &= e.equals(ed);
		
		if (ret)
			Console.OUT.println("Triangular-Matrix multiply passed!");
		else
			Console.OUT.println("--------Triangular-matrix multiply test failed!--------");
		return ret;		
		
	}
	public def testSolve():Boolean {
		var ret:Boolean = true;
		Console.OUT.println("Starting matrix-triangular solver test");
		val X:DenseMatrix(M,M) = DenseMatrix.make(M,M).initRandom();
		val A:TriMatrix(M)    = TriMatrix.make(M).initRandom();
		val B:DenseMatrix(M,M) = A % X;
		
		//A.print("TriMatrix A");
		//X.print("Matrix X");
		//B.print("Mult result B");
		A.solveSelfMultMat(B);// A % X = B  
		//B.print();
		ret &= X.equals(B);
		
		val C:DenseMatrix(M,M) = X % A;
		A.solveMatMultSelf(C);
		ret &= X.equals(C);
		
		if (ret)
			Console.OUT.println("Matrix-Triangular solver passed!");
		else
			Console.OUT.println("--------Matrix-Triangular solver test failed!--------");
		return ret;		
		
	}

	
}
