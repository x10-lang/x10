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
import x10.util.Team;
import x10.util.GrowableRail;
import x10.util.RailUtils;

/*
 * TODO:
 * -> maximum retry for restore failures
 * -> support more than 1 place failure.  when a palce dies, store.rebackup()
 * -> no need to notify place death for collectives
 * */
public class LocalViewResilientExecutorOpt {
    private var placeTempData:PlaceLocalHandle[PlaceTempData];
    private transient var places:PlaceGroup;
    private var team:Team;
    private val itersPerCheckpoint:Long;
    private var isResilient:Boolean = false;
    // if step_local() are implicitly synchronized, no need for a step barrier inside the executor
    private val implicitStepSynchronization:Boolean; 
    private val createReadOnlyStore:Boolean;
    private val VERBOSE = (System.getenv("EXECUTOR_DEBUG") != null 
                        && System.getenv("EXECUTOR_DEBUG").equals("1"));
    
    //parameters for killing places at different times
    private val KILL_STEP = (System.getenv("EXECUTOR_KILL_STEP") != null)?Long.parseLong(System.getenv("EXECUTOR_KILL_STEP")):-1;
    private val KILL_STEP_PLACE = (System.getenv("EXECUTOR_KILL_STEP_PLACE") != null)?Long.parseLong(System.getenv("EXECUTOR_KILL_STEP_PLACE")):-1;
    // index of the checkpoint first checkpoint (0), second checkpoint (1), ...etc
    private val KILL_CHECKVOTING_INDEX = (System.getenv("EXECUTOR_KILL_CHECKVOTING") != null)?Long.parseLong(System.getenv("EXECUTOR_KILL_CHECKVOTING")):-1;
    private val KILL_CHECKVOTING_PLACE = (System.getenv("EXECUTOR_KILL_CHECKVOTING_PLACE") != null)?Long.parseLong(System.getenv("EXECUTOR_KILL_CHECKVOTING_PLACE")):-1;   
    private val KILL_RESTOREVOTING_INDEX = (System.getenv("EXECUTOR_KILL_RESTOREVOTING") != null)?Long.parseLong(System.getenv("EXECUTOR_KILL_RESTOREVOTING")):-1;
    private val KILL_RESTOREVOTING_PLACE = (System.getenv("EXECUTOR_KILL_RESTOREVOTING_PLACE") != null)?Long.parseLong(System.getenv("EXECUTOR_KILL_RESTOREVOTING_PLACE")):-1;   
    
    private transient var runTime:Long = 0;
    private transient var remakeTime:Long = 0;
    private transient var reconstructTeamTime:Long = 0;
    private transient var remakeCount:Long = 0;    
    private transient var failureDetectionTime:Long = 0;
    private transient var failureDetectionCount:Long = 0;
    private transient var applicationInitializationTime:Long = 0;
    
    private transient var restoreRequired:Boolean = false;
    private transient var remakeRequired:Boolean = false;
    
    private val CHECKPOINT_OPERATION = 1;
    private val RESTORE_OPERATION = 2;
    
    class PlaceTempData {
    	
    	private val VERBOSE_EXECUTOR_PLACE_LOCAL = (System.getenv("EXECUTOR_PLACE_LOCAL") != null 
                && System.getenv("EXECUTOR_PLACE_LOCAL").equals("1"));
    	
        //used by place hammer
        var place0KillPlaceTime:Long = -1;
        
        var lastCheckpointIter:Long = -1;
        var commitCount:Long = 0;
        val snapshots:Rail[DistObjectSnapshot];
    	val readOnlySnapshot:DistObjectSnapshot;
        
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
        
        //used for initializing spare places with the same values from Place0
        public def this(snapshots:Rail[DistObjectSnapshot], checkTimes:ArrayList[Long], checkAgreeTimes:ArrayList[Long], 
        		restoreTimes:ArrayList[Long], restoreAgreeTimes:ArrayList[Long],
        		stepTimes:ArrayList[Long], lastCheckpointIter:Long, commitCount:Long,
        		readOnlySnapshot:DistObjectSnapshot){
            this.checkpointTimes = checkTimes;
            this.checkpointAgreementTimes = checkAgreeTimes;
            this.stepTimes = stepTimes;
            this.restoreTimes = restoreTimes;
            this.restoreAgreementTimes = restoreAgreeTimes;
            this.snapshots = snapshots;
            this.lastCheckpointIter = lastCheckpointIter;
            this.commitCount = commitCount;
            this.readOnlySnapshot = readOnlySnapshot;
        }
    
        public def this(snapshots:Rail[DistObjectSnapshot], readOnlySnapshot:DistObjectSnapshot){
            this.checkpointTimes = new ArrayList[Long]();
            this.checkpointAgreementTimes = new ArrayList[Long]();
            this.restoreTimes = new ArrayList[Long]();
            this.restoreAgreementTimes = new ArrayList[Long]();
            this.stepTimes = new ArrayList[Long]();
            this.snapshots = snapshots;
            this.readOnlySnapshot = readOnlySnapshot;
        }
        
        private def getConsistentSnapshot():DistObjectSnapshot{
            val idx = commitCount % 2;
            if (VERBOSE_EXECUTOR_PLACE_LOCAL) Console.OUT.println("["+here+"] Consistent Checkpoint Index ["+idx+"] ...");
            return snapshots(idx);
        }  
        
        public def getNextSnapshot():DistObjectSnapshot {
            val idx = (commitCount+1) % 2;
            if (VERBOSE_EXECUTOR_PLACE_LOCAL) Console.OUT.println("["+here+"] Temp Checkpoint Index ["+idx+"] ...");
            return snapshots(idx);
        }

        /** Cancel a snapshot, in case of failure during checkpoint. */
        public def cancelOtherSnapshot() {
        	val idx = (commitCount+1) % 2;
        	if (VERBOSE_EXECUTOR_PLACE_LOCAL) Console.OUT.println("["+here+"] Deleting Checkpoint At Index ["+idx+"] ...");
            snapshots(idx).deleteAll_local();
        }
        
        public def commit() {
            commitCount++; // switch to the new snapshot
            if (VERBOSE_EXECUTOR_PLACE_LOCAL) Console.OUT.println("["+here+"] Committed count ["+commitCount+"] ...");
        }
        
        //must be called after a commit
        public def rollback(){
        	commitCount--; // switch to the new snapshot
        	if (VERBOSE_EXECUTOR_PLACE_LOCAL) Console.OUT.println("["+here+"] Rollbacked count ["+commitCount+"] ...");
        }
    }
    
    public def this(itersPerCheckpoint:Long, places:PlaceGroup, implicitStepSynchronization:Boolean, createReadOnlyStore:Boolean) {
        this.places = places;
        this.itersPerCheckpoint = itersPerCheckpoint;
        this.implicitStepSynchronization = implicitStepSynchronization;
        this.createReadOnlyStore = createReadOnlyStore;
        if (itersPerCheckpoint > 0 && x10.xrx.Runtime.RESILIENT_MODE > 0) {
            isResilient = true;
            if (!x10.xrx.Runtime.x10rtAgreementSupport()){
            	throw new UnsupportedOperationException("This executor requires an agreement algorithm from the transport layer ...");
        	}
            if (VERBOSE){
            	Console.OUT.println("EXECUTOR_KILL_STEP="+KILL_STEP);
            	Console.OUT.println("EXECUTOR_KILL_STEP_PLACE="+KILL_STEP_PLACE);
            	Console.OUT.println("EXECUTOR_KILL_CHECKVOTING="+KILL_CHECKVOTING_INDEX);
            	Console.OUT.println("EXECUTOR_KILL_CHECKVOTING_PLACE="+KILL_CHECKVOTING_PLACE);
            	Console.OUT.println("EXECUTOR_KILL_RESTOREVOTING_INDEX"+KILL_RESTOREVOTING_INDEX);
            	Console.OUT.println("EXECUTOR_KILL_RESTOREVOTING_PLACE"+KILL_RESTOREVOTING_PLACE);
            }
        }
    }

    public def run(app:LocalViewResilientIterativeAppOpt) {
        run(app, Timer.milliTime());
    }
    
    //the startRunTime parameter is added to allow the executor to consider 
    //any initlization time done by the application before starting the executor  
    public def run(app:LocalViewResilientIterativeAppOpt, startRunTime:Long) {
    	Console.OUT.println("LocalViewResilientExecutor: Application start time ["+startRunTime+"] ...");
        applicationInitializationTime = Timer.milliTime() - startRunTime;
        val root = here;
        val snapshots = (isResilient)?new Rail[DistObjectSnapshot](2, (i:Long)=>DistObjectSnapshot.make(places)):null;
        var tmpROSnapshot:DistObjectSnapshot = null;
        if (isResilient && createReadOnlyStore){
        	tmpROSnapshot = DistObjectSnapshot.make(places);
        }
        val readOnlySnapshot = tmpROSnapshot;
        placeTempData = PlaceLocalHandle.make[PlaceTempData](places, ()=>new PlaceTempData(snapshots, readOnlySnapshot));
        team = new Team(places);
        var globalIter:Long = 0;
        
        do{
            try {
            	/*** Starting a Restore Operation *****/
                if (remakeRequired) {
                    if (placeTempData().lastCheckpointIter > -1) {
                        if (VERBOSE) Console.OUT.println("Restoring to iter " + placeTempData().lastCheckpointIter);
                        remakeTime -= Timer.milliTime();
                        val restorePGResult = PlaceGroupBuilder.createRestorePlaceGroup(places);
                        val newPG = restorePGResult.newGroup;
                        val addedPlaces = restorePGResult.newAddedPlaces;
                        
                        if (VERBOSE){
                            var str:String = "";
                            for (p in newPG)
                                str += p.id + ",";
                            Console.OUT.println("Restore places are: " + str);
                        } 
                        reconstructTeamTime -= Timer.milliTime();
                        team = new Team(newPG);
                        reconstructTeamTime += Timer.milliTime();
                        app.remake(newPG, team, addedPlaces);
                        ///////////////////////////////////////////////////////////
                        //Initialize the new places with the same info at place 0//
                        val lastCheckIter = placeTempData().lastCheckpointIter;
                        val tmpPlace0CheckpointTimes = placeTempData().checkpointTimes;
                        val tmpPlace0CheckpointAgreeTimes = placeTempData().checkpointAgreementTimes;
                        val tmpPlace0RestoreTimes = placeTempData().restoreTimes;
                        val tmpPlace0RestoreAgreeTimes = placeTempData().restoreAgreementTimes;
                        val tmpPlace0StepTimes = placeTempData().stepTimes;
                        val tmpPlace0CommitCount = placeTempData().commitCount;
                        val tmpPlace0ReadOnlySnapshot = placeTempData().readOnlySnapshot;
                        
                        for (sparePlace in addedPlaces){
                            Console.OUT.println("LocalViewResilientExecutor: Adding place["+sparePlace+"] ...");           
                            PlaceLocalHandle.addPlace[PlaceTempData](placeTempData, sparePlace, 
                            		()=>new PlaceTempData(snapshots,tmpPlace0CheckpointTimes, tmpPlace0CheckpointAgreeTimes, 
                            				tmpPlace0RestoreTimes, tmpPlace0RestoreAgreeTimes, tmpPlace0StepTimes, lastCheckIter,
                            				tmpPlace0CommitCount, tmpPlace0ReadOnlySnapshot));
                        }
                        ///////////////////////////////////////////////////////////
                        places = newPG;
                        globalIter = lastCheckIter;
                        remakeRequired = false;
                        restoreRequired = true;
                        remakeTime += Timer.milliTime();
                        remakeCount++;
                        Console.OUT.println("LocalViewResilientExecutor: All remake steps completed successfully ...");
                    } else {
                        throw new UnsupportedOperationException("process failure occurred but no valid checkpoint exists!");
                    }
                }
                
                //to be copied to all places
                val tmpRestoreRequired = restoreRequired;
                val tmpGlobalIter = globalIter;
                val placesCount = places.size();
                finish ateach(Dist.makeUnique(places)) {
                    var localIter:Long = tmpGlobalIter;
                    var localRestoreJustDone:Boolean = false;
                    var localRestoreRequired:Boolean = tmpRestoreRequired;
                    
                    while ( !app.isFinished_local() || localRestoreRequired) {
                    	var stepStartTime:Long = -1; // (-1) is used to differenciate between checkpoint exceptions and step exceptions
                        try{
                        	/**Local Restore Operation**/
                        	if (localRestoreRequired){
                        		checkpointRestoreProtocol_local(RESTORE_OPERATION, app, team, root, placesCount);
                        		localRestoreRequired = false;
                        		localRestoreJustDone = true;
                        	}
                        	
                        	/**Local Checkpointing Operation**/
                        	if (!localRestoreJustDone) {
                                //take new checkpoint only if restore was not done in this iteration
                                if (isResilient && (localIter % itersPerCheckpoint) == 0) {
                                    if (VERBOSE) Console.OUT.println("["+here+"] checkpointing at iter " + localIter);
                                    checkpointRestoreProtocol_local(CHECKPOINT_OPERATION, app, team, root, placesCount);
                                    placeTempData().lastCheckpointIter = localIter;
                                }
                            } else {
                            	localRestoreJustDone = false;
                            }
                        	
                        	if (isResilient && KILL_STEP == localIter && here.id == KILL_STEP_PLACE){
                        		executorKillHere("step_local()");
                        	}

                        	stepStartTime = Timer.milliTime();
                        	if (!implicitStepSynchronization){
                        	    //to sync places & also to detect DPE
                        	    team.barrier();
                        	}
                            app.step_local();
                            placeTempData().stepTimes.add(Timer.milliTime()-stepStartTime);
                            
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
                        failureDetectionTime += Timer.milliTime() - placeTempData().place0KillPlaceTime;
                        failureDetectionCount++;
                        //FIXME: currently we are only able to detect failure detection time only when we kill places
                    }
            	}
            	else
            		throw iterEx;
            }
        }while(remakeRequired || !app.isFinished_local());
        
        val runTime = (Timer.milliTime() - startRunTime);
        
        calculateTimingStatistics();
        
        
        val averageCheckpoint          = avergaMaxMinRails(placeTempData().placeMinCheckpoint,          placeTempData().placeMaxCheckpoint);
        val averageCheckpointAgreement = avergaMaxMinRails(placeTempData().placeMinCheckpointAgreement, placeTempData().placeMaxCheckpointAgreement);
        val averageRestore             = avergaMaxMinRails(placeTempData().placeMinRestore,             placeTempData().placeMaxRestore);
        val averageRestoreAgreement    = avergaMaxMinRails(placeTempData().placeMinRestoreAgreement,    placeTempData().placeMaxRestoreAgreement);
        val averageSteps               = avergaMaxMinRails(placeTempData().placeMinStep,                placeTempData().placeMaxStep);
        
        
        Console.OUT.println("=========Detailed Statistics============");
        Console.OUT.println("Steps-place0:" + railToString(placeTempData().stepTimes.toRail()));
        
        Console.OUT.println("Steps-avg:" + railToString(averageSteps));
    	Console.OUT.println("Steps-min:" + railToString(placeTempData().placeMinStep));
    	Console.OUT.println("Steps-max:" + railToString(placeTempData().placeMaxStep));
    	
    	Console.OUT.println("CheckpointData-avg:" + railToString(averageCheckpoint));
    	Console.OUT.println("CheckpointData-min:" + railToString(placeTempData().placeMinCheckpoint));
    	Console.OUT.println("CheckpointData-max:" + railToString(placeTempData().placeMaxCheckpoint));
    	
    	Console.OUT.println("CheckpointAgree-avg:" + railToString(averageCheckpointAgreement));
    	Console.OUT.println("CheckpointAgree-min:" + railToString(placeTempData().placeMinCheckpointAgreement));
    	Console.OUT.println("CheckpointAgree-max:" + railToString(placeTempData().placeMaxCheckpointAgreement));
    	
    	Console.OUT.println("RestoreData-avg:" + railToString(averageRestore));
    	Console.OUT.println("RestoreData-min:" + railToString(placeTempData().placeMinRestore));
    	Console.OUT.println("RestoreData-max:" + railToString(placeTempData().placeMaxRestore));
    	
    	Console.OUT.println("RestoreAgree-avg:" + railToString(averageRestoreAgreement));
    	Console.OUT.println("RestoreAgree-min:" + railToString(placeTempData().placeMinRestoreAgreement));
    	Console.OUT.println("RestoreAgree-max:" + railToString(placeTempData().placeMaxRestoreAgreement));
    	
        Console.OUT.println("=========Totals by averaging Min/Max statistics============");
        Console.OUT.println("Initialization:"      + applicationInitializationTime);
        Console.OUT.println();
        Console.OUT.println("Steps:"               + railSum(averageSteps));
        Console.OUT.println("   ---AverageSingleStep:" + (railSum(averageSteps)/averageSteps.size) );
        Console.OUT.println();
        Console.OUT.println("CheckpointData:"      + railToString(averageCheckpoint));
        Console.OUT.println("CheckpointAgreement:" + railToString(averageCheckpointAgreement)  );
        Console.OUT.println("   ---TotalCheckpointing:"+ (railSum(averageCheckpoint)+railSum(averageCheckpointAgreement) ) );
  
        Console.OUT.println();
        Console.OUT.println("Failure Detection:"   + failureDetectionTime);
        Console.OUT.println("Remake:"              + remakeTime);
        Console.OUT.println("...........TeamReconstruction:"+reconstructTeamTime);
        
        Console.OUT.println("RestoreData:"         + railSum(averageRestore));
        Console.OUT.println("RestoreAgreement:"    + railSum(averageRestoreAgreement) );
        Console.OUT.println("   ---TotalRecovery:" + (failureDetectionTime + remakeTime + railSum(averageRestore) + railSum(averageRestoreAgreement) ));
   
        Console.OUT.println("=============================");
        Console.OUT.println("Actual RunTime:" + runTime);
        val calcTotal = applicationInitializationTime + 
            railSum(averageSteps) + 
            (railSum(averageCheckpoint)+railSum(averageCheckpointAgreement) ) + 
            (failureDetectionTime + remakeTime + railSum(averageRestore) + railSum(averageRestoreAgreement) );
            
        Console.OUT.println("Calculated RunTime based on Averages:" + calcTotal 
            + "   ---Difference:" + (runTime-calcTotal));
        
        Console.OUT.println("=========Counts============");
        Console.OUT.println("StepCount:"+averageSteps.size);
        Console.OUT.println("CheckpointCount:"+averageCheckpoint.size);
        Console.OUT.println("RestoreCount:"+averageRestore.size);
        Console.OUT.println("RemakeCount:"+remakeCount);
        Console.OUT.println("FailureDetectionCount:"+failureDetectionCount);
        
        if (VERBOSE){
            var str:String = "";
            for (p in places)
                str += p.id + ",";
            Console.OUT.println("List of final survived places are: " + str);            
        }
    }
    
    
    private def calculateTimingStatistics(){
    	finish for (place in places) at(place) async {
            ////// checkpoint times ////////
    		val chkCount = placeTempData().checkpointTimes.size();
    		placeTempData().placeMaxCheckpoint = new Rail[Long](chkCount);
    		placeTempData().placeMinCheckpoint = new Rail[Long](chkCount);
    		val dst1max = placeTempData().placeMaxCheckpoint;
    		val dst1min = placeTempData().placeMinCheckpoint;
    	    team.allreduce(placeTempData().checkpointTimes.toRail(), 0, dst1max, 0, chkCount, Team.MAX);
    	    team.allreduce(placeTempData().checkpointTimes.toRail(), 0, dst1min, 0, chkCount, Team.MIN);
    	    
    	    ////// step times ////////
    	    val stpCount = placeTempData().stepTimes.size();
    		placeTempData().placeMaxStep = new Rail[Long](stpCount);
    		placeTempData().placeMinStep = new Rail[Long](stpCount);
    		val dst2max = placeTempData().placeMaxStep;
    		val dst2min = placeTempData().placeMinStep;
    	    team.allreduce(placeTempData().stepTimes.toRail(), 0, dst2max, 0, stpCount, Team.MAX);
    	    team.allreduce(placeTempData().stepTimes.toRail(), 0, dst2min, 0, stpCount, Team.MIN);
    	    
    	    ////// restore times ////////
    	    val restCount = placeTempData().restoreTimes.size();
    	    placeTempData().placeMaxRestore = new Rail[Long](restCount);
    	    placeTempData().placeMinRestore = new Rail[Long](restCount);
    	    val dst3max = placeTempData().placeMaxRestore;
    	    val dst3min = placeTempData().placeMinRestore;
    	    team.allreduce(placeTempData().restoreTimes.toRail(), 0, dst3max, 0, restCount, Team.MAX);
    	    team.allreduce(placeTempData().restoreTimes.toRail(), 0, dst3min, 0, restCount, Team.MIN);
    	    
    	    ////// checkpoint agreement times ////////
    	    val chkAgreeCount = placeTempData().checkpointAgreementTimes.size();
    		placeTempData().placeMaxCheckpointAgreement = new Rail[Long](chkAgreeCount);
    		placeTempData().placeMinCheckpointAgreement = new Rail[Long](chkAgreeCount);
    		val dst4max = placeTempData().placeMaxCheckpointAgreement;
    		val dst4min = placeTempData().placeMinCheckpointAgreement;
    	    team.allreduce(placeTempData().checkpointAgreementTimes.toRail(), 0, dst4max, 0, chkAgreeCount, Team.MAX);
    	    team.allreduce(placeTempData().checkpointAgreementTimes.toRail(), 0, dst4min, 0, chkAgreeCount, Team.MIN);
    	    
    	    
    	    ////// restore agreement times ////////
    	    val restAgreeCount = placeTempData().restoreAgreementTimes.size();
    		placeTempData().placeMaxRestoreAgreement = new Rail[Long](restAgreeCount);
    		placeTempData().placeMinRestoreAgreement = new Rail[Long](restAgreeCount);
    		val dst5max = placeTempData().placeMaxRestoreAgreement;
    		val dst5min = placeTempData().placeMinRestoreAgreement;
    	    team.allreduce(placeTempData().restoreAgreementTimes.toRail(), 0, dst5max, 0, restAgreeCount, Team.MAX);
    	    team.allreduce(placeTempData().restoreAgreementTimes.toRail(), 0, dst5min, 0, restAgreeCount, Team.MIN);
    	    
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
    private def checkpointRestoreProtocol_local(operation:Long, app:LocalViewResilientIterativeAppOpt, team:Team, root:Place, placesCount:Long){
    	val op:String = (operation==CHECKPOINT_OPERATION)?"Checkpoint":"Restore";
    
        val startOperation = Timer.milliTime();
        val excs = new GrowableRail[CheckedThrowable]();

        var vote:Int = 1N;
        try{
            if (operation == CHECKPOINT_OPERATION)
                app.checkpoint_local(placeTempData().getNextSnapshot(), placeTempData().readOnlySnapshot);
            else
            	app.restore_local(placeTempData().getConsistentSnapshot(), placeTempData().readOnlySnapshot, placeTempData().lastCheckpointIter);
            if (VERBOSE) Console.OUT.println(here+" Succeeded in operation ["+op+"_local]");
        }catch(ex:Exception){
            vote = 0N;
            excs.add(ex);
            Console.OUT.println("["+here+"]  EXCEPTION MESSAGE while "+op+" = " + ex.getMessage());
            ex.printStackTrace();
        }
        
        if (operation == CHECKPOINT_OPERATION)
            placeTempData().checkpointTimes.add(Timer.milliTime() - startOperation);
        else if (operation == RESTORE_OPERATION)
        	placeTempData().restoreTimes.add(Timer.milliTime() - startOperation);
        
        
        if ((operation == CHECKPOINT_OPERATION && KILL_CHECKVOTING_INDEX == placeTempData().checkpointTimes.size() && 
               here.id == KILL_CHECKVOTING_PLACE) || 
               (operation == RESTORE_OPERATION    && KILL_RESTOREVOTING_INDEX == placeTempData().restoreTimes.size() && 
         	   here.id == KILL_RESTOREVOTING_PLACE)) {
            executorKillHere(op);
        }
        
        val startAgree = Timer.milliTime();
        try{
        	if (VERBOSE) Console.OUT.println(here+" Starting agree call in operation ["+op+"]");
        	val success = team.agree(vote);
        	if (success == 1N) {
        		if (VERBOSE) Console.OUT.println(here+" Agreement succeeded in operation ["+op+"]");
        		if (operation == CHECKPOINT_OPERATION){
        		    placeTempData().commit();
                    placeTempData().cancelOtherSnapshot();
        		}
        	}
        	else{
        		//Failure due to a reason other than place failure, will need to abort.
        		throw new Exception("[Fatal Error] Agreement failed in operation ["+op+"]   success = ["+success+"]");
        	}
        }
        catch(agrex:Exception){
        	excs.add(agrex);
        }
        if (operation == CHECKPOINT_OPERATION)
            placeTempData().checkpointAgreementTimes.add(Timer.milliTime() - startAgree);
        else if (operation == RESTORE_OPERATION)
        	placeTempData().restoreAgreementTimes.add(Timer.milliTime() - startAgree);
        
        	
        if (excs.size() > 0){
        	throw new MultipleExceptions(excs);
        }
    }
    
    private def executorKillHere(op:String) {
    	at(Place(0)){
			placeTempData().place0KillPlaceTime = Timer.milliTime();
            Console.OUT.println("[Hammer Log] Time before killing is ["+placeTempData().place0KillPlaceTime+"] ...");
		}
		Console.OUT.println("[Hammer Log] Killing ["+here+"] before "+op+" ...");
		System.killHere();
    }
    
    public def railToString[T](r:Rail[T]):String {
    	var str:String = "";
        for (x in r)
        	str += x + ",";
        return str;
    }
    
    public def railSum(r:Rail[Double]):Double {
    	return RailUtils.reduce(r, (x:Double, y:Double) => x+y, 0.0);
    }
    
    public def avergaMaxMinRails[T](max:Rail[T], min:Rail[T]):Rail[Double] {
        val result = new Rail[Double](max.size);
        for (i in 0..(max.size-1)){
        	result(i) = (max(i) as Double + min(i) as Double)/2.0;
        	result(i) = ((result(i)*100) as Long)/100.0;  //two decimal points only
        }
        return result;
    }
    
}


/*Test commands:
==> Kill place during a step:

simple test  spare-restore:
===========================
X10_NUM_IMMEDIATE_THREADS=1 \
X10_EXIT_BY_SIGKILL=1 \
EXECUTOR_KILL_STEP=15 \
EXECUTOR_KILL_STEP_PLACE=7 \
X10_PLACE_GROUP_RESTORE_MODE=1 \
X10_RESILIENT_MODE=1 \
X10_RESILIENT_STORE_MODE=1 \
mpirun -np 9 -am ft-enable-mpi \
--mca errmgr_rts_hnp_proc_fail_xcast_delay 0 \
--mca orte_base_help_aggregate 0 \
bin/lulesh2.0 -e 1 -k 100 -s 10 -i 50 -p



X10_ASYMMETRIC_IMMEDIATE_THREAD=true \


simple test  non resilient:
===========================
mpirun -np 8 \
--mca orte_base_help_aggregate 0 \
bin/lulesh2.0 -s 10 -i 50 -p


mpirun -np 64 -am ft-enable-mpi \
--mca errmgr_rts_hnp_proc_fail_xcast_delay 0 \
--mca orte_base_help_aggregate 0 \
bin/lulesh2.0 -s 10 -i 50 -p




X10_EXIT_BY_SIGKILL=1 \
X10RT_MPI_DEBUG_PRINT=0 \
X10_RESILIENT_VERBOSE=0 \
EXECUTOR_KILL_STEP=15 \
EXECUTOR_KILL_STEP_PLACE=7 \
X10_RESILIENT_STORE_VERBOSE=0 \
X10_TEAM_DEBUG_INTERNALS=1 \
X10_PLACE_GROUP_RESTORE_MODE=1 \
EXECUTOR_DEBUG=1 \
X10_RESILIENT_MODE=1 \
X10_ASYMMETRIC_IMMEDIATE_THREAD=true \
mpirun -np 9 -am ft-enable-mpi \
--mca errmgr_rts_hnp_proc_fail_xcast_delay 0 \
--mca orte_base_help_aggregate 0 \
bin/lulesh2.0 -e 1 -k 10 -s 10 -i 50 -p


X10RT_MPI_PROBE_SLEEP_MICROSECONDS=10000 \
X10RT_MPI_DEBUG_PROBE_PLACE=80 \
X10RT_MPI_DEBUG_SEND_RECV_PLACE=8 \
X10_RESILIENT_VERBOSE=0 \
X10RT_MPI_DEBUG_PRINT=0 \
EXECUTOR_KILL_STEP=15 \
EXECUTOR_KILL_STEP_PLACE=2 \
EXECUTOR_KILL_RESTOREVOTING=0 \
EXECUTOR_KILL_RESTOREVOTING_PLACE=6 \
X10_RESILIENT_STORE_VERBOSE=1 \
X10_TEAM_DEBUG_INTERNALS=1 \
X10_PLACE_GROUP_RESTORE_MODE=1 \
X10_ASYMMETRIC_IMMEDIATE_THREAD=true \
X10_NUM_IMMEDIATE_THREADS=2 \
EXECUTOR_DEBUG=1 \
X10_RESILIENT_MODE=1 \
mpirun -np 10 -am ft-enable-mpi \
--mca errmgr_rts_hnp_proc_fail_xcast_delay 0 \
bin/lulesh2.0 -e 2 -k 10 -s 10 -i 50 -p




X10_EXIT_BY_SIGKILL=1 \
EXECUTOR_KILL_STEP=15 \
EXECUTOR_KILL_STEP_PLACE=7 \
X10_PLACE_GROUP_RESTORE_MODE=1 \
X10_RESILIENT_MODE=1 \
X10_ASYMMETRIC_IMMEDIATE_THREAD=true \
mpirun -np 9 -am ft-enable-mpi \
--mca errmgr_rts_hnp_proc_fail_xcast_delay 0 \
--mca orte_base_help_aggregate 0 \
bin/lulesh2.0 -e 1 -k 10 -s 10 -i 50 -p


==> Kill place before checkpoint voting:

EXECUTOR_KILL_CHECKVOTING=1 \
EXECUTOR_KILL_CHECKVOTING_PLACE=3 \
EXECUTOR_KILL_STEP=5 \
EXECUTOR_KILL_STEP_PLACE=3 \
X10_RESILIENT_STORE_VERBOSE=1 \
X10_TEAM_DEBUG_INTERNALS=0 \
X10_PLACE_GROUP_RESTORE_MODE=1 \
EXECUTOR_DEBUG=1 \
X10_RESILIENT_MODE=1 \
mpirun -np 9 -am ft-enable-mpi \
--mca errmgr_rts_hnp_proc_fail_xcast_delay 0 \
bin/lulesh2.0 -e 1 -k 3 -s 10 -i 10 -p

*/
