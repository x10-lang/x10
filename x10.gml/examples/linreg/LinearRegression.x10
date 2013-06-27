/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */
package linreg;

import x10.util.Timer;

import x10.matrix.Debug;
import x10.matrix.Matrix;
import x10.matrix.Vector;
import x10.matrix.blas.DenseMatrixBLAS;

import x10.matrix.block.Grid;
import x10.matrix.sparse.SparseCSC;
import x10.matrix.sparse.SparseMultDenseToDense;

import x10.matrix.distblock.DistGrid;
import x10.matrix.distblock.DistMap;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DupVector;
import x10.matrix.distblock.DistVector;

/**
 * Parallel linear regression based on GML distributed 
 * dense/sparse matrix
 */
public class LinearRegression{

	//Input matrix
	public val V:DistBlockMatrix;
	public val b:Vector(V.N);
	//Parammeters
	public val iteration:Long;
	static val lambda:Double = 0.000001;
	
	public val w:Vector(V.N);

	val d_p:DupVector(V.N);
	val p:Vector(V.N);
	val Vp:DistVector(V.M);

	val r:Vector(V.N);
	val d_q:DupVector(V.N);
	val q:Vector(V.N);
	
	//----Profiling-----
	public var parCompT:Long=0;
	public var seqCompT:Long=0;
	public var commT:Long;
	
	public def this(v:DistBlockMatrix, b_:Vector(v.N), it:Int) {
		iteration = it;
		V =v;
		b =b_ as Vector(V.N);

		Vp = DistVector.make(V.M, V.getAggRowBs());
				
		r  = Vector.make(V.N);
		d_p= DupVector.make(V.N);
		p  = d_p.local();
		
		d_q= DupVector.make(V.N);
		q  = d_q.local();

		w  = Vector.make(V.N);
	}
	
	public static def make(mV:Int, nV:Int, nRowBs:Int, nColBs:Int, nzd:Double, it:Int) {
		//grid = new Grid(mV, nV, Place.MAX_PLACES, 1);
		val V = DistBlockMatrix.makeSparse(mV, nV, nRowBs, nColBs, Place.MAX_PLACES, 1, nzd);
		val b = Vector.make(nV);

		Console.OUT.printf("Start init sparse matrix V(%d,%d) blocks(%dx%d) ", mV, nV, nRowBs, nColBs);
		Console.OUT.printf("dist(%dx%d) nzd:%f\n", Place.MAX_PLACES, 1, nzd);
		V.initRandom();

		Debug.flushln("Done. Start init other matrices, b, r, p, q, and w");		
		b.initRandom();

		return new LinearRegression(V, b, it);
	}

	public def run():Vector {
		var ct:Long;
		var alpha:Double=0.0;
		var beta:Double =0.0;
					  
					  
	    b.copyTo(r);
		b.copyTo(p);
		r.scale(-1.0);

		var norm_r2:Double = r.norm();
		var old_norm_r2:Double;

		for (1..iteration) {
			
			d_p.sync();

			// Parallel computing

			ct = Timer.milliTime();
			// 10: q=((t(V) %*% (V %*% p)) )
			d_q.mult(Vp.mult(V, d_p), V);

			parCompT += Timer.milliTime() - ct;
			

			// Sequential computing

			ct = Timer.milliTime();
			//q = q + lambda*p
			q.scaleAdd(lambda, p);
			
			// 11: alpha= norm_r2/(t(p)%*%q);
			alpha = norm_r2 / p.dotProd(q);
			
			// 12: w=w+alpha*p;
			w.scaleAdd(alpha, p);
			
			// 13: old norm r2=norm r2;
			old_norm_r2 = norm_r2;

			// 14: r=r+alpha*q;
			r.scaleAdd(alpha, q);
			norm_r2 = r.norm(r);

			// 15: beta=norm r2/old norm r2;
			beta = norm_r2/old_norm_r2;

			// 16: p=-r+beta*p;
			p.scale(beta).cellSub(r);
			
			seqCompT += Timer.milliTime() - ct;
			// 17: i=i+1;
		}
		commT = d_q.getCommTime() + d_p.getCommTime();
		//w.print("Parallel result");
		return w;
	}
		
}
