/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2016.
 */

import harness.x10Test;
import x10.compiler.Pragma;
import x10.regionarray.Dist;
import x10.util.concurrent.AtomicBoolean;

// NUM_PLACES: 3
// RESILIENT_X10_ONLY

/**
 * Test errors in finish-at-async handled correctly at all places
 */
public class TestFinishAtAsync(victim:Long) extends x10Test  {

    val gatesPlh = PlaceLocalHandle.make[Rail[AtomicBoolean]](Place.places(), () => new Rail[AtomicBoolean](Place.numPlaces(), (Long) => new AtomicBoolean(false)));

    public def run() {
        if (Place.numPlaces() < 3) {
            Console.OUT.println("3 places are necessary for this test");
            return false;
        }
        try {
            finish ateach(place in Dist.makeUnique()) {
                val sourceId = here.id;
                async {
                    System.threadSleep(1000);
                    if (here.id == victim) System.killHere();
                    // open the gates
                    val gates = gatesPlh();
                    for (i in 0..(gates.size-1)) {
                        gates(i).set(true);
                    }
                }
                for (targetPlace in Place.places()) {
                    if (targetPlace != here) {
                        try {
                            //Console.OUT.println(here + " about to send remote async to " + targetPlace);
                            @Pragma(Pragma.FINISH_ASYNC) finish at (targetPlace) async {
                                //Console.OUT.println(here + " waiting for gate " + sourceId);
                                val gates = gatesPlh();
                                while (gates(sourceId).get() == false) {
                                    System.threadSleep(0);
                                }
                                //Console.OUT.println(here + " passed through gate " + sourceId);
                            }
                            //Console.OUT.println(here + " completed remote async to " + targetPlace);
                        } catch (me:MultipleExceptions) {
                            val dper = me.getExceptionsOfType[DeadPlaceException]();
                            if (dper.size > 0) {
                                //Console.OUT.println(here + " inner caught DPE: " + dper(0));
                            } else {
                                throw me;
                            }
                        }
                    }
                }
            }
        } catch (me:MultipleExceptions) {
            val dper = me.getExceptionsOfType[DeadPlaceException]();
            if (dper.size > 0) {
                //Console.OUT.println("outer caught DPE: " + dper(0));
            } else {
                throw me;
            }
        } 

	    return true;
    }

    public static def main(args:Rail[String]) {
        var victim:Long = Place.numPlaces()-1; 
        if (args.size > 0) {
            victim = Long.parseLong(args(0));
        }
	    new TestFinishAtAsync(victim).execute();
    }
}
