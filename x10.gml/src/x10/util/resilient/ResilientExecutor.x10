/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014-2015.
 *  (C) Copyright Sara Salem Hamouda 2014-2015.
 */

package x10.util.resilient;

import x10.util.Timer;
import x10.util.Random;
import x10.matrix.util.PlaceGroupBuilder;
import x10.util.resilient.PlaceHammer;

public class ResilientExecutor {
    private val store:ResilientStoreForApp;
    private var places:PlaceGroup;
    private val itersPerCheckpoint:Long;
    private var isResilient:Boolean = false;
    private val VERBOSE = false;

    private var runTime:Long = 0;
    private var checkpointTime:Long = 0;
    private var checkpointCount:Long = 0;
    private var restoreTime:Long = 0;
    private var restoreCount:Long = 0;
    private var stepExecTime:Long = 0;
    private var stepExecCount:Long = 0;
    private var hammer:PlaceHammer = null;
    
    private var restoreJustDone:Boolean = false;
    
    public def this(itersPerCheckpoint:Long, places:PlaceGroup) {
        this.places = places;
        this.itersPerCheckpoint = itersPerCheckpoint;
        if (itersPerCheckpoint > 0 && x10.xrx.Runtime.RESILIENT_MODE > 0) {
            isResilient = true;
            store = new ResilientStoreForApp();
            val hammerConfigFile = System.getenv("X10_GML_HAMMER_FILE");
            if (hammerConfigFile != null && !hammerConfigFile.equals("")){                
                hammer = PlaceHammer.make(hammerConfigFile);
            }
        } else {
            store = null;
        }
    }

    public def run(app:ResilientIterativeApp) {
        val startRun = Timer.milliTime();
        var restoreRequired:Boolean = false;
        var iter:Long = 0;
        var lastCheckpointIter:Long = -1;

        if (isTimerHammerActive())
            hammer.startTimerHammer();
        
        while (!app.isFinished()) {
            try {
                if (restoreRequired) {
                    if (lastCheckpointIter > -1) {
                        val startRestore = Timer.milliTime();
                        val newPG = PlaceGroupBuilder.createRestorePlaceGroup(places);
                        Console.OUT.println("restoring at iter " + lastCheckpointIter);

                        if (isIterativeHammerActive()){
                            val tmpIter = iter;
                            async hammer.checkKillRestore(tmpIter);
                        }
                        
                        app.restore(newPG, store, lastCheckpointIter);
                        
                        iter = lastCheckpointIter;
                        places = newPG;
                        if (VERBOSE){
                            Console.OUT.println("Used Places After Restore ...");
                            for (x in places)
                                Console.OUT.println(x);
                        }
                        restoreRequired = false;
                        restoreTime += (Timer.milliTime() - startRestore);
                        restoreCount++;
                        
                        restoreJustDone = true;
                    } else {
                        throw new UnsupportedOperationException("failure occurred at iter "
                            + iter + " but no valid checkpoint exists!");
                    }
                }

                if (isIterativeHammerActive()){
                    val tmpIter = iter;
                    async hammer.checkKillStep(tmpIter);
                }

                if (!restoreJustDone) {
                    //take new checkpoint only if restore was not done in this iteration
                    if (isResilient && (iter % itersPerCheckpoint) == 0) {
                        if (VERBOSE) Console.OUT.println("checkpointing at iter " + iter);
                        try {
                            val startCheckpoint = Timer.milliTime();
                
                            if (isIterativeHammerActive()) {
                                val tmpIter = iter;
                                async hammer.checkKillCheckpoint(tmpIter);
                            }
                
                            app.checkpoint(store);
                
                            lastCheckpointIter = iter;
                            checkpointTime += (Timer.milliTime() - startCheckpoint);
                            checkpointCount++;
                        } catch (ex:Exception) {
                            processCheckpointException(ex);
                            restoreRequired = true;
                        }
                    }
                } else {
                    restoreJustDone = false;
                }

                val startStep = Timer.milliTime();
                app.step();
                stepExecTime += (Timer.milliTime() - startStep);
                stepExecCount++;

                iter++;
                
            } catch (iterEx:Exception) {
                processIterationException(iterEx);
                restoreRequired = true;
            }
        }
        runTime = (Timer.milliTime() - startRun);
        if (isTimerHammerActive())
            hammer.stopTimerHammer();
        Console.OUT.println("ResilientExecutor completed:checkpointTime:"+checkpointTime+":restoreTime:"+restoreTime+":stepsTime:"+stepExecTime+":AllTime:"+runTime+":checkpointCount:"+checkpointCount+":restoreCount:"+restoreCount+":stepsCount:"+stepExecCount);
    }
    
    
    private def processCheckpointException(ex:Exception){
        if (ex instanceof DeadPlaceException) {
            val deadExp = ex as DeadPlaceException; 
            Console.OUT.println("place failure during checkpoint: cancelling snapshot!");
            deadExp.printStackTrace();
            store.cancelSnapshot();            
        }
        else if (ex instanceof MultipleExceptions) {
            val mulExp = ex as MultipleExceptions;
            val filtered = mulExp.filterExceptionsOfType[DeadPlaceException]();
            if (filtered != null) throw filtered;
            Console.OUT.println("place failure (MultipleExceptions) during checkpoint: cancelling snapshot!");
            val deadPlaceExceptions = mulExp.getExceptionsOfType[DeadPlaceException]();
            for (dpe in deadPlaceExceptions) {
                dpe.printStackTrace();
            }
            store.cancelSnapshot();            
        }
        else 
            throw ex;
    }
    
    private def processIterationException(ex:Exception) {
        if (ex instanceof DeadPlaceException) {
            val dpe = ex as DeadPlaceException;            
            if (!isResilient) {
                throw dpe;
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
    
    private def isTimerHammerActive() = (hammer != null && hammer.isTimerHammer());
    private def isIterativeHammerActive() = (hammer != null && hammer.isIterativeHammer());
}
