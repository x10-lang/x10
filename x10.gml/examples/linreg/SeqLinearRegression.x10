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

import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.ElemType;

/**
 * Sequential implementation of linear regression.
 * Based on linear regression script in SystemML from Ghoting et al. (2011).
 * @see Ghoting et al. (2011) SystemML: Declarative machine learning on
 *      MapReduce. Proceedings of ICDE 2011 doi:10.1109/ICDE.2011.5767930
 */
public class SeqLinearRegression {
    static val lambda = 1e-6 as Float; // regularization parameter

    /** Matrix of training examples */
	public val V:DenseMatrix;
    /** Vector of training regression targets */
    public val y:Vector(V.M);

    /** Learned model weight vector, used for future predictions */
    public val w:Vector(V.N);

	public val maxIterations:Long;
	
	val p:Vector(V.N);
	val Vp:Vector(V.M);

	val r:Vector(V.N);
	val q:Vector(V.N);
	
	public def this(V:DenseMatrix, y:Vector(V.M), it:Long) {
		this.V = V;
        this.y = y;
		maxIterations = it;

		Vp = Vector.make(V.M);
		r  = Vector.make(V.N);
		p  = Vector.make(V.N);
		q  = Vector.make(V.N);
		w  = Vector.make(V.N);
		w.init(0.0 as ElemType);
	}
	
	public def run():Vector {
        // 4: r=-(t(V) %*% y)
        r.mult(y, V);
        // 5: p=-r
        r.copyTo(p);
        // 4: r=-(t(V) %*% y)
        r.scale(-1.0 as ElemType);
        // 6: norm_r2=sum(r*r)
        var norm_r2:ElemType = r.dot(r);

		for (1..maxIterations) {
			// 10: q=((t(V) %*% (V %*% p)) + lambda*p)
			q.mult(Vp.mult(V, p), V).scaleAdd(lambda, p);
			// 11: alpha= norm_r2/(t(p)%*%q);
			val alpha = norm_r2 / p.dotProd(q);
			
			// 12: w=w+alpha*p;
			w.scaleAdd(alpha, p);
			
			// 13: old norm r2=norm r2;
			val old_norm_r2 = norm_r2;

			// 14: r=r+alpha*q;
			r.scaleAdd(alpha, q);
            // 15: norm_r2=sum(r*r);
			norm_r2 = r.dot(r);
			// 16: beta=norm r2/old norm r2;
			val beta = norm_r2/old_norm_r2;
			// 17: p=-r+beta*p;
			p.scale(beta).cellSub(r);
		}

		return w;
	}
}
