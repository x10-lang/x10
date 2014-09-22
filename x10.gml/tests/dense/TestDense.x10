/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011-2014.
 *  (C) Copyright Australian National University 2011-2013.
 */

import harness.x10Test;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.util.MathTool;

/**
 * This class contains test cases for dense matrix operations.
 */
public class TestDense extends x10Test {
	public val M:Long;
	public val N:Long;

	public def this(m:Long, n:Long) {
		M = m; N = n;
	}

    public def run():Boolean {
		Console.OUT.println("Starting dense matrix tests on "+
							M+"x"+N+ " matrices");
		var ret:Boolean = true;
		ret &= (testClone());
		ret &= (testInit());
		ret &= (testTrans());
		ret &= (testScale());
		ret &= (testAdd());
		ret &= (testAddSub());
		ret &= (testAddAssociative());
		ret &= (testScaleAdd());
		ret &= (testCellMult());
		ret &= (testCellDiv());

        ret &= (testNorm());
        ret &= (testMaxNorm());
        ret &= (testTrace());

		return ret;
	}
    
	public def testClone():Boolean{
		Console.OUT.println("Dense Matrix clone test");
		val dm = DenseMatrix.make(M,N);
		dm.initRandom(); // same as  val dm = DenseMatrix.makeRand(M, N); 
		
		val dm1 = dm.clone();
		var ret:Boolean = dm.equals(dm1);
				
		if (!ret)
			Console.OUT.println("--------Dense Matrix Clone test failed!--------");
		
		dm(1, 1) = dm(M-1,N-1) = 10.0;
		if ((dm(1,1)==dm(M-1,N-1)) && (dm(1,1)==10.0)) {
			ret &= true;
		} else {
			ret &= false;
			Console.OUT.println("---------- Dense Matrix chain assignment test failed!-------");
		}
		
		return ret;
	}

	public def testInit():Boolean {
		Console.OUT.println("Dense Matrix initialization test");
		var ret:Boolean = true;
		val den = DenseMatrix.make(M,N).init((r:Long, c:Long)=>(1.0+r+c));
		
		for (var c:Long=0; c<N; c++)
			for (var r:Long=0; r<M; r++)
				ret &= (den(r,c) == 1.0+r+c);
		
		if (!ret)
			Console.OUT.println("--------Dense matrix initialization func failed!--------");

		return ret;
	}
	
	public def testTrans():Boolean {
		Console.OUT.println("Dense Matrix transpose test");
		var ret:Boolean = true;
		val src = DenseMatrix.make(M,N).init((r:Long,c:Long)=>1.0+r+c*M);
		val srcT = src.T();
		for (var c:Long=0; c<N; c++)
			for (var r:Long=0; r<M; r++)
				ret &= (src(r,c) == srcT(c,r));
		
		if (!ret)
			Console.OUT.println("--------Dense matrix transpose func failed!--------");

		return ret;
	}
	
	public def testScale():Boolean{
		Console.OUT.println("Dense matrix scaling test");
		val dm = DenseMatrix.makeRand(M, N);
		val dm1  = dm * 2.5;
		dm1.scale(1.0/2.5);
		val ret = dm.equals(dm1);
		if (!ret)
			Console.OUT.println("--------Dense matrix Scaling test failed!--------");

		return ret;
	}

	public def testAdd():Boolean {
		Console.OUT.println("Dense matrix addition test");
		val dm = DenseMatrix.makeRand(M, N);
		val dm1 = -dm;
		val dm0 = dm + dm1;
		val ret = dm0.equals(0.0);
		if (!ret)
			Console.OUT.println("--------Add: dm + dm*-1 test failed--------");
		return ret;
	}

	public def testAddSub():Boolean {
		Console.OUT.println("Dense matrix add-sub test");
		val dm = DenseMatrix.makeRand(M, N);
		val dm1= DenseMatrix.makeRand(M, N);
		val dm2= dm  + dm1;
		val dm_c  = dm2 - dm1;
		val ret   = dm.equals(dm_c);
		if (!ret)
			Console.OUT.println("--------Dense matrix Add-sub test failed!--------");

		return ret;
	}

	public def testAddAssociative():Boolean {
		Console.OUT.println("Dense matrix associative test");

		val a = DenseMatrix.makeRand(M, N);
		val b = DenseMatrix.makeRand(M, N);
		val c = DenseMatrix.makeRand(M, N);
		val c1 = a + b + c;
		val c2 = a + (b + c);
		val ret = c1.equals(c2);
		if (!ret)
			Console.OUT.println("--------Add associative test failed!--------");

		return ret;
	}

	public def testScaleAdd():Boolean {
		Console.OUT.println("Dense Matrix scaling-add test");

		val a = DenseMatrix.makeRand(M, N);
		val b = DenseMatrix.makeRand(M, N);
		val a1= a * 0.2;
		val a2= a * 0.8;
		val ret = a.equals(a1+a2);
		if (!ret)
			Console.OUT.println("--------Dense matrix scaling-add test failed!--------");

		return ret;
	}

	public def testCellMult():Boolean {
		Console.OUT.println("Dense Matrix cellwise mult test");

		val a = DenseMatrix.makeRand(M, N);
		val b = DenseMatrix.makeRand(M, N);
		val c = (a + b) * a;
		val d = a * a + b * a;
		val ret = c.equals(d);
		if (!ret)
			Console.OUT.println("--------Dense matrix cellwise mult test failed!--------");

		return ret;
	}

	public def testCellDiv():Boolean {
		Console.OUT.println("Dense Matrix cellwise mult-div test");

		val a = DenseMatrix.makeRand(M, N);
		val b = DenseMatrix.makeRand(M, N);
		val c = (a + b) * a;
		val d =  c / (a + b);
		val ret = d.equals(a);
		if (!ret)
			Console.OUT.println("--------Dense matrix cellwise mult-div test failed!--------");

		return ret;
	}

    public def testNorm():Boolean {
        Console.OUT.println("Dense Matrix norm test");

        val a = DenseMatrix.makeRand(M, N);
        val alpha = 2.5;
        val b = a * alpha;
        val aNorm = a.norm();
        val bNorm = b.norm();
        val ret = MathTool.equals(bNorm, aNorm*alpha);
        if (!ret)
            Console.OUT.println("--------Dense matrix norm test failed!--------");

        return ret;
    }

    public def testMaxNorm():Boolean {
        Console.OUT.println("Dense Matrix max norm test ");

        val a = DenseMatrix.makeRand(M, N);
        val b = DenseMatrix.makeRand(M, N);
        val aMaxNorm = a.maxNorm();
        val bMaxNorm = b.maxNorm();
        val c = a + b;
        val cMaxNorm = c.maxNorm();
        val ret = cMaxNorm <= (aMaxNorm+bMaxNorm);
        if (!ret)
            Console.OUT.println("--------Dense matrix max norm test failed!--------");
        return ret;
    }

    public def testTrace():Boolean {
        Console.OUT.println("Dense Matrix trace test");

        val a = DenseMatrix.makeRand(M, N);
        val b = DenseMatrix.makeRand(M, N);
        val aTrace = a.trace();
        val bTrace = b.trace();
        val c = a + b;
        val cTrace = c.trace();
        val ret = MathTool.equals(cTrace, (aTrace+bTrace));
        if (!ret)
            Console.OUT.println("--------Dense matrix trace test failed!--------");

        return ret;
    }

    public static def main(args:Rail[String]) {
		val m = (args.size > 0) ? Long.parse(args(0)):5;
		val n = (args.size > 1) ? Long.parse(args(1)):6;
		new TestDense(m, n).execute();
	}
}
