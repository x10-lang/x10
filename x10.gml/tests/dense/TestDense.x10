/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 *  (C) Copyright Australian National University 2011.
 */

import x10.io.Console;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.MathTool;

/**
   This class contails test cases for dense matrix addition, scaling, and negative operations.
   <p>

   <p>
 */
public class TestDense{

    public static def main(args:Array[String](1)) {
		val m = (args.size > 0) ? Int.parse(args(0)):50;
		val n = (args.size > 1) ? Int.parse(args(1)):51;
		val testcase = new AddScalTest(m, n);
		testcase.run();
		val propertiesTest = new PropertiesTest(m, n);
		propertiesTest.run();
	}
}


class AddScalTest {

	public val M:Int;
	public val N:Int;

	public def this(m:Int, n:Int) {
		M = m; N = n;
	}

    public def run (): void {
		Console.OUT.println("Starting dense matrix clone/add/sub/scaling tests on "+
							M+"x"+N+ " matrices");
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

		if (ret)
			Console.OUT.println("Test passed!");
		else
			Console.OUT.println("----------------Test failed!----------------");
	}

    
	public def testClone():Boolean{

		Console.OUT.println("Starting dense Matrix clone test");
		val dm = DenseMatrix.make(M,N);
		dm.initRandom(); // same as  val dm = DenseMatrix.makeRand(M, N); 
		
		val dm1 = dm.clone();
		var ret:Boolean = dm.equals(dm1);
				
		if (ret)
			Console.OUT.println("Dense Matrix Clone test passed!");
		else
			Console.OUT.println("--------Dense Matrix Clone test failed!--------");
		
		dm(1, 1) = dm(2,2) = 10.0;
		
		if ((dm(1,1)==dm(2,2)) && (dm(1,1)==10.0)) {
			ret &= true;
			Console.OUT.println("Dense Matrix chain assignment test passed!");
		} else {
			ret &= false;
			Console.OUT.println("---------- Dense Matrix chain assignment test failed!-------");
		}
		
		return ret;
	}

	public def testScale():Boolean{
		Console.OUT.println("Starting dense matrix scaling test");
		val dm = DenseMatrix.makeRand(M, N);
		val dm1  = dm * 2.5;
		dm1.scale(1.0/2.5);
		val ret = dm.equals(dm1);
		if (ret)
			Console.OUT.println("Dense Matrix scaling test passed!");
		else
			Console.OUT.println("--------Dense matrix Scaling test failed!--------");	
		return ret;
	}

	public def testAdd():Boolean {
		Console.OUT.println("Starting dense matrix addition test");
		val dm = DenseMatrix.makeRand(M, N);
		val dm1 = -dm;
		val dm0 = dm + dm1;
		val ret = dm0.equals(0.0);
		if (ret)
			Console.OUT.println("Add: dm + dm*-1 test passed");
		else
			Console.OUT.println("--------Add: dm + dm*-1 test failed--------");
		return ret;
	}

	public def testAddSub():Boolean {
		Console.OUT.println("Starting dense matrix add-sub test");
		val dm = DenseMatrix.makeRand(M, N);
		val dm1= DenseMatrix.makeRand(M, N);
		//sp.print("Input:");
		val dm2= dm  + dm1;
		//sp2.print("Add result:");
		//
		val dm_c  = dm2 - dm1;
		val ret   = dm.equals(dm_c);
		//sp_c.print("Another add result:");
		if (ret)
			Console.OUT.println("Dense matrix Add-sub test passed!");
		else
			Console.OUT.println("--------Dense matrix Add-sub test failed!--------");
		return ret;
	}

	public def testAddAssociative():Boolean {
		Console.OUT.println("Starting dense matrix associative test");

		val a = DenseMatrix.makeRand(M, N);
		val b = DenseMatrix.makeRand(M, N);
		val c = DenseMatrix.makeRand(M, N);
		val c1 = a + b + c;
		val c2 = a + (b + c);
		val ret = c1.equals(c2);
		if (ret)
			Console.OUT.println("Add associative test passed!");
		else
			Console.OUT.println("--------Add associative test failed!--------");
		return ret;
	}

	public def testScaleAdd():Boolean {
		Console.OUT.println("Starting dense Matrix scaling-add test");

		val a = DenseMatrix.makeRand(M, N);
		val b = DenseMatrix.makeRand(M, N);
		val a1= a * 0.2;
		val a2= a * 0.8;
		val ret = a.equals(a1+a2);
		if (ret)
			Console.OUT.println("Dense Matrix scaling-add test passed!");
		else
			Console.OUT.println("--------Dense matrix scaling-add test failed!--------");
		return ret;
	}

	public def testCellMult():Boolean {
		Console.OUT.println("Starting dense Matrix cellwise mult test");

		val a = DenseMatrix.makeRand(M, N);
		val b = DenseMatrix.makeRand(M, N);
		val c = (a + b) * a;
		val d = a * a + b * a;
		val ret = c.equals(d);
		if (ret)
			Console.OUT.println("Dense Matrix cellwise mult passed!");
		else
			Console.OUT.println("--------Dense matrix cellwise mult test failed!--------");
		return ret;
	}

	public def testCellDiv():Boolean {
		Console.OUT.println("Starting dense Matrix cellwise mult-div test");

		val a = DenseMatrix.makeRand(M, N);
		val b = DenseMatrix.makeRand(M, N);
		val c = (a + b) * a;
		val d =  c / (a + b);
		val ret = d.equals(a);
		if (ret)
			Console.OUT.println("Dense Matrix cellwise mult-div passed!");
		else
			Console.OUT.println("--------Dense matrix cellwise mult-div test failed!--------");
		return ret;
	}

}

class PropertiesTest {

    public val M:Int;
    public val N:Int;

    public def this(m:Int, n:Int) {
        M = m; N = n;
    }

    public def run (): void {
        Console.OUT.println("Starting dense matrix property method tests on "+
                            M+"x"+N+ " matrices");
        var ret:Boolean = true;
        ret &= (testNorm());
        ret &= (testMaxNorm());
        ret &= (testTrace());

        if (ret)
            Console.OUT.println("Test passed!");
        else
            Console.OUT.println("----------------Test failed!----------------");
    }

    public def testNorm():Boolean {
        Console.OUT.println("Starting dense Matrix norm test");

        val a = DenseMatrix.makeRand(M, N);
        val alpha = 2.5;
        val b = a * alpha;
        val aNorm = a.norm();
        val bNorm = b.norm();
        val ret = MathTool.equals(bNorm, aNorm*alpha);
        if (ret)
            Console.OUT.println("Dense Matrix norm test passed!");
        else
            Console.OUT.println("--------Dense matrix norm test failed!--------");
        return ret;
    }

    public def testMaxNorm():Boolean {
        Console.OUT.println("Starting dense Matrix max norm test ");

        val a = DenseMatrix.makeRand(M, N);
        val b = DenseMatrix.makeRand(M, N);
        val aMaxNorm = a.maxNorm();
        val bMaxNorm = b.maxNorm();
        val c = a + b;
        val cMaxNorm = c.maxNorm();
        val ret = cMaxNorm <= (aMaxNorm+bMaxNorm);
        if (ret)
            Console.OUT.println("Dense Matrix max norm test passed!");
        else
            Console.OUT.println("--------Dense matrix max norm test failed!--------");
        return ret;
    }

    public def testTrace():Boolean {
        Console.OUT.println("Starting dense Matrix trace test");

        val a = DenseMatrix.makeRand(M, N);
        val b = DenseMatrix.makeRand(M, N);
        val aTrace = a.trace();
        val bTrace = b.trace();
        val c = a + b;
        val cTrace = c.trace();
        val ret = MathTool.equals(cTrace, (aTrace+bTrace));
        if (ret)
            Console.OUT.println("Dense Matrix trace test passed!");
        else
            Console.OUT.println("--------Dense matrix trace test failed!--------");
        return ret;
    }
}
