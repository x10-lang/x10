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
import x10.matrix.ElemType;
import x10.matrix.Vector;
import x10.matrix.ElemType;
import x10.regionarray.Dist;

import x10.util.ArrayList;

import x10.matrix.distblock.DistVector;
import x10.matrix.distblock.DupVector;
import x10.matrix.distblock.DistBlockMatrix;

import x10.util.Timer;

import x10.matrix.util.Debug;

import x10.util.resilient.iterative.LocalViewResilientIterativeApp;
import x10.util.resilient.iterative.LocalViewResilientExecutor;
import x10.util.resilient.iterative.ApplicationSnapshotStore;
import x10.util.Team;

public class LogisticRegression implements LocalViewResilientIterativeApp {
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
    public val w:DupVector(X.N);
    
    val tmp_y:DistVector(X.M);

    val o:DistVector(X.M);
    val grad:DupVector(X.N);
    
    val eta0 = 0.0f;
    val eta1 = 0.25f;
    val eta2 = 0.75f;
    val sigma1 = 0.25f;
    val sigma2 = 0.5f;
    val sigma3 = 4.0f;
    val psi = 0.1f;     

    var lastCheckpointDelta:ElemType;
    var lastCheckpointObj:ElemType;
    var norm_r2:ElemType; 
    var alpha:ElemType;    
    
    var logisticD:DistVector(X.M); // value does not change after being initialized
    
    // Temp memory space
    val s:DupVector(X.N);
    val r:DupVector(X.N);
    val d:DupVector(X.N);
    val Hd:DupVector(X.N);
    val onew:DistVector(X.M);
    val wnew:DupVector(X.N);
    val logisticnew:DistVector(X.M);    
    
    public var parCompT:Long=0;
    public var seqCompT:Long=0;
    public var commT:Long;
    
    private val chkpntIterations:Long;
    private val nzd:Float;
    private val root:Place;
    
    private var appTempDataPLH:PlaceLocalHandle[AppTempData];
    var team:Team;
    
    public def this(x_:DistBlockMatrix, y:DistVector, w:DupVector, logisticD:DistVector, it:Long, nit:Long, sparseDensity:Float, chkpntIter:Long, places:PlaceGroup, team:Team) {
        X=x_;
        this.y = y as DistVector(X.M);
        this.w = w as DupVector(X.N);
        this.logisticD = logisticD as DistVector(X.M);
        
        val rowBs = X.getAggRowBs();
        tmp_y  = DistVector.make(X.M, rowBs, places, team);
        o      = DistVector.make(X.M, rowBs, places, team);
        grad   = DupVector.make(X.N, places,team);
        // Add temp memory space
        s      = DupVector.make(X.N, places, team);
        r      = DupVector.make(X.N, places, team);
        d      = DupVector.make(X.N, places, team);
        Hd     = DupVector.make(X.N, places, team);
        onew   = DistVector.make(X.M, rowBs, places, team);
        wnew   = DupVector.make(X.N, places, team);
        logisticnew = DistVector.make(X.M, rowBs, places, team);
        
        maxIterations = it;
        maxinneriter =nit;
        chkpntIterations = chkpntIter;
        nzd = sparseDensity;
        root = here;
        this.team = team;
    }

    public static def make(mX:Long, nX:Long, nRowBs:Long, nColBs:Long, nzd:Float, it:Long, nit:Long, chkpntIter:Long, places:PlaceGroup, team:Team){
        val X:DistBlockMatrix(mX, nX);
        if (nzd < MAX_SPARSE_DENSITY) {
            X = DistBlockMatrix.makeSparse(mX, nX, nRowBs, nColBs, places.size(), 1, nzd, places);
        } else {
            Console.OUT.println("using dense matrix as non-zero density = " + nzd);
            X = DistBlockMatrix.makeDense(mX, nX, nRowBs, nColBs, places.size(), 1, places);
        }
        val w = DupVector.make(X.N, places, team);
        val aggRowBs = X.getAggRowBs();
        val y = DistVector.make(X.M, aggRowBs, places, team);
        val logisticD = DistVector.make(X.M, aggRowBs, X.places(), team);
        
        val root = here;
        finish for (place in places) at(place) async {
            X.initRandom_local(1, 10);
            y.initRandom_local(1, 10);
            w.initRandom_local(root);
        }
        
        return new LogisticRegression(X, y, w, logisticD, it, nit, nzd, chkpntIter, places, team);
    }
    
    public def isFinished_local() {
        return appTempDataPLH().iter >= maxIterations;
    }
    
    
    public def run(startTime:Long) {
        val start = (startTime != 0)?startTime:Timer.milliTime();  
        assert (X.isDistVertical()) : "dist block matrix must have vertical distribution";
        val places = X.places();
        appTempDataPLH = PlaceLocalHandle.make[AppTempData](places, ()=>new AppTempData());
        
        new LocalViewResilientExecutor(chkpntIterations, places).run(this, start);
    }
    
    public def step_local():void {
       //initialization steps
        if (appTempDataPLH().iter == 0) {
            //o = X %*% w
            compute_XmultB_local(o, w);
        
            //logisticD = 1.0/(1.0 + exp( -y * o))
            logisticD.map_local(y, o, (y_i:ElemType, o_i:ElemType)=> (1.0 / (1.0 + Math.exp(-y_i * o_i))) as ElemType);
        
            //obj = 0.5 * t(w) %*% w + C*sum(logisticD)
            appTempDataPLH().obj = (0.5 * w.dot_local(w) + C*logisticD.sum_local()) as ElemType;

            //grad = w + C*t(X) %*% ((logisticD - 1)*y)        
            compute_grad_local(grad, logisticD);
            
            
        
            //logisticD = logisticD*(1-logisticD)
            logisticD.map_local((x:ElemType)=> (x*(1.0-x)) as ElemType);

            seqCompT -= Timer.milliTime();

            //delta = sqrt(sum(grad*grad))
            appTempDataPLH().delta = grad.norm_local();

            //# starting point for CG
            //# boolean for convergence check
            //converge = (delta < tol) | (iter > maxiter)
            appTempDataPLH().converge = (appTempDataPLH().delta < tol) | (appTempDataPLH().iter > maxIterations);
            //norm_r2 = sum(grad*grad)
            norm_r2 = grad.dot_local(grad);
            //alpha = t(w) %*% w
            alpha = w.dot_local(w);
            seqCompT += Timer.milliTime();

            
        }
    
        seqCompT -= Timer.milliTime();
        //             norm_grad = sqrt(sum(grad*grad))
        val norm_grad = grad.norm_local();
        //             # SOLVE TRUST REGION SUB-PROBLEM
        //zeros_D = Rand(rows = D, cols = 1, min = 0.0, max = 0.0);
        //             s = zeros_D
        s.reset_local();
        //             r = -grad
        
        r.scale_local(-1.0 as ElemType, grad);
        //             d = r
        r.copyTo_local(d);

        //             inneriter = 0
        val inneriter:Long=0;
        //             innerconverge = ( sqrt(sum(r*r)) <= psi * norm_grad) 
        var innerconverge:Boolean;// = (r.norm() <= psi * norm_grad);
        innerconverge = false;
        while (!innerconverge) {
            //  
            //                 norm_r2 = sum(r*r)
            
            norm_r2 = r.dot_local(r);
            
            //                 Hd = d + C*(t(X) %*% (logisticD*(X %*% d)))
            seqCompT += Timer.milliTime(); // next step is parallel
            compute_Hd_local(Hd, logisticD, d);
            seqCompT -= Timer.milliTime();

            val stt = Timer.milliTime();
            //                 alpha_deno = t(d) %*% Hd 
            val alpha_deno = d.dot_local(Hd);
            //                 alpha = norm_r2 / alpha_deno
            alpha = norm_r2 / alpha_deno;
            //                 s = s + castAsScalar(alpha) * d
            s.scaleAdd_local(alpha, d);
            //                 sts = t(s) %*% s
            val sts = s.dot(s);
            //                 delta2 = delta*delta 
            val delta2 = appTempDataPLH().delta*appTempDataPLH().delta;
            //                 shouldBreak = false;
            var shouldBreak:Boolean = false;
            
            if (sts > delta2) {
                //                     std = t(s) %*% d
                val std = s.dot_local(d);
                //                     dtd = t(d) %*% d
                val dtd = d.dot_local(d);
                //                     rad = sqrt(std*std + dtd*(delta2 - sts))
                val rad = Math.sqrt(std*std+dtd*(delta2-sts));
                var tau:ElemType;
                if(std >= 0) {
                    tau = (delta2 - sts)/(std + rad);
                } else {
                    tau = (rad - std)/dtd;
                }
                //                     s = s + castAsScalar(tau) * d
                s.scaleAdd_local(tau, d);
                //                     r = r - castAsScalar(tau) * Hd
                r.scaleAdd_local(-tau, Hd);
                //                     #break
                shouldBreak = true;
                innerconverge = true;
            } 
            //                 
            if (!shouldBreak) {
                //                     r = r - castAsScalar(alpha) * Hd
                r.scaleAdd_local(-alpha, Hd);
                //                     old_norm_r2 = norm_r2 
                val old_norm_r2 = norm_r2;
                //                     norm_r2 = sum(r*r)
                norm_r2 = r.dot_local(r);
                //                     beta = norm_r2/old_norm_r2
                val beta = norm_r2/old_norm_r2;
                //                     d = r + beta*d
                d.scale_local(beta).cellAdd_local(r);
                //                     innerconverge = (sqrt(norm_r2) <= psi * norm_grad) | (inneriter < maxinneriter)
                innerconverge = (Math.sqrt(norm_r2) <= psi * norm_grad) | (inneriter < maxinneriter);
            }
        }
        //             # END TRUST REGION SUB-PROBLEM
        //             # compute rho, update w, obtain delta
        //             qk = -0.5*(t(s) %*% (grad - r))
        
        val qk = (-0.5 * s.dot_local(grad.local() - r.local() )) as ElemType;
        
        //             wnew = w + s
        wnew.cellAdd_local(w, s);
        seqCompT += Timer.milliTime();
        //             onew = X %*% wnew
        compute_XmultB_local(onew, wnew);
        
        //             logisticnew = 1.0/(1.0 + exp(-y * o ))
        logisticnew.map_local(y, o, (y_i:ElemType, o_i:ElemType)=> ( (1.0 / (1.0 + Math.exp(-y_i * o_i))) as ElemType ));

        
        //             objnew = 0.5 * t(wnew) %*% wnew + C * sum(logisticnew)
        val objnew = (0.5 * wnew.dot_local(wnew) + C * logisticnew.sum_local()) as ElemType;

        //             
        //             rho = (objnew - obj) / qk
        val rho = (objnew - appTempDataPLH().obj)/qk;
        //             snorm = sqrt(sum( s * s ))
        val snorm = s.norm_local();
        if (rho > eta0){
            //                 w = wnew
            wnew.copyTo_local(w);
            //                 o = onew
            onew.copyTo_local(o);
            //                 grad = w + C*t(X) %*% ((logisticnew - 1) * y )
            compute_grad_local(grad, logisticnew);
        } 
        appTempDataPLH().iter = appTempDataPLH().iter + 1;
        appTempDataPLH().converge = (norm_r2 < (tol * tol)) | (appTempDataPLH().iter > maxIterations);
        if (rho < eta0){
            appTempDataPLH().delta = Math.min(Math.max(alpha , sigma1) * snorm, sigma2 * appTempDataPLH().delta ) as ElemType;            
        } else {
            if (rho < eta1){
                appTempDataPLH().delta = Math.max(sigma1 * appTempDataPLH().delta, Math.min(alpha  * snorm, sigma2 * appTempDataPLH().delta)) as ElemType;                
            } else { 
                if (rho < eta2) {
                    appTempDataPLH().delta = Math.max(sigma1 * appTempDataPLH().delta, Math.min(alpha * snorm, sigma3 * appTempDataPLH().delta)) as ElemType;                    
                } else {
                    appTempDataPLH().delta = Math.max(appTempDataPLH().delta, Math.min(alpha * snorm, sigma3 * appTempDataPLH().delta)) as ElemType;                    
                }
            }
        }
    }
    
    private def compute_XmultB_local(result:DistVector(X.M), opB:DupVector(X.N)):void {
        // o = X %*% w
        result.mult_local(X, opB);
    }
    
    private def compute_grad_local(grad:DupVector(X.N), logistic:DistVector(X.M)):void {
        // grad = w + C*t(X) %*% ((logistic - 1)*y)
        logistic.map_local(logistic, y, (x:ElemType, v:ElemType)=> {(x - 1.0f) * v});
        compute_tXmultB_local(grad, logistic);
        val stt = Timer.milliTime();
        grad.scale_local(C).cellAdd_local(w);
        seqCompT += Timer.milliTime() - stt;
    }

    
    private def compute_Hd_local(Hd:DupVector(X.N), logisticD:DistVector(X.M), d:DupVector(X.N)):void {
        // Hd = d + C*(t(X) %*% (logisticD*(X %*% d)))
        compute_XmultB_local(tmp_y, d);
        tmp_y.cellMult_local(logisticD);
        compute_tXmultB_local(Hd, tmp_y);
        val stt = Timer.milliTime();
        Hd.scale_local(C).cellAdd_local(d);
        seqCompT += Timer.milliTime() - stt;
    }
    
    private def compute_tXmultB_local(result:DupVector(X.N), B:DistVector(X.M)):void {
        result.mult_local(root, B, X);
    }
    
    
    public def checkpoint(store:ApplicationSnapshotStore):void {
        store.startNewSnapshot();
        store.saveReadOnly(X);
        store.save(y);
        store.save(grad);
        store.save(o);
        store.save(w);
        store.save(logisticD);
        store.commit();
        lastCheckpointDelta = appTempDataPLH().delta;
        lastCheckpointObj = appTempDataPLH().obj;
    }
    
    public def restore(newPlaces:PlaceGroup, store:ApplicationSnapshotStore, lastCheckpointIter:Long, newAddedPlaces:ArrayList[Place]):void{
        val oldPlaces = X.places();
        val newTeam = new Team(newPlaces);
        
        val newRowPs = newPlaces.size();
        val newColPs = 1;
        Console.OUT.println("Going to restore LogisticRegression app, newRowPs["+newRowPs+"], newColPs["+newColPs+"] ...");
        
        // redistribute all matrices / vectors to new PlaceGroup
        if (nzd < MAX_SPARSE_DENSITY) {
            X.remakeSparse(newRowPs, newColPs, nzd, newPlaces, newAddedPlaces);
        } else {
            X.remakeDense(newRowPs, newColPs, newPlaces, newAddedPlaces);
        }
        val rowBs = X.getAggRowBs();
        y.remake(rowBs, newPlaces, newTeam, newAddedPlaces);
        o.remake(rowBs, newPlaces, newTeam, newAddedPlaces);
        onew.remake(rowBs, newPlaces, newTeam, newAddedPlaces);
        
        grad.remake(newPlaces, newTeam, newAddedPlaces);
        w.remake(newPlaces, newTeam, newAddedPlaces);
        s.remake(newPlaces, newTeam, newAddedPlaces);
        r.remake(newPlaces, newTeam, newAddedPlaces);
        d.remake(newPlaces, newTeam, newAddedPlaces);
        Hd.remake(newPlaces, newTeam, newAddedPlaces);
        wnew.remake(newPlaces, newTeam, newAddedPlaces);
        
        tmp_y.remake(rowBs, newPlaces, newTeam, newAddedPlaces);
        logisticD.remake(rowBs, newPlaces, newTeam, newAddedPlaces);
        logisticnew.remake(rowBs, newPlaces, newTeam, newAddedPlaces);
        
        
        store.restore();
        
        PlaceLocalHandle.destroy(oldPlaces, appTempDataPLH, (Place)=>true);
        appTempDataPLH = PlaceLocalHandle.make[AppTempData](newPlaces, ()=>new AppTempData());
        //adjust the iteration number and the norm value
        finish ateach(Dist.makeUnique(newPlaces)) {
            appTempDataPLH().iter = lastCheckpointIter;
            appTempDataPLH().delta = lastCheckpointDelta;
            appTempDataPLH().obj = lastCheckpointObj;
        }
        Console.OUT.println("Restore succeeded. Restarting from iteration["+appTempDataPLH().iter+"] delta["+appTempDataPLH().delta+"] ...");
    }
    
    class AppTempData{
        public var delta:ElemType;
        public var iter:Long;
        public var obj:ElemType; // value does not change after being initialized
        public var converge:Boolean;
    }
}
