/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2011.
 */

import harness.x10Test;
import x10.regionarray.*;

/**
 * Test of DistArray.getLocalPortion(), which works iff
 * the local portion of a DistArray is a dense rectangular region.
 */
public class LocalPortion extends x10Test {

    public def run(): boolean = {
        val uniqueDistArray = DistArray.make[Int](Dist.makeUnique());
        finish for(place in uniqueDistArray.dist.places()) at(place) {
            val placeId = here.id;
            val localPortion = uniqueDistArray.getLocalPortion();
            chk(localPortion.region.equals(Region.make(placeId,placeId)));
        }

        val rectRegion = Region.make(1..10, 1..10);
        val constantDistArray = DistArray.make[Int](Dist.makeConstant(rectRegion));
        finish for(place in constantDistArray.dist.places()) at(place) {
            val localPortion = constantDistArray.getLocalPortion();
            val r1 = constantDistArray.dist(here);
            val r2 = localPortion.region;
            chk(r1.equals(r2));
        }

        val blockDistArray = DistArray.make[Int](Dist.makeBlock(rectRegion, 0));
        finish for(place in blockDistArray.dist.places()) at(place) {
            val localPortion = blockDistArray.getLocalPortion();
            val r1 = blockDistArray.dist(here);
            val r2 = localPortion.region;
            chk(r1.equals(r2));
        }

        val blockBlockDistArray = DistArray.make[Int](Dist.makeBlockBlock(rectRegion, 0, 1));
        finish for(place in blockBlockDistArray.dist.places()) at(place) {
            val localPortion = blockBlockDistArray.getLocalPortion();
            val r1 = blockBlockDistArray.dist(here);
            val r2 = localPortion.region;
            chk(r1.equals(r2));
        }

        val triRegion = Region.makeUpperTriangular(4);
        val constantTriDistArray = DistArray.make[Int](Dist.makeConstant(triRegion));
        try {
            val localPortion = constantTriDistArray.getLocalPortion();
            throw new Exception("getLocalPortion for triangular region should throw exception!");
        } catch (e: UnsupportedOperationException) {
            // correct operation
        }

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new LocalPortion().execute();
    }
}
