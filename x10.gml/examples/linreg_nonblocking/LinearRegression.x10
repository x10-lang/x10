/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2014.
 */

import x10.util.Timer;

import x10.matrix.util.Debug;
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.block.Grid;
import x10.matrix.dist.DistSparseMatrix;
import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DupDenseMatrix;

/**
 * Parallel linear regression based on GML distributed 
 * dense/sparse matrix
 */
public class LinearRegression{
	public val grid:Grid;

	public val V:DistSparseMatrix;
	public val b:DenseMatrix;
	public val w:DenseMatrix(V.N, 1L);

	public val iteration:Long;
	val lambda:Double;
	
	val p:DenseMatrix(V.N, 1L);
	val d_p:DupDenseMatrix(V.N, 1L);
	val p1:DenseMatrix(V.N, 1L);
	val Vp:DistDenseMatrix(V.M, 1L);

	val r:DenseMatrix(V.N, 1L);
	val d_q:DupDenseMatrix(V.N, 1L);
	val d_q2:DupDenseMatrix(V.N, 1L);
	val q:DenseMatrix(V.N, 1L);
	
	//----Profiling-----
	public var parCompT:Long=0;
	public var seqCompT:Long=0;
	public var commT:Long;
	
	public def this(mV:Long, nV:Long, nzd:Double, it:Long) {
		grid = new Grid(mV, nV, Place.numPlaces(), 1);
		V = DistSparseMatrix.make(grid, nzd);
		b = DenseMatrix.make(nV, 1L);
		//V.printBlockMemAlloc(); 

		Debug.flushln("Start init sparse matrix V " + 
 					  "("+V.M+","+V.N+") density:"+nzd);
		V.initRandom();

		Debug.flushln("Done. Start init other matrices, b, r, p, q, and w");		
		b.initRandom();
		
		iteration = it;
		lambda = 0.000001;
		Vp = DistDenseMatrix.make(new Grid(V.M, 1L, Place.numPlaces(), 1L));
		
		r  = DenseMatrix.make(V.N, 1L);
		d_p= DupDenseMatrix.make(V.N, 1L);
		p  = d_p.local();
	   
		p1 = DenseMatrix.make(V.N, 1L);
		d_q= DupDenseMatrix.make(V.N, 1L);
		q  = d_q.local();

		d_q2 = DupDenseMatrix.make(V.N, 1L);
		w  = DenseMatrix.make(V.N, 1L);
		w.init(0.0);
		Debug.flushln("Init done");
	}

	public def run():DenseMatrix {
		var ct:Long;
		var alpha:Double=0.0;
		var beta:Double =0.0;
					  
	    b.copyTo(r as DenseMatrix(b.M, b.N));
		r.scale(-1.0);
		b.copyTo(p as DenseMatrix(b.M, b.N));

		var norm_r2:Double = r.norm(r);
		var old_norm_r2:Double;
		val pq = DenseMatrix.make(1, 1);

		for (1..iteration) {
			d_p.sync();

			// Parallel computing
			ct = Timer.milliTime();
			// 10: q=((t(V) %*% (V %*% p)) + lambda*p)
			d_q2.transMult(V, Vp.mult(V, d_p), false);
			//Vp.mult(V, d_p);
			//d_q.transMult(V, Vp, d_q2, false); 	q.print("Parallel V^t * V * p:\n");
			parCompT += Timer.milliTime() - ct;
			

			// Sequential computing
			ct = Timer.milliTime();
			q.cellAdd(p1.scale(lambda));        //q.print("parallel q + p * lamdba:");
			
			// 11: alpha= norm_r2/(t(p)%*%q);
			alpha = norm_r2 / pq.transMult(p, q)(0, 0);
			
			// 12: w=w+alpha*p;
			p.copyTo(p1);
			w.cellAdd(p1.scale(alpha));
			
			// 13: old norm r2=norm r2;
			old_norm_r2 = norm_r2;

			// 14: r=r+alpha*q;
			r.cellAdd(q.scale(alpha));
			norm_r2 = r.norm(r);

			// 15: beta=norm r2/old norm r2;
			beta = norm_r2/old_norm_r2;

			// 16: p=-r+beta*p;
			p.scale(beta).cellSub(r);
			
			seqCompT += Timer.milliTime() - ct;
			// 17: i=i+1;
		}
		commT = d_q2.commTime + d_p.commTime;
		return w;
	}
		
}
