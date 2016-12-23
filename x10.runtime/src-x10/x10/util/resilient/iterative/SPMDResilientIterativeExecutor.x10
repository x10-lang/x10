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
import x10.regionarray.Dist;
import x10.util.ArrayList;
import x10.util.HashSet;
import x10.util.HashMap;
import x10.util.Team;
import x10.util.RailUtils;
import x10.xrx.Runtime;
import x10.util.resilient.PlaceManager;
import x10.util.resilient.PlaceManager.ChangeDescription;
import x10.util.resilient.localstore.Cloneable;
import x10.util.resilient.store.Store;

public class SPMDResilientIterativeExecutor (home:Place) {
    private static val VERBOSE = (System.getenv("EXECUTOR_DEBUG") != null 
                                && System.getenv("EXECUTOR_DEBUG").equals("1"));

    private val manager:GlobalRef[PlaceManager]{self.home == this.home};
    private val resilientMap:Store[Cloneable];
    private var plh:PlaceLocalHandle[PlaceTempData];
    private var team:Team;
    private val itersPerCheckpoint:Long;
    private val isResilient:Boolean;
    // if step() are implicitly synchronized, no need for a step barrier inside the executor
    private val implicitStepSynchronization:Boolean; 
    
    // configuration parameters for killing places at different times
    private var simplePlaceHammer:SimplePlaceHammer;

    private transient var ckptTimes:ArrayList[Long] = new ArrayList[Long]();
    private transient var remakeTimes:ArrayList[Long] = new ArrayList[Long]();
    private transient var appRemakeTimes:ArrayList[Long] = new ArrayList[Long]();
    private transient var reconstructTeamTimes:ArrayList[Long] = new ArrayList[Long]();
    private transient var resilientMapRecoveryTimes:ArrayList[Long] = new ArrayList[Long]();
    private transient var failureDetectionTimes:ArrayList[Long] = new ArrayList[Long]();
    private transient var applicationInitializationTime:Long = 0;
    private transient var startRunTime:Long = 0;
    private transient var lastCkptVersion:Long = -1;
    private transient var lastCkptIter:Long = -1;
    
    public def this(itersPerCheckpoint:Long, sparePlaces:Long, supportShrinking:Boolean, implicitStepSynchronization:Boolean) {
        property(here);

        isResilient = itersPerCheckpoint > 0 && x10.xrx.Runtime.RESILIENT_MODE > 0;
        this.itersPerCheckpoint = itersPerCheckpoint;
        this.implicitStepSynchronization = implicitStepSynchronization;
        val mgr = new PlaceManager(sparePlaces, supportShrinking);
        this.manager = GlobalRef[PlaceManager](mgr);
        team = new Team(mgr.activePlaces());
        if (isResilient) {
            this.resilientMap = Store.make[Cloneable]("_map_", mgr.activePlaces());
            this.simplePlaceHammer = new SimplePlaceHammer();
            if (VERBOSE){
                simplePlaceHammer.printPlan();
            }
        }
        else {        	            
            this.resilientMap = null;
            this.simplePlaceHammer = null;
        }
    }

    public def run(app:SPMDResilientIterativeApp){here == home} {
        run(app, Timer.milliTime());
    }
    
    public def setHammer(h:SimplePlaceHammer){here == home} {
        simplePlaceHammer = h;
    }

    public def activePlaces(){here == home} = manager().activePlaces();

    public def team(){here == home} = team;

    //the startRunTime parameter is added to allow the executor to consider 
    //any initialization time done by the application before starting the executor
    public def run(app:SPMDResilientIterativeApp, startRunTime:Long){here == home} {
        if (simplePlaceHammer != null) {
            simplePlaceHammer.scheduleTimers();
        }
        this.startRunTime = startRunTime;
        Console.OUT.println("SPMDResilientIterativeExecutor: Application start time ["+startRunTime+"] ...");
        val root = here;
        plh = PlaceLocalHandle.make[PlaceTempData](manager().activePlaces(), ()=>new PlaceTempData());
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
                	tmpGlobalIter = plh().globalIter;
                }
                
                /*** Checkpoint (save new version) ***/                
                if (isResilient && !tmpRestoreFlag) {
                    checkpoint(app);
                }
                
                val restoreRequired = tmpRestoreFlag;
                val ckptVersion = lastCkptVersion;
                val globalIter = tmpGlobalIter;
                
                Console.OUT.println("SPMDResilientIterativeExecutor iter: " + plh().globalIter + " remakeRequired["+remakeRequired+"] restoreRequired["+restoreRequired+"] ...");           
                finish for (p in manager().activePlaces()) at (p) async {
                    plh().globalIter = globalIter;
                    
                    /*** Restore ***/
                    if (restoreRequired){
                        restore(app, globalIter);
                    }
                    else {
                    	//increment the last version of the keys
                    	val iter = plh().lastCkptKeys.iterator(); 
                    	while (iter.hasNext()) {
                    		val key = iter.next();
                    		plh().ckptKeyVersion.put(key, ckptVersion);
                    	}
                    }
                    
                    var localIter:Long = 0;
                    
                    while ( !app.isFinished_local() && 
                            (!isResilient || (isResilient && localIter < itersPerCheckpoint)) ) {
                        var stepStartTime:Long = -1; // (-1) is used to differenciate between checkpoint exceptions and step exceptions
                            
                        if ( isResilient && simplePlaceHammer.sayGoodBye(plh().globalIter) ) {
                            executorKillHere("step()");
                        }

                        stepStartTime = Timer.milliTime();
                        if (!implicitStepSynchronization){
                            //to sync places & also to detect DPE
                            team.barrier();
                        }
                        
                        app.step_local();
                        
                        plh().stat.stepTimes.add(Timer.milliTime()-stepStartTime);
                        
                        plh().globalIter ++;
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
                    if (plh().place0KillPlaceTime != -1){
                        failureDetectionTimes.add(Timer.milliTime() - plh().place0KillPlaceTime);
                        plh().place0KillPlaceTime =  -1;
                    }
                } else {
                    throw iterEx; // not a DPE; rethrow
                }
            }
        }while(remakeRequired || !app.isFinished_local());
        
        calculateTimingStatistics();

    }
    
    private def remake(app:SPMDResilientIterativeApp){here == home} {
        if (lastCkptIter == -1) {
            throw new UnsupportedOperationException("process failure occurred but no valid checkpoint exists!");
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
        val startTeamCreate = Timer.milliTime(); 
        team = new Team(changes.newActivePlaces);
        reconstructTeamTimes.add( Timer.milliTime() - startTeamCreate);

        val newPG = changes.newActivePlaces;
        for (p in changes.addedPlaces){
            val realId = p.id;
            val virtualId = newPG.indexOf(p);
            val victimStat =  plh().place0VictimsStats.getOrElse(virtualId, plh().stat);
            val p0GlobalIter = plh().globalIter;
            val p0AllCkptKeys = plh().ckptKeyVersion;
            val p0LastCkptKeys = plh().lastCkptKeys;
            PlaceLocalHandle.addPlace[PlaceTempData](plh, p, ()=>new PlaceTempData(victimStat, p0GlobalIter, p0LastCkptKeys, p0AllCkptKeys));
        }

        val startAppRemake = Timer.milliTime();
        app.remake(changes, team);
        appRemakeTimes.add(Timer.milliTime() - startAppRemake);                        
        
        restoreRequired = true;
        remakeTimes.add(Timer.milliTime() - startRemake) ;                        
        Console.OUT.println("SPMDResilientIterativeExecutor: All remake steps completed successfully lastCkptVersion=["+lastCkptVersion+"]...");
        return restoreRequired;
    }
    
    private def checkpoint(app:SPMDResilientIterativeApp){here == home} {
        val startCheckpoint = Timer.milliTime();
        //take new checkpoint only if restore was not done in this iteration
        if (VERBOSE) Console.OUT.println("checkpointing at iter " + plh().globalIter);
        val newVersion = (lastCkptVersion+1)%2;
        finish for (p in manager().activePlaces()) at (p) async {
            plh().lastCkptKeys.clear();            
            val ckptMap = app.getCheckpointData_local();
            if (ckptMap != null) {
            	val verMap = new HashMap[String,Cloneable]();            	
                val iter = ckptMap.keySet().iterator();
                while (iter.hasNext()) {                    
                    val appKey = iter.next();
                    val key = appKey +":v" + newVersion;
                    val value = ckptMap.getOrThrow(appKey);
                    verMap.put(key, value);
                    plh().lastCkptKeys.add(appKey); 
                    //if (VERBOSE) Console.OUT.println(here + "checkpointing key["+appKey+"]  version["+newVersion+"] succeeded ...");
                }
                resilientMap.setAll(verMap);
            }
            
        }
        lastCkptVersion = newVersion;
        lastCkptIter = plh().globalIter;
        ckptTimes.add(Timer.milliTime() - startCheckpoint);
    }
    
    private def restore(app:SPMDResilientIterativeApp, lastCkptIter:Long) {
    	val startRestoreData = Timer.milliTime();        
        val restoreDataMap = new HashMap[String,Cloneable]();
        val iter = plh().ckptKeyVersion.keySet().iterator();
        while (iter.hasNext()) {
            val appKey = iter.next();
            val keyVersion = plh().ckptKeyVersion.getOrThrow(appKey);
            val key = appKey + ":v" + keyVersion;
            val value = resilientMap.get(key);
            restoreDataMap.put(appKey, value);
            //if (VERBOSE) Console.OUT.println(here + "restoring key["+appKey+"]  version["+keyVersion+"] succeeded ...");
        }
        app.restore_local(restoreDataMap, lastCkptIter);        
        
        plh().stat.restoreTimes.add(Timer.milliTime() - startRestoreData);
    }
    
    private def calculateTimingStatistics(){here == home} {
        val runTime = (Timer.milliTime() - startRunTime);
        Console.OUT.println("Application completed, calculating runtime statistics ...");
        finish for (place in manager().activePlaces()) at(place) async {
        	val stpCount = plh().stat.stepTimes.size();
        	val minStepCount = team.allreduce(stpCount, Team.MIN);
        	
            ////// step times ////////
            if (stpCount != minStepCount)
                Console.OUT.println(here + " stpCount=" + stpCount + " minStepCount=" + minStepCount);
            
            plh().stat.placeMaxStep = new Rail[Long](minStepCount);
            plh().stat.placeMinStep = new Rail[Long](minStepCount);
            plh().stat.placeSumStep = new Rail[Long](minStepCount);
            val dst2max = plh().stat.placeMaxStep;
            val dst2min = plh().stat.placeMinStep;
            val dst2sum = plh().stat.placeSumStep;
            team.allreduce(plh().stat.stepTimes.toRail(), 0, dst2max, 0, minStepCount, Team.MAX);
            team.allreduce(plh().stat.stepTimes.toRail(), 0, dst2min, 0, minStepCount, Team.MIN);
            team.allreduce(plh().stat.stepTimes.toRail(), 0, dst2sum, 0, minStepCount, Team.ADD);

            if (x10.xrx.Runtime.RESILIENT_MODE > 0n){                
                ////// restore times ////////
                val restCount = plh().stat.restoreTimes.size();
                val minRestCount = team.allreduce(restCount, Team.MIN);
                if (minRestCount > 0) {
                    plh().stat.placeMaxRestore = new Rail[Long](minRestCount);
                    plh().stat.placeMinRestore = new Rail[Long](minRestCount);
                    plh().stat.placeSumRestore = new Rail[Long](minRestCount);
                    val dst3max = plh().stat.placeMaxRestore;
                    val dst3min = plh().stat.placeMinRestore;
                    val dst3sum = plh().stat.placeSumRestore;
                    team.allreduce(plh().stat.restoreTimes.toRail(), 0, dst3max, 0, minRestCount, Team.MAX);
                    team.allreduce(plh().stat.restoreTimes.toRail(), 0, dst3min, 0, minRestCount, Team.MIN);
                    team.allreduce(plh().stat.restoreTimes.toRail(), 0, dst3sum, 0, minRestCount, Team.ADD);
                }
            }
        }
        
        val averageSteps = computeAverages(plh().stat.placeSumStep);
        
        var averageRestore:Rail[Double] = null;
        if (isResilient && plh().stat.placeSumRestore != null){
            averageRestore = computeAverages(plh().stat.placeSumRestore);
        }
        
        Console.OUT.println("=========Detailed Statistics============");
        Console.OUT.println("Steps-place0:" + railToString(plh().stat.stepTimes.toRail()));
        Console.OUT.println("Steps-avg:" + railToString(averageSteps));
        Console.OUT.println("Steps-min:" + railToString(plh().stat.placeMinStep));
        Console.OUT.println("Steps-max:" + railToString(plh().stat.placeMaxStep));
        
        if (isResilient){
            Console.OUT.println("Checkpoint:" + railToString(ckptTimes.toRail()));            
            
            Console.OUT.println("RestoreData-avg:" + railToString(averageRestore));
            Console.OUT.println("RestoreData-min:" + railToString(plh().stat.placeMinRestore));
            Console.OUT.println("RestoreData-max:" + railToString(plh().stat.placeMaxRestore));
            
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
        Console.OUT.println(">>>>>>>>>>>>>>TotalSteps:"+ railSum(averageSteps) as Long);
        Console.OUT.println();
        if (isResilient){
            Console.OUT.println("Checkpoint-all:" + railToString(ckptTimes.toRail()));
            Console.OUT.println("   ---AverageCheckpoint:" + railAverage(ckptTimes.toRail()) );
            Console.OUT.println(">>>>>>>>>>>>>>TotalCheckpointingTime:" + railSum(ckptTimes.toRail()) as Long);
      
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
            Console.OUT.println(">>>>>>>>>>>>>>TotalRecovery:" + (railSum(failureDetectionTimes.toRail()) + railSum(remakeTimes.toRail()) + railSum(averageRestore) ) as Long);
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
                for (p in manager().activePlaces())
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
    
    private def railToString[T](r:Rail[T]):String {
        if (r == null)
            return "";
        var str:String = "";
        for (x in r)
            str += x + ",";
        return str;
    }
    
    private def railSum(r:Rail[Long]):Long {
        if (r == null)
            return 0;
        return RailUtils.reduce(r, (x:Long, y:Long) => x+y, 0);
    }

    private def railSum(r:Rail[Double]):Double {
        if (r == null)
            return 0.0;
        return RailUtils.reduce(r, (x:Double, y:Double) => x+y, 0.0);
    }
    
    private def railAverage(r:Rail[Long]):Double {
        if (r == null)
            return 0.0;
        val railAvg = railSum(r) as Double / r.size;        
        return Math.round(railAvg);
    }    
    
    private def railAverage(r:Rail[Double]):Double {
        if (r == null)
            return 0.0;
        val railAvg = railSum(r) / r.size;        
        return  Math.round(railAvg);
    }
    
    private def computeAverages[T](sum:Rail[T]){here==home}:Rail[Double] {
        val result = new Rail[Double](sum.size);
        for (i in 0..(sum.size-1)) {
            result(i) = (sum(i) as Double) / manager().activePlaces().size;
            result(i) = ((result(i)*100) as Long)/100.0;  //two decimal points only
        }
        return result;
    }
    
    private def executorKillHere(op:String) {        
        val stat = plh().stat;
        val victimId = here.id;
        at (Place(0)) {            
            plh().addVictim(victimId,stat);
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
            Console.OUT.println("[Hammer Log] Time before killing is ["+place0KillPlaceTime+"] ...");
        }
    }
    
    class PlaceStatistics {        
        val restoreTimes:ArrayList[Long];
        val stepTimes:ArrayList[Long];
        
        var placeMaxRestore:Rail[Long];
        var placeMaxStep:Rail[Long];
        var placeMinRestore:Rail[Long];
        var placeMinStep:Rail[Long];
        var placeSumRestore:Rail[Long];
        var placeSumStep:Rail[Long];
        
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

