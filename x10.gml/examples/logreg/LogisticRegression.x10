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
import x10.matrix.Vector;
import x10.matrix.ElemType;

import x10.matrix.distblock.DistVector;
import x10.matrix.distblock.DupVector;
import x10.matrix.distblock.DistBlockMatrix;

import x10.util.Timer;

import x10.matrix.util.Debug;

import x10.util.resilient.ResilientIterativeApp;
import x10.util.resilient.ResilientExecutor;
import x10.util.resilient.ResilientStoreForApp;

public class LogisticRegression implements ResilientIterativeApp {
    static val MAX_SPARSE_DENSITY = 0.1f;
    val C = 2;
    val tol = 0.000001f;
    val maxIterations:Long;
    val maxinneriter:Long; 
    
    /** Matrix of training examples */
    public val X:DistBlockMatrix;
    /** Vector of training regression targets */
    public val y:DistVector(X.M);
    /** Learned model weight vector, used for future predictions */
    public val w:Vector(X.N);

    val dup_w:DupVector(X.N); 
    
    val tmp_y:DistVector(X.M);

    val o:DistVector(X.M);
    val grad:Vector(X.N);
    
    val eta0 = 0.0f;
    val eta1 = 0.25f;
    val eta2 = 0.75f;
    val sigma1 = 0.25f;
    val sigma2 = 0.5f;
    val sigma3 = 4.0f;
    val psi = 0.1f;     

    var iter:Long =0;
    var converge:Boolean;
    var delta:ElemType;
    var lastCheckpointDelta:ElemType;
    var norm_r2:ElemType; 
    var alpha:ElemType;    
    var obj:ElemType; // value does not change after being initialized
    var logisticD:DistVector(X.M); // value does not change after being initialized
    
    // Temp memory space
    val s:Vector(X.N);
    val r:Vector(X.N);
    val d:Vector(X.N);
    val Hd:Vector(X.N);
    val onew:DistVector(X.M);
    val wnew:Vector(X.N);
    val logisticnew:DistVector(X.M);    
    
    public var parCompT:Long=0;
    public var seqCompT:Long=0;
    public var commT:Long;
    
    private val chkpntIterations:Long;
    private val nzd:Float;
    private var places:PlaceGroup;

    public def this(x_:DistBlockMatrix, y:DistVector, w:Vector, it:Long, nit:Long, sparseDensity:Float, chkpntIter:Long, places:PlaceGroup) {
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
        
        maxIterations = it;
        maxinneriter =nit;
        chkpntIterations = chkpntIter;
        nzd = sparseDensity;
        this.places = places;
    }

    public static def make(mX:Long, nX:Long, nRowBs:Long, nColBs:Long, nzd:Float, it:Long, nit:Long, chkpntIter:Long, places:PlaceGroup){
        val X:DistBlockMatrix(mX, nX);
        if (nzd < MAX_SPARSE_DENSITY) {
            X = DistBlockMatrix.makeSparse(mX, nX, nRowBs, nColBs, places.size(), 1, nzd, places);
        } else {
            Console.OUT.println("using dense matrix as non-zero density = " + nzd);
            X = DistBlockMatrix.makeDense(mX, nX, nRowBs, nColBs, places.size(), 1, places);
        }
        val w = Vector.make(X.N);
        val y = DistVector.make(X.M, X.getAggRowBs(), places);
        
        //X = Rand(rows = 1000, cols = 1000, min = 1, max = 10, pdf = "uniform");
        X.initRandom(1, 10);
        y.initRandom(1, 10);
        //w = Rand(rows=D, cols=1, min=0.0, max=0.0);
        w.initRandom();
        
        return new LogisticRegression(X, y, w, it, nit, nzd, chkpntIter, places);
    }
    
    public def run() {
        //o = X %*% w
        compute_XmultB(o, w);
        //logistic = 1.0/(1.0 + exp( -y * o))
        val logistic = DistVector.make(X.M, X.getAggRowBs(), X.places());
        logistic.map(y, o, (y_i:ElemType, o_i:ElemType)=> (1.0 / (1.0 + Math.exp(-y_i * o_i))) as ElemType);
        
        //obj = 0.5 * t(w) %*% w + C*sum(logistic)
        obj = (0.5 * w.dot(w) + C*logistic.sum()) as ElemType;

        //grad = w + C*t(X) %*% ((logistic - 1)*y)        
        compute_grad(grad, logistic);
        
        //logisticD = logistic*(1-logistic)
        logisticD = logistic.clone();
        logisticD.map((x:ElemType)=> (x*(1.0-x)) as ElemType);

        seqCompT -= Timer.milliTime();

        //delta = sqrt(sum(grad*grad))
        delta = grad.norm();

        //# starting point for CG
        //# boolean for convergence check
        //converge = (delta < tol) | (iter > maxiter)
        converge = (delta < tol) | (iter > maxIterations);
        //norm_r2 = sum(grad*grad)
        norm_r2 = grad.dot(grad);
        //alpha = t(w) %*% w
        alpha = w.dot(w);
        seqCompT += Timer.milliTime();

        Debug.flushln("Done initialization. Starting converging iteration");
        
        new ResilientExecutor(chkpntIterations, places).run(this);
        
        parCompT += logistic.getCalcTime() + logisticnew.getCalcTime()
                 + o.getCalcTime() + onew.getCalcTime()
                 + tmp_y.getCalcTime() + dup_w.getCalcTime();
        commT = o.getCommTime() + onew.getCommTime()
                 + tmp_y.getCommTime() + dup_w.getCommTime();
    }
    
    public def step():void {
        seqCompT -= Timer.milliTime();
        //             norm_grad = sqrt(sum(grad*grad))
        val norm_grad = grad.norm();
        //             # SOLVE TRUST REGION SUB-PROBLEM
        //zeros_D = Rand(rows = D, cols = 1, min = 0.0, max = 0.0);
        //             s = zeros_D
        s.reset();
        //             r = -grad
        r.scale(-1.0 as ElemType, grad);
        //             d = r
        r.copyTo(d);

        //             inneriter = 0
        val inneriter:Long=0;
        //             innerconverge = ( sqrt(sum(r*r)) <= psi * norm_grad) 
        var innerconverge:Boolean;// = (r.norm() <= psi * norm_grad);
        innerconverge = false;
        while (!innerconverge) {
            //  
            //                 norm_r2 = sum(r*r)
            norm_r2 = r.dot(r);
            //                 Hd = d + C*(t(X) %*% (logisticD*(X %*% d)))
            seqCompT += Timer.milliTime(); // next step is parallel
            compute_Hd(Hd, logisticD, d);
            seqCompT -= Timer.milliTime();

            val stt = Timer.milliTime();
            //                 alpha_deno = t(d) %*% Hd 
            val alpha_deno = d.dot(Hd);
            //                 alpha = norm_r2 / alpha_deno
            alpha = norm_r2 / alpha_deno;
            //                 s = s + castAsScalar(alpha) * d
            s.scaleAdd(alpha, d);
            //                 sts = t(s) %*% s
            val sts = s.dot(s);
            //                 delta2 = delta*delta 
            val delta2 = delta*delta;
            //                 shouldBreak = false;
            var shouldBreak:Boolean = false;
            if (sts > delta2) {
                //                     std = t(s) %*% d
                val std = s.dot(d);
                //                     dtd = t(d) %*% d
                val dtd = d.dot(d);
                //                     rad = sqrt(std*std + dtd*(delta2 - sts))
                val rad = Math.sqrt(std*std+dtd*(delta2-sts));
                var tau:ElemType;
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
                norm_r2 = r.dot(r);
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
        val qk = (-0.5 * s.dot(grad-r)) as ElemType;

        //             wnew = w + s
        wnew.cellAdd(w, s);
        seqCompT += Timer.milliTime();
        //             onew = X %*% wnew
        compute_XmultB(onew, wnew); 

        //             logisticnew = 1.0/(1.0 + exp(-y * o ))
        logisticnew.map(y, o, (y_i:ElemType, o_i:ElemType)=> ( (1.0 / (1.0 + Math.exp(-y_i * o_i))) as ElemType ));

        
        //             objnew = 0.5 * t(wnew) %*% wnew + C * sum(logisticnew)
        val objnew = (0.5 * wnew.dot(wnew) + C * logisticnew.sum()) as ElemType;

        //             
        //             rho = (objnew - obj) / qk
        val rho = (objnew - obj)/qk;
        //             snorm = sqrt(sum( s * s ))
        val snorm = s.norm();
        if (rho > eta0){            
            //                 w = wnew
            wnew.copyTo(w);
            //                 o = onew
            onew.copyTo(o);
            //                 grad = w + C*t(X) %*% ((logisticnew - 1) * y )
            compute_grad(grad, logisticnew);
        } 
        iter = iter + 1;
        converge = (norm_r2 < (tol * tol)) | (iter > maxIterations);
        if (rho < eta0){
            delta = Math.min(Math.max(alpha , sigma1) * snorm, sigma2 * delta ) as ElemType;            
        } else {
            if (rho < eta1){
                delta = Math.max(sigma1 * delta, Math.min(alpha  * snorm, sigma2 * delta)) as ElemType;                
            } else { 
                if (rho < eta2) {
                    delta = Math.max(sigma1 * delta, Math.min(alpha * snorm, sigma3 * delta)) as ElemType;                    
                } else {
                    delta = Math.max(delta, Math.min(alpha * snorm, sigma3 * delta)) as ElemType;                    
                }
            }
        }
    }
    
    private def compute_XmultB(result:DistVector(X.M), opB:Vector(X.N)):void {
        // o = X %*% w
        dup_w.copyFrom(opB);
        result.mult(X, dup_w, false);
    }
    
    private def compute_grad(grad:Vector(X.N), logistic:DistVector(X.M)):void {
        // grad = w + C*t(X) %*% ((logistic - 1)*y)
        logistic.map(logistic, y, (x:ElemType, v:ElemType)=> {(x - 1.0f) * v});
        compute_tXmultB(grad, logistic);
        val stt = Timer.milliTime();
        grad.scale(C).cellAdd(w);
        seqCompT += Timer.milliTime() - stt;
    }
    
    private def compute_Hd(Hd:Vector(X.N), logisticD:DistVector(X.M), d:Vector(X.N)):void {
        // Hd = d + C*(t(X) %*% (logisticD*(X %*% d)))
        compute_XmultB(tmp_y, d);
        tmp_y.cellMult(logisticD);
        compute_tXmultB(Hd, tmp_y);
        val stt = Timer.milliTime();
        Hd.scale(C).cellAdd(d);
        seqCompT += Timer.milliTime() - stt;
    }
    
    private def compute_tXmultB(result:Vector(X.N), B:DistVector(X.M)):void {
        dup_w.mult(B, X, false);
        dup_w.local().copyTo(result);
    }
    
    public def isFinished() {
        return iter >= maxIterations;
    }
    
    public def checkpoint(store:ResilientStoreForApp):void {        
        store.startNewSnapshot();
        store.saveReadOnly(X);
        store.save(y);
        store.save(grad);
        store.save(o);
        store.save(w);
        store.save(logisticD);
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
}
