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

import x10.util.Team;

// NUM_PLACES: 7
// RESILIENT_X10_ONLY

/**
 * Test correct handling of place failure by Team.barrier
 */
public class TestBarrier(victim:Long) extends x10Test {

    public def run(): Boolean {
        val successGR = new GlobalRef[Cell[Boolean]](new Cell[Boolean](true));
        try {
            finish for (place in Place.places()) at (place) async {
                Team.WORLD.barrier(); // warm up comms
                try {
                    finish {
                        if (here.id == victim) {
                            // kill this place at some point before the start of the second barrier
                            async System.killHere();
                            System.sleep(2000); // give async a really good chance to kill us before we hit the barrier
                        }
                        Team.WORLD.barrier();
                    }
                    Team.WORLD.barrier(); // need two consecutive barriers guarantee failure detection

                    Console.ERR.println(here + " passed second barrier!");
                    at (successGR) {
                        val success = successGR();
                        success() = false;
                    }
                } catch(ex:Exception) {
                    // we expect one of the barriers to throw an exception, because we killed a place
                }
            }
        } catch(ex:Exception) {
            // we expect an exception here, because we killed a place
        }

        val success = successGR();
        return success.value;
    }

    public static def main(var args: Rail[String]): void {
        var victim:Long = Place.numPlaces()-1; 
        if (args.size > 0) {
            victim = Long.parseLong(args(0));
        }
        new TestBarrier(victim).execute();
    }
}
