/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2015.
 */
import x10.util.Timer;

import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.block.Grid;
import x10.matrix.dist.DistDenseMatrix;
import x10.matrix.dist.DistSparseMatrix;
import x10.matrix.dist.DupDenseMatrix;
import x10.matrix.dist.DistMultDupToDist;
import x10.matrix.dist.DistMultDistToDup;
import x10.matrix.util.Debug;

public class LogisticRegression {
	val C = 2;
	val tol = 0.000001;
	val maxiter:Long;
	val maxinneriter:Long; 
	
	//X = Rand(rows = 1000, cols = 1000, min = 1, max = 10, pdf = "uniform");
	val X:DistSparseMatrix;
	//N = nrow(X)
	//D = ncol(X)
	//y = Rand(rows = 1000, cols = 1, min = 1, max = 10, pdf = "uniform");
	val y:DenseMatrix(X.M,1);
	//w = Rand(rows=D, cols=1, min=0.0, max=0.0);
	val w:DenseMatrix(X.N,1);

	val dup_w:DupDenseMatrix(X.N, 1); 
	val dst_y:DistDenseMatrix(X.M, 1);
	
	val tmp_y:DenseMatrix(X.M,1);
	
	val dst_ty:DistDenseMatrix(1, X.M);

	val eta0 = 0.0;
	val eta1 = 0.25;
	val eta2 = 0.75;
	val sigma1 = 0.25;
	val sigma2 = 0.5;
	val sigma3 = 4.0;
	val psi = 0.1; 	

	public var paraRunTime:Long=0;
	public var commUseTime:Long=0;
	
	public def this(x_:DistSparseMatrix, y_:DenseMatrix(x_.M,1), w_:DenseMatrix(x_.N,1),
					it:Long, nit:Long) {
		X=x_; y=y_ as DenseMatrix(X.M, 1);	w=w_ as DenseMatrix(X.N, 1);
		
		val prt_y = new Grid(X.M, 1, Place.numPlaces(), 1);
		dst_y  = DistDenseMatrix.make(prt_y) as DistDenseMatrix(X.M, 1);
		dup_w  = DupDenseMatrix.make(X.N, 1);
		
		val prt_ty = new Grid(1, X.M, 1, Place.numPlaces());
		dst_ty = DistDenseMatrix.make(prt_ty) as DistDenseMatrix(1, X.M);

		tmp_y  = DenseMatrix.make(X.M,1);
		maxiter = it;
		maxinneriter =nit;
	}
	
	public def run() {
		//o = X %*% w
		val o = DenseMatrix.make(X.M, 1);
		compute_XmultB(o, w);
		//logistic = 1.0/(1.0 + exp( -y * o))
		val logistic = y.clone();
        logistic.map(y, o, (y_i:Double, o_i:Double)=> { 1.0 / (1.0 + Math.exp(-y_i * o_i)) });

		//obj = 0.5 * t(w) %*% w + C*sum(logistic)
		val obj = 0.5 * w.norm(w) + C*logistic.sum(); 
		
		//grad = w + C*t(X) %*% ((logistic - 1)*y)
		val grad = DenseMatrix.make(X.N, 1);//w.clone();
		compute_grad(grad, logistic);
		
		//Additional memory space allocation
		val Xd = DenseMatrix.make(X.M, 1);
		
		//logisticD = logistic*(1-logistic)
		val logisticD = logistic.clone();
        logisticD.map((x:Double)=> {x*(1.0-x)});

		//delta = sqrt(sum(grad*grad))
		val sq = grad.norm(grad);
		var delta:Double = Math.sqrt(grad.norm(grad));
		
		//# number of iterations
		//iter = 0
		var iter:Long =0;
		
		//# starting point for CG
		//zeros_D = Rand(rows = D, cols = 1, min = 0.0, max = 0.0);
		val zeros_D = DenseMatrix.make(X.N, 1, 0.0);
		//# boolean for convergence check
		//converge = (delta < tol) | (iter > maxiter)
		var converge:Boolean = (delta < tol) | (iter > maxiter);
		//norm_r2 = sum(grad*grad)
		var norm_r2:Double = grad.norm(grad);
		//alpha = t(w) %*% w
		var alpha:Double = w.norm(w);
		// Add temp memory space
		val s = DenseMatrix.make(X.N,1);
		val r = DenseMatrix.make(X.N,1);
		val d = DenseMatrix.make(X.N,1);
		val Hd = DenseMatrix.make(X.N,1);
		val onew = DenseMatrix.make(X.M, 1);
		val wnew = DenseMatrix.make(X.N, 1);
		val logisticnew = DenseMatrix.make(X.M, 1);		
		Debug.flushln("Done initialization. Starting converging iteration");
		while(!converge) {
			
			// 			norm_grad = sqrt(sum(grad*grad))
			val norm_grad = Math.sqrt(grad.norm(grad));
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
				val sts = s.norm(s);
				// 				delta2 = delta*delta 
				val delta2 = delta*delta;
				// 				shouldBreak = false;
				var shouldBreak:Boolean = false;
				if (sts > delta2) {
					// 
					// 					std = t(s) %*% d
					val std = s.norm(d);
					// 					dtd = t(d) %*% d
					val dtd = d.norm(d);
					// 					rad = sqrt(std*std + dtd*(delta2 - sts))
					val rad = Math.sqrt(std*std+dtd*(delta2-sts));
					var tau:Double;
					if(std >= 0) {
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
				// 				
				// 				
			}  
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
			y.copyTo(logisticnew);
			logisticnew.scale(-1).cellMult(o).cellAdd(1.0).cellDivBy(1.0);
			
			// 			objnew = 0.5 * t(wnew) %*% wnew + C * sum(logisticnew)
			val objnew = 0.5 * wnew.norm(wnew) + C * logisticnew.sum();
			// 			
			// 			rho = (objnew - obj) / qk
			val rho = (objnew - obj)/qk;
			// 			snorm = sqrt(sum( s * s ))
			val snorm = Math.sqrt(s.norm(s));
			if (rho > eta0) {
				// 				
				// 				w = wnew
				wnew.copyTo(w);
				// 				o = onew
				onew.copyTo(o);
				// 				grad = w + C*t(X) %*% ((logisticnew - 1) * y )
				compute_grad(grad, logisticnew);
			} 
			// 			
			iter = iter + 1;
			converge = (norm_r2 < (tol * tol)) | (iter > maxiter);
			// 
			if (rho < eta0){
				delta = Math.min(Math.max(alpha, sigma1) * snorm, sigma2 * delta );
			} else {
				if (rho < eta1){
					delta = Math.max(sigma1 * delta, Math.min(alpha  * snorm, sigma2 * delta));
				} else { 
					if (rho < eta2) {
						delta = Math.max(sigma1 * delta, Math.min(alpha * snorm, sigma3 * delta));
					} else {
						delta = Math.max(delta, Math.min(alpha * snorm, sigma3 * delta));
					}
				}
			}
		}
		commUseTime += dup_w.getCommTime()+dst_y.getCommTime();
	}

	private def compute_XmultB(result:DenseMatrix(X.M, 1), opB:DenseMatrix(X.N, 1)):void {
		//o = X %*% w
		val stt:Long=Timer.milliTime();
		dup_w.copyFrom(opB);
		DistMultDupToDist.comp(X, dup_w, dst_y);
		
		val ctt:Long = Timer.milliTime();
		dst_y.copyTo(result); //gather single column matrix
		commUseTime += Timer.milliTime() - ctt;
		paraRunTime += Timer.milliTime() - stt;
	}
	
	private def compute_tXmultB(result:DenseMatrix(X.N,1), opB:DenseMatrix(X.M,1)):void {
		
		val stt = Timer.milliTime();
		dst_y.copyFrom(opB);//Scattering
		DistMultDistToDup.compTransMult(X, dst_y, dup_w, false);
		dup_w.local().copyTo(result);
		paraRunTime += Timer.milliTime() - stt;
	}
	
	private def compute_grad(grad:DenseMatrix(X.N, 1), logistic:DenseMatrix(X.M, 1)):void {
		//grad = w + C*t(X) %*% ((logistic - 1)*y)
		logistic.copyTo(tmp_y);
		tmp_y.cellSub(1.0).cellMult(y);
		compute_tXmultB(grad, tmp_y);
		grad.scale(C);
		grad.cellAdd(w);
	}
	
	private def compute_Hd(Hd:DenseMatrix(X.N, 1), 
							 logisticD:DenseMatrix(X.M, 1), 
							 d:DenseMatrix(X.N, 1)):void {
		// 				Hd = d + C*(t(X) %*% (logisticD*(X %*% d)))
		compute_XmultB(tmp_y, d);
		tmp_y.cellMult(logisticD);
		compute_tXmultB(Hd, tmp_y);
		Hd.scale(C).cellAdd(d);
	}
	
}
