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
import x10.util.resilient.localstore.ResilientStore;

// SOURCEPATH: x10.dist/samples/resiliency
// NUM_PLACES: 10

// This test creates a uniform distribution of d dimensional
// points that is distributed such that each of the 2^d Places
// gets points in only "quadrant" of the space. We look for
// 2^d clusters and expect to get to a converged solution of
// exactly one centroid in each quadrant

public class ResilientKMeansTest extends x10Test {
    static val signVectors = [[-1f as Float,-1f,-1f],[-1f,-1f,1f],[-1f,1f,-1f],[-1f,1f,1f],
                              [1f,-1f,-1f],[1f,-1f,1f],[1f,1f,-1f],[1f as Float,1f,1f]];

    public def run():boolean {
         val rs = ResilientStore.make(2);
         val pg = rs.getActivePlaces();
         
         chk(pg.numPlaces() == 8, "This test requires 8 active places; given "+pg.numPlaces());

         val d = 3;
         val nPoints = 200000;
         val k = 8; // 2^d

         // Create globally uniform, but locally skewed distribution
         val initPoints = (Place) => {
             val virtualPlace = pg.indexOf(here);
             val rand = new Random(virtualPlace);
             val pts = new Array_2[Float](nPoints, d, (Long,Long) => rand.nextFloat());
             val signVector:Rail[Float] = signVectors(virtualPlace);
             for (i in 0..(nPoints-1)) {
                 for (j in 0..(d-1)) {
                     pts(i,j) *= signVector(j);
                 }
             }
             pts
         };

         if (x10.xrx.Runtime.RESILIENT_MODE > 0) {
             ResilientKMeans.setHammerConfig("3,15", "2,7");
         }
         val clusters = ResilientKMeans.computeClusters(pg, initPoints, 3, k, 20, 1e-6f, false, 5, rs);

         var pass:Boolean = true;

         // We should end up with one centroid in the middle (0.5,0.5,0.5)
         // of each quadrant.
         // First check magnitude
         for (v in clusters) {
             if (Math.abs(Math.abs(v) - 0.5f) > 0.01f) {
                 pass = false;
                 Console.OUT.printf("Centroid coordinate %.4f too far from expected magnitude of 0.5\n", v);
             }
         }

         // Next check quadrant coverage
         val quadrants = new Array_2[Long](k, d, (i:Long,j:Long) => Math.signum(clusters(i,j)) < 0f ? 0 : 1);
         val quadrantCount = new Rail[Long](k);
         for (i in 0..(k-1)) {
             val quadrant = 4*quadrants(i,0) + 2*quadrants(i,1) + quadrants(i,2);
             quadrantCount(quadrant) += 1;
         }
         for (i in quadrantCount.range()) {
             if (quadrantCount(i) != 1) {
                 pass = false;
                 Console.OUT.println("Failure: had "+quadrantCount(i)+" centroids in quadrant "+i);
             }
         }
         if (!pass) {
             Console.OUT.println("Computed clusters: ");
             ResilientKMeans.printPoints(clusters);
         }

         return pass;
    }

    public static def main(args:Rail[String]) {
	new ResilientKMeansTest().execute();
    }
}
