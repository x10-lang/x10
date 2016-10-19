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
public class SPMDResilientIterativeExecutorULFM {
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
    // index of the checkpoint first checkpoint (0), second checkpoint (1), ...etc    
    private val KILL_CHECKVOTING_INDEX = (System.getenv("EXECUTOR_KILL_CHECKVOTING") != null)?Long.parseLong(System.getenv("EXECUTOR_KILL_CHECKVOTING")):-1;
    private val KILL_CHECKVOTING_PLACE = (System.getenv("EXECUTOR_KILL_CHECKVOTING_PLACE") != null)?Long.parseLong(System.getenv("EXECUTOR_KILL_CHECKVOTING_PLACE")):-1;   
    private val KILL_RESTOREVOTING_INDEX = (System.getenv("EXECUTOR_KILL_RESTOREVOTING") != null)?Long.parseLong(System.getenv("EXECUTOR_KILL_RESTOREVOTING")):-1;
    private val KILL_RESTOREVOTING_PLACE = (System.getenv("EXECUTOR_KILL_RESTOREVOTING_PLACE") != null)?Long.parseLong(System.getenv("EXECUTOR_KILL_RESTOREVOTING_PLACE")):-1;   
    
    private transient var remakeTimes:ArrayList[Double] = new ArrayList[Double]();
    private transient var appRemakeTimes:ArrayList[Double] = new ArrayList[Double]();
    private transient var reconstructTeamTimes:ArrayList[Double] = new ArrayList[Double]();
    private transient var resilientMapRecoveryTimes:ArrayList[Double] = new ArrayList[Double]();
    private transient var failureDetectionTimes:ArrayList[Double] = new ArrayList[Double]();
    private transient var applicationInitializationTime:Long = 0;
    private transient var startRunTime:Long = 0;
    private transient var restoreRequired:Boolean = false;
    private transient var remakeRequired:Boolean = false;
    
    private val CHECKPOINT_OPERATION = 1;
    private val RESTORE_OPERATION = 2;
    
    public def this(itersPerCheckpoint:Long, resilientMap:ResilientStore, implicitStepSynchronization:Boolean) {
        
        this.itersPerCheckpoint = itersPerCheckpoint;
        this.implicitStepSynchronization = implicitStepSynchronization;
        
        if (itersPerCheckpoint > 0 && x10.xrx.Runtime.RESILIENT_MODE > 0 && resilientMap != null) {
            isResilient = true;
            this.resilientMap = resilientMap;
            this.simplePlaceHammer = new SimplePlaceHammer(HAMMER_STEPS, HAMMER_PLACES);
            places = resilientMap.getActivePlaces();
            if (!x10.xrx.Runtime.x10rtAgreementSupport()){
                throw new UnsupportedOperationException("This executor requires an agreement algorithm from the transport layer ...");
            }
            if (VERBOSE){         
                Console.OUT.println("HAMMER_STEPS="+HAMMER_STEPS);
                Console.OUT.println("HAMMER_PLACES="+HAMMER_PLACES);
                Console.OUT.println("EXECUTOR_KILL_CHECKVOTING="+KILL_CHECKVOTING_INDEX);
                Console.OUT.println("EXECUTOR_KILL_CHECKVOTING_PLACE="+KILL_CHECKVOTING_PLACE);
                Console.OUT.println("EXECUTOR_KILL_RESTOREVOTING_INDEX"+KILL_RESTOREVOTING_INDEX);
                Console.OUT.println("EXECUTOR_KILL_RESTOREVOTING_PLACE"+KILL_RESTOREVOTING_PLACE);
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
        var globalIter:Long = 0;        
        applicationInitializationTime = Timer.milliTime() - startRunTime;
        
        do{
            try {
                /*** Starting a Restore Operation *****/
                
                /*** Remake ***/
                var tmpRestoreFlag:Boolean = false;
                if (remakeRequired) {
                    tmpRestoreFlag = remake(app);
                    if (tmpRestoreFlag) {
                        globalIter = placeTempData().lastCheckpointIter;
                    }
                    remakeRequired = false;
                }
                Console.OUT.println("SPMDResilientIterativeExecutorULFM: remakeRequired["+remakeRequired+"] restoreRequired["+restoreRequired+"] ...");           
                
                //to be copied to all places
                val tmpRestoreRequired = tmpRestoreFlag;
                val tmpGlobalIter = globalIter;
                val placesCount = places.size();
                finish ateach(Dist.makeUnique(places)) {
                    //Console.OUT.println("Starting all over again: stepCount = " + placeTempData().stat.stepTimes.size());
                    var localIter:Long = tmpGlobalIter;
                    var localRestoreJustDone:Boolean = false;
                    var localRestoreRequired:Boolean = tmpRestoreRequired;
                
                    while ( !app.isFinished_local() || localRestoreRequired) {
                        var stepStartTime:Long = -1; // (-1) is used to differenciate between checkpoint exceptions and step exceptions
                        try{
                            /**Local Restore Operation**/
                            if (localRestoreRequired){
                                checkpointRestoreProtocol(RESTORE_OPERATION, app, team, root, placesCount);
                                localRestoreRequired = false;
                                localRestoreJustDone = true;
                            }
                            
                            /**Local Checkpointing Operation**/
                            if (!localRestoreJustDone) {
                                //take new checkpoint only if restore was not done in this iteration
                                if (isResilient && (localIter % itersPerCheckpoint) == 0) {
                                    if (VERBOSE) Console.OUT.println("["+here+"] checkpointing at iter " + localIter);
                                    checkpointRestoreProtocol(CHECKPOINT_OPERATION, app, team, root, placesCount);
                                    placeTempData().lastCheckpointIter = localIter;
                                }
                            } else {
                                localRestoreJustDone = false;
                            }
                            
                            if ( isResilient && simplePlaceHammer.sayGoodBye(localIter) ) {
                                executorKillHere("step()");
                            }

                            stepStartTime = Timer.milliTime();
                            if (!implicitStepSynchronization){
                                //to sync places & also to detect DPE
                                team.barrier();
                            }
                            
                            app.step_local();
                            
                            placeTempData().stat.stepTimes.add(Timer.milliTime()-stepStartTime);
                            
                            localIter++;
                            
                        } catch (ex:Exception) {
                            throw ex;
                        }//step catch block
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
        if (placeTempData().lastCheckpointIter == -1) {
            throw new UnsupportedOperationException("process failure occurred but no valid checkpoint exists!");
        }
        
        if (VERBOSE) Console.OUT.println("Restoring to iter " + placeTempData().lastCheckpointIter);
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
            val victimStat =  placeTempData().place0VictimsStats.getOrThrow(virtualId);
            addedPlaces.add(Place(realId));
            val p0AllCkptKeys = placeTempData().allCkptKeys;
            PlaceLocalHandle.addPlace[PlaceTempData](placeTempData, Place(realId), ()=>new PlaceTempData(victimStat, p0AllCkptKeys));
        }

        val startAppRemake = Timer.milliTime();
        app.remake(newPG, team, addedPlaces);
        appRemakeTimes.add(Timer.milliTime() - startAppRemake);                        
        
        val lastCheckIter = placeTempData().lastCheckpointIter;
        places = newPG;
        restoreRequired = true;
        remakeTimes.add(Timer.milliTime() - startRemake) ;                        
        Console.OUT.println("SPMDResilientIterativeExecutorULFM: All remake steps completed successfully ...");
    
        return restoreRequired;
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
                ////// checkpoint times ////////
                val chkCount = placeTempData().stat.checkpointTimes.size();
                if (chkCount > 0) {
                    placeTempData().stat.placeMaxCheckpoint = new Rail[Long](chkCount);
                    placeTempData().stat.placeMinCheckpoint = new Rail[Long](chkCount);
                    val dst1max = placeTempData().stat.placeMaxCheckpoint;
                    val dst1min = placeTempData().stat.placeMinCheckpoint;
                    team.allreduce(placeTempData().stat.checkpointTimes.toRail(), 0, dst1max, 0, chkCount, Team.MAX);
                    team.allreduce(placeTempData().stat.checkpointTimes.toRail(), 0, dst1min, 0, chkCount, Team.MIN);
                }
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
                ////// checkpoint agreement times ////////
                val chkAgreeCount = placeTempData().stat.checkpointAgreementTimes.size();
                if (chkAgreeCount > 0) {
                    placeTempData().stat.placeMaxCheckpointAgreement = new Rail[Long](chkAgreeCount);
                    placeTempData().stat.placeMinCheckpointAgreement = new Rail[Long](chkAgreeCount);
                    val dst4max = placeTempData().stat.placeMaxCheckpointAgreement;
                    val dst4min = placeTempData().stat.placeMinCheckpointAgreement;
                    team.allreduce(placeTempData().stat.checkpointAgreementTimes.toRail(), 0, dst4max, 0, chkAgreeCount, Team.MAX);
                    team.allreduce(placeTempData().stat.checkpointAgreementTimes.toRail(), 0, dst4min, 0, chkAgreeCount, Team.MIN);
                }
                
                ////// restore agreement times ////////
                val restAgreeCount = placeTempData().stat.restoreAgreementTimes.size();
                if (restAgreeCount > 0) {
                    placeTempData().stat.placeMaxRestoreAgreement = new Rail[Long](restAgreeCount);
                    placeTempData().stat.placeMinRestoreAgreement = new Rail[Long](restAgreeCount);
                    val dst5max = placeTempData().stat.placeMaxRestoreAgreement;
                    val dst5min = placeTempData().stat.placeMinRestoreAgreement;
                    team.allreduce(placeTempData().stat.restoreAgreementTimes.toRail(), 0, dst5max, 0, restAgreeCount, Team.MAX);
                    team.allreduce(placeTempData().stat.restoreAgreementTimes.toRail(), 0, dst5min, 0, restAgreeCount, Team.MIN);
                }
                
            }
        }
        
        val averageSteps = avergaMaxMinRails(placeTempData().stat.placeMinStep,  placeTempData().stat.placeMaxStep);
        var averageCheckpoint:Rail[Double] = null;
        var averageCheckpointAgreement:Rail[Double] = null;
        var averageRestore:Rail[Double] = null;
        var averageRestoreAgreement:Rail[Double] = null;
        if (isResilient){
            if (placeTempData().stat.checkpointTimes.size() > 0)
                averageCheckpoint          = avergaMaxMinRails(placeTempData().stat.placeMinCheckpoint,          placeTempData().stat.placeMaxCheckpoint);
            
            if (placeTempData().stat.checkpointAgreementTimes.size() > 0)
                averageCheckpointAgreement = avergaMaxMinRails(placeTempData().stat.placeMinCheckpointAgreement, placeTempData().stat.placeMaxCheckpointAgreement);
            
            if (placeTempData().stat.restoreTimes.size() > 0)
                averageRestore             = avergaMaxMinRails(placeTempData().stat.placeMinRestore,             placeTempData().stat.placeMaxRestore);
            
            if ( placeTempData().stat.restoreAgreementTimes.size() > 0)
                averageRestoreAgreement    = avergaMaxMinRails(placeTempData().stat.placeMinRestoreAgreement,    placeTempData().stat.placeMaxRestoreAgreement);
        }
        
        Console.OUT.println("=========Detailed Statistics============");
        Console.OUT.println("Steps-place0:" + railToString(placeTempData().stat.stepTimes.toRail()));
        
        Console.OUT.println("Steps-avg:" + railToString(averageSteps));
        Console.OUT.println("Steps-min:" + railToString(placeTempData().stat.placeMinStep));
        Console.OUT.println("Steps-max:" + railToString(placeTempData().stat.placeMaxStep));
        
        if (isResilient){
            Console.OUT.println("CheckpointData-avg:" + railToString(averageCheckpoint));
            Console.OUT.println("CheckpointData-min:" + railToString(placeTempData().stat.placeMinCheckpoint));
            Console.OUT.println("CheckpointData-max:" + railToString(placeTempData().stat.placeMaxCheckpoint));
            
            Console.OUT.println("CheckpointAgree-avg:" + railToString(averageCheckpointAgreement));
            Console.OUT.println("CheckpointAgree-min:" + railToString(placeTempData().stat.placeMinCheckpointAgreement));
            Console.OUT.println("CheckpointAgree-max:" + railToString(placeTempData().stat.placeMaxCheckpointAgreement));
            
            Console.OUT.println("RestoreData-avg:" + railToString(averageRestore));
            Console.OUT.println("RestoreData-min:" + railToString(placeTempData().stat.placeMinRestore));
            Console.OUT.println("RestoreData-max:" + railToString(placeTempData().stat.placeMaxRestore));
            
            Console.OUT.println("RestoreAgree-avg:" + railToString(averageRestoreAgreement));
            Console.OUT.println("RestoreAgree-min:" + railToString(placeTempData().stat.placeMinRestoreAgreement));
            Console.OUT.println("RestoreAgree-max:" + railToString(placeTempData().stat.placeMaxRestoreAgreement));
            
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
            Console.OUT.println("CheckpointData:"             + railToString(averageCheckpoint));
            Console.OUT.println("   ---AverageCheckpointData:" + railAverage(averageCheckpoint) );
            Console.OUT.println("CheckpointAgreement:"             + railToString(averageCheckpointAgreement)  );
            Console.OUT.println("   ---AverageCheckpointAgreement:" + railAverage(averageCheckpointAgreement)  );
            Console.OUT.println(">>>>>>>>>>>>>>TotalCheckpointing:"+ (railSum(averageCheckpoint)+railSum(averageCheckpointAgreement) ) );
      
            Console.OUT.println();
            Console.OUT.println("FailureDetection-all:"        + railToString(failureDetectionTimes.toRail()) );
            Console.OUT.println("   ---FailureDetection:"   + railAverage(failureDetectionTimes.toRail()) );
            
            
            
            Console.OUT.println("ResilientMapRecovery-all:"      + railToString(resilientMapRecoveryTimes.toRail()) );
            Console.OUT.println("   ---ResilientMapRecovery:" + railAverage(resilientMapRecoveryTimes.toRail()) );
            Console.OUT.println("AppRemake-all:"      + railToString(appRemakeTimes.toRail()) );
            Console.OUT.println("   ---AppRemake:" + railAverage(appRemakeTimes.toRail()) );
            Console.OUT.println("TeamReconstruction-all:"      + railToString(reconstructTeamTimes.toRail()) );
            Console.OUT.println("   ---TeamReconstruction:" + railAverage(reconstructTeamTimes.toRail()) );
            Console.OUT.println("TotalRemake-all:"                   + railToString(remakeTimes.toRail()) );
            Console.OUT.println("   ---TotalRemake:"              + railAverage(remakeTimes.toRail()) );
            
            
            Console.OUT.println("RestoreData:"         + railToString(averageRestore));
            Console.OUT.println("   ---RestoreData:"    + railAverage(averageRestore));
            Console.OUT.println("RestoreAgreement:"         + railToString(averageRestoreAgreement) );
            Console.OUT.println("   ---RestoreAgreement:"    + railAverage(averageRestoreAgreement) );
            Console.OUT.println(">>>>>>>>>>>>>>TotalRecovery:" + (railSum(failureDetectionTimes.toRail()) + railSum(remakeTimes.toRail()) + railSum(averageRestore) + railSum(averageRestoreAgreement) ));
        }
        Console.OUT.println("=============================");
        Console.OUT.println("Actual RunTime:" + runTime);
        var calcTotal:Double = applicationInitializationTime + railSum(averageSteps);
        
        if (isResilient){
            calcTotal += (railSum(averageCheckpoint)+railSum(averageCheckpointAgreement) ) + 
                (railSum(failureDetectionTimes.toRail()) + railSum(remakeTimes.toRail()) + railSum(averageRestore) + railSum(averageRestoreAgreement) );
        }
        Console.OUT.println("Calculated RunTime based on Averages:" + calcTotal 
            + "   ---Difference:" + (runTime-calcTotal));
        
        Console.OUT.println("=========Counts============");
        Console.OUT.println("StepCount:"+averageSteps.size);
        if (isResilient){
            Console.OUT.println("CheckpointCount:"+(averageCheckpoint==null?0:averageCheckpoint.size));
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
    
    //Checkpointing will only occur in resilient mode
    /**
     * lastCheckpointIter    needed only for restore
     * */
    private def checkpointRestoreProtocol(operation:Long, app:SPMDResilientIterativeApp, team:Team, root:Place, placesCount:Long){
        val op:String = (operation==CHECKPOINT_OPERATION)?"Checkpoint":"Restore";
        var str:String = "";
        val excs = new GrowableRail[CheckedThrowable]();
        val startOperation = Timer.milliTime();
        var operationTime:Long = 0;
        val trans = resilientMap.startLocalTransaction();        
        var vote:Long = 1;
        try{
            if (operation == CHECKPOINT_OPERATION) {
                val x = Timer.milliTime();
                val ckptMap = app.getCheckpointData_local();
                if (ckptMap != null) {
                    val iter = ckptMap.keySet().iterator();
                    while (iter.hasNext()) {
                        val key = iter.next();
                        val value = ckptMap.getOrThrow(key);
                        trans.put(key, value);
                        
                        placeTempData().allCkptKeys.add(key);
                    }
                }
                str += " put["+(Timer.milliTime() - x)+"]";
            }
            else {
                val x = Timer.milliTime();
                
                val restoreDataMap = new HashMap[String,Cloneable]();
                val iter = placeTempData().allCkptKeys.iterator();
                while (iter.hasNext()) {
                    val key = iter.next();
                    val value = trans.get(key);
                    restoreDataMap.put(key, value);
                }
                str += " get["+(Timer.milliTime() - x)+"]";
                
                val y = Timer.milliTime();
                app.restore_local(restoreDataMap, placeTempData().lastCheckpointIter);
                str += " restore["+(Timer.milliTime() - y)+"]";
            }
            val z = Timer.milliTime();
               trans.prepare();
               str += " prepare["+(Timer.milliTime() - z)+"]";
            if (VERBOSE) Console.OUT.println(here+" Succeeded in operation ["+op+"]  myVote["+vote+"]");
        }catch(ex:Exception){
            vote = 0;
            excs.add(ex);
            if (VERBOSE) Console.OUT.println(here+" Failed in operation ["+op+"]  myVote["+vote+"]  exception["+ex.getMessage()+"]");
            ex.printStackTrace();
        }
        
        operationTime = Timer.milliTime() - startOperation;
        
        if ((operation == CHECKPOINT_OPERATION && KILL_CHECKVOTING_INDEX == placeTempData().stat.checkpointTimes.size() && 
               here.id == KILL_CHECKVOTING_PLACE) || 
               (operation == RESTORE_OPERATION    && KILL_RESTOREVOTING_INDEX == placeTempData().stat.restoreTimes.size() && 
                here.id == KILL_RESTOREVOTING_PLACE)) {
            executorKillHere(op);
        }
        
        val startAgree = Timer.milliTime();
        var success:Int = 1N;
        var commit:Boolean = true;
        try{
            if (VERBOSE) Console.OUT.println(here+" Starting agree call in operation ["+op+"]");
                success = team.agree((vote as Int));
            
            if (success != 1N)
                commit = false;
        }
        catch(agrex:Exception){
            //agrex.printStackTrace();
            excs.add(agrex);
            commit = false;
        }
        
        str += " agree["+(Timer.milliTime() - startAgree)+"]";
        
        if (operation == CHECKPOINT_OPERATION)
            placeTempData().stat.checkpointAgreementTimes.add(Timer.milliTime() - startAgree);
        else if (operation == RESTORE_OPERATION)
            placeTempData().stat.restoreAgreementTimes.add(Timer.milliTime() - startAgree);
        
        //no exceptions expected from commit/rollback
        val startCompleteTrans = Timer.milliTime();
        if (commit) {
            trans.commit();
            str += " commit["+(Timer.milliTime() - startCompleteTrans)+"]";
        }
        else {
            trans.rollback();
            str += " rollback["+(Timer.milliTime() - startCompleteTrans)+"]";
        }
        operationTime += Timer.milliTime() - startCompleteTrans;

        if (operation == CHECKPOINT_OPERATION)
            placeTempData().stat.checkpointTimes.add(operationTime);
        else if (operation == RESTORE_OPERATION)
            placeTempData().stat.restoreTimes.add(operationTime);
        
        //Console.OUT.println("Log: "+ here + " " + str);
        if (excs.size() > 0){
            throw new MultipleExceptions(excs);
        }
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
        var lastCheckpointIter:Long = -1;
        val stat:PlaceStatistics;
        val place0VictimsStats:HashMap[Long,PlaceStatistics];//key=victim_index value=its_old_statistics
        
        var allCkptKeys:HashSet[String] = new HashSet[String]();
        
        //used for initializing spare places with the same values from Place0
        private def this(otherStat:PlaceStatistics, allCkptKeys:HashSet[String]){
            this.stat = otherStat;
            this.place0VictimsStats = here.id == 0? new HashMap[Long,PlaceStatistics]() : null;
            this.allCkptKeys = allCkptKeys;
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
        val checkpointTimes:ArrayList[Long];
        val checkpointAgreementTimes:ArrayList[Long];
        val restoreTimes:ArrayList[Long];
        val restoreAgreementTimes:ArrayList[Long];
        val stepTimes:ArrayList[Long];
    
        var placeMaxCheckpoint:Rail[Long];
        var placeMaxCheckpointAgreement:Rail[Long];
        var placeMaxRestore:Rail[Long];
        var placeMaxRestoreAgreement:Rail[Long];
        var placeMaxStep:Rail[Long];
        var placeMinCheckpoint:Rail[Long];
        var placeMinCheckpointAgreement:Rail[Long];
        var placeMinRestore:Rail[Long];
        var placeMinRestoreAgreement:Rail[Long];
        var placeMinStep:Rail[Long];
        
        public def this() {
            checkpointTimes = new ArrayList[Long]();
            checkpointAgreementTimes = new ArrayList[Long]();
            restoreTimes = new ArrayList[Long]();
            restoreAgreementTimes = new ArrayList[Long]();
            stepTimes = new ArrayList[Long]();
        }
        
        public def this(obj:PlaceStatistics) {
            this.checkpointTimes = obj.checkpointTimes;
            this.checkpointAgreementTimes = obj.checkpointAgreementTimes;
            this.restoreTimes = obj.restoreTimes;
            this.restoreAgreementTimes = obj.restoreAgreementTimes;
            this.stepTimes = obj.stepTimes;
        }
        
        /*
        public def stepsRail():Rail[Long] {
            val rail = new Rail[Long](stepTimes.size());
            val iter = stepTimes.keySet().iterator();
            var str:String = "";
            while (iter.hasNext()) {
                val i = iter.next();
                val t = stepTimes.getOrThrow(i);
                var sum:Long = 0;
                for (x in t) {
                    sum += x;
                }
                rail(i) = sum;
                str+= rail(i)+",";
            }            
            //Console.OUT.println(here + "=>" + str);
            return rail;
        }
        
        public def addStepTime(i:Long, t:Long) {
            var list:ArrayList[Long] = stepTimes.getOrElse(i, null);
            if (list == null) {
                list = new ArrayList[Long]();
                stepTimes.put(i,list);
            }
            list.add(t);
        }
        */
    }
}