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
package logreg;

import x10.util.Timer;

import x10.matrix.Vector;
import x10.matrix.distblock.DistVector;
import x10.matrix.distblock.DupVector;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.util.Debug;
import x10.util.resilient.DistObjectSnapshot;
import x10.util.resilient.ResilientIterativeApp;
import x10.util.resilient.ResilientExecutor;
import x10.util.resilient.ResilientStoreForApp;

public class LogisticRegression implements ResilientIterativeApp {
    static val MAX_SPARSE_DENSITY = 0.1;
    val C = 2;
    val tol = 0.000001;
    val maxiter:Long;
    val maxinneriter:Long; 
    
    //X = Rand(rows = 1000, cols = 1000, min = 1, max = 10, pdf = "uniform");
    public val X:DistBlockMatrix;
    public val y:DistVector(X.M);
    //w = Rand(rows=D, cols=1, min=0.0, max=0.0);
    public val w:Vector(X.N);

    val dup_w:DupVector(X.N); 
    
    val tmp_y:DistVector(X.M);

    val o:DistVector(X.M);
    val grad:Vector(X.N);
    
    val eta0 = 0.0;
    val eta1 = 0.25;
    val eta2 = 0.75;
    val sigma1 = 0.25;
    val sigma2 = 0.5;
    val sigma3 = 4.0;
    val psi = 0.1;     

    var iter:Long =0;
    var converge:Boolean;
    var delta:Double;
    var lastCheckpointDelta:Double;
    var norm_r2:Double; 
    var alpha:Double;    
    var obj:Double; // value does not change after being initialized
    var logisticD:DistVector(X.M); // value does not change after being initialized
    
    // Temp memory space
    val s:Vector(X.N);
    val r:Vector(X.N);
    val d:Vector(X.N);
    val Hd:Vector(X.N);
    val onew:DistVector(X.M);
    val wnew:Vector(X.N);
    val logisticnew:DistVector(X.M);    
    
    public var paraRunTime:Long=0;
    public var commUseTime:Long=0;
    
    private val chkpntIterations:Long;
    private val nzd:Double;
    private var X_snapshot:DistObjectSnapshot;

    public def this(x_:DistBlockMatrix, y:DistVector, w:Vector, it:Long, nit:Long, sparseDensity:Double, chkpntIter:Long, places:PlaceGroup) {
        X=x_;
        this.y = y as DistVector(X.M);
        this.w = w as Vector(X.N);
        
        dup_w  = DupVector.make(X.N, places);

        val rowBs = X.getAggRowBs();
        tmp_y  = DistVector.make(X.M, rowBs, places);
        o      = DistVector.make(X.M, rowBs, places);
        grad   = Vector.make(X.N);
        // Add temp memory space
        s      = Vector.make(X.N);
        r      = Vector.make(X.N);
        d      = Vector.make(X.N);
        Hd     = Vector.make(X.N);
        onew   = DistVector.make(X.M, rowBs, places);
        wnew   = Vector.make(X.N);
        logisticnew = DistVector.make(X.M, rowBs, places);

        maxiter = it;
        maxinneriter =nit;
        chkpntIterations = chkpntIter;
        nzd = sparseDensity;
    }

    public static def make(mX:Long, nX:Long, nRowBs:Long, nColBs:Long, nzd:Double, it:Long, nit:Long, chkpntIter:Long, places:PlaceGroup){
        val X:DistBlockMatrix(mX, nX);
        if (nzd < MAX_SPARSE_DENSITY) {
            X = DistBlockMatrix.makeSparse(mX, nX, nRowBs, nColBs, places.size(), 1, nzd, places);
        } else {
            Console.OUT.println("using dense matrix as non-zero density = " + nzd);
            X = DistBlockMatrix.makeDense(mX, nX, nRowBs, nColBs, places.size(), 1, places);
        }
        val w = Vector.make(X.N);
        val y = DistVector.make(X.M, X.getAggRowBs(), places);

        X.initRandom(1, 10);
        y.initRandom(1, 10);
        w.initRandom();

        return new LogisticRegression(X, y, w, it, nit, nzd, chkpntIter, places);
    }

    public def run() {
        //o = X %*% w
        compute_XmultB(o, w);
        //logistic = 1.0/(1.0 + exp( -y * o))
        val logistic = DistVector.make(X.M, X.getAggRowBs(), X.places());
        logistic.map(y, o, (y_i:Double, o_i:Double)=> { 1.0 / (1.0 + Math.exp(-y_i * o_i)) });

        //obj = 0.5 * t(w) %*% w + C*sum(logistic)
        obj = 0.5 * w.norm() + C*logistic.sum();

        //grad = w + C*t(X) %*% ((logistic - 1)*y)        
        compute_grad(grad, logistic);

        //logisticD = logistic*(1-logistic)
        logisticD = logistic.clone();
        logisticD.map((x:Double)=> {x*(1.0-x)});

        //delta = sqrt(sum(grad*grad))
        delta = Math.sqrt(grad.norm());

        //# starting point for CG
        //# boolean for convergence check
        //converge = (delta < tol) | (iter > maxiter)
        converge = (delta < tol) | (iter > maxiter);
        //norm_r2 = sum(grad*grad)
        norm_r2 = grad.norm();
        //alpha = t(w) %*% w
        alpha = w.norm();
        Debug.flushln("Done initialization. Starting converging iteration");

        new ResilientExecutor(chkpntIterations).run(this);
        
        commUseTime += dup_w.getCommTime()+y.getCommTime();
    }

    public def step():void{
        //             norm_grad = sqrt(sum(grad*grad))
        val norm_grad = Math.sqrt(grad.norm());
        //             # SOLVE TRUST REGION SUB-PROBLEM
        //zeros_D = Rand(rows = D, cols = 1, min = 0.0, max = 0.0);
        //             s = zeros_D
        s.reset();
        //             r = -grad
        r.scale(-1.0, grad);
        //             d = r
        r.copyTo(d);
        //             inneriter = 0
        val inneriter:Long=0;
        //             innerconverge = ( sqrt(sum(r*r)) <= psi * norm_grad) 
        var innerconverge:Boolean;// = (Math.sqrt(r.norm(r)) <= psi * norm_grad);
        innerconverge = false;
        while (!innerconverge) {
            //  
            //                 norm_r2 = sum(r*r)
            norm_r2 = r.norm();
            //                 Hd = d + C*(t(X) %*% (logisticD*(X %*% d)))
            compute_Hd(Hd, logisticD, d);
            //                 alpha_deno = t(d) %*% Hd 
            val alpha_deno = d.norm(Hd);
            //                 alpha = norm_r2 / alpha_deno
            alpha = norm_r2 / alpha_deno;
            //                 s = s + castAsScalar(alpha) * d
            s.scaleAdd(alpha, d);
            //                 sts = t(s) %*% s
            val sts = s.norm();
            //                 delta2 = delta*delta 
            val delta2 = delta*delta;
            //                 shouldBreak = false;
            var shouldBreak:Boolean = false;
            if (sts > delta2) {
                //                     std = t(s) %*% d
                val std = s.norm(d);
                //                     dtd = t(d) %*% d
                val dtd = d.norm();
                //                     rad = sqrt(std*std + dtd*(delta2 - sts))
                val rad = Math.sqrt(std*std+dtd*(delta2-sts));
                var tau:Double;
                if(std >= 0) {
                    tau = (delta2 - sts)/(std + rad);
                } else {
                    tau = (rad - std)/dtd;
                }
                //                     s = s + castAsScalar(tau) * d
                s.scaleAdd(tau, d);
                //                     r = r - castAsScalar(tau) * Hd
                r.scaleAdd(-tau, Hd);
                //                     #break
                shouldBreak = true;
                innerconverge = true;
            } 
            //                 
            if (!shouldBreak) {
                //                     r = r - castAsScalar(alpha) * Hd
                r.scaleAdd(-alpha, Hd);
                //                     old_norm_r2 = norm_r2 
                val old_norm_r2 = norm_r2;
                //                     norm_r2 = sum(r*r)
                norm_r2 = r.norm();
                //                     beta = norm_r2/old_norm_r2
                val beta = norm_r2/old_norm_r2;
                //                     d = r + beta*d
                d.scale(beta).cellAdd(r);
                //                     innerconverge = (sqrt(norm_r2) <= psi * norm_grad) | (inneriter < maxinneriter)
                innerconverge = (Math.sqrt(norm_r2) <= psi * norm_grad) | (inneriter < maxinneriter);
            }
        }
        //             # END TRUST REGION SUB-PROBLEM
        //             # compute rho, update w, obtain delta
        //             qk = -0.5*(t(s) %*% (grad - r))
        val qk = -0.5 * s.norm(grad-r);
        //             wnew = w + s
        wnew.cellAdd(w, s);
        //             onew = X %*% wnew
        compute_XmultB(onew, wnew); 
        //             logisticnew = 1.0/(1.0 + exp(-y * o ))
        val stt = Timer.milliTime();
        logisticnew.map(y, o, (y_i:Double, o_i:Double)=> { 1.0 / (1.0 + Math.exp(-y_i * o_i)) });
        paraRunTime += Timer.milliTime() - stt;
        
        //             objnew = 0.5 * t(wnew) %*% wnew + C * sum(logisticnew)
        val objnew = 0.5 * wnew.norm() + C * logisticnew.sum();
        //             
        //             rho = (objnew - obj) / qk
        val rho = (objnew - obj)/qk;
        //             snorm = sqrt(sum( s * s ))
        val snorm = Math.sqrt(s.norm());
        if (rho > eta0){            
            //                 w = wnew
            wnew.copyTo(w);
            //                 o = onew
            onew.copyTo(o);
            //                 grad = w + C*t(X) %*% ((logisticnew - 1) * y )
            compute_grad(grad, logisticnew);
        } 
        iter = iter + 1;
        converge = (norm_r2 < (tol * tol)) | (iter > maxiter);
        if (rho < eta0){
            delta = Math.min(Math.max(alpha , sigma1) * snorm, sigma2 * delta );            
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

    private def compute_XmultB(result:DistVector(X.M), opB:Vector(X.N)):void {
        // o = X %*% w
        val stt = Timer.milliTime();
        dup_w.copyFrom(opB);
        result.mult(X, dup_w, false);
        paraRunTime += Timer.milliTime() - stt;
    }
    
    private def compute_grad(grad:Vector(X.N), logistic:DistVector(X.M)):void {
        // grad = w + C*t(X) %*% ((logistic - 1)*y)
        val stt = Timer.milliTime();
        logistic.map(y, (x:Double, v:Double)=> {(x - 1.0) * v});
        compute_tXmultB(grad, logistic);
        paraRunTime += Timer.milliTime() - stt;
        grad.scale(C).cellAdd(w);
    }
    
    private def compute_Hd(Hd:Vector(X.N), logisticD:DistVector(X.M), d:Vector(X.N)):void {
        // Hd = d + C*(t(X) %*% (logisticD*(X %*% d)))
        compute_XmultB(tmp_y, d);
        val stt = Timer.milliTime();        
        tmp_y.cellMult(logisticD);
        compute_tXmultB(Hd, tmp_y);
        paraRunTime += Timer.milliTime() - stt;
        Hd.scale(C).cellAdd(d);
    }
    
    private def compute_tXmultB(result:Vector(X.N), B:DistVector(X.M)):void {
        dup_w.mult(B, X, false);
        dup_w.local().copyTo(result);
    }

    public def isFinished():Boolean{
        return converge;
    }

    public def checkpoint(store:ResilientStoreForApp):void {        
        store.startNewSnapshot();
        finish {
            async {
        	    if (X_snapshot == null)
            	    X_snapshot = X.makeSnapshot();
        	    store.save(X, X_snapshot, true);
            }
            async store.save(y);
            async store.save(grad);
            async store.save(o);
            async store.save(w);
            async store.save(logisticD);
        }
        store.commit();
        lastCheckpointDelta = delta;
    }

    public def restore(newPlaces:PlaceGroup, store:ResilientStoreForApp, lastCheckpointIter:Long):void{        
        val newRowPs = newPlaces.size();
        val newColPs = 1;
        Console.OUT.println("Going to restore LogisticRegression app, newRowPs["+newRowPs+"], newColPs["+newColPs+"] ...");

        // redistribute all matrices / vectors to new PlaceGroup
        if (nzd < MAX_SPARSE_DENSITY) {
            X.remakeSparse(newRowPs, newColPs, nzd, newPlaces);
        } else {
            X.remakeDense(newRowPs, newColPs, newPlaces);
        }
        val rowBs = X.getAggRowBs();
        y.remake(rowBs, newPlaces);
        o.remake(rowBs, newPlaces);
        onew.remake(rowBs, newPlaces);

        tmp_y.remake(rowBs, newPlaces);
        logisticD.remake(rowBs, newPlaces);
        logisticnew.remake(rowBs, newPlaces);
        dup_w.remake(newPlaces);

        store.restore();
        iter = lastCheckpointIter;
        delta = lastCheckpointDelta;
        Console.OUT.println("Restore succeeded. Restarting from iteration["+iter+"] delta["+delta+"] ...");
    }
    
    public def getMaxIterations():Long{
        return maxiter;
    }
}
