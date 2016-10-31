/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2016.
 */
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.ElemType;

/**
 * Sequential implementation of linear regression.
 * Based on linear regression script in SystemML
 * @see Elgohary et al. (2016). "Compressed linear algebra for large-scale
 *      machine learning". http://dx.doi.org/10.14778/2994509.2994515
 */
public class SeqLinearRegression {
    static val lambda = 1e-6 as Float; // regularization parameter
    public val tolerance:Float;

    /** Matrix of training examples */
	public val X:DenseMatrix;
    /** Vector of training regression targets */
    public val y:Vector(X.M);

    /** Learned model weight vector, used for future predictions */
    public val w:Vector(X.N);

	public val maxIterations:Long;
	
	val p:Vector(X.N);
	val Xp:Vector(X.M);

	val r:Vector(X.N);
	val q:Vector(X.N);
	
	public def this(X:DenseMatrix, y:Vector(X.M), it:Long, tolerance:Float) {
		this.X = X;
        this.y = y;
        if (it > 0) {
            this.maxIterations = it;
        } else {
            this.maxIterations = X.N; // number of features
        }
        this.tolerance = tolerance;

		Xp = Vector.make(X.M);
		r  = Vector.make(X.N);
		p  = Vector.make(X.N);
		q  = Vector.make(X.N);
		w  = Vector.make(X.N);
		w.init(0.0 as ElemType);
	}
	
	public def run():Vector {
        // 4: r = -(t(X) %*% y);
        r.mult(y, X);
        r.scale(-1.0 as ElemType);

        // 5: norm_r2 = sum(r * r); p = -r;
        r.copyTo(p);
        p.scale(-1.0 as ElemType);
        var norm_r2:ElemType = r.dot(r);
        val norm_r2_initial = norm_r2;
        val norm_r2_target = norm_r2_initial * tolerance * tolerance;

        Console.OUT.println("||r|| initial value = " + Math.sqrt(norm_r2)
         + ",  target value = " + Math.sqrt(norm_r2_target));

		for (var iter:Int = 0n; iter < maxIterations && norm_r2 > norm_r2_target; ++iter) {
            // compute conjugate gradient
            // 9: q = ((t(X) %*% (X %*% p)) + lambda * p);
			q.mult(Xp.mult(X, p), X).scaleAdd(lambda, p);
            q(q.M-1) -= lambda * p(q.M-1); // don't regularize intercept!

            // 11: alpha = norm_r2 / sum(p * q);
			val alpha = norm_r2 / p.dotProd(q);
			
            // update model and residuals
            // 13: w = w + alpha * p;
			w.scaleAdd(alpha, p);

            // 14: r = r + alpha * q;
			r.scaleAdd(alpha, q);

            // 15: old_norm_r2 = norm_r2;
			val old_norm_r2 = norm_r2;

            // 16: norm_r2 = sum(r^2);
			norm_r2 = r.dot(r);

            // 17: p = -r + norm_r2/old_norm_r2 * p;
			p.scale(norm_r2/old_norm_r2).cellSub(r);

            Console.OUT.println("Iteration " + iter
             + ":  ||r|| / ||r init|| = "
                 + Math.sqrt(norm_r2 / norm_r2_initial));
		}

		return w;
	}
}
