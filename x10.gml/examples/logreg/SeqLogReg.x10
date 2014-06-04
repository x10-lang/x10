/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

package logreg;

import x10.util.Timer;

import x10.matrix.DenseMatrix;
import x10.matrix.Vector;
import x10.matrix.util.Debug;

public class SeqLogReg {
	val C = 2;
	val tol = 0.000001;
	val maxiter:Long;
	val maxinneriter:Long;
	
	//X = Rand(rows = 1000, cols = 1000, min = 1, max = 10, pdf = "uniform");
	val X:DenseMatrix;
	//N = nrow(X)
	//D = ncol(X)
	//y = Rand(rows = 1000, cols = 1, min = 1, max = 10, pdf = "uniform");
	val y:Vector(X.M);
	//w = Rand(rows=D, cols=1, min=0.0, max=0.0);
	val w:Vector(X.N);
	
	val eta0 = 0.0;
	val eta1 = 0.25;
	val eta2 = 0.75;
	val sigma1 = 0.25;
	val sigma2 = 0.5;
	val sigma3 = 4.0;
	val psi = 0.1; 	
	
	val tmp_w:Vector(X.N);
	val tmp_y:Vector(X.M);
	
	public def this(x_:DenseMatrix, y_:Vector(x_.M), w_:Vector(x_.N),
					it:Long, nit:Long) {
		X=x_; 
		y=y_ as Vector(X.M);
		w=w_ as Vector(X.N);
		
		tmp_w = Vector.make(X.N);
		tmp_y = Vector.make(X.M);
		
		maxiter = it;
		maxinneriter=nit;
	}
	
	public def run() {
		Debug.flushln("Starting logistic regression");
		//o = X %*% w
		val o:Vector(X.M)=Vector.make(X.M, 1);
		compute_XmultB(o, w);
		//logistic = 1.0/(1.0 + exp( -y * o))
		val logistic:Vector(X.M) = y.clone();
		logistic.scale(-1).cellMult(o).exp().cellAdd(1.0).cellDivBy(1.0);
		//logistic.print("Sequential logistic:");
		//obj = 0.5 * t(w) %*% w + C*sum(logistic)
		val obj = 0.5 * w.norm(w) + C*logistic.sum(); 
		
		//grad = w + C*t(X) %*% ((logistic - 1)*y)
		val grad:Vector(X.N) = Vector.make(X.N, 1);
		compute_grad(grad, logistic);
				
		//logisticD = logistic*(1-logistic)
		val logisticD:Vector(X.M) = logistic.clone();//Vector.make(logistic);
		logisticD.cellSubFrom(1.0).cellMult(logistic);

		//delta = sqrt(sum(grad*grad))
		val sq = grad.norm(grad);
		var delta:Double = Math.sqrt(grad.norm(grad));
		//Debug.flushln("Dist "+ sq+ " Delta is "+delta);
		
		//# number of iterations
		//iter = 0
		var iter:Long =0;
		
		//# starting point for CG
		//zeros_D = Rand(rows = D, cols = 1, min = 0.0, max = 0.0);
		val zeros_D:Vector(X.N) = Vector.make(X.N);
		//# boolean for convergence check
		//converge = (delta < tol) | (iter > maxiter)
		var converge:Boolean = (delta < tol) | (iter > maxiter);
		//norm_r2 = sum(grad*grad)
		var norm_r2:Double = grad.norm(grad);
		//alpha = t(w) %*% w
		var alpha:Double = w.norm(w);
		// Add temp memory space
		val s:Vector(X.N) = Vector.make(X.N);
		val r:Vector(X.N)= Vector.make(X.N);
		val d:Vector(X.N)= Vector.make(X.N);
		val Hd:Vector(X.N) = Vector.make(X.N);
		val onew:Vector(X.M) = Vector.make(X.M);
		val wnew:Vector(X.N) = Vector.make(X.N);
		val logisticnew:Vector(X.M) = Vector.make(X.M);		
		Debug.flushln("Done initialization. Starting converging iteration");
		while(!converge) {
			
// 			norm_grad = sqrt(sum(grad*grad))
			var norm_grad:Double=Math.sqrt(grad.norm(grad));
// 			# SOLVE TRUST REGION SUB-PROBLEM
// 			s = zeros_D
			s.reset();
// 			r = -grad
			grad.copyTo(r);
			r.scale(-1);
// 			d = r
			r.copyTo(d);
// 			inneriter = 0
			val inneriter:Long=0;
// 			innerconverge = ( sqrt(sum(r*r)) <= psi * norm_grad) 
			var innerconverge:Boolean;// = (Math.sqrt(r.norm(r)) <= psi * norm_grad);
 			innerconverge = false;
 			while (!innerconverge) {
//  
// 				norm_r2 = sum(r*r)
 				norm_r2 = r.norm(r);
// 				Hd = d + C*(t(X) %*% (logisticD*(X %*% d)))
 				compute_Hd(Hd, logisticD, d);

// 				alpha_deno = t(d) %*% Hd 
 				val alpha_deno = d.norm(Hd);
// 				alpha = norm_r2 / alpha_deno
 				alpha = norm_r2 / alpha_deno;
// 				s = s + castAsScalar(alpha) * d
 				s.cellAdd(alpha * d);
// 				sts = t(s) %*% s
 				val sts:Double = s.norm(s);
// 				delta2 = delta*delta 
 				val delta2 = delta*delta;
// 				stsScalar = castAsScalar(sts)
 				var stsScalar:Double = sts;
// 				shouldBreak = false;
 				var shouldBreak:Boolean = false;
 				if (stsScalar > delta2) {
// 
// 					std = t(s) %*% d
 					val std = s.norm(d);
// 					dtd = t(d) %*% d
 					val dtd = d.norm(d);
// 					rad = sqrt(std*std + dtd*(delta2 - sts))
 					val rad = Math.sqrt(std*std+dtd*(delta2-sts));
// 					stdScalar = castAsScalar(std)
 					val stdScalar = std;
 					var tau:Double;
 					if(stdScalar >= 0) {
 						tau = (delta2 - sts)/(std + rad);
 					} else {
 						tau = (rad - std)/dtd;
 					}
// 					
// 					s = s + castAsScalar(tau) * d
 					s.cellAdd(tau*d);
// 					r = r - castAsScalar(tau) * Hd
 					r.cellSub(tau * Hd);
// 					
// 					#break
// 					shouldBreak = true;
 					shouldBreak = true;
// 					innerconverge = true;
 					innerconverge = true;
// 					
 				} 
// 				
 				if (!shouldBreak) {
// 
// 					r = r - castAsScalar(alpha) * Hd
 					r.cellSub(alpha * Hd);
// 					old_norm_r2 = norm_r2 
 					val old_norm_r2 = norm_r2;
// 					norm_r2 = sum(r*r)
 					norm_r2 = r.norm(r);
// 					beta = norm_r2/old_norm_r2
 					val beta = norm_r2/old_norm_r2;
// 					d = r + beta*d
 					d.scale(beta).cellAdd(r);
// 					innerconverge = (sqrt(norm_r2) <= psi * norm_grad) | (inneriter < maxinneriter)
 					innerconverge = (Math.sqrt(norm_r2) <= psi * norm_grad) | (inneriter < maxinneriter);
 				}				
 			}  
// 			Debug.flushln("Dend trust region sub-problem");
// 			# END TRUST REGION SUB-PROBLEM
// 			# compute rho, update w, obtain delta
// 			qk = -0.5*(t(s) %*% (grad - r))
 			val qk = -0.5 * s.norm(grad-r);
// 			
// 			wnew = w + s
 			w.copyTo(wnew);
 			wnew.cellAdd(s);
// 			onew = X %*% wnew
 			compute_XmultB(onew, wnew);
// 			logisticnew = 1.0/(1.0 + exp(-y * o ))
 			logisticnew.scale(-1).cellMult(o).cellAdd(1.0).cellDivBy(1.0);
			
// 			objnew = 0.5 * t(wnew) %*% wnew + C * sum(logisticnew)
 			val objnew = 0.5 * wnew.norm(wnew) + C * logisticnew.sum();
// 			
// 			rho = (objnew - obj) / qk
 			val rho = (objnew - obj)/qk;
// 			rhoScalar = castAsScalar(rho);
 			val rhoScalar = rho;
// 			snorm = sqrt(sum( s * s ))
 			val snorm = Math.sqrt(s.norm(s));
 			if (rhoScalar > eta0) {
// 				
// 				w = wnew
 				wnew.copyTo(w);
// 				o = onew
 				onew.copyTo(o);
// 				grad = w + C*t(X) %*% ((logisticnew - 1) * y )
 				compute_grad(grad, logisticnew);
// 				
 			} 
// 			
 			iter = iter + 1;
 			converge = (norm_r2 < (tol * tol)) | (iter > maxiter);
// 
// 			alphaScalar = castAsScalar(alpha)
 			val alphaScalar = alpha;			
 			if (rhoScalar < eta0){
 				delta = Math.min(Math.max( alphaScalar , sigma1) * snorm, sigma2 * delta );
 			} else {
 				if (rhoScalar < eta1){
 					delta = Math.max(sigma1 * delta, Math.min( alphaScalar  * snorm, sigma2 * delta));
 				} else { 
 					if (rhoScalar < eta2) {
 						delta = Math.max(sigma1 * delta, Math.min( alphaScalar * snorm, sigma3 * delta));
 					} else {
 						delta = Math.max(delta, Math.min( alphaScalar * snorm, sigma3 * delta));
 					}
 				}
 			}
 			Debug.flushln("End of converging iteration");
		}
		Debug.flushln("End");
	}
	

	
	protected def compute_XmultB(result:Vector(X.M), opB:Vector(X.N)):void {
		//o = X %*% w
		Debug.flushln("Start sequential X * nw ");
		result.mult(X, opB);
		Debug.flushln("Done X * nw");	
		//result.print("Sequental X % B result:");

	}
	
	protected def compute_tXmultB(result:Vector(X.N), 
								  opB:Vector(X.M)):void {
		Debug.flushln("Start sequential X^t * b ");
		result.transMult(X, opB);
		Debug.flushln("Done");
		//result.print("Sequental X^t % B result:");
	}
	
	protected def compute_grad(grad:Vector(X.N), logistic:Vector(X.M)):void {
		//grad = w + C*t(X) %*% ((logistic - 1)*y)
		//Debug.flushln("Start sequential computing grad");
		logistic.copyTo(tmp_y);
		tmp_y.cellSub(1.0).cellMult(y);
		compute_tXmultB(grad, tmp_y);
		grad.scale(C);
		grad.cellAdd(w);
		//Debug.flushln("Done");
		//grad.print("Sequential grad:");
	}
	
	protected def compute_Hd(Hd:Vector(X.N), 
							logistricD:Vector(X.M), 
							d:Vector(X.N)):void {
		// 				Hd = d + C*(t(X) %*% (logisticD*(X %*% d)))
		//Debug.flushln("Start sequential computing Hd");
		compute_XmultB(tmp_y, d);
		//Debug.flushln("Done Xd <- X * d");
		tmp_y.cellMult(logistricD);
		//Debug.flushln("Done logD cellmult");
		compute_tXmultB(Hd, tmp_y);
		Hd.scale(C).cellAdd(d);
		//Debug.flushln("Done compute Hd");
		//Hd.print("Sequential Hd:");
	}
	
}
