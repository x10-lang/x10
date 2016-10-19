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
import x10.util.resilient.localstore.*;
/*
 * TODO:
 * -> maximum retry for restore failures
 * -> support more than 1 place failure.  when a palce dies, store.rebackup()
 * -> no need to notify place death for collectives
 * */
public class SPMDResilientIterativeExecutor {
    private val resilientMap:ResilientStore;
    private var placeTempData:PlaceLocalHandle[PlaceTempData];
    private var places:PlaceGroup;
    private var team:Team;
    private val itersPerCheckpoint:Long;
    private var isResilient:Boolean = false;
    // if step() are implicitly synchronized, no need for a step barrier inside the executor
    private val implicitStepSynchronization:Boolean; 
    private val VERBOSE = (System.getenv("EXECUTOR_DEBUG") != null 
                        && System.getenv("EXECUTOR_DEBUG").equals("1"));
    
    //parameters for killing places at different times
    private val simplePlaceHammer:SimplePlaceHammer;
    private val HAMMER_STEPS = System.getenv("KILL_STEPS");
    private val HAMMER_PLACES = System.getenv("KILL_PLACES");
    
    private transient var ckptTimes:ArrayList[Double] = new ArrayList[Double]();
    private transient var remakeTimes:ArrayList[Double] = new ArrayList[Double]();
    private transient var appRemakeTimes:ArrayList[Double] = new ArrayList[Double]();
    private transient var reconstructTeamTimes:ArrayList[Double] = new ArrayList[Double]();
    private transient var resilientMapRecoveryTimes:ArrayList[Double] = new ArrayList[Double]();
    private transient var failureDetectionTimes:ArrayList[Double] = new ArrayList[Double]();
    private transient var applicationInitializationTime:Long = 0;
    private transient var startRunTime:Long = 0;
    
    private var lastCkptVersion:Long = -1;
    private var lastCkptIter:Long = -1;
    
    public def this(itersPerCheckpoint:Long, resilientMap:ResilientStore, implicitStepSynchronization:Boolean) {
        this.itersPerCheckpoint = itersPerCheckpoint;
        this.implicitStepSynchronization = implicitStepSynchronization;
        if (itersPerCheckpoint > 0 && x10.xrx.Runtime.RESILIENT_MODE > 0 && resilientMap != null) {
            isResilient = true;            
            this.resilientMap = resilientMap;
            this.simplePlaceHammer = new SimplePlaceHammer(HAMMER_STEPS, HAMMER_PLACES);
            places = resilientMap.getActivePlaces();
            if (VERBOSE){
                Console.OUT.println("HAMMER_STEPS="+HAMMER_STEPS);
                Console.OUT.println("HAMMER_PLACES="+HAMMER_PLACES);
            }
        }
        else {        	            
            this.resilientMap = null;
            this.simplePlaceHammer = null;
            places = Place.places();
        }
    }

    public def run(app:SPMDResilientIterativeApp) {
        run(app, Timer.milliTime());
    }
    
    //the startRunTime parameter is added to allow the executor to consider 
    //any initlization time done by the application before starting the executor  
    public def run(app:SPMDResilientIterativeApp, startRunTime:Long) {
        this.startRunTime = startRunTime;
        Console.OUT.println("SPMDResilientIterativeExecutor: Application start time ["+startRunTime+"] ...");
        val root = here;
        team = new Team(places);
        placeTempData = PlaceLocalHandle.make[PlaceTempData](places, ()=>new PlaceTempData());
        var tmpGlobalIter:Long = 0;        
        applicationInitializationTime = Timer.milliTime() - startRunTime;
        var remakeRequired:Boolean = false;
        
        do{
            try {
                /*** Remake ***/
                var tmpRestoreFlag:Boolean = false;
                if (remakeRequired) {
                    tmpRestoreFlag = remake(app);
                    if (tmpRestoreFlag) {
                    	tmpGlobalIter = lastCkptIter;
                    }
                    remakeRequired = false;
                }
                else {
                	tmpGlobalIter = placeTempData().globalIter;
                }
                
                /*** Checkpoint (save new version) ***/                
                if (isResilient && !tmpRestoreFlag) {
                    checkpoint(app);
                }
                
                val restoreRequired = tmpRestoreFlag;
                val ckptVersion = lastCkptVersion;
                val globalIter = tmpGlobalIter;
                
                Console.OUT.println("SPMDResilientIterativeExecutor: remakeRequired["+remakeRequired+"] restoreRequired["+restoreRequired+"] ...");           
                finish ateach(Dist.makeUnique(places)) {
                    placeTempData().globalIter = globalIter;
                    
                    /*** Restore ***/
                    if (restoreRequired){
                        restore(app, globalIter);
                    }
                    else {
                    	//increment the last version of the keys
                    	val iter = placeTempData().lastCkptKeys.iterator(); 
                    	while (iter.hasNext()) {
                    		val key = iter.next();
                    		placeTempData().ckptKeyVersion.put(key, ckptVersion);
                    	}
                    }
                    
                    var localIter:Long = 0;
                    
                    while ( !app.isFinished_local() && 
                            (!isResilient || (isResilient && localIter < itersPerCheckpoint)) ) {
                        var stepStartTime:Long = -1; // (-1) is used to differenciate between checkpoint exceptions and step exceptions
                            
                        if ( isResilient && simplePlaceHammer.sayGoodBye(placeTempData().globalIter) ) {
                            executorKillHere("step()");
                        }

                        stepStartTime = Timer.milliTime();
                        if (!implicitStepSynchronization){
                            //to sync places & also to detect DPE
                            team.barrier();
                        }
                        
                        app.step_local();
                        
                        placeTempData().stat.stepTimes.add(Timer.milliTime()-stepStartTime);
                        
                        placeTempData().globalIter ++;
                        localIter++;
                        
                    }//while !isFinished
                }//finish ateach
            }
            catch (iterEx:Exception) {
                iterEx.printStackTrace();
                //exception from finish_ateach  or from restore
                if (isResilient && containsDPE(iterEx)){
                    remakeRequired = true;
                    Console.OUT.println("[Hammer Log] Time DPE discovered is ["+Timer.milliTime()+"] ...");
                    if (placeTempData().place0KillPlaceTime != -1){
                        failureDetectionTimes.add(Timer.milliTime() - placeTempData().place0KillPlaceTime);
                        placeTempData().place0KillPlaceTime =  -1;
                    }
                }
            }
        }while(remakeRequired || !app.isFinished_local());
        
        calculateTimingStatistics();

    }
    
    private def remake(app:SPMDResilientIterativeApp) {
        if (lastCkptIter == -1) {
            throw new UnsupportedOperationException("process failure occurred but no valid checkpoint exists!");
        }
        
        if (VERBOSE) Console.OUT.println("Restoring to iter " + lastCkptIter);
        var restoreRequired:Boolean = false;
        val startRemake = Timer.milliTime();                    
        
        val startResilientMapRecovery = Timer.milliTime(); 
        val addedPlacesMap = resilientMap.recoverDeadPlaces();
        resilientMapRecoveryTimes.add(Timer.milliTime() - startResilientMapRecovery);
        
        val newPG = resilientMap.getActivePlaces();
        if (VERBOSE){
            var str:String = "";
            for (p in newPG)
                str += p.id + ",";
            Console.OUT.println("Restore places are: " + str);
        } 
        val startTeamCreate = Timer.milliTime(); 
        team = new Team(newPG);
        reconstructTeamTimes.add( Timer.milliTime() - startTeamCreate);
        
        val addedPlaces = new ArrayList[Place]();
        val iter = addedPlacesMap.keySet().iterator();
        while (iter.hasNext()) {
            val realId = iter.next();
            val virtualId = addedPlacesMap.getOrThrow(realId);
            val victimStat =  placeTempData().place0VictimsStats.getOrElse(virtualId, placeTempData().stat);
            addedPlaces.add(Place(realId));            
            val p0GlobalIter = placeTempData().globalIter;
            val p0AllCkptKeys = placeTempData().ckptKeyVersion;
            val p0LastCkptKeys = placeTempData().lastCkptKeys;
            PlaceLocalHandle.addPlace[PlaceTempData](placeTempData, Place(realId), ()=>new PlaceTempData(victimStat, p0GlobalIter, p0LastCkptKeys, p0AllCkptKeys));
        }

        val startAppRemake = Timer.milliTime();
        app.remake(newPG, team, addedPlaces);
        appRemakeTimes.add(Timer.milliTime() - startAppRemake);                        
        
        places = newPG;
        
        restoreRequired = true;
        remakeTimes.add(Timer.milliTime() - startRemake) ;                        
        Console.OUT.println("SPMDResilientIterativeExecutor: All remake steps completed successfully lastCkptVersion=["+lastCkptVersion+"]...");
        return restoreRequired;
    }
    
    private def checkpoint(app:SPMDResilientIterativeApp) {
        val startCheckpoint = Timer.milliTime();
        //take new checkpoint only if restore was not done in this iteration
        if (VERBOSE) Console.OUT.println("checkpointing at iter " + placeTempData().globalIter);
        val newVersion = (lastCkptVersion+1)%2;
        finish ateach(Dist.makeUnique(places)) {
            placeTempData().lastCkptKeys.clear();
            val trans = resilientMap.startLocalTransaction();
            val ckptMap = app.getCheckpointData_local();
            if (ckptMap != null) {
                val iter = ckptMap.keySet().iterator();
                while (iter.hasNext()) {                    
                    val appKey = iter.next();
                    val key = appKey +":v" + newVersion;
                    val value = ckptMap.getOrThrow(appKey);
                    trans.put(key, value);
                    placeTempData().lastCkptKeys.add(appKey); 
                    //if (VERBOSE) Console.OUT.println(here + "checkpointing key["+appKey+"]  version["+newVersion+"] succeeded ...");
                }                
            }
            trans.prepareAndCommit();
        }
        lastCkptVersion = newVersion;
        lastCkptIter = placeTempData().globalIter;
        ckptTimes.add(Timer.milliTime() - startCheckpoint);
    }
    
    private def restore(app:SPMDResilientIterativeApp, lastCkptIter:Long) {
    	val startRestoreData = Timer.milliTime();
        val trans = resilientMap.startLocalTransaction();
        val restoreDataMap = new HashMap[String,Cloneable]();
        val iter = placeTempData().ckptKeyVersion.keySet().iterator();
        while (iter.hasNext()) {
            val appKey = iter.next();
            val keyVersion = placeTempData().ckptKeyVersion.getOrThrow(appKey);
            val key = appKey + ":v" + keyVersion;
            val value = trans.get(key);
            restoreDataMap.put(appKey, value);
            //if (VERBOSE) Console.OUT.println(here + "restoring key["+appKey+"]  version["+keyVersion+"] succeeded ...");
        }
        app.restore_local(restoreDataMap, lastCkptIter);
        trans.prepareAndCommit();
        
        placeTempData().stat.restoreTimes.add(Timer.milliTime() - startRestoreData);
    }
    
    private def calculateTimingStatistics(){
        val runTime = (Timer.milliTime() - startRunTime);
        Console.OUT.println("Application completed, calculating runtime statistics ...");
        finish for (place in places) at(place) async {
            ////// step times ////////
            val stpCount = placeTempData().stat.stepTimes.size();
            //Console.OUT.println(here + " stpCount=" + stpCount);
            placeTempData().stat.placeMaxStep = new Rail[Long](stpCount);
            placeTempData().stat.placeMinStep = new Rail[Long](stpCount);
            val dst2max = placeTempData().stat.placeMaxStep;
            val dst2min = placeTempData().stat.placeMinStep;
            team.allreduce(placeTempData().stat.stepTimes.toRail(), 0, dst2max, 0, stpCount, Team.MAX);
            team.allreduce(placeTempData().stat.stepTimes.toRail(), 0, dst2min, 0, stpCount, Team.MIN);

            if (x10.xrx.Runtime.RESILIENT_MODE > 0n){                
                ////// restore times ////////
                val restCount = placeTempData().stat.restoreTimes.size();
                if (restCount > 0) {
                    placeTempData().stat.placeMaxRestore = new Rail[Long](restCount);
                    placeTempData().stat.placeMinRestore = new Rail[Long](restCount);
                    val dst3max = placeTempData().stat.placeMaxRestore;
                    val dst3min = placeTempData().stat.placeMinRestore;
                    team.allreduce(placeTempData().stat.restoreTimes.toRail(), 0, dst3max, 0, restCount, Team.MAX);
                    team.allreduce(placeTempData().stat.restoreTimes.toRail(), 0, dst3min, 0, restCount, Team.MIN);
                }
            }
        }
        
        val averageSteps = avergaMaxMinRails(placeTempData().stat.placeMinStep,  placeTempData().stat.placeMaxStep);
        
        var averageRestore:Rail[Double] = null;
        if (isResilient){            
            if (placeTempData().stat.restoreTimes.size() > 0)
                averageRestore             = avergaMaxMinRails(placeTempData().stat.placeMinRestore,             placeTempData().stat.placeMaxRestore);
        }
        
        Console.OUT.println("=========Detailed Statistics============");
        Console.OUT.println("Steps-place0:" + railToString(placeTempData().stat.stepTimes.toRail()));
        
        Console.OUT.println("Steps-avg:" + railToString(averageSteps));
        Console.OUT.println("Steps-min:" + railToString(placeTempData().stat.placeMinStep));
        Console.OUT.println("Steps-max:" + railToString(placeTempData().stat.placeMaxStep));
        
        if (isResilient){
            Console.OUT.println("Checkpoint:" + railToString(ckptTimes.toRail()));            
            
            Console.OUT.println("RestoreData-avg:" + railToString(averageRestore));
            Console.OUT.println("RestoreData-min:" + railToString(placeTempData().stat.placeMinRestore));
            Console.OUT.println("RestoreData-max:" + railToString(placeTempData().stat.placeMaxRestore));
            
            Console.OUT.println("FailureDetection-place0:" + railToString(failureDetectionTimes.toRail()));
            
            Console.OUT.println("ResilientMapRecovery-place0:" + railToString(resilientMapRecoveryTimes.toRail()));
            Console.OUT.println("AppRemake-place0:" + railToString(appRemakeTimes.toRail()));
            Console.OUT.println("TeamReconstruction-place0:" + railToString(reconstructTeamTimes.toRail()));
            Console.OUT.println("AllRemake-place0:" + railToString(remakeTimes.toRail()));
        }
        Console.OUT.println("=========Totals by averaging Min/Max statistics============");
        Console.OUT.println(">>>>>>>>>>>>>>Initialization:"      + applicationInitializationTime);
        Console.OUT.println();
        Console.OUT.println("   ---AverageSingleStep:" + railAverage(averageSteps));
        Console.OUT.println(">>>>>>>>>>>>>>TotalSteps:"+ railSum(averageSteps) );
        Console.OUT.println();
        if (isResilient){
            Console.OUT.println("Checkpoint-all:" + railToString(ckptTimes.toRail()));
            Console.OUT.println("   ---AverageCheckpoint:" + railAverage(ckptTimes.toRail()) );
            Console.OUT.println(">>>>>>>>>>>>>>TotalCheckpointingTime:" + railSum(ckptTimes.toRail())  );
      
            Console.OUT.println();
            Console.OUT.println("FailureDetection-all:"        + railToString(failureDetectionTimes.toRail()) );
            Console.OUT.println("   ---AverageFailureDetection:"   + railAverage(failureDetectionTimes.toRail()) );
                        
            Console.OUT.println("ResilientMapRecovery-all:"      + railToString(resilientMapRecoveryTimes.toRail()) );
            Console.OUT.println("   ---AverageResilientMapRecovery:" + railAverage(resilientMapRecoveryTimes.toRail()) );
            Console.OUT.println("AppRemake-all:"      + railToString(appRemakeTimes.toRail()) );
            Console.OUT.println("   ---AverageAppRemake:" + railAverage(appRemakeTimes.toRail()) );
            Console.OUT.println("TeamReconstruction-all:"      + railToString(reconstructTeamTimes.toRail()) );
            Console.OUT.println("   ---AverageTeamReconstruction:" + railAverage(reconstructTeamTimes.toRail()) );
            Console.OUT.println("TotalRemake-all:"                   + railToString(remakeTimes.toRail()) );
            Console.OUT.println("   ---AverageTotalRemake:"              + railAverage(remakeTimes.toRail()) );
            
            Console.OUT.println("RestoreData-all:"      + railToString(averageRestore));
            Console.OUT.println("   ---AverageRestoreData:"    + railAverage(averageRestore));
            Console.OUT.println(">>>>>>>>>>>>>>TotalRecovery:" + (railSum(failureDetectionTimes.toRail()) + railSum(remakeTimes.toRail()) + railSum(averageRestore) ));
        }
        Console.OUT.println("=============================");
        Console.OUT.println("Actual RunTime:" + runTime);
        var calcTotal:Double = applicationInitializationTime + railSum(averageSteps);
        
        if (isResilient){
            calcTotal += railSum(ckptTimes.toRail()) + 
                (railSum(failureDetectionTimes.toRail()) + railSum(remakeTimes.toRail()) + railSum(averageRestore) );
        }
        Console.OUT.println("Calculated RunTime based on Averages:" + calcTotal 
            + "   ---Difference:" + (runTime-calcTotal));
        
        Console.OUT.println("=========Counts============");
        Console.OUT.println("StepCount:"+averageSteps.size);
        if (isResilient){
            Console.OUT.println("CheckpointCount:"+(ckptTimes==null?0:ckptTimes.size()));
            Console.OUT.println("RestoreCount:"+(averageRestore==null?0:averageRestore.size));
            Console.OUT.println("RemakeCount:"+remakeTimes.size());
            Console.OUT.println("FailureDetectionCount:"+failureDetectionTimes.size());
        
            if (VERBOSE){
                var str:String = "";
                for (p in places)
                    str += p.id + ",";
                Console.OUT.println("List of final survived places are: " + str);            
            }
            Console.OUT.println("=============================");
        }
    }
    
    private def processIterationException(ex:Exception) {
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
    
    public def railToString[T](r:Rail[T]):String {
        if (r == null)
            return "";
        var str:String = "";
        for (x in r)
            str += x + ",";
        return str;
    }
    
    public def railSum(r:Rail[Double]):Double {
        if (r == null)
            return 0.0;
        return RailUtils.reduce(r, (x:Double, y:Double) => x+y, 0.0);
    }
    
    
    public def railAverage(r:Rail[Double]):Double {
        if (r == null)
            return 0.0;
        val railAvg = railSum(r) / r.size;        
        return  Math.round(railAvg);
    }
    
    public def avergaMaxMinRails[T](max:Rail[T], min:Rail[T]):Rail[Double] {
        val result = new Rail[Double](max.size);
        for (i in 0..(max.size-1)){
            result(i) = (max(i) as Double + min(i) as Double)/2.0;
            result(i) = ((result(i)*100) as Long)/100.0;  //two decimal points only
        }
        return result;
    }
    
    private def executorKillHere(op:String) {        
        val stat = placeTempData().stat;
        val index = places.indexOf(here);
        at(Place(0)) {            
            placeTempData().addVictim(index,stat);
        }
        Console.OUT.println("[Hammer Log] Killing ["+here+"] before "+op+" ...");
        System.killHere();
    }
    
    
    class PlaceTempData {
        private val VERBOSE_EXECUTOR_PLACE_LOCAL = (System.getenv("EXECUTOR_PLACE_LOCAL") != null 
                && System.getenv("EXECUTOR_PLACE_LOCAL").equals("1"));
        //used by place hammer
        var place0KillPlaceTime:Long = -1;
        val place0VictimsStats:HashMap[Long,PlaceStatistics];//key=victim_index value=its_old_statistics
        
        
        val stat:PlaceStatistics;        
        var globalIter:Long = 0;
        
        var lastCkptKeys:HashSet[String] = new HashSet[String]();
        var ckptKeyVersion:HashMap[String,Long] = new HashMap[String,Long]();
        
        //used for initializing spare places with the same values from Place0
        private def this(otherStat:PlaceStatistics, gIter:Long, lastCkptKeys:HashSet[String], ckptKeyVersion:HashMap[String,Long]){
            this.stat = otherStat;
            this.place0VictimsStats = here.id == 0? new HashMap[Long,PlaceStatistics]() : null;            
            this.globalIter = gIter;
            this.lastCkptKeys = lastCkptKeys;
            this.ckptKeyVersion = ckptKeyVersion;
        }
    
        public def this(){
            stat = new PlaceStatistics();
            this.place0VictimsStats = here.id == 0? new HashMap[Long,PlaceStatistics]() : null;
        }
        
        public def addVictim(index:Long, stat:PlaceStatistics) {
            assert(here.id == 0);
            place0VictimsStats.put(index, stat);
            place0KillPlaceTime = Timer.milliTime();
            Console.OUT.println("[Hammer Log] Time before killing is ["+placeTempData().place0KillPlaceTime+"] ...");
        }
    }
    
    class PlaceStatistics {        
        val restoreTimes:ArrayList[Long];
        val stepTimes:ArrayList[Long];
        
        var placeMaxRestore:Rail[Long];
        var placeMaxStep:Rail[Long];
        var placeMinRestore:Rail[Long];
        var placeMinStep:Rail[Long];
        
        public def this() {            
            restoreTimes = new ArrayList[Long]();
            stepTimes = new ArrayList[Long]();
        }
        
        public def this(obj:PlaceStatistics) {            
            this.restoreTimes = obj.restoreTimes;
            this.stepTimes = obj.stepTimes;
        }
    }
}