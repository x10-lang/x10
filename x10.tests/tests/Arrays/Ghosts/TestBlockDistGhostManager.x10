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

// NUM_PLACES: 4

import harness.x10Test;

import x10.regionarray.Dist;
import x10.regionarray.Region;

public class TestBlockDistGhostManager extends x10Test {

    public static def testGhostRegion(periodic:Boolean):Boolean {
        val r = Region.makeRectangular(0..10, 1..20);
        val bd = Dist.makeBlock(r, 0);

        finish for(place in bd.places()) at(place) {
            val ghostWidth = 1;
            val ghostMgr = bd.getLocalGhostManager(ghostWidth, periodic);
            val localRegion = bd(here);
            val ghostRegion = ghostMgr.getGhostRegion(here);
            if (periodic) {
                chk(ghostRegion.min(0) == localRegion.min(0)-ghostWidth);
                chk(ghostRegion.max(0) == localRegion.max(0)+ghostWidth);
            } else {
                chk(ghostRegion.min(0) == Math.max(bd.region.min(0), localRegion.min(0)-ghostWidth));
                chk(ghostRegion.max(0) == Math.min(bd.region.max(0), localRegion.max(0)+ghostWidth));
            }
            chk(ghostRegion.min(1) == localRegion.min(1));
            chk(ghostRegion.max(1) == localRegion.max(1));
        }

        return true;
    }

    public def testGetNeighbors(periodic:Boolean):Boolean {
        val r = Region.makeRectangular(5..15, 0..9);
        val bd = Dist.makeBlock(r, 1);

        finish for(place in bd.places()) at(place) {
            val ghostWidth = 1;
            val ghostMgr = bd.getLocalGhostManager(ghostWidth, periodic);
            val neighbors = ghostMgr.getNeighbors();
            val places = bd.places();
            val numPlaces = places.size;
            if (places.indexOf(here) == 0) {
                // leftmost place
                if (periodic) {
                    chk(neighbors(0) == places(numPlaces-1));
                } else {
                    chk(neighbors(0) == here);
                }
                chk(neighbors(1) == places(1%numPlaces));
            } else if (places.indexOf(here) == numPlaces-1) {
                // rightmost place
                chk(neighbors(0) == places(numPlaces-2));
                if (periodic) {
                    chk(neighbors(1) == places(0));
                } else {
                    chk(neighbors(1) == here);
                }
            } else {
                chk(neighbors(0) == places(places.indexOf(here)-1));
                chk(neighbors(1) == places(places.indexOf(here)+1));
            }
        }
        return true;
    }

    public def run() {
        var success:Boolean = true;

        success &= testGhostRegion(false);
        success &= testGhostRegion(true);
        success &= testGetNeighbors(false);
        success &= testGetNeighbors(true);
       
        return success;    
    }

    public static def main(var args: Rail[String]) {
        new TestBlockDistGhostManager().execute();
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
