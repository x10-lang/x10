/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2014.
 *  (C) Copyright Australian National University 2011.
 */

import harness.x10Test;

import x10.matrix.Vector;
import x10.matrix.util.MathTool;

/**
 * This class contains test cases for vector operations.
 */
public class TestVector extends x10Test {
	public val M:Long;

	public def this(m:Long) {
		M = m;
	}

    public def run():Boolean {
		Console.OUT.println("Vector clone/add/sub/scaling tests on "+
							M + "-vectors");
		var ret:Boolean = true;
		ret &= (testClone());
		ret &= (testScale());
		ret &= (testAdd());
		ret &= (testAddSub());
		ret &= (testAddAssociative());
		ret &= (testScaleAdd());
		ret &= (testCellMult());
		ret &= (testCellDiv());
		ret &= (testNorms());
		ret &= (testDotProduct());
		ret &= (testExp());

        return ret;
	}

	public def testClone():Boolean{
		Console.OUT.println("Vector clone test");
		val dm = Vector.make(M);
		dm.initRandom(); // same as  val dm = Vector.make(N).initRandom(); 
		
		val dm1 = dm.clone();
		var ret:Boolean = dm.equals(dm1);
				
		if (!ret)
			Console.OUT.println("--------Vector Clone test failed!--------");
		
		dm(1) = dm(2) = 10.0;
		
		if ((dm(1)==dm(2)) && (dm(1)==10.0)) {
			ret &= true;
		} else {
			ret &= false;
			Console.OUT.println("---------- Vector chain assignment test failed!-------");
		}
		
		return ret;
	}

	public def testScale():Boolean{
		Console.OUT.println("Vector scaling test");
		val dm = Vector.make(M).initRandom();
		val dm1  = dm * 2.5;
		dm1.scale(1.0/2.5);
		val ret = dm.equals(dm1 as Vector(dm.M));
		if (!ret)
			Console.OUT.println("--------Vector Scaling test failed!--------");	
		return ret;
	}

	public def testAdd():Boolean {
		Console.OUT.println("Vector addition test");
		val dm = Vector.make(M).initRandom();
		val dm1:Vector(M) = -1 * dm;
		val dm0 = dm + dm1;
		val ret = dm0.equals(0.0);
		if (!ret)
			Console.OUT.println("--------Vector Add: dm + dm*-1 test failed--------");
		return ret;
	}

	public def testAddSub():Boolean {
		Console.OUT.println("Vector add-sub test");
		val dm = Vector.make(M).initRandom();
		val dm1= Vector.make(M).initRandom();
		val dm2= dm  + dm1;

		val dm_c  = dm2 - dm1;
		val ret   = dm.equals(dm_c);
		if (!ret)
			Console.OUT.println("--------Vector Add-sub test failed!--------");
		return ret;
	}

	public def testAddAssociative():Boolean {
		Console.OUT.println("Vector associative test");

		val a = Vector.make(M).init(1.0);
		val b = Vector.make(M).initRandom(1, 10);
		val c = Vector.make(M).initRandom(10, 100);
		val c1 = a + b + c;
		val c2 = a + (b + c);
		val ret = c1.equals(c2);
		if (!ret)
			Console.OUT.println("--------Vector Add associative test failed!--------");
		return ret;
	}

	public def testScaleAdd():Boolean {
		Console.OUT.println("Vector scaling-add test");

		val a:Vector(M) = Vector.make(M).initRandom();
		val b:Vector(M) = Vector.make(M).initRandom();
		val a1:Vector(a.M)= a * 0.2;
		val a2:Vector(a.M)= a * 0.8;
		val a3= a1 + a2;
		val ret = a.equals(a3 as Vector(a.M));
		if (!ret)
			Console.OUT.println("--------Vector scaling-add test failed!--------");
		return ret;
	}

	public def testCellMult():Boolean {
		Console.OUT.println("Vector cellwise mult test");

		val a = Vector.make(M).initRandom(1, 10);
		val b = Vector.make(M).initRandom(10, 100);
		val c = (a + b) * a;
		val d = a * a + b * a;
		val ret = c.equals(d);
		if (!ret)
			Console.OUT.println("--------Vector cellwise mult test failed!--------");
		return ret;
	}

	public def testCellDiv():Boolean {
		Console.OUT.println("Vector cellwise mult-div test");

		val a = Vector.make(M).init(1);
		val b = Vector.make(M).init(1);
		val c = (a + b) * a;
		val d =  c / (a + b);
		val ret = d.equals(a);
		if (!ret)
			Console.OUT.println("--------Vector cellwise mult-div test failed!--------");
		return ret;
	}

    public def testNorms():Boolean {
        Console.OUT.println("Vector norm tests");

        var ret:Boolean = true;

        val a = Vector.make(M).initRandom();
        val alpha = 2.5;
        val b = a * alpha;
        val zero = Vector.make(M, 0.0);
        val negA = zero - a;

        // L_1 norm and distance
        val a1Norm = a.l1Norm();
        val b1Norm = b.l1Norm();

        ret &= MathTool.equals(b1Norm, a1Norm*alpha);

        val a1Distance = Vector.manhattanDistance(a, zero);
        val negA1Distance = Vector.manhattanDistance(negA, zero);

        ret &= MathTool.equals(a1Distance, negA1Distance);

        // L_2 norm and distance
        val aNorm = a.norm();
        val bNorm = b.norm();

        ret &= MathTool.equals(bNorm, aNorm*alpha);

        val aDistance = Vector.distance(a, zero);
        val negADistance = Vector.distance(negA, zero);

        ret &= MathTool.equals(aDistance, negADistance);

        // L_Inf norm and distance
        val aMaxNorm = a.lInfNorm();
        val bMaxNorm = b.lInfNorm();

        ret &= MathTool.equals(bMaxNorm, aMaxNorm*alpha);

        val aMaxDistance = Vector.chebyshevDistance(a, zero);
        val negAMaxDistance = Vector.chebyshevDistance(negA, zero);

        ret &= MathTool.equals(aMaxDistance, negAMaxDistance);

        if (!ret)
            Console.OUT.println("--------Vector norm tests failed!--------");
        return ret;
    }

    public def testDotProduct():Boolean {
        Console.OUT.println("Vector dot product test");

        val a = Vector.make(M).initRandom();
        val b = Vector.make(M).initRandom();
        val dot = a.dotProd(b);
        val blasDot = a.blasTransProduct(b);
        val ret = MathTool.equals(dot, blasDot);
        if (!ret)
            Console.OUT.println("--------Vector dot product test failed!--------");
        return ret;
    }

    public def testExp():Boolean {
        Console.OUT.println("Vector exp test");

        val a = Vector.make(M).initRandom();
        val aExp = new Vector(a).exp();
        var ret:Boolean = true;
        for (i in 0..(a.M-1)) {
            ret &= (Math.exp(a(i)) == aExp(i));
        }
        if (!ret)
            Console.OUT.println("--------Vector exp test failed!--------");
        return ret;
    }

    public static def main(args:Rail[String]) {
		val n = (args.size > 0) ? Long.parse(args(0)):4;
		new TestVector(n).execute();
	}
}
