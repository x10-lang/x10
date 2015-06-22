/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

import x10.compiler.Ifndef;

import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.block.Grid;
import x10.matrix.block.DenseBlockMatrix;
import x10.matrix.dist.DistDenseMatrix;

public class TestDistDense extends x10Test {
    static def ET(a:Double)= a as ElemType;
    static def ET(a:Float)= a as ElemType;
    public val nzp:Float;
    public val M:Long;
    public val N:Long;
    public val K:Long;
    
    public def this(args:Rail[String]) {
	M = args.size > 0 ? Long.parse(args(0)):4;
	nzp = args.size > 1 ?Float.parse(args(1)):0.5f;
	N = args.size > 2 ? Long.parse(args(2)):(M as Int)+1;
	K = args.size > 3 ? Long.parse(args(3)):(M as Int)+2;
    }
    
    public def run():Boolean {
	Console.OUT.println("Starting dist dense matrix clone/add/sub/scaling tests");
	Console.OUT.printf("Matrix M:%d K:%d N:%d\n", M, N, K);
	
	var ret:Boolean = true;
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
	
	return ret;
    }
    
    public def testClone():Boolean{
	var ret:Boolean = true;
	Console.OUT.println("Dist dense Matrix clone test");
	val ddm = DistDenseMatrix.make(M, N);
	ddm.initRandom();
	
	val ddm1 = ddm.clone();
	ret = ddm.equals(ddm1);
	
	if (!ret)
	    Console.OUT.println("--------DistDense Matrix Clone test failed!--------");
	
	ddm(1, 1) = ddm(2,2) = ET(10.0);
	
	if ((ddm(1,1)==ddm(2,2)) && (ddm(1,1)==ET(10.0))) {
	    ret &= true;
	} else {
	    ret &= false;
	    Console.OUT.println("----------Dist Dense Matrix chain assignment test failed!-------");
	}
	
	return ret;
    }
    
    public def testInit():Boolean {
	Console.OUT.println("Dist Dense Matrix initialization test");
	var ret:Boolean = true;
	val ddm = DistDenseMatrix.make(M,N).init((r:Long, c:Long)=>ET(1.0+r+c));
	for (var c:Long=0; c<M; c++)
	    for (var r:Long=0; r<M; r++)
		ret &= (ddm(r,c) == ET(1.0+r+c));
	
	if (!ret)
	    Console.OUT.println("--------Dist Dense matrix initialization func failed!--------");
	
	return ret;
    }
    
    public def testCopyTo():Boolean {
	var ret:Boolean = true;
	Console.OUT.println("Dist dense Matrix copyTo test");
	val ddm = DistDenseMatrix.make(M, N);
	val dbm = DenseBlockMatrix.make(ddm.grid);
	val dm  = DenseMatrix.make(M,N);
	
	ddm.initRandom();
	
        @Ifndef("MPI_COMMU") { // TODO Gather in DBM.copyTo deadlocks!
	    ddm.copyTo(dbm);
	    ret &= ddm.equals(dbm);
	    
	    dbm.copyTo(dm);
	    ret &= dbm.equals(dm);
	    
	    if (!ret)
		Console.OUT.println("--------Dist dense matrix copyTo test failed!--------");
	}
	return ret;
    }
    
    public def testScale():Boolean{
	Console.OUT.println("Dist dense matrix scaling test");
	val dm = DistDenseMatrix.make(M, N);
	dm.initRandom();
	val dm1  = dm * ET(2.5);
	val m = dm.toDense();
	val m1 = m * ET(2.5);
	val ret = dm1.equals(m1);
	if (!ret)
	    Console.OUT.println("--------Dist dense matrix Scaling test failed!--------");
	
	return ret;
    }
    
    public def testAdd():Boolean {
	Console.OUT.println("Dist dense matrix addition test");
	val dm = DistDenseMatrix.make(M, N);
	dm.initRandom();
	val dm1 = dm * ET(-1.0);
	val dm0 = dm + dm1;
	val ret = dm0.equals(ET(0.0));
	if (!ret)
	    Console.OUT.println("--------DistDenseMatrix Add: dm + dm*-1 test failed--------");
	
	return ret;
    }
    
    public def testAddSub():Boolean {
	Console.OUT.println("Dist dense matrix add-sub test");
	val dm = DistDenseMatrix.make(M, N);
	dm.initRandom();
	val dm1= DistDenseMatrix.make(M, N);
	dm.initRandom();
	
	val dm2= dm  + dm1;
	val dm_c  = dm2 - dm1;
	val ret   = dm.equals(dm_c);
	if (!ret)
	    Console.OUT.println("--------DistDenseMatrix Add-sub test failed!--------");
	
	return ret;
    }
    
    
    public def testAddAssociative():Boolean {
	Console.OUT.println("Dist dense matrix associative test");
	
	val a = DistDenseMatrix.make(M, N);
	val b = DistDenseMatrix.make(M, N);
	val c = DistDenseMatrix.make(M, N);
	a.initRandom();
	b.initRandom();
	c.initRandom();
	val c1 = a + b + c;
	val c2 = a + (b + c);
	val ret = c1.equals(c2);
	if (!ret)
	    Console.OUT.println("--------DistDenseMatrix Add associative test failed!--------");
	
	return ret;
    }
    
    public def testScaleAdd():Boolean {
	Console.OUT.println("Dist dense Matrix scaling-add test");
	
	val a = DistDenseMatrix.make(M, N);
	a.initRandom();
	
	val m = a.toDense();
	val a1= a * ET(0.2);
	val a2= ET(0.8) * a;
	var ret:Boolean = a.equals(a1+a2);
	ret &= a.equals(m);
	
	if (!ret)
	    Console.OUT.println("--------DistDenseMatrix scaling-add test failed!--------");
	
	return ret;
    }
    
    public def testCellMult():Boolean {
	Console.OUT.println("Dist dense Matrix cellwise mult test");
	
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
	
	if (!ret)
	    Console.OUT.println("--------Dist dense matrix cellwise mult test failed!--------");
	
	return ret;
    }
    
    public def testCellDiv():Boolean {
	Console.OUT.println("Dist dense matrix cellwise mult-div test");
	
	val a = DistDenseMatrix.make(M, N);
	val b = DistDenseMatrix.make(M, N);
	a.initRandom();
	b.initRandom();
	
	val c = (a + b) * a;
	val d =  c / (a + b);
	var ret:Boolean = d.equals(a);
	
	if (!ret)
	    Console.OUT.println("--------Dist dense matrix cellwise mult-div test failed!--------");
	
	return ret;
    }
    
    public static def main(args:Rail[String]) {
	new TestDistDense(args).execute();
    }
}
