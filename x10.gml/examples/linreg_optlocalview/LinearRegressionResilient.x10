/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2016.
 *  (C) Copyright Sara Salem Hamouda 2014-2016.
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

import x10.util.resilient.iterative.LocalViewResilientIterativeAppOpt;
import x10.util.resilient.iterative.LocalViewResilientExecutorOpt;
import x10.util.resilient.iterative.DistObjectSnapshot;

import x10.util.Team;
import x10.util.concurrent.AtomicInteger;
/**
 * Parallel linear regression based on GML distributed
 * dense/sparse matrix
 */
public class LinearRegressionResilient implements LocalViewResilientIterativeAppOpt {
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
        appTempDataPLH = PlaceLocalHandle.make[AppTempData](places, ()=>new AppTempData(0));
        val implicitBarrier = true;
        val createReadOnlyStore = true;
        new LocalViewResilientExecutorOpt(checkpointFreq, places, implicitBarrier, createReadOnlyStore).run(this, start);
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
        Vp.mult_local(V, d_p);  // no collectives
        d_q.mult_local(root, Vp, V);   //Team.allReduce
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
    
    public def checkpoint_local(store:DistObjectSnapshot, readOnlyDataStore:DistObjectSnapshot):void {
    	    val statusRail = new Rail[AtomicInteger](4, new AtomicInteger(0N));
    	    for (i in 0..3){
    	    	val curIter = i as Int;
    	        async {
	                try{
	                	switch(curIter){
	                	    case 0N: if (appTempDataPLH().iter == 0) V.makeSnapshot_local("V", readOnlyDataStore);
	                	    case 1N: d_p.makeSnapshot_local("d_p", store);
	                	    case 2N: d_q.makeSnapshot_local("d_q", store);
	                	    case 3N: d_r.makeSnapshot_local("d_r", store);
	                	}
	            	    atomic statusRail(curIter).set(1N);
	                }catch(ex:Exception){
	        	        ex.printStackTrace();
	        	        atomic statusRail(curIter).set(2N);
	                }
    	        }
    	    }
            d_w.makeSnapshot_local("d_w", store);
            appTempDataPLH().checkpointNorm = appTempDataPLH().norm_r2;
    	    when(isCompleteCheckpointRestore(statusRail));
    	    
    	    if (isFialedCheckpointRestore(statusRail))
    	    	throw new Exception(here + "  failed to checkpoint ..."); 
    }
    
    public def restore_local(store:DistObjectSnapshot, readOnlyDataStore:DistObjectSnapshot, lastCheckpointIter:Long):void {
    	val statusRail = new Rail[AtomicInteger](4, new AtomicInteger(0N));
	    for (i in 0..3){
	    	val curIter = i as Int;
	        async {
                try{
                	switch(curIter){
                	    case 0N: V.restoreSnapshot_local("V", readOnlyDataStore);
                	    case 1N: d_p.restoreSnapshot_local("d_p", store);
                	    case 2N: d_q.restoreSnapshot_local("d_q", store);
                	    case 3N: d_r.restoreSnapshot_local("d_r", store);
                	}
            	    atomic statusRail(curIter).set(1N);
                }catch(ex:Exception){
        	        ex.printStackTrace();
        	        atomic statusRail(curIter).set(2N);
                }
	        }
	    }
	    
	    d_w.restoreSnapshot_local("d_w", store);
	    appTempDataPLH().iter = lastCheckpointIter;
        appTempDataPLH().norm_r2 = appTempDataPLH().checkpointNorm;
        
	    when(isCompleteCheckpointRestore(statusRail));
	    
	    if (isFialedCheckpointRestore(statusRail))
	    	throw new Exception(here + "  failed to restore ..."); 
    }
    /**
     * Restore from the snapshot with new PlaceGroup
     */
    
    public def remake(newPg:PlaceGroup, newTeam:Team, newAddedPlaces:ArrayList[Place]):void {
        val oldPlaces = V.places();
        val newRowPs = newPg.size();
        val newColPs = 1;
        //remake all the distributed data structures
        finish {
        	if (nzd < MAX_SPARSE_DENSITY) {
        		async V.remakeSparse(newRowPs, newColPs, nzd, newPg, newAddedPlaces);
        	} else {
        		async V.remakeDense(newRowPs, newColPs, newPg, newAddedPlaces);
        	}
        	async d_p.remake(newPg, newTeam, newAddedPlaces);
        	async d_q.remake(newPg, newTeam, newAddedPlaces);
        	async d_r.remake(newPg, newTeam, newAddedPlaces);
        	async d_w.remake(newPg, newTeam, newAddedPlaces);
        	async Vp.remake(V.getAggRowBs(), newPg, newTeam, newAddedPlaces);
    
        	val checkpointNorm = appTempDataPLH().checkpointNorm;
        	for (sparePlace in newAddedPlaces){
        		Console.OUT.println("Adding place["+sparePlace+"] to appTempDataPLH ...");
        		PlaceLocalHandle.addPlace[AppTempData](appTempDataPLH, sparePlace, ()=>new AppTempData(checkpointNorm));
        	}	 
        }
        Console.OUT.println("Restore succeeded. Restarting from iteration["+appTempDataPLH().iter+"] norm["+appTempDataPLH().norm_r2+"] ...");
    }    
    
    
    private def isCompleteCheckpointRestore(statusRail:Rail[AtomicInteger]):Boolean{
    	for (x in statusRail){
    		if (x.get() == 0N)
    			return false;
    	}
    	return true;
    }
    
    private def isFialedCheckpointRestore(statusRail:Rail[AtomicInteger]):Boolean{
    	for (x in statusRail){
    		if (x.get() == 2N)
    			return true;
    	}
    	return false;
    }
    
    
    class AppTempData{
        public var norm_r2:ElemType;
        public var iter:Long;
        public var checkpointNorm:ElemType;
        
        def this(checkpointNorm:ElemType) {
        	this.checkpointNorm = checkpointNorm;
        }
        
        public var localCompTime:Long;
        public var globalCompTime:Long;
        
    }
}
