/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import x10.io.Console;
import x10.util.Random;

/**
 * A KMeans object o can compute K means of a given set of 
 * points of dimension o.myDim.
 * <p> 
 * This class implements a sequential program, that is readily parallelizable.
 * 
 * @author cunningham
 * @author vj 
 */

public class KMeans(myDim:Int) {

    const DIM=2,  K=4, POINTS=2000, ITERATIONS=50;
    const EPS=0.01F;
    
    static type ValVector(k:Int) = ValRail[Float](k);
    static type ValVector = ValVector(DIM);
    
    static type Vector(k:Int) = Rail[Float](k);
    static type Vector = Vector(DIM);
    
    static type SumVector(d:Int) = V{self.dim==d};
    static type SumVector = SumVector(DIM);
    /**
     * V represents the sum of 'count' number of vectors of dimension 'dim'.
     * 
     */

    static class V(dim:Int) implements (Int)=>Float {
        var vec: Vector(dim);
        var count:Int;
        def this(dim:Int, init:(Int)=>Float): SumVector(dim) {
           property(dim);
           vec = Rail.make[Float](this.dim, init);
           count = 0;
        }
        public def apply(i:Int) = vec(i);
        def makeZero() {
            for ((i) in 0..dim-1) 
                vec(i) =0.0F;
            count=0;
        }
        def addIn(a:ValVector(dim)) {
            for ((i) in 0..dim-1) 
                vec(i) += a(i);
            count++;
        }
        def div(f:Int) {
            for ((i) in 0..dim-1)
                vec(i) /= f;
        }
        def dist(a:ValVector(dim)):Float {
            var dist:Float=0.0F;
            for ((i) in 0..dim-1) {
                val tmp = vec(i)-a(i);
                dist += tmp*tmp;
            }
            return dist;
        }
        def dist(a:SumVector(dim)!):Float {
            var dist:Float=0.0F;
            for ((i) in 0..dim-1) {
                val tmp = vec(i)-a(i);
                dist += tmp*tmp;
            }
            return dist;
        }
        def print() {
            Console.OUT.println();
            for ((i) in 0..dim-1) {
                Console.OUT.print((i>0? " " : "") + vec(i));
            }
        }
        def normalize() { div(count);}
        def count() = count;
    }
    
    
    def this(myDim:Int):KMeans{self.myDim==myDim} {
        property(myDim);
    }
     static type KMeansData(myK:Int, myDim:Int)= Rail[SumVector(myDim)](myK);
    /**
     * Compute myK means for the given set of points of dimension myDim.
     */

    def computeMeans(myK:Int, points: ValRail[ValVector(myDim)]): KMeansData(myK, myDim) {
        var redCluster : KMeansData(myK, myDim) =
            Rail.make[SumVector(myDim)](myK, (i:Int)=> new V(myDim, (j:Int)=>points(i)(j)));
        var blackCluster: KMeansData(myK, myDim) =
            Rail.make[SumVector(myDim)](myK, (i:Int)=> new V(myDim, (j:Int)=>0.0F));
        for ((i) in 1..ITERATIONS) {
            val tmp = redCluster;
            redCluster = blackCluster;
            blackCluster=tmp;
            for ((p) in 0..POINTS-1) { 
                var closest:Int = -1;
                var closestDist:Float = Float.MAX_VALUE;
                val point = points(p);
                for ((k) in 0..myK-1) { // compute closest mean in cluster.
                    val dist = blackCluster(k).dist(point);
                    if (dist < closestDist) {
                        closestDist = dist;
                        closest = k;
                    }
                }
                redCluster(closest).addIn(point);
            }
            for ((k) in 0..myK-1) 
                redCluster(k).normalize(); 
            
            var b:Boolean = true;
                for ((k) in 0..myK-1) {
                    if (redCluster(k).dist(blackCluster(k)) > EPS) {
                        b=false;
                        break;
                    }
                }
            if (b) 
                break;
            for ((k) in 0..myK-1) 
                blackCluster(k).makeZero(); 
        }
        return redCluster;  
    }
  
    public static def main (args : Rail[String]) {
        val rnd = new Random(0);
        val points = ValRail.make[ValVector](POINTS, 
                        (Int)=>ValRail.make[Float](DIM, (Int)=>rnd.nextFloat()));
        val result = new KMeans(DIM).computeMeans(K, points);
        for ((k) in 0..K-1) result(k).print();
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
