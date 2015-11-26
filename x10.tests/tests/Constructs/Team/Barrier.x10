/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2015.
 */

import harness.x10Test;
import x10.util.Team;

// NUM_PLACES: 17

/**
 * Unit test for Team.barrier
 */
public class Barrier extends x10Test {

    public def run(): Boolean {
        Console.OUT.println("Doing barrier for World ("+Place.numPlaces()+" places)");
        finish for (p in Place.places()) {
            at(p) async {
                for (i in 1..3) {
                    Team.WORLD.barrier();
                }
            }
        }

        return true;
    }

    public static def main(args: Rail[String]) {
        new Barrier().execute();
    }
}
