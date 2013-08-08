/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.TriDense;

/**
   This class contains test cases for triangular dense matrix addition, scaling, and negation operations.
 */
public class TestTriDense{
    public static def main(args:Rail[String]) {
		val m = (args.size > 0) ? Long.parse(args(0)):5;
		val testcase = new TriMatTest(m);
		testcase.run();
	}
}

class TriMatTest {
	public val M:Long;

	public def this(m:Long) {
		M = m;
	}

    public def run (): void {
		Console.OUT.println("Starting Triangular matrix clone/add/sub/scaling tests on "+
							M+"x"+ M + " matrices");
		var ret:Boolean = true;
 		// Set the matrix function
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
		ret &= (testSolve());

		if (ret)
			Console.OUT.println("Test passed!");
		else
			Console.OUT.println("----------------Test failed!----------------");
	}

    
	public def testClone():Boolean{

		Console.OUT.println("Starting Triangular Matrix clone test");
		val dm = TriDense.make(false, M).init(1.0);
		
		val dm1 = dm.clone();
		var ret:Boolean = dm.equals(dm1);

		if (ret)
			Console.OUT.println("Triangular Matrix Clone test passed!");
		else
			Console.OUT.println("--------Triangular Matrix Clone test failed!--------");
		val dmm = dm.toDense();
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
	
	public def testInit():Boolean {
		Console.OUT.println("Starting Triangular Matrix initialization test");
		var ret:Boolean = true;
		val sym = TriDense.make(false, M).init((r:Long, c:Long)=>(1.0+10*r+c));
		
		for (var c:Long=0; c<M; c++)
			for (var r:Long=c; r<M; r++)
				ret &= (sym(r,c) == 1.0+10*r+c);
		
		if (ret)
			Console.OUT.println("Triangular matrix initialization func test passed!");
		else
			Console.OUT.println("--------Triangular matrix initialization func failed!--------");	
		return ret;
	}
	
	public def testScale():Boolean{
		Console.OUT.println("Starting Triangular matrix scaling test");
		val dm  = TriDense.make(false, M).initRandom();
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
		Console.OUT.println("Starting Triangular matrix addition test");
		val dm:TriDense(M)  = TriDense.make(false, M).initRandom();
		val dm1:TriDense(M) = -1.0 * dm;
		val dm0:DenseMatrix = dm + dm1;
		var ret:Boolean = dm0.equals(0.0);
	
		if (ret)
			Console.OUT.println("Triangular Add: dm + dm*-1 test passed");
		else
			Console.OUT.println("--------Triangular Add: dm + dm*-1 test failed--------");

		return ret;
	}

	public def testAddSub():Boolean {
		Console.OUT.println("Starting Triangular matrix add-sub test");
		val dm = TriDense.make(false, M).initRandom();
		val dm1= TriDense.make(false, M).initRandom();
		val dm2 = dm + dm1;
		//
		val dm_c  = dm2 - dm1;
		val ret   = dm.equals(dm_c as Matrix(dm.M, dm.N));
		if (ret)
			Console.OUT.println("Triangular matrix Add-sub test passed!");
		else
			Console.OUT.println("--------Triangular matrix Add-sub test failed!--------");
		
		return ret;
	}

	public def testAddAssociative():Boolean {
		Console.OUT.println("Starting Triangular matrix associative test");

		val a = TriDense.make(M).initRandom();
		val b = TriDense.make(M).initRandom();
		val c = TriDense.make(M).initRandom();
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

		val a = TriDense.make(false, M).initRandom();
		val b = TriDense.make(false, M).initRandom();
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

		val a = TriDense.make(M).init(1.0);
		val b = TriDense.make(M).init(2.0);
		val c = (a + b) * a;
		val aa= a*a;
		val ba= b*a;
		
		val d = a * a + b * a;
		var ret:Boolean = c.equals(d);
		
// 		val da = a.toDense();
// 		val db = b.toDense();
// 		val dd = (a+b) * da;
// 
// 		ret &= dd.equals(d);
		
		if (ret)
			Console.OUT.println("Triangular Matrix cellwise mult passed!");
		else
			Console.OUT.println("--------Triangular matrix cellwise mult test failed!--------");
		return ret;
	}

	public def testCellDiv():Boolean {
		Console.OUT.println("Starting Triangular Matrix cellwise mult-div test");

		val a = TriDense.make(M).initRandom();
		val b = TriDense.make(M).initRandom();
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
		val a:TriDense(M)     = TriDense.make(M).init(1);//Random();
		val b:DenseMatrix(M,M) = DenseMatrix.make(M,M).init(1);//Random();
		//val ad:DenseMatrix(M,M) = DenseMatrix.make(M,M);
		val ad:DenseMatrix(M,M) = a.toDense();
		//a.copyTo(ad);
		val c = a % b;
		val d = ad % b;
		ret= d.equals(c as Matrix(M,M));
		
		val e = b % a;
		val ed= b % ad;
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
		val A:TriDense(M)    = TriDense.make(M).initRandom();
		val B:DenseMatrix(M,M) = A % X;
		
		A.solveSelfMultMat(B);// A % X = B  
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
