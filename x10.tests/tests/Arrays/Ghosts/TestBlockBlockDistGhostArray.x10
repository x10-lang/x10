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

// NUM_PLACES: 6

import harness.x10Test;

import x10.regionarray.Dist;
import x10.regionarray.DistArray;
import x10.regionarray.PeriodicBoundaryConditions;
import x10.regionarray.Region;

public class TestBlockBlockDistGhostArray extends x10Test {
    static val DUMMY_VALUE = -1.0;

    public static def testExchangeGhosts(periodic:Boolean):Boolean {
        val r = Region.makeRectangular(1..4, 1..3);
        val bbd = Dist.makeBlockBlock(r, 0, 1);
        val ghostWidth = 1;
        val a = DistArray.make[Double](bbd, DUMMY_VALUE, ghostWidth, periodic);

        finish for (place in a.dist.places()) at(place) async {
            // every place sets each point in its locally held region to its place ID
            val regionHere = bbd(here);
            for (p in regionHere) {
                a(p) = here.id as Double;
            }

            val ghostRegion = a.localRegion();
            for (p in ghostRegion) {
                if (regionHere.contains(p)) {
                    chk(a.getPeriodic(p) == here.id as Double);
                } else {
                    chk(a.getPeriodic(p) == DUMMY_VALUE);
                }
            }

            a.sendGhostsLocal();
            a.waitForGhostsLocal();

            // check that ghosts from other places were correctly received as source place IDs
            for (p in ghostRegion) {
                val sourcePlaceId = bbd(PeriodicBoundaryConditions.wrapPeriodic(p, bbd.region)).id;
                chk(a.getPeriodic(p) == sourcePlaceId as Double);
            }
        }

        return true;
    }

    public def run() {
        var success:Boolean = true;

        success &= testExchangeGhosts(false);
        success &= testExchangeGhosts(true);
       
        return success;    
    }

    public static def main(var args: Rail[String]) {
        new TestBlockBlockDistGhostArray().execute();
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
