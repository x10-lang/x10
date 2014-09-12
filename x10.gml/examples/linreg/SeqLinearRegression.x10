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
package linreg;

import x10.util.Timer;

import x10.matrix.DenseMatrix;
import x10.matrix.Vector;

/**
   Sequential implementation of linear regression
 */
public class SeqLinearRegression{

	public val iteration:Long;
	public val w:Vector(V.N);
	val lambda:Double;

	public val V:DenseMatrix;
	public val b:Vector;
	
	val p:Vector(V.N);
	val Vp:Vector(V.M);

	val r:Vector(V.N);
	val q:Vector(V.N);
	
	public def this(v:DenseMatrix, inb:Vector(v.N), it:Long) {
		V =v; 
		b = inb; 
		iteration = it;

		lambda = 0.000001;

		Vp = Vector.make(V.M);
		r  = Vector.make(V.N);
		p  = Vector.make(V.N);
		q  = Vector.make(V.N);
		w  = Vector.make(V.N);
		w.init(0.0);

	}
	
	public def run():Vector {
		var alpha:Double=0.0;
		var beta:Double =0.0;
					  
	    b.copyTo(r);
		b.copyTo(p);
		r.scale(-1.0);

		var norm_r2:Double = r.norm();
		var old_norm_r2:Double;
		val pq = Vector.make(1);
		
		for (1..iteration) {
			// 10: q=((t(V) %*% (V %*% p)) + lambda*p)
			q.mult(Vp.mult(V, p), V).scaleAdd(lambda, p);
			// 11: alpha= norm_r2/(t(p)%*%q);
			alpha = norm_r2 / p.dotProd(q);
			
			// 12: w=w+alpha*p;
			w.scaleAdd(p, alpha);
			
			// 13: old norm r2=norm r2;
			old_norm_r2 = norm_r2;

			// 14: r=r+alpha*q;
			r.scaleAdd(alpha, q);
			norm_r2 = r.norm();
			// 15: beta=norm r2/old norm r2;
			beta = norm_r2/old_norm_r2;
			// 16: p=-r+beta*p;
			p.scale(beta).cellSub(r);
			// 17: i=i+1;
		}
		//w.print("Seq result");
		return w;
	}
		
}
