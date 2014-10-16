/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014.
 */

import harness.x10Test;
import x10.regionarray.*;

/**
 * Test of x10.regionarray.DistArray.getPatch().
 * @see similar test in ../Array for x10.array.DistArray
 */
public class DistArrayPatch extends x10Test {

    public def run():Boolean {
        val block1DistArray = DistArray.make[Double](Dist.makeBlock(Region.make(4..123)), ([i]:Point(1))=>i as Double);
        testGetPatch(block1DistArray);

        val block2DistArray = DistArray.make[Double](Dist.makeBlock(Region.make([1..11,2..13])), ([i,j]:Point(2))=>(10*i+j) as Double);
        testGetPatch(block2DistArray);

        val blockBlock2DistArray = DistArray.make[Double](Dist.makeBlockBlock(Region.make([3..13,1..12])), ([i,j]:Point(2))=>(10*i+j) as Double);
        testGetPatch(blockBlock2DistArray);

        val block3DistArray = DistArray.make[Double](Dist.makeBlock(Region.make([1..4,0..3,2..7])), ([i,j,k]:Point(3))=>(100*i+10*j+k) as Double);
        testGetPatch(block3DistArray);

        val blockBlock3DistArray = DistArray.make[Double](Dist.makeBlockBlock(Region.make([0..5,1..6,2..6])), ([i,j,k]:Point(3))=>(100*i+10*j+k) as Double);
        testGetPatch(blockBlock3DistArray);

        return true;
    }

    public def testGetPatch(a:DistArray[Double]) {
Console.OUT.println("testing " + a);
        finish for (place in a.dist.places()) at(place) async {
            val localRegion = a.dist(here);
            if (!localRegion.isEmpty()) {
                val mid = localRegion.min(0) + (localRegion.max(0)-localRegion.min(0)+1)/2;
                val secondHalf = (Region.make(mid..localRegion.max(0)) * localRegion.eliminate(0)) as Region(a.rank){rect};
                val patch = a.getPatch(secondHalf);
                for (p in secondHalf) {
                    chk(a(p) == patch(p));
                }
            }
        }
    }

    public static def main(args:Rail[String]):void {
        new DistArrayPatch().execute();
    }
}
