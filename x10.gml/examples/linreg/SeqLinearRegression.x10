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

package linreg;

import x10.io.Console;
import x10.util.Timer;
//
import x10.matrix.Debug;
//
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.sparse.SparseCSC;

import x10.matrix.blas.DenseMultBLAS;
import x10.matrix.sparse.SparseMultDenseToDense;

/**
   Sequential implementation of linear regression
   <p>

   <p>
 */
public class SeqLinearRegression{

	public val iteration:Int;
	public val w:DenseMatrix(V.N, 1);
	val lambda:Double;

	public val V:DenseMatrix;
	public val b:DenseMatrix;
	
	val p:DenseMatrix(V.N, 1);
	val p1:DenseMatrix(V.N, 1);
	val Vp:DenseMatrix(V.M, 1);

	val r:DenseMatrix(V.N, 1);
	val q:DenseMatrix(V.N, 1);
	
	public def this(v:DenseMatrix, inb:DenseMatrix(v.N,1), it:Int) {
		V =v; 
		b = inb; 
		iteration = it;

		lambda = 0.000001;

		Vp = DenseMatrix.make(V.M, 1);
		r  = DenseMatrix.make(V.N, 1);
		p  = DenseMatrix.make(V.N, 1);
		p1 = DenseMatrix.make(V.N, 1);
		q  = DenseMatrix.make(V.N, 1);
		w  = DenseMatrix.make(V.N, 1);
		w.init(0.0);

	}
	
	public def run():DenseMatrix {
		var alpha:Double=0.0;
		var beta:Double =0.0;
					  
	    b.copyTo(r as DenseMatrix(b.M, b.N));
		r.scale(-1.0);
		b.copyTo(p as DenseMatrix(b.M, b.N));

		var norm_r2:Double = r.norm(r);
		var old_norm_r2:Double;
		val pq = DenseMatrix.make(1, 1);

		for (1..iteration) {
			// 10: q=((t(V) %*% (V %*% p)) + lambda*p)
			q.transMult(V, Vp.mult(V, p)).cellAdd(p1.scale(lambda));
			//Vp.mult(denV, p);                   Vp.print("Sequential V * p:\n");
			//q.transMult(denV, Vp);              q.print("Sequential V^t * V * p:\n");
			//q.cellAdd(p1.scale(lambda));        q.print("Sequentail q + lambda * p\n");

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

			// 17: i=i+1;
		}
		return w;
	}
		
}
