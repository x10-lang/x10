/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;
import x10.regionarray.*;
import x10.util.ArrayList;

/**
 * Testing block dist.
 *
 * Randomly generate block dists and check
 * index-to-place mapping for conformance with spec.
 *
 * The dist block(R, Q) distributes the elements of R (in
 * order) over the set of places Q in blocks along one axis as follows
 * Let p equal |R.axis| div N and q equal |R.axis| mod N, where N is the size of Q. The first
 * q places get successive blocks of size (p + 1) and the remaining
 * places get blocks of size p.
 *
 * This tests the block distribution with a given random subset of places,
 * not all places
 *
 * @author kemal 5/2005
 */
public class BlockDistWithPlaceGroup extends x10Test {

    public static COUNT = 200n;
    public static L = 5n;

    public def run(): boolean = {
        var passed:Boolean = true;
        for (tries in 1n..COUNT) {
            val lb1: int = ranInt(-L, L);
            val lb2: int = ranInt(-L, L);
            val ub1: int = ranInt(lb1, L);
            val ub2: int = ranInt(lb2, L);
            val R = Region.make(lb1..ub1, lb2..ub2);
            val totalPoints = (ub1-lb1+1n)*(ub2-lb2+1n);
            val axisPoints = ub1-lb1+1n;
            val placeGroup = createRandPlaceGroup();
            val np = placeGroup.numPlaces();

            val DBlock = Dist.makeBlock(R, 0n, placeGroup);
            val p = axisPoints/np;
            val q = axisPoints%np;
            var offsWithinPlace:long = 0L;
            var pn: long = 0L;

            for (i in lb1..ub1) {
                for (j in lb2..ub2) {
	            if (!DBlock(i, j).equals(placeGroup(pn))) {
                        Console.OUT.println("FAIL: ");
                        Console.OUT.println("\tap = " + axisPoints + " lb1 = "+lb1+" ub1 = "+ub1+" lb2 = "+lb2+" ub2 = "+ub2+" totalPoints = "+totalPoints+" p = "+p+" q = "+q);
                        Console.OUT.println("\tplaceNum = "+placeGroup(pn)+" offsWithinPlace = "+offsWithinPlace+" i = "+i+" j = "+j+" DBlock[i,j] = "+DBlock(i,j).id);
                        passed = false;
                    }
                }
                offsWithinPlace++;
                if (offsWithinPlace == (p + (pn < q ? 1 : 0))) {
                    //time to go to next place
                    offsWithinPlace = 0;
                    pn++;
                }
            }
        }
        return passed;
    }

    /**
     * Create a random, non-empty subset of the places
     */
    def createRandPlaceGroup():PlaceGroup {
        val places = new ArrayList[Place]();
        do {
            val THRESH: int = ranInt(10n, 90n);
            for (p in Place.places()) {
                val x:int = ranInt(0n, 99n);
                if (x >= THRESH) {
                    places.add(p);
                }
            }
        } while (places.size() == 0L);
        return new SparsePlaceGroup(places.toRail());
    }

    public static def main(var args: Rail[String]): void = {
        new BlockDistWithPlaceGroup().execute();
    }
}
