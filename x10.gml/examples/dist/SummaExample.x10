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

import x10.compiler.Ifdef;
import x10.compiler.Ifndef;

import x10.matrix.Matrix;
import x10.matrix.util.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.block.Grid;

import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;
import x10.matrix.dist.DistSparseMatrix;

import x10.matrix.dist.summa.SummaDense;
import x10.matrix.dist.summa.SummaSparse;

/**
 * Examples of distributed dense/sparse matrix multiplication using SUMMA algorithm
 */
public class SummaExample{
	
    public static def main(args:Rail[String]) {
    	val m = args.size > 0 ? Long.parse(args(0)):21;
    	val n = args.size > 1 ? Long.parse(args(1)):23;
    	val k = args.size > 2 ? Long.parse(args(2)):25;	
    	val p = args.size > 3 ? Double.parse(args(3)):0.5;
    	val testcase = new RunSummaExample(m, n, k, p);
		testcase.run();
	}

    static class RunSummaExample(M:Long, N:Long, K:Long, nzd:Double) {
    	public val pA:Grid;
    	public val pB:Grid;
    	public val pC:Grid;
    	
    	public def this(m:Long, n:Long, k:Long, p:Double) {
    		property(m, n, k, p);
    		
    		val numP = Place.numPlaces();//Place.numPlaces();
    		
    		// All input matrices must be partitioned in the same way
    		// of having same number of row and column blocks.
    		pA = Grid.make(M, K); //Partition matrix 
    		pB = Grid.make(K, N);
    		pC = Grid.make(M, N);
    	}
    	
    	public def run(): void {
    		var ret:Boolean = true;
    		
    		testDenseMult();
    		testDenseMultTrans();
    		testSparse();
    		testSparseMultTrans();
    		
    	}

    	public def testDenseMult():DistDenseMatrix {
    		val numP = Place.numPlaces();//Place.numPlaces();
    		Console.OUT.printf("\nTest SUMMA dist dense matrix over %d places\n", numP);
    		val da = DistDenseMatrix.make(pA);
    		da.initRandom();
            //Console.OUT.println("Input A\n" + da);
    		
    		val db = DistDenseMatrix.make(pB);
    		db.initRandom();
    		//Console.OUT.println("Input B\n" + db);
    		
    		val dc = DistDenseMatrix.make(pC);
    		
    		Debug.flushln("Start calling SUMMA Dense X10 routine");
    		SummaDense.mult(1, 0.0, da, db, dc);
    		Debug.flushln("SUMMA done");
    		
    		return dc;
    	}
    	
    	public def testDenseMultTrans():DistDenseMatrix {
    		val numP = Place.numPlaces();//Place.numPlaces();
    		Console.OUT.printf("\nTest SUMMA dist dense matrix multTrans over %d places\n", numP);
    		val da = DistDenseMatrix.make(M, K);
    		da.initRandom();
            //Console.OUT.println("Input A\n" + da);
    		
    		val db = DistDenseMatrix.make(N, K);
    		db.initRandom();
    		//Console.OUT.println("Input B\n" + db);
    		
    		val dc = DistDenseMatrix.make(M, N);
    		
    		Debug.flushln("Start calling SUMMA Dense multTrans X10 routine");
    		SummaDense.multTrans(1, 0.0, da, db, dc);
    		Debug.flushln("SUMMA done");
    		//Console.OUT.println("Summa result\n" + dc);
    		
    		return dc;
    	}

    	public def testSparse():DistDenseMatrix {
    		val numP = Place.numPlaces();//Place.numPlaces();
    		Console.OUT.printf("\nTest SUMMA dist sparse matrix over %d places and sparsity %f\n", 
    				numP, nzd);
    		val da = DistSparseMatrix.make(pA, nzd);
    		da.initRandom();
    		//Console.OUT.println("Input A\n" + da);
    		
    		val db = DistSparseMatrix.make(pB, nzd);
    		db.initRandom();
    		//Console.OUT.println("Input B\n" + db);
    		
    		val dc = DistDenseMatrix.make(pC);
    		
    		Debug.flushln("Start calling SUMMA sparse mult sparse to dense X10 routine");
    		SummaSparse.mult(1, 0.0, da, db, dc);
    		Debug.flushln("SUMMA done");
    		
    		return dc;
    	}

    	public def testSparseMultTrans():DistDenseMatrix {
    		val numP = Place.numPlaces();//Place.numPlaces();
    		Console.OUT.printf("\nTest SUMMA x10 dist dense matrix multTrans over %d places\n", numP);
    		val da = DistSparseMatrix.make(M, K, nzd); 
    		da.initRandom();
    		
    		val db = DistSparseMatrix.make(N, K, nzd);
    		db.initRandom();
    		
    		val dc = DistDenseMatrix.make(M, N);
    		
    		Debug.flushln("Start calling SUMMA sparse multTrans X10 routine");
    		SummaSparse.multTrans(1, 0.0, da, db, dc);
    		Debug.flushln("SUMMA done");
    		
    		return dc;
    	}
    }
}
