/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 *  (C) Copyright Australian National University 2011.
 */

import x10.io.Console;

import x10.matrix.Matrix;
import x10.matrix.Vector;
import x10.matrix.distblock.DupVector;

/**
   <p>

   <p>
 */
public class TestDupVector{

    public static def main(args:Array[String](1)) {
		val n = (args.size > 0) ? Int.parse(args(0)):4;
		val testcase = new TestRunDV(n);
		testcase.run();
	}
}


class TestRunDV {

	public val M:Int;

	public def this(m:Int) {
		M = m;
	}

    public def run (): void {
		Console.OUT.println("Starting dup vector clone/add/sub/scaling tests on "+
							M + "-vectors");
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
		ret &= (testReduce());

		if (ret)
			Console.OUT.println("DupVector test passed!");
		else
			Console.OUT.println("----------------DupVector test failed!----------------");
	}

    
	public def testClone():Boolean{

		Console.OUT.println("Starting dup vector clone test");
		val dm = DupVector.make(M);
		dm.initRandom(); // same as  val dm = Vector.make(N).initRandom(); 
		
		val dm1 = dm.clone();
		var ret:Boolean = dm.local().equals(dm1.local());
		ret &= dm.checkSync();
		ret &= dm.checkSync();
		if (ret)
			Console.OUT.println("DupVector Clone test passed!");
		else
			Console.OUT.println("--------DupVector Clone test failed!--------");
		
		dm(1) = dm(2) = 10.0;
		
		if ((dm(1)==dm(2)) && (dm(1)==10.0)) {
			ret &= true;
			Console.OUT.println("DupVector chain assignment test passed!");
		} else {
			ret &= false;
			Console.OUT.println("---------- DupVector chain assignment test failed!-------");
		}
		
		return ret;
	}

	public def testScale():Boolean{
		Console.OUT.println("Starting dup vector scaling test");
		val dm = DupVector.make(M).initRandom();
		val dm1  = dm * 2.5;
		dm1.scale(1.0/2.5);
		var ret:Boolean = dm1.checkSync();
		ret &= dm.equals(dm1 as DupVector(dm.M));
		
		if (ret)
			Console.OUT.println("DupVector scaling test passed!");
		else
			Console.OUT.println("--------DupVector Scaling test failed!--------");	
		return ret;
	}

	public def testAdd():Boolean {
		Console.OUT.println("Starting duplicated vector addition test");
		val dm = DupVector.make(M).initRandom();
		val dm1:DupVector(M) = -1 * dm;
		val dm0 = dm + dm1;
		val ret = dm0.equals(0.0);
		if (ret)
			Console.OUT.println("DupVector Add: dm + dm*-1 test passed");
		else
			Console.OUT.println("--------DupVector Add: dm + dm*-1 test failed--------");
		return ret;
	}

	public def testAddSub():Boolean {
		Console.OUT.println("Starting Duplicated Vector add-sub test");
		val dm = DupVector.make(M).initRandom();
		val dm1= DupVector.make(M).initRandom();
		//sp.print("Input:");
		val dm2= dm  + dm1;
		//sp2.print("Add result:");
		//
		val dm_c  = dm2 - dm1;
		val ret   = dm.equals(dm_c);
		//sp_c.print("Another add result:");
		if (ret)
			Console.OUT.println("DupVector Add-sub test passed!");
		else
			Console.OUT.println("--------DupVector Add-sub test failed!--------");
		return ret;
	}

	public def testAddAssociative():Boolean {
		Console.OUT.println("Starting DupVector associative test");

		val a = DupVector.make(M).init(1.0);
		val b = DupVector.make(M).initRandom(1, 10);
		val c = DupVector.make(M).initRandom(10, 100);
		val c1 = a + b + c;
		val c2 = a + (b + c);
		val ret = c1.equals(c2);
		if (ret)
			Console.OUT.println("DupVector Add associative test passed!");
		else
			Console.OUT.println("--------DupVector Add associative test failed!--------");
		return ret;
	}

	public def testScaleAdd():Boolean {
		Console.OUT.println("Starting duplicated vector scaling-add test");

		val a:DupVector(M) = DupVector.make(M).initRandom();
		val b:DupVector(M) = DupVector.make(M).initRandom();
		val a1:DupVector(a.M)= a * 0.2;
		val a2:DupVector(a.M)= a * 0.8;
		val a3= a1 + a2;
		val ret = a.equals(a3 as DupVector(a.M));
		if (ret)
			Console.OUT.println("DupVector scaling-add test passed!");
		else
			Console.OUT.println("--------DupVector scaling-add test failed!--------");
		return ret;
	}

	public def testCellMult():Boolean {
		Console.OUT.println("Starting duplicated vector cellwise mult test");

		val a = DupVector.make(M).initRandom(1, 10);
		val b = DupVector.make(M).initRandom(10, 100);
		val c = (a + b) * a;
		val d = a * a + b * a;
		val ret = c.equals(d);
		if (ret)
			Console.OUT.println("DupVector cellwise mult passed!");
		else
			Console.OUT.println("--------DupVector cellwise mult test failed!--------");
		return ret;
	}

	public def testCellDiv():Boolean {
		Console.OUT.println("Starting duplicated vector cellwise mult-div test");

		val a = DupVector.make(M).init(1);
		val b = DupVector.make(M).init(1);
		//a.print();
		val c = (a + b) * a;
		//c.print();
		val d =  c / (a + b);
		//d.print();
		val ret = d.equals(a);
		if (ret)
			Console.OUT.println("DupVector cellwise mult-div passed!");
		else
			Console.OUT.println("--------DupVector cellwise mult-div test failed!--------");
		return ret;
	}
	
	public def testReduce():Boolean {
		Console.OUT.println("Starting duplicated vector cellwise mult-div test");
		val np = Place.MAX_PLACES as Double;
		val a = DupVector.make(M).init(1);
		a.reduceSum();
		var ret:Boolean = a.local().equals(np);
		
		if (ret)
			Console.OUT.println("DupVector reduce sum passed!");
		else
			Console.OUT.println("--------DupVector reduce sume test failed!--------");
		
		a.init(1).allReduceSum();
		ret = a.equals(np);

		if (ret)
			Console.OUT.println("DupVector all reduce sum passed!");
		else
			Console.OUT.println("--------DupVector all reduce sume test failed!--------");

		return ret;
	}
 }
