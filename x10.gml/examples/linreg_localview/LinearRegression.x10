/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2014.
 *  (C) Copyright Sara Salem Hamouda 2014.
 */
import x10.matrix.Vector;
import x10.matrix.ElemType;
import x10.regionarray.Dist;
import x10.util.Timer;
import x10.util.ArrayList;
import x10.matrix.distblock.DistBlockMatrix;
import x10.matrix.distblock.DupVector;
import x10.matrix.distblock.DistVector;

import x10.matrix.util.Debug;
import x10.util.resilient.iterative.PlaceGroupBuilder;

import x10.util.resilient.iterative.LocalViewResilientIterativeApp;
import x10.util.resilient.iterative.LocalViewResilientExecutor;
import x10.util.resilient.iterative.ApplicationSnapshotStore;

import x10.util.Team;

/**
 * Parallel linear regression based on GML distributed
 * dense/sparse matrix
 */
public class LinearRegression implements LocalViewResilientIterativeApp {
    public static val MAX_SPARSE_DENSITY = 0.1f;
    static val lambda = 1e-6 as Float; // regularization parameter
    
    /** Matrix of training examples */
    public val V:DistBlockMatrix;
    /** Vector of training regression targets */
    public val y:DistVector(V.M);
    /** Learned model weight vector, used for future predictions */    
    public val d_w:DupVector(V.N);
    
    public val maxIterations:Long;
    
    val d_p:DupVector(V.N);
    val Vp:DistVector(V.M);
    
    val d_r:DupVector(V.N);
    val d_q:DupVector(V.N);
    
    private val checkpointFreq:Long;
    var lastCheckpointNorm:ElemType;
    
    //----Profiling-----
    public var parCompT:Long=0;
    public var seqCompT:Long=0;
    public var commT:Long;
    private val nzd:Float;
    private val root:Place;
    
    private var appTempDataPLH:PlaceLocalHandle[AppTempData];
    var team:Team;
    
    public def this(v:DistBlockMatrix, y:DistVector(v.M), it:Long, chkpntIter:Long, sparseDensity:Float, places:PlaceGroup, team:Team) {
        maxIterations = it;
        this.V = v;
        this.y = y;
        
        Vp = DistVector.make(V.M, V.getAggRowBs(), places, team);
        
        d_r  = DupVector.make(V.N, places, team);
        d_p= DupVector.make(V.N, places, team);
        
        d_q= DupVector.make(V.N, places, team);
        
        d_w = DupVector.make(V.N, places, team);      
        
        this.checkpointFreq = chkpntIter;
        
        nzd = sparseDensity;
        root = here;
        this.team = team;
    }
    
    public def isFinished_local() {
        return appTempDataPLH().iter >= maxIterations;
    }
    
    //startTime parameter added to account for the time taken by RunLinReg to initialize the input data
    public def run(startTime:Long) {
        val start = (startTime != 0)?startTime:Timer.milliTime();  
        assert (V.isDistVertical()) : "dist block matrix must have vertical distribution";
        val places = V.places();
        appTempDataPLH = PlaceLocalHandle.make[AppTempData](places, ()=>new AppTempData());
        new LocalViewResilientExecutor(checkpointFreq, places).run(this, start);
        return d_w.local();
    }
    
    public def getResult() = d_w.local();
    
    public def step_local() {
        // Parallel computing
        if (appTempDataPLH().iter == 0) {
            team.barrier();
            
appTempDataPLH().globalCompTime -= Timer.milliTime();
            d_r.mult_local(root, y, V);        
appTempDataPLH().globalCompTime += Timer.milliTime();
            
appTempDataPLH().localCompTime -= Timer.milliTime();
            val r = d_r.local(); 
        
            // 5: p=-r
            r.copyTo(d_p.local());
            // 4: r=-(t(V) %*% y)
            r.scale(-1.0 as ElemType);
            // 6: norm_r2=sum(r*r)
            appTempDataPLH().norm_r2 = r.dot(r);
            
appTempDataPLH().localCompTime += Timer.milliTime();
        }

        // 10: q=((t(V) %*% (V %*% p)) )

        //////Global view step:  d_q.mult(Vp.mult(V, d_p), V);
appTempDataPLH().globalCompTime -= Timer.milliTime();
        Vp.mult_local(V, d_p);
        d_q.mult_local(root, Vp, V);
appTempDataPLH().globalCompTime += Timer.milliTime();
        
        // Replicated Computation at each place
appTempDataPLH().localCompTime -= Timer.milliTime();            
        var ct:Long = Timer.milliTime();
        //q = q + lambda*p
        val p = d_p.local();
        val q = d_q.local();
        val r = d_r.local(); 
        q.scaleAdd(lambda, p);
            
        // 11: alpha= norm_r2/(t(p)%*%q);
        val alpha = appTempDataPLH().norm_r2 / p.dotProd(q);
             
        // 12: w=w+alpha*p;
        d_w.local().scaleAdd(alpha, p);
            
        // 13: old norm r2=norm r2;
        val old_norm_r2 = appTempDataPLH().norm_r2;
            
        // 14: r=r+alpha*q;
        r.scaleAdd(alpha, q);

        // 15: norm_r2=sum(r*r);
        appTempDataPLH().norm_r2 = r.dot(r);

        // 16: beta=norm_r2/old_norm_r2;
        val beta = appTempDataPLH().norm_r2/old_norm_r2;
            
        // 17: p=-r+beta*p;
        p.scale(beta).cellSub(r);                
       
        appTempDataPLH().iter++;        
appTempDataPLH().localCompTime += Timer.milliTime();
    }
    
    
    public def checkpoint(resilientStore:ApplicationSnapshotStore) {    
        resilientStore.startNewSnapshot();
        resilientStore.saveReadOnly(V);
        resilientStore.save(d_p);
        resilientStore.save(d_q);
        resilientStore.save(d_r);
        resilientStore.save(d_w);
        resilientStore.commit();
        lastCheckpointNorm = appTempDataPLH().norm_r2;
    }
    
    /**
     * Restore from the snapshot with new PlaceGroup
     */
    public def restore(newPg:PlaceGroup, store:ApplicationSnapshotStore, lastCheckpointIter:Long, newAddedPlaces:ArrayList[Place]) {
        val oldPlaces = V.places();
        val newTeam = new Team(newPg);
        val newRowPs = newPg.size();
        val newColPs = 1;
        //remake all the distributed data structures
        if (nzd < MAX_SPARSE_DENSITY) {
            V.remakeSparse(newRowPs, newColPs, nzd, newPg, newAddedPlaces);
        } else {
            V.remakeDense(newRowPs, newColPs, newPg, newAddedPlaces);
        }
        d_p.remake(newPg, newTeam, newAddedPlaces);
        d_q.remake(newPg, newTeam, newAddedPlaces);
        d_r.remake(newPg, newTeam, newAddedPlaces);
        d_w.remake(newPg, newTeam, newAddedPlaces);
        Vp.remake(V.getAggRowBs(), newPg, newTeam, newAddedPlaces);
        
        store.restore();
        
        //TODO: make a snapshottable class for the app data
        PlaceLocalHandle.destroy(oldPlaces, appTempDataPLH, (Place)=>true);
        appTempDataPLH = PlaceLocalHandle.make[AppTempData](newPg, ()=>new AppTempData());
        //adjust the iteration number and the norm value
        finish ateach(Dist.makeUnique(newPg)) {
            appTempDataPLH().iter = lastCheckpointIter;
            appTempDataPLH().norm_r2 = lastCheckpointNorm;
        }
        Console.OUT.println("Restore succeeded. Restarting from iteration["+appTempDataPLH().iter+"] norm["+appTempDataPLH().norm_r2+"] ...");
    }    
    
    class AppTempData{
        public var norm_r2:ElemType;
        public var iter:Long;
        
        public var localCompTime:Long;
        public var globalCompTime:Long;
        
    }
}
