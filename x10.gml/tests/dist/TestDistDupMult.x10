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

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.ElemType;

import x10.matrix.blas.DenseMatrixBLAS;
import x10.matrix.block.Grid;

import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;
import x10.matrix.dist.DupDenseMatrix;

import x10.matrix.dist.DistMultDupToDist;

/**
 * This class contains test cases for dist duplicated matrix multiplication.
 */
public class TestDistDupMult extends x10Test {
    static def ET(a:Double)= a as ElemType;
    static def ET(a:Float)= a as ElemType;
    public val nnz:Float;
    public val M:Long;
    public val N:Long;
    public val K:Long;
    
    public def this(args:Rail[String]) {
	M = args.size > 0 ? Long.parse(args(0)):50;
	nnz = args.size > 1 ?Float.parse(args(1)):0.5f;
	N = args.size > 2 ? Long.parse(args(2)):(M as Int)+1;
	K = args.size > 3 ? Long.parse(args(3)):(M as Int)+2;	
    }
    
    public def run():Boolean {
	var ret:Boolean = true;
	ret &= (testDistS_DupD());
	
	return ret;
    }
    
    
    public def testDistS_DupD():Boolean {
        var ret:Boolean = true;
	@Ifndef("MPI_COMMU") { // TODO DupDenseMatrix.init deadlocks!
	    val numP = Place.numPlaces();
	    Console.OUT.printf("Test Dist sparse mult Dup dense over %d places\n", numP);
	    val gpartA = new Grid(M, K, numP, 1);
	    val da = DistSparseMatrix.make(gpartA, nnz);
	    da.initRandom(nnz);
	    
	    val db = DupDenseMatrix.makeRand(K, N);
	    db.initRandom();
	    
	    val gpartC = new Grid(M, N, numP, 1);
	    val dc = DistDenseMatrix.make(gpartC);
	    
	    DistMultDupToDist.comp(da, db, dc, false);
	    
	    val ma = da.toDense();
	    val mb = db.getMatrix();
	    val mc = DenseMatrix.make(ma.M, mb.N);
	    DenseMatrixBLAS.comp(ET(1.0), ma, mb, ET(0.0), mc);
	    
	    ret = dc.equals(mc);
	    if (!ret)
		Console.OUT.println("-----DistCSC-DupDense multplication test failed!-----");
	}
	return ret;
    }
    
    public static def main(args:Rail[String]) {
	new TestDistDupMult(args).execute();
    }
}
