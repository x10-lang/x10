/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */


import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

/**
 * Examples of dense matrix multiplication and cell-wise operations using GML.
 */
public class DenseExample{

    public static def main(args:Rail[String]) {
		val m = (args.size > 0) ? Int.parse(args(0)):50;
		val n = (args.size > 1) ? Int.parse(args(1)):51;
		val testcase = new RunDenseExample(m, n);
		testcase.run();
	}


    
    public static class RunDenseExample(M:Long, N:Long) {
    	    	
    	public def this(m:Long, n:Long) {
    		property(m, n);
    	}
    	
    	public def run (): void {
    		Console.OUT.println("Starting dense matrix clone/add/sub/scaling exaple on "+
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
    		ret &= (testMatMult());
    		
    		if (ret)
    			Console.OUT.println("Test passed!");
    		else
    			Console.OUT.println("----------------Test failed!----------------");
    	}
    	
    	public def testClone():Boolean{
    		
    		val dm = DenseMatrix.make(M,N);
    		dm.initRandom(); // same as  val dm = DenseMatrix.makeRand(M, N); 
    		
    		val dm1 = dm.clone();
    		val ret = dm.equals(dm1);
    		if (ret)
    			Console.OUT.println("Dense Matrix Clone test passed!");
    		else
    			Console.OUT.println("--------Dense Matrix Clone test failed!--------");
    		return ret;
    	}
    	
    	public def testScale():Boolean{
    		val dm = DenseMatrix.make(M, N); //Create a dense matrix instance
    		dm.initRandom();                 //Ininital all elements in dense matrix with random values
    		val dm1  = dm * 2.5;             //Perform scaling operation on all elements using operator overloading
    		dm1.scale(1.0/2.5);              //Perform scaling operation using method call
    		val ret = dm.equals(dm1);        //Test all elements of two matrices
    		if (ret)
    			Console.OUT.println("Dense Matrix scaling test passed!");
    		else
    			Console.OUT.println("--------Dense matrix Scaling test failed!--------");	
    		return ret;
    	}
    	
    	public def testAdd():Boolean {
    		val dm = DenseMatrix.make(M, N);
    		dm.initRandom();
    		val dm1 = dm * -1.0;
    		val dm0 = dm + dm1;              //Perform matrix cell addition 
    		val ret = dm0.equals(0.0);
    		if (ret)
    			Console.OUT.println("Add: dm + dm*-1 test passed");
    		else
    			Console.OUT.println("--------Add: dm + dm*-1 test failed--------");
    		return ret;
    	}
    	
    	public def testAddSub():Boolean {
    		val dm = DenseMatrix.make(M, N);
    		val dm1= DenseMatrix.make(M, N);
    		dm.initRandom();
    		dm1.initRandom();
    		
    		val dm2= dm  + dm1;
    		val dm_c  = dm2 - dm1;
    		val ret   = dm.equals(dm_c);
    		if (ret)
    			Console.OUT.println("Dense matrix Add-sub test passed!");
    		else
    			Console.OUT.println("--------Dense matrix Add-sub test failed!--------");
    		return ret;
    	}
    	
    	public def testAddAssociative():Boolean {
    		
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
    		
    		val a = DenseMatrix.makeRand(M, N);
    		val b = DenseMatrix.makeRand(M, N);
    		
    		val c = (a + b) * a;    //Matrix cellwise multiplication
    		val d = a * a + b * a;  
    		
    		val ret = c.equals(d);
    		if (ret)
    			Console.OUT.println("Dense Matrix cellwise mult passed!");
    		else
    			Console.OUT.println("--------Dense matrix cellwise mult test failed!--------");
    		return ret;
    	}
    	
    	public def testCellDiv():Boolean {
    		
    		val a = DenseMatrix.makeRand(M, N);
    		val b = DenseMatrix.makeRand(M, N);
    		
    		val c = (a + b) * a;
    		val d =  c / (a + b);     //Matrix cellwise division
    		val ret = d.equals(a);
    		if (ret)
    			Console.OUT.println("Dense Matrix cellwise mult-div passed!");
    		else
    			Console.OUT.println("--------Dense matrix cellwise mult-div test failed!--------");
    		return ret;
    	}
    	
    	public def testMatMult():Boolean {
    		val K = N;
    		Console.OUT.printf("\nTest Dense matrix multiply associative: %dx%d * %dx%d * %dx%d\n",
    				M, K, K, N, N, M);
    		val a = DenseMatrix.make(M, K);
    		val b = DenseMatrix.make(K, N);
    		val c = DenseMatrix.make(N, M);
    		a.initRandom(); 
    		b.initRandom(); 
    		c.initRandom();
    		
    		val d = (a % b) % c;
    		val d1= a % (b % c);
    		
    		val ret = d1.equals(d);
    		Console.OUT.printf("Result matrix: %dx%d\n", d.M, d.N);
    		if (ret)
    			Console.OUT.println("Dense matrix multiply associative test passed!");
    		else
    			Console.OUT.println("-----Dense matrix multiply associative test failed!-----");
    		return ret;
    	}
    }
}
