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

public class TestBlockBlockDistGhostManager extends x10Test {

    public static def testGhostRegion2(periodic:Boolean):Boolean {
        val r = Region.makeRectangular(0..10, 1..20);
        val bd = Dist.makeBlockBlock(r, 0, 1);

        val ghostWidth = 1;
        val ghostMgr = bd.getLocalGhostManager(ghostWidth, periodic);

        finish for(place in bd.places()) at(place) {
            val localRegion = bd(here);
            val ghostRegion = ghostMgr.getGhostRegion(here);
            var min0:Long = localRegion.min(0)-ghostWidth;
            var max0:Long = localRegion.max(0)+ghostWidth;
            var min1:Long = localRegion.min(1)-ghostWidth;
            var max1:Long = localRegion.max(1)+ghostWidth;
            if (!periodic) {
                min0 = Math.max(min0, bd.region.min(0));
                max0 = Math.min(max0, bd.region.max(0));
                min1 = Math.max(min1, bd.region.min(1));
                max1 = Math.min(max1, bd.region.max(1));
            }
            chk(ghostRegion.min(0) == min0);
            chk(ghostRegion.max(0) == max0);
            chk(ghostRegion.min(1) == min1);
            chk(ghostRegion.max(1) == max1);
        }

        return true;
    }

    public static def testGhostRegion3(periodic:Boolean):Boolean {
        val r = Region.makeRectangular(1..8, 0..7, 3..15);
        val bd = Dist.makeBlockBlock(r, 0, 1);

        val ghostWidth = 1;
        val ghostMgr = bd.getLocalGhostManager(ghostWidth, periodic);

        finish for(place in bd.places()) at(place) {
            val localRegion = bd(here);
            val ghostRegion = ghostMgr.getGhostRegion(here);
            var min0:Long = localRegion.min(0)-ghostWidth;
            var max0:Long = localRegion.max(0)+ghostWidth;
            var min1:Long = localRegion.min(1)-ghostWidth;
            var max1:Long = localRegion.max(1)+ghostWidth;
            var min2:Long = localRegion.min(2)-ghostWidth;
            var max2:Long = localRegion.max(2)+ghostWidth;
            if (!periodic) {
                min0 = Math.max(min0, bd.region.min(0));
                max0 = Math.min(max0, bd.region.max(0));
                min1 = Math.max(min1, bd.region.min(1));
                max1 = Math.min(max1, bd.region.max(1));
            }
            min2 = Math.max(min2, bd.region.min(2));
            max2 = Math.min(max2, bd.region.max(2));
            chk(ghostRegion.min(0) == min0);
            chk(ghostRegion.max(0) == max0);
            chk(ghostRegion.min(1) == min1);
            chk(ghostRegion.max(1) == max1);
            chk(ghostRegion.min(2) == min2);
            chk(ghostRegion.max(2) == max2);
        }

        return true;
    }

    public def testGetNeighbors2(periodic:Boolean):Boolean {
        val r = Region.makeRectangular(4..17, 2..12);
        val bd = Dist.makeBlockBlock(r, 0, 1);

        finish for(place in bd.places()) at(place) {
            val ghostWidth = 1;
            val ghostMgr = bd.getLocalGhostManager(ghostWidth, periodic);
            val neighbors = ghostMgr.getNeighbors();
            val localRegion = bd(here);
            for (neighbor in neighbors) {
                val ghostRegion = ghostMgr.getGhostRegion(neighbor);
                val overlap = localRegion.intersection(ghostRegion);
                chk(!overlap.isEmpty());
            }
        }
        return true;
    }

    public def testGetNeighbors3(periodic:Boolean):Boolean {
        val r = Region.makeRectangular(5..15, 0..19, 0..9);
        val bd = Dist.makeBlockBlock(r, 1, 2);

        finish for(place in bd.places()) at(place) {
            val ghostWidth = 1;
            val ghostMgr = bd.getLocalGhostManager(ghostWidth, periodic);
            val neighbors = ghostMgr.getNeighbors();
            val localRegion = bd(here);
            for (neighbor in neighbors) {
                val ghostRegion = ghostMgr.getGhostRegion(neighbor);
                val overlap = localRegion.intersection(ghostRegion);
                chk(!overlap.isEmpty());
            }
        }
        return true;
    }

    public def run() {
        var success:Boolean = true;

        success &= testGhostRegion2(false);
        success &= testGhostRegion2(true);
        success &= testGhostRegion3(false);
        success &= testGhostRegion3(true);
        success &= testGetNeighbors2(false);
        success &= testGetNeighbors2(true);
        success &= testGetNeighbors3(false);
        success &= testGetNeighbors3(true);
       
        return success;    
    }

    public static def main(var args: Rail[String]) {
        new TestBlockBlockDistGhostManager().execute();
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
