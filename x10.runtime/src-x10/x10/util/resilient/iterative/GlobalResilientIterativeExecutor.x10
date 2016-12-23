/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 *  (C) Copyright Sara Salem Hamouda 2014-2016.
 */

package x10.util.resilient.iterative;

import x10.util.Timer;

import x10.util.Random;
import x10.regionarray.Dist;
import x10.util.ArrayList;
import x10.util.HashSet;
import x10.util.HashMap;
import x10.util.Team;
import x10.util.GrowableRail;
import x10.util.RailUtils;
import x10.xrx.Runtime;
import x10.util.resilient.PlaceManager;
import x10.util.resilient.store.Store;
import x10.util.resilient.localstore.Cloneable;

public class GlobalResilientIterativeExecutor (home:Place) {
    private static val VERBOSE = (System.getenv("EXECUTOR_DEBUG") != null
                               && System.getenv("EXECUTOR_DEBUG").equals("1"));

    private val manager:GlobalRef[PlaceManager]{self.home == this.home};
    private val resilientMap:Store[Cloneable];
    private val appStore:ApplicationSnapshotStore;
    private var lastCkptIter:Long = -1;
    private val itersPerCheckpoint:Long;
    private val isResilient:Boolean;
     
    // configuration parameters for killing places at different times
    private var simplePlaceHammer:SimplePlaceHammer;

    //timing variables
    private transient var restoreTimes:ArrayList[Double] = new ArrayList[Double]();
    private transient var stepTimes:ArrayList[Double] = new ArrayList[Double]();
    private transient var ckptTimes:ArrayList[Double] = new ArrayList[Double]();
    private transient var remakeTimes:ArrayList[Double] = new ArrayList[Double]();
    private transient var appRemakeTimes:ArrayList[Double] = new ArrayList[Double]();
    private transient var resilientMapRecoveryTimes:ArrayList[Double] = new ArrayList[Double]();
    private transient var failureDetectionTimes:ArrayList[Double] = new ArrayList[Double]();
    private transient var applicationInitializationTime:Long = 0;
    private transient var startRunTime:Long = 0;
    private transient var killPlaceTime:Long = -1;
    
    public def this(itersPerCheckpoint:Long, sparePlaces:Long, supportShrinking:Boolean) {
        property(here);

        isResilient = itersPerCheckpoint > 0 && x10.xrx.Runtime.RESILIENT_MODE > 0;
    	this.itersPerCheckpoint = itersPerCheckpoint;
        val mgr = new PlaceManager(sparePlaces, supportShrinking);
        this.manager = GlobalRef[PlaceManager](mgr);
        if (isResilient) {
            this.resilientMap = Store.make[Cloneable]("_map_", mgr.activePlaces());
            appStore = new ApplicationSnapshotStore();
            simplePlaceHammer = new SimplePlaceHammer();
            if (VERBOSE) {
                simplePlaceHammer.printPlan();
            }
        } else {
            this.resilientMap = null;
            this.appStore = null;
            this.simplePlaceHammer = null;
        }
    }

    public def run(app:GlobalResilientIterativeApp){here == home} {
        run(app, Timer.milliTime());
    }

    public def setHammer(h:SimplePlaceHammer){here == home} {
        simplePlaceHammer = h;
    }

    public def activePlaces(){here == home} = manager().activePlaces();

    //the startRunTime parameter is added to allow the executor to consider 
    //any initialization time done by the application before starting the executor  
    public def run(app:GlobalResilientIterativeApp, startRunTime:Long){here == home} {
        if (simplePlaceHammer != null) {
            simplePlaceHammer.scheduleTimers();
        }
        this.startRunTime = startRunTime;
        Console.OUT.println("GlobalResilientIterativeExecutor: Application start time ["+startRunTime+"] ...");
        
        applicationInitializationTime = Timer.milliTime() - startRunTime;
        var remakeRequired:Boolean = false;
        var globalIter:Long = 0;
        
        while(!app.isFinished()){
            try {
                /*** Remake and Restore ***/
                var restored:Boolean = false;
                if (remakeRequired) {
                    remake(app);
                    restore();
                    globalIter = lastCkptIter;
                    restored = true;
                    remakeRequired = false;
                    Console.OUT.println("[Hammer Log] Remake/Restore completed at ["+Timer.milliTime()+"] ...");
                }                
                
                /*** Checkpoint ***/                
                if (isResilient && !restored) {
                    checkpoint(app, globalIter);                    
                    lastCkptIter = globalIter;
                }
                else {
                	restored = false;
                }

                var localIter:Long = 0;
                while ( !app.isFinished() && 
                        (!isResilient || (isResilient && localIter < itersPerCheckpoint)) ) {
                    if ( isResilient) {
                    	val victim = simplePlaceHammer.getVictimPlaceId(globalIter);
                    	if (victim != -1)
                    		executorKillThere(Place(victim),"step()");
                    }

                    val stepStartTime = Timer.milliTime();                        
                    app.step();
                    stepTimes.add(Timer.milliTime()-stepStartTime);
                    
                    globalIter ++;
                    localIter++;
                    
                }//while !isFinished
            }
            catch (iterEx:Exception) {
                iterEx.printStackTrace();
                //exception from finish_ateach  or from restore
                if (isResilient && containsDPE(iterEx)){
                    remakeRequired = true;
                    Console.OUT.println("[Hammer Log] Time DPE discovered is ["+Timer.milliTime()+"] ...");
                    if (killPlaceTime != -1){
                        failureDetectionTimes.add(Timer.milliTime() - killPlaceTime);
                        killPlaceTime =  -1;
                    }
                } else {
                    throw iterEx; // not a DPE; rethrow
                }
            }
        }
        
        calculateTimingStatistics();

    }
    
    private def remake(app:GlobalResilientIterativeApp){here == home} {
        if (lastCkptIter == -1) {
            Console.OUT.println("process failure occurred but no valid checkpoint exists!");
            System.killHere();
        }
        
        if (VERBOSE) Console.OUT.println("Restoring to iter " + lastCkptIter);
        var restoreRequired:Boolean = false;
        val startRemake = Timer.milliTime();                    
        
        val startResilientMapRecovery = Timer.milliTime();
        val changes = manager().rebuildActivePlaces();
        resilientMap.updateForChangedPlaces(changes);
        resilientMapRecoveryTimes.add(Timer.milliTime() - startResilientMapRecovery);
        
        if (VERBOSE){
            var str:String = "";
            for (p in changes.newActivePlaces)
                str += p.id + ",";
            Console.OUT.println("Restore places are: " + str);
        } 

        val startAppRemake = Timer.milliTime();
        app.remake(changes, lastCkptIter);
        appRemakeTimes.add(Timer.milliTime() - startAppRemake);                        
        
        restoreRequired = true;
        remakeTimes.add(Timer.milliTime() - startRemake);
        Console.OUT.println("All remake steps completed successfully ...");
        return restoreRequired;
    }
    
    private def checkpoint(app:GlobalResilientIterativeApp, globalIter:Long){here == home} {
    	if (VERBOSE) Console.OUT.println("checkpointing at iter " + globalIter);
        val startCheckpoint = Timer.milliTime();
        app.checkpoint(appStore);
        
        val newVersion = appStore.nextCheckpointVersion();
        val first = globalIter == 0;
        finish for (p in manager().activePlaces()) at (p) async {
            val start = System.nanoTime();
            val ckptMap = appStore.getCheckpointData_local(first);
            if (ckptMap != null) {
            	val verMap = new HashMap[String,Cloneable]();
                val iter = ckptMap.keySet().iterator();
                while (iter.hasNext()) {                    
                    val appKey = iter.next();
                    val key = appKey +":v" + newVersion;
                    val value = ckptMap.getOrThrow(appKey);
                    verMap.put(key, value);
                    if (VERBOSE) Console.OUT.println(here + "checkpointing key["+appKey+"]  version["+newVersion+"] succeeded ...");
                }
                resilientMap.setAll(verMap);
            }
            val end = System.nanoTime();
            if (VERBOSE) Console.OUT.println(here+" my checkpoint time "+((end-start) / 1e9)+" seconds");
        }
        appStore.commitCheckpoint(newVersion);
        ckptTimes.add(Timer.milliTime() - startCheckpoint);
    }
    
    private def restore(){here == home} {
    	val startRestoreData = Timer.milliTime();
    	val keyVersions = appStore.getRestoreKeyVersions();
        finish for (p in manager().activePlaces()) at (p) async {
	        val restoreDataMap = new HashMap[String,Cloneable]();
	        val iter = keyVersions.keySet().iterator();
	        while (iter.hasNext()) {
	            val appKey = iter.next();
	            val keyVersion = keyVersions.getOrThrow(appKey);
	            val key = appKey + ":v" + keyVersion;
	            val value = resilientMap.get(key);
	            restoreDataMap.put(appKey, value);
	            if (VERBOSE) Console.OUT.println(here + "restoring key["+appKey+"]  version["+keyVersion+"] succeeded ...");
	        }
	        appStore.restore_local(restoreDataMap);
    	}
    	restoreTimes.add(Timer.milliTime() - startRestoreData);
    }
    
    private def calculateTimingStatistics(){here == home} {
        Console.OUT.println("=========Detailed Statistics============");
        val runTime = (Timer.milliTime() - startRunTime);
        
        Console.OUT.println("Steps:" + listToString(stepTimes));
        
        if (isResilient){
            Console.OUT.println("Checkpoint:" + listToString(ckptTimes));
            Console.OUT.println("RestoreData:" + listToString(restoreTimes));            
            Console.OUT.println("FailureDetection:" + listToString(failureDetectionTimes));            
            Console.OUT.println("ResilientMapRecovery:" + listToString(resilientMapRecoveryTimes));
            Console.OUT.println("AppRemake:" + listToString(appRemakeTimes));            
            Console.OUT.println("AllRemake:" + listToString(remakeTimes));
        }
        Console.OUT.println("=========Totals by averaging Min/Max statistics============");
        Console.OUT.println(">>>>>>>>>>>>>>Initialization:"      + applicationInitializationTime);
        Console.OUT.println();
        Console.OUT.println("   ---AverageSingleStep:" + listAverage(stepTimes));
        Console.OUT.println(">>>>>>>>>>>>>>TotalSteps:"+ listSum(stepTimes) );
        Console.OUT.println();
        if (isResilient){
            Console.OUT.println("Checkpoint-all:" + listToString(ckptTimes));
            Console.OUT.println("   ---AverageCheckpoint:" + listAverage(ckptTimes) );
            Console.OUT.println(">>>>>>>>>>>>>>TotalCheckpointingTime:" + listSum(ckptTimes)  );
      
            Console.OUT.println();
            Console.OUT.println("FailureDetection-all:"        + listToString(failureDetectionTimes) );
            Console.OUT.println("   ---AverageFailureDetection:"   + listAverage(failureDetectionTimes) );
                        
            Console.OUT.println("ResilientMapRecovery-all:"      + listToString(resilientMapRecoveryTimes) );
            Console.OUT.println("   ---AverageResilientMapRecovery:" + listAverage(resilientMapRecoveryTimes) );
            Console.OUT.println("AppRemake-all:"      + listToString(appRemakeTimes) );
            Console.OUT.println("   ---AverageAppRemake:" + listAverage(appRemakeTimes) );            
            Console.OUT.println("TotalRemake-all:"                   + listToString(remakeTimes) );
            Console.OUT.println("   ---AverageTotalRemake:"              + listAverage(remakeTimes) );
            
            Console.OUT.println("RestoreData-all:"      + listToString(restoreTimes));
            Console.OUT.println("   ---AverageRestoreData:"    + listAverage(restoreTimes));
            Console.OUT.println(">>>>>>>>>>>>>>TotalRecovery:" + (listSum(failureDetectionTimes) + listSum(remakeTimes) + listSum(restoreTimes) ));
        }
        Console.OUT.println("=============================");
        Console.OUT.println("Actual RunTime:" + runTime);
        var calcTotal:Double = applicationInitializationTime + listSum(stepTimes);
        
        if (isResilient){
            calcTotal += listSum(ckptTimes) + 
                (listSum(failureDetectionTimes) + listSum(remakeTimes) + listSum(restoreTimes) );
        }
        Console.OUT.println("Calculated RunTime based on Averages:" + calcTotal 
            + "   ---Difference:" + (runTime-calcTotal));
        
        Console.OUT.println("=========Counts============");
        Console.OUT.println("StepCount:"+stepTimes.size());
        if (isResilient){
            Console.OUT.println("CheckpointCount:"+(ckptTimes==null?0:ckptTimes.size()));
            Console.OUT.println("RestoreCount:"+(restoreTimes==null?0:restoreTimes.size()));
            Console.OUT.println("RemakeCount:"+remakeTimes.size());
            Console.OUT.println("FailureDetectionCount:"+failureDetectionTimes.size());
        
            if (VERBOSE){
                var str:String = "";
                for (p in manager().activePlaces())
                    str += p.id + ",";
                Console.OUT.println("List of final survived places are: " + str);            
            }
            Console.OUT.println("=============================");
        }
    }
    
    private def processIterationException(ex:Exception){here == home} {
        if (ex instanceof DeadPlaceException) {
            ex.printStackTrace();
            if (!isResilient) {
                throw ex;
            }
        }
        else if (ex instanceof MultipleExceptions) {
            val mulExp = ex as MultipleExceptions;
            if (isResilient) {                
                val filtered = mulExp.filterExceptionsOfType[DeadPlaceException]();
                if (filtered != null) throw filtered;
                val deadPlaceExceptions = mulExp.getExceptionsOfType[DeadPlaceException]();
                for (dpe in deadPlaceExceptions) {
                    dpe.printStackTrace();
                }
            } else {
                throw mulExp;
            }
        }
        else
            throw ex;
    }
    
    private def containsDPE(ex:Exception):Boolean{
        if (ex instanceof DeadPlaceException)
            return true;
        if (ex instanceof MultipleExceptions) {
            val mulExp = ex as MultipleExceptions;
            val deadPlaceExceptions = mulExp.getExceptionsOfType[DeadPlaceException]();
            if (deadPlaceExceptions == null)
                return false;
            else
                return true;
        }
        
        return false;
    }
    
    public def listToString[T](r:ArrayList[T]):String {
        if (r == null)
            return "";
        var str:String = "";
        for (x in r)
            str += x + ",";
        return str;
    }
    
    public def listSum(r:ArrayList[Double]):Double {
        if (r == null)
            return 0.0;
        return RailUtils.reduce(r.toRail(), (x:Double, y:Double) => x+y, 0.0);
    }
    
    public def listAverage(r:ArrayList[Double]):Double {
        if (r == null)
            return 0.0;
        val railAvg = listSum(r) / r.size();        
        return  Math.round(railAvg);
    }    
    
    private def executorKillThere(victim:Place, op:String) {
    	killPlaceTime = Timer.milliTime();
    	Console.OUT.println("[Hammer Log] Killing ["+victim+"] ...");
        try {
            System.killThere(victim);
        } catch (e:DeadPlaceException) {
            Console.OUT.println("[Hammer Log] ["+victim+"] was already dead ...");
        }
    }
    
}