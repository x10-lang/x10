/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 *  (C) Copyright Australian National University 2011.
 */

import x10.compiler.Ifndef;

import x10.matrix.Matrix;
import x10.matrix.Vector;
import x10.matrix.distblock.DistVector;

public class TestDistVector{

    public static def main(args:Rail[String]) {
		val n = (args.size > 0) ? Long.parse(args(0)):4;
		val testcase = new DistVectorTest(n);
		testcase.run();
	}
}

class DistVectorTest {
	public val M:Long;

	public def this(m:Long) {
		M = m;
	}

    public def run (): void {
		Console.OUT.println("Starting distributed vector clone/add/sub/scaling tests on "+
							M + "-vectors");
		var ret:Boolean = true;
	@Ifndef("MPI_COMMU") { // TODO Deadlocks!
		ret &= (testClone());
		ret &= (testScale());
		ret &= (testAdd());
		ret &= (testAddSub());
		ret &= (testAddAssociative());
		ret &= (testScaleAdd());
		ret &= (testCellMult());
		ret &= (testCellDiv());
		ret &= (testScatterGather());
    }
		if (ret)
			Console.OUT.println("DistVector test passed!");
		else
			Console.OUT.println("----------------DistVector test failed!----------------");
	}

    
	public def testClone():Boolean{

		Console.OUT.println("Starting vector clone test");
		val dm = DistVector.make(M);
		dm.initRandom(); // same as  val dm = Vector.make(N).initRandom(); 
		
		val dm1 = dm.clone();
		var ret:Boolean = dm.equals(dm1);
				
		if (ret)
			Console.OUT.println("DistVector Clone test passed!");
		else
			Console.OUT.println("--------DistVector Clone test failed!--------");
		
		dm(1) = dm(2) = 10.0;
		
		if ((dm(1)==dm(2)) && (dm(1)==10.0)) {
			ret &= true;
			Console.OUT.println("DistVector chain assignment test passed!");
		} else {
			ret &= false;
			Console.OUT.println("---------- DistVector chain assignment test failed!-------");
		}
		
		return ret;
	}

	public def testScale():Boolean{
		Console.OUT.println("Starting distributed vector scaling test");
		val dm = DistVector.make(M).initRandom();
		val dm1  = dm * 2.5;
		dm1.scale(1.0/2.5);
		val ret = dm.equals(dm1 as DistVector(dm.M));
		if (ret)
			Console.OUT.println("DistVector scaling test passed!");
		else
			Console.OUT.println("--------DistVector Scaling test failed!--------");	
		return ret;
	}

	public def testAdd():Boolean {
		Console.OUT.println("Starting distributed vector addition test");
		val dm = DistVector.make(M).initRandom();
		val dm1:DistVector(M) = -1 * dm;
		val dm0 = dm + dm1;
		val ret = dm0.equals(0.0);
		if (ret)
			Console.OUT.println("DistVector Add: dm + dm*-1 test passed");
		else
			Console.OUT.println("--------DistVector Add: dm + dm*-1 test failed--------");
		return ret;
	}

	public def testAddSub():Boolean {
		Console.OUT.println("Starting distributed vector add-sub test");
		val dm = DistVector.make(M).initRandom();
		val dm1= DistVector.make(M).initRandom();
		val dm2= dm  + dm1;
		//
		val dm_c  = dm2 - dm1;
		val ret   = dm.equals(dm_c);
		if (ret)
			Console.OUT.println("DistVector Add-sub test passed!");
		else
			Console.OUT.println("--------DistVector Add-sub test failed!--------");
		return ret;
	}

	public def testAddAssociative():Boolean {
		Console.OUT.println("Starting distributed vector associative test");

		val a = DistVector.make(M).init(1.0);
		val b = DistVector.make(M).initRandom(1n, 10n);
		val c = DistVector.make(M).initRandom(10n, 100n);
		val c1 = a + b + c;
		val c2 = a + (b + c);
		val ret = c1.equals(c2);
		if (ret)
			Console.OUT.println("DistVector Add associative test passed!");
		else
			Console.OUT.println("--------DistVector Add associative test failed!--------");
		return ret;
	}

	public def testScaleAdd():Boolean {
		Console.OUT.println("Starting distributed vector scaling-add test");

		val a:DistVector(M) = DistVector.make(M).initRandom();
		val b:DistVector(M) = DistVector.make(M).initRandom();
		val a1:DistVector(a.M)= a * 0.2;
		val a2:DistVector(a.M)= a * 0.8;
		val a3= a1 + a2;
		val ret = a.equals(a3 as DistVector(a.M));
		if (ret)
			Console.OUT.println("DistVector scaling-add test passed!");
		else
			Console.OUT.println("--------DistVector scaling-add test failed!--------");
		return ret;
	}

	public def testCellMult():Boolean {
		Console.OUT.println("Starting distributed vector cellwise mult test");

		val a = DistVector.make(M).initRandom(1n, 10n);
		val b = DistVector.make(M).initRandom(10n, 100n);
		val c = (a + b) * a;
		val d = a * a + b * a;
		val ret = c.equals(d);
		if (ret)
			Console.OUT.println("DistVector cellwise mult passed!");
		else
			Console.OUT.println("--------DistVector cellwise mult test failed!--------");
		return ret;
	}

	public def testCellDiv():Boolean {
		Console.OUT.println("Starting distributed vector cellwise mult-div test");

		val a = DistVector.make(M).init(1);
		val b = DistVector.make(M).init(1);
		val c = (a + b) * a;
		val d =  c / (a + b);
		val ret = d.equals(a);
		if (ret)
			Console.OUT.println("DistVector cellwise mult-div passed!");
		else
			Console.OUT.println("--------DistVector cellwise mult-div test failed!--------");
		return ret;
	}

	public def testScatterGather():Boolean {
		Console.OUT.println("Starting distributed vector scatter-gather test");

		val a = DistVector.make(M);
		val b = Vector.make(M).init(1);
		val c = Vector.make(M);
		a.copyFrom(b);
		a.copyTo(c);
		val ret = b.equals(c);
		
		if (ret)
			Console.OUT.println("DistVector scatter-gather passed!");
		else
			Console.OUT.println("--------DistVector scatter-gather test failed!--------");
		return ret;
	}
}
