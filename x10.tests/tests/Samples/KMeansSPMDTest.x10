/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

import harness.x10Test;
import x10.util.Random;
import x10.array.Array_2;

// SOURCEPATH: x10.dist/samples

public class KMeansSPMDTest extends x10Test {
    public def run():boolean {
         val pg = Place.places();

         // Create 4 clusters of random points around (-10,-10), (10,10), (-10,10), and (10,-10)
         val initPoints = (Place) => {
             val rand = new Random(here.id);
             val pts = new Array_2[Float](20000, 2, (i:Long, j:Long) => {
                 switch ((i%4) as Int) {
                     case 0n: return -10.5f + rand.nextFloat();
                     case 1n: return 9.5f + rand.nextFloat();
                     case 2n: return (j==0 ? -10.5f : 9.5f) + rand.nextFloat();
                     default: return (j==0 ? 9.5f : -10.5f) + rand.nextFloat();
                 }
             });
             pts
         };
         val clusters = KMeansSPMD.computeClusters(Place.places(), initPoints, 2, 4, 50, 1e-6f, false);

         // We know the inital centroids were selected by averaging the initial 4
         // points in each place.  Therefore we know the expected order of the centroids
         // and can do a very simple test for correctness.
         val expected = Array_2.makeView([ -10f, -10f, 10f, 10f, -10f, 10f, 10f, -10f ], 4, 2);
         var pass:Boolean = true;
         for ([i,j] in expected.indices()) {
             pass &= (Math.abs(clusters(i,j) - expected(i,j)) < 0.01);
         }

         if (!pass) {
             Console.OUT.println("Expected clusters: ");
             KMeansSPMD.printPoints(expected);
             Console.OUT.println("Actual clusters: ");
             KMeansSPMD.printPoints(clusters);
         }

         return pass;
    }

    public static def main(args:Rail[String]) {
	new KMeansSPMDTest().execute();
    }
}
