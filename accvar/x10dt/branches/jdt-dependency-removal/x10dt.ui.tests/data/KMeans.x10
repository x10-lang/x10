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
 *  Converted to 2.1 on 9/1/2010
 */
public class KMeans(myDim:Int) {

    static DIM=2,  K=4, POINTS=2000, ITERATIONS=50;
    static EPS=0.01F;
    
    static type ValVector(k:Int) = Array[Float]{self.rank==1,self.size==k,self.rect,self.zeroBased};
    static type ValVector = ValVector(DIM);
    
    static type Vector(k:Int) = Array[Float]{self.rank==1,self.size==k,self.rect,self.zeroBased};
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
        def this(dim:Int, init:(int)=>Float): SumVector(dim) {
           property(dim);
           vec = new Array[Float](this.dim, init);
           count = 0;
        }
        public def apply(i:Int) = vec(i);
        def makeZero() {
            for ([i] in 0..dim-1) 
                vec(i) =0.0F;
            count=0;
        }
        def addIn(a:ValVector(dim)) {
            for ([i] in 0..dim-1) 
                vec(i) += a(i);
            count++;
        }
        def div(f:Int) {
            for ([i] in 0..dim-1)
                vec(i) /= f;
        }
        def dist(a:ValVector(dim)):Float {
            var dist:Float=0.0F;
            for ([i] in 0..dim-1) {
                val tmp = vec(i)-a(i);
                dist += tmp*tmp;
            }
            return dist;
        }
        def dist(a:SumVector(dim)):Float {
            var dist:Float=0.0F;
            for ([i] in 0..dim-1) {
                val tmp = vec(i)-a(i);
                dist += tmp*tmp;
            }
            return dist;
        }
        def print() {
            Console.OUT.println();
            for ([i] in 0..dim-1) {
                Console.OUT.print((i>0? " " : "") + vec(i));
            }
        }
        def normalize() { div(count);}
        def count() = count;
    }
    
    
    def this(myDim:Int):KMeans{self.myDim==myDim} {
        property(myDim);
    }
     static type KMeansData(myK:Int, myDim:Int)= Array[SumVector(myDim)]{self.rank==1,self.size==myK,self.rect,self.zeroBased};
    /**
     * Compute myK means for the given set of points of dimension myDim.
     */

    def computeMeans(myK:Int, points: Array[ValVector(myDim)](1)): KMeansData(myK, myDim) {
        var redCluster : KMeansData(myK, myDim) =
            new Array[SumVector(myDim)](myK, (i:int)=> new V(myDim, (j:int)=>points(i)(j)));
        var blackCluster: KMeansData(myK, myDim) =
            new Array[SumVector(myDim)](myK, (i:int)=> new V(myDim, (j:int)=>0.0F));
        for ([i] in 1..ITERATIONS) {
            val tmp = redCluster;
            redCluster = blackCluster;
            blackCluster=tmp;
            for ([p] in 0..POINTS-1) { 
                var closest:Int = -1;
                var closestDist:Float = Float.MAX_VALUE;
                val point = points(p);
                for ([k] in 0..myK-1) { // compute closest mean in cluster.
                    val dist = blackCluster(k).dist(point);
                    if (dist < closestDist) {
                        closestDist = dist;
                        closest = k;
                    }
                }
                redCluster(closest).addIn(point);
            }
            for ([k] in 0..myK-1) 
                redCluster(k).normalize(); 
            
            var b:Boolean = true;
                for ([k] in 0..myK-1) {
                    if (redCluster(k).dist(blackCluster(k)) > EPS) {
                        b=false;
                        break;
                    }
                }
            if (b) 
                break;
            for ([k] in 0..myK-1) 
                blackCluster(k).makeZero(); 
        }
        return redCluster;  
    }
  
    public static def main (Array[String]) {
        val rnd = new Random(0);
        val points = new Array[ValVector](POINTS, 
                        (int)=>new Array[Float](DIM, (int)=>rnd.nextFloat()) as ValVector);
        val result = new KMeans(DIM).computeMeans(K, points);
        for ([k] in 0..K-1) result(k).print();
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
