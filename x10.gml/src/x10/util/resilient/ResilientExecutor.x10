/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014.
 *  (C) Copyright Sara Salem Hamouda 2014.
 */
package x10.util.resilient;

import x10.util.Timer;
import x10.util.Random;

public class ResilientExecutor {
    private val store:ResilientStoreForApp;
    private var places:PlaceGroup;
    private val itersPerCheckpoint:Long;
    private var isResilient:Boolean = false;
    private val VERBOSE = true;

    private var runTime:Long = 0;
    private var checkpointTime:Long = 0;
    private var checkpointCount:Long = 0;
    private var restoreTime:Long = 0;
    private var restoreCount:Long = 0;
    private var stepExecTime:Long = 0;
    private var stepExecCount:Long = 0;
    
    public def this(itersPerCheckpoint:Long) {
        places = Place.places();
        this.itersPerCheckpoint = itersPerCheckpoint;
        if (itersPerCheckpoint > 0 && Runtime.RESILIENT_MODE > 0) {
            isResilient = true;
            store = new ResilientStoreForApp();
        } else {
            store = null;
        }
    }

    public def run(app:ResilientIterativeApp) {
        val startRun = Timer.milliTime();

        //val rand = new Random(System.nanoTime());
        val killIter:Long = app.getMaxIterations()/2; //rand.nextLong(app.getMaxIterations()-1) + 1;
        val killPlaceIndex:Long = places.size()/2; //rand.nextLong(places.size()-1) + 1;
        
        var restoreRequired:Boolean = false;
        var simulatePlaceDeathDone:Boolean = false;

        var iter:Long = 0;
        var lastCheckpointIter:Long = -1;

        // Checkpoint before first iter
        if (isResilient){            
            val startFirstCheckpoint = Timer.milliTime();
            app.checkpoint(store);
            lastCheckpointIter = iter;
            checkpointTime += (Timer.milliTime() - startFirstCheckpoint);
            checkpointCount++;            
        }

        while (!app.isFinished()) {
            try {
                if (restoreRequired) {
                    if (lastCheckpointIter > -1) {
                        val startRestore = Timer.milliTime();
                        val newPG = places.filterDeadPlaces();

                        if (VERBOSE) Console.OUT.println("restoring at iter " + lastCheckpointIter);

                        app.restore(newPG, store, lastCheckpointIter);
                        iter = lastCheckpointIter;

                        restoreRequired = false;
                        restoreTime += (Timer.milliTime() - startRestore);
                        restoreCount++;
                    } else {
                        throw new UnsupportedOperationException("failure occurred at iter "
                            + iter + " but no valid checkpoint exists!");
                    }
                }


                // TODO use an external 'hammer' to kill places
                if (isResilient && !simulatePlaceDeathDone 
                 && iter == killIter && places.size() > 1) {
                    simulatePlaceDeathDone = true;
                    at (places(killPlaceIndex)) async {
                        Console.OUT.println("at iteration " + killIter + " killing " + here);
                        Console.OUT.flush();
                        System.killHere();
                    }
                }
                val startStep = Timer.milliTime();
                app.step();
                stepExecTime += (Timer.milliTime() - startStep);
                stepExecCount++;

                iter++;

                if (isResilient && (iter % itersPerCheckpoint) == 0) {
                    if (VERBOSE) Console.OUT.println("checkpointing at iter " + iter);
                    try {
                        val startCheckpoint = Timer.milliTime();
                        app.checkpoint(store);
                        lastCheckpointIter = iter;
                        checkpointTime += (Timer.milliTime() - startCheckpoint);
                        checkpointCount++;
                    } catch (deadExp:DeadPlaceException) {
                        Console.OUT.println("place failure during checkpoint: cancelling snapshot!");
                        deadExp.printStackTrace();
                        store.cancelSnapshot();
                        restoreRequired = true;
                    } catch (mulExp:MultipleExceptions) {
                        val filtered = mulExp.filterExceptionsOfType[DeadPlaceException]();
                        if (filtered != null) throw filtered;
                        Console.OUT.println("place failure (MultipleExceptions) during checkpoint: cancelling snapshot!");
                        val deadPlaceExceptions = mulExp.getExceptionsOfType[DeadPlaceException]();
                        for (dpe in deadPlaceExceptions) {
                            dpe.printStackTrace();
                        }
                        store.cancelSnapshot();
                        restoreRequired = true;
                    }
                }
            } catch (dpe:DeadPlaceException) {
                dpe.printStackTrace();
                if (!isResilient) {
                    throw dpe;
                } else {
                    restoreRequired = true;
                }
            } catch (mulExp:MultipleExceptions) {
                if (isResilient) {
                    val filtered = mulExp.filterExceptionsOfType[DeadPlaceException]();
                    if (filtered != null) throw filtered;
                    val deadPlaceExceptions = mulExp.getExceptionsOfType[DeadPlaceException]();
                    for (dpe in deadPlaceExceptions) {
                        dpe.printStackTrace();
                    }
                    restoreRequired = true;
                } else {
                    throw mulExp;
                }
            }
        }
        runTime = (Timer.milliTime() - startRun);
        Console.OUT.println("ResilientExecutor completed:checkpointTime:"+checkpointTime+":restoreTime:"+restoreTime+":stepsTime:"+stepExecTime+":AllTime:"+runTime+":checkpointCount:"+checkpointCount+":restoreCount:"+restoreCount+":stepsCount:"+stepExecCount);
    }
}
