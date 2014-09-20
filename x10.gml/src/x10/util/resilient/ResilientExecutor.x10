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

public class ResilientExecutor {
    private val store:ResilientStoreForApp;
    private var places:PlaceGroup;
    private val killIter:Long = 5;
    private val itersPerCheckpoint:Long;
    private var isResilient:Boolean = false;

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
        var restoreRequired:Boolean = false;
        var simulatePlaceDeathDone:Boolean = false;

        var iter:Long = 0;
        var lastCheckpointIter:Long = -1;

        while (!app.isFinished()) {
            if (restoreRequired) {
                val newPG = places.filterDeadPlaces();

                Console.OUT.println("The place group after filtering the dead ...");
                for (p in newPG)
                    Console.OUT.println(p);

                app.restore(newPG, store);
                iter = lastCheckpointIter;

                restoreRequired = false;
            }

            try {
                // TODO use an external 'hammer' to kill places
                if (isResilient && !simulatePlaceDeathDone 
                 && iter == killIter && places.size() > 1) {
                    simulatePlaceDeathDone = true;
                    at (places(1)) async {
                        Console.OUT.println("killing " + here);
                        Console.OUT.flush();
                        System.killHere();
                    }
                }

                app.step();

                iter++;

                if (isResilient && (iter % itersPerCheckpoint) == 0) {
                    app.checkpoint(store);
                    lastCheckpointIter = iter;
                }
            } catch (deadExp:DeadPlaceException) {
                deadExp.printStackTrace();
                if (!isResilient)
                    throw deadExp;
                else
                    restoreRequired = true;
            } catch (mulExp:MultipleExceptions) {
                mulExp.printStackTrace();
                val deadPlaceExceptions = (mulExp as MultipleExceptions).getExceptionsOfType[DeadPlaceException]();
                if (isResilient & deadPlaceExceptions.size > 0)
                    restoreRequired = true;
                else
                    throw mulExp;
            }
        }
    }
}
