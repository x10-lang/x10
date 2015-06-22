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
package logreg;

import x10.matrix.ElemType;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.util.Debug;

/**
 * Sequential implementation of logistic regression
 */
public class SeqLogReg {
        val C = 2;
        val tol = 0.000001f;
        val maxIterations:Long;
        val maxinneriter:Long;
        
    /** Matrix of training examples */
        val X:DenseMatrix;
    /** Vector of training regression targets */
        val y:Vector(X.M);
    /** Learned model weight vector, used for future predictions */
	val w:Vector(X.N);
	
	val eta0 = 0.0;
	val eta1 = 0.25;
	val eta2 = 0.75;
	val sigma1 = 0.25;
	val sigma2 = 0.5;
	val sigma3 = 4.0;
	val psi = 0.1; 	
	
	val tmp_y:Vector(X.M);
	
	public def this(x_:DenseMatrix, y_:Vector(x_.M), w_:Vector(x_.N),
					it:Long, nit:Long) {
		X=x_; 
		y=y_ as Vector(X.M);
		w=w_ as Vector(X.N);
		
		tmp_y = Vector.make(X.M);
		
		maxIterations = it;
		maxinneriter=nit;
	}
	
	public def run() {
		//o = X %*% w
		val o = Vector.make(X.M);
		compute_XmultB(o, w);
		//logistic = 1.0/(1.0 + exp( -y * o))
		val logistic = Vector.make(X.M);
		logistic.map(y, o, (y_i:ElemType, o_i:ElemType)=> { 1.0 / (1.0 + Math.exp(-y_i * o_i)) });
		//obj = 0.5 * t(w) %*% w + C*sum(logistic)
		val obj = 0.5 * w.dot(w) + C*logistic.sum();
		
		//grad = w + C*t(X) %*% ((logistic - 1)*y)
		val grad = Vector.make(X.N);
		compute_grad(grad, logistic);
				
		//logisticD = logistic*(1-logistic)
        val logisticD = new Vector(logistic.M, (i:Long)=> {logistic(i)*(1.0-logistic(i))});

		//delta = sqrt(sum(grad*grad))
		var delta:ElemType = grad.norm();
		
		//# number of iterations
		//iter = 0
		var iter:Long =0;
		
		//# starting point for CG
		//zeros_D = Rand(rows = D, cols = 1, min = 0.0, max = 0.0);
		//# boolean for convergence check
		//converge = (delta < tol) | (iter > maxiter)
		var converge:Boolean = (delta < tol) | (iter > maxIterations);
		//norm_r2 = sum(grad*grad)
		var norm_r2:ElemType = grad.dot(grad);
		//alpha = t(w) %*% w
		var alpha:ElemType = w.dot(w);
		// Add temp memory space
		val s = Vector.make(X.N);
		val r = Vector.make(X.N);
		val d = Vector.make(X.N);
		val Hd = Vector.make(X.N);
		val onew = Vector.make(X.M);
		val wnew = Vector.make(X.N);
		val logisticnew = Vector.make(X.M);		
		Debug.flushln("Done initialization. Starting converging iteration");
		while(!converge) {
// 			norm_grad = sqrt(sum(grad*grad))
			val norm_grad = grad.norm();
// 			# SOLVE TRUST REGION SUB-PROBLEM
// 			s = zeros_D
			s.reset();
// 			r = -grad
			r.scale(-1.0, grad);
// 			d = r
			r.copyTo(d);
// 			inneriter = 0
			val inneriter:Long=0;
// 			innerconverge = ( sqrt(sum(r*r)) <= psi * norm_grad) 
			var innerconverge:Boolean;// = (r.norm() <= psi * norm_grad);
 			innerconverge = false;
 			while (!innerconverge) {
// 				norm_r2 = sum(r*r)
 				norm_r2 = r.dot(r);
// 				Hd = d + C*(t(X) %*% (logisticD*(X %*% d)))
 				compute_Hd(Hd, logisticD, d);

// 				alpha_deno = t(d) %*% Hd 
 				val alpha_deno = d.dot(Hd);
// 				alpha = norm_r2 / alpha_deno
 				alpha = norm_r2 / alpha_deno;
// 				s = s + castAsScalar(alpha) * d
 				s.scaleAdd(alpha, d);
// 				sts = t(s) %*% s
 				val sts = s.dot(s);
// 				delta2 = delta*delta 
 				val delta2 = delta*delta;
// 				shouldBreak = false;
 				var shouldBreak:Boolean = false;
 				if (sts > delta2) {
// 					std = t(s) %*% d
 					val std = s.dot(d);
// 					dtd = t(d) %*% d
 					val dtd = d.dot(d);
// 					rad = sqrt(std*std + dtd*(delta2 - sts))
 					val rad = Math.sqrt(std*std+dtd*(delta2-sts));
 					var tau:ElemType;
 					if(std >= 0) {
 						tau = (delta2 - sts)/(std + rad);
 					} else {
 						tau = (rad - std)/dtd;
 					}

//                                      s = s + castAsScalar(tau) * d
                                        s.scaleAdd(tau, d);
//                                      r = r - castAsScalar(tau) * Hd
                                        r.scaleAdd(-tau, Hd);

//                                      #break
                                        shouldBreak = true;
                                        innerconverge = true;
                                } 

 				if (!shouldBreak) {
// 					r = r - castAsScalar(alpha) * Hd
 					r.scaleAdd(-alpha, Hd);
// 					old_norm_r2 = norm_r2 
 					val old_norm_r2 = norm_r2;
// 					norm_r2 = sum(r*r)
 					norm_r2 = r.dot(r);
// 					beta = norm_r2/old_norm_r2
 					val beta = norm_r2/old_norm_r2;
// 					d = r + beta*d
 					d.scale(beta).cellAdd(r);
// 					innerconverge = (sqrt(norm_r2) <= psi * norm_grad) | (inneriter < maxinneriter)
 					innerconverge = (Math.sqrt(norm_r2) <= psi * norm_grad) | (inneriter < maxinneriter);
 				}				
 			}  
// 			# END TRUST REGION SUB-PROBLEM
// 			# compute rho, update w, obtain delta
// 			qk = -0.5*(t(s) %*% (grad - r))
 			val qk = -0.5 * s.dot(grad - r);
// 			
// 			wnew = w + s
 			wnew.cellAdd(w, s);
// 			onew = X %*% wnew
 			compute_XmultB(onew, wnew);
// 			logisticnew = 1.0/(1.0 + exp(-y * o ))
            logisticnew.map(y, o, (y_i:ElemType, o_i:ElemType)=> { 1.0 / (1.0 + Math.exp(-y_i * o_i)) });
// 			objnew = 0.5 * t(wnew) %*% wnew + C * sum(logisticnew)
 			val objnew = 0.5 * wnew.dot(wnew) + C * logisticnew.sum();

// 			rho = (objnew - obj) / qk
 			val rho = (objnew - obj)/qk;
// 			snorm = sqrt(sum( s * s ))
 			val snorm = s.norm();
 			if (rho > eta0) {
// 				w = wnew
 				wnew.copyTo(w);
// 				o = onew
 				onew.copyTo(o);
// 				grad = w + C*t(X) %*% ((logisticnew - 1) * y )
 				compute_grad(grad, logisticnew);
 			} 

                        iter = iter + 1;
                        converge = (norm_r2 < (tol * tol)) | (iter > maxIterations);

 			if (rho < eta0){
 				delta = Math.min(Math.max(alpha, sigma1) * snorm, sigma2 * delta );
 			} else {
 				if (rho < eta1){
 					delta = Math.max(sigma1 * delta, Math.min(alpha * snorm, sigma2 * delta));
 				} else { 
 					if (rho < eta2) {
 						delta = Math.max(sigma1 * delta, Math.min(alpha * snorm, sigma3 * delta));
 					} else {
 						delta = Math.max(delta, Math.min(alpha * snorm, sigma3 * delta));
 					}
 				}
 			}
		}
	}
	
	protected def compute_XmultB(result:Vector(X.M), opB:Vector(X.N)):void {
		//o = X %*% w
		result.mult(X, opB);
	}
	
	protected def compute_tXmultB(result:Vector(X.N), 
								  opB:Vector(X.M)):void {
		result.transMult(X, opB);
	}
	
	protected def compute_grad(grad:Vector(X.N), logistic:Vector(X.M)):void {
		//grad = w + C*t(X) %*% ((logistic - 1)*y)
        logistic.map(logistic, y, (x:ElemType, v:ElemType)=> {(x - 1.0) * v});
		compute_tXmultB(grad, logistic);
		grad.scale(C);
		grad.cellAdd(w);
	}
	
	protected def compute_Hd(Hd:Vector(X.N), 
							logisticD:Vector(X.M), 
							d:Vector(X.N)):void {
		// 				Hd = d + C*(t(X) %*% (logisticD*(X %*% d)))
		compute_XmultB(tmp_y, d);
		tmp_y.cellMult(logisticD);
		compute_tXmultB(Hd, tmp_y);
		Hd.scale(C).cellAdd(d);
	}
	
}
