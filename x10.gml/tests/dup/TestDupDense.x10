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
import x10.matrix.ElemType;

import x10.matrix.dist.DupDenseMatrix;

public class TestDupDense extends x10Test {
    static def ET(a:Double)= a as ElemType;
    static def ET(a:Float)= a as ElemType;
    public val M:Long;
    public val N:Long;
    public val K:Long;
    
    public def this(args:Rail[String]) {
	M = args.size > 0 ? Long.parse(args(0)):50;
	N = args.size > 1 ? Long.parse(args(1)):(M as Int)+1;
	K = args.size > 2 ? Long.parse(args(2)):(M as Int)+2;
    }
    
    public def run():Boolean {
	Console.OUT.println("Dup dense matrix tests in " + Place.numPlaces()+" places");
	var ret:Boolean=true;
	@Ifndef("MPI_COMMU") { // TODO Deadlocks!
	    ret &=testClone();
	    ret &=testClone();
	    ret &=testAddSub();
	    ret &=testCellMult();
	    ret &=testCellDiv();
	}
	return ret;
    }
    
    public def testClone():Boolean {
	Console.OUT.println("Dup dense Matrix clone test");
	val d1 = DupDenseMatrix.makeRand(N,M);
	val d2 = d1.clone();
	val d3 = d1 - d2;
	
	var ret:Boolean = d3.equals(ET(0.0));
	ret &= d1.syncCheck();
	ret &= d2.syncCheck();
	
	if (!ret)
	    Console.OUT.println("--------Dup Dense Matrix clone test failed!--------");
	
        d1(1, 1) = d2(2,2) = ET(10.0);
	
       	if ((d1(1,1)==d2(2,2)) && (d1(1,1)== ET(10.0))) {
	    ret &= true;
    	} else {
	    ret &= false;
	    Console.OUT.println("---------- Dup Dense Matrix chain assignment test failed!-------");
    	}
	
    	return ret;
    }
    
    public def testAddSub():Boolean{
	Console.OUT.println("Dup dense Matrix add test");
	val d1 = DupDenseMatrix.makeRand(M,N);
	val d2 = DupDenseMatrix.makeRand(M,N);
	val d3 = (d1 + d2) - (d2 + d1);
	var ret:Boolean = d3.equals(ET(0.0));
	
	ret &= d3.syncCheck();
	if (!ret)
	    Console.OUT.println("--------Dup Dense Matrix addsub test failed!--------");
	return ret;
    }
    
    public def testCellMult():Boolean {
	Console.OUT.println("Dup dense Matrix cellwise mult test");
	
	val a = DupDenseMatrix.makeRand(M, N);
	val b = DupDenseMatrix.makeRand(M, N);
	val c = (a + b) * a;
	val d = a * a + b * a;
	var ret:Boolean = c.equals(d);
	ret &= c.syncCheck();
	ret &= d.syncCheck();
	if (!ret)
	    Console.OUT.println("--------Dup dense matrix cellwise mult test failed!--------");
	return ret;
    }
    
    public def testCellDiv():Boolean {
	Console.OUT.println("Dup dense Matrix cellwise mult-div test");
	
	val a = DupDenseMatrix.makeRand(M, N);
	val b = DupDenseMatrix.makeRand(M, N);
	val c = (a + b) * a;
	val d =  c / (a + b);
	var ret:Boolean = d.equals(a);
	ret &= c.syncCheck();
	ret &= d.syncCheck();
	if (!ret)
	    Console.OUT.println("--------Dup dense matrix cellwise mult-div test failed!--------");
	return ret;
    }
    
    public static def main(args:Rail[String]) {
	new TestDupDense(args).execute();
    }
}
