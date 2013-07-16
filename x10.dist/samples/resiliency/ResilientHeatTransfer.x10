/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2013.
 */
import x10.regionarray.*;
import x10.util.*;

/**
 * Resilient HeatTransfer which uses resilient DistArray
 * Partially based on HeatTransfer_v2.x10
 * Uses ResilientDistArray
 * @author kawatiya
 * 
 * For Managed X10:
 *   $ x10 ResilientHeatTransfer.x10
 *   $ X10_RESILIENT_PLACE_ZERO=1 X10_NPLACES=4 run.sh x10 ResilientHeatTransfer [size]
 * For Native X10:
 *   $ x10c++ ResilientHeatTransfer.x10 -o ResilientHeatTransfer
 *   $ X10_RESILIENT_PLACE_ZERO=1 X10_NPLACES=4 run.sh runx10 ResilientHeatTransfer [size]
 */
public class ResilientHeatTransfer {
    static val epsilon = 1.0e-5;
    static val ITERATIONS=200;
    
    static val livePlaces = new ArrayList[Place]();
    static val restore_needed = new Cell[Boolean](false);
    
    public static def main(args:Rail[String]) {
        val n = (args.size>=1) ? Int.parseInt(args(0)) : 10;
        Console.OUT.println("HeatTransfer for " + n + "x" + n + ", epsilon=" + epsilon);
        
        /*
         * Initialize the data
         */
        val BigR = Region.make(0..(n+1), 0..(n+1));
        val SmallR = Region.make(1..n, 1..n);
        val LastRow = Region.make(0..0, 1..n);
        
        /* Variables to be recalculated when place dies */
        for (pl in Place.places()) livePlaces.add(pl); // livePlaces should be sorted
        var BigD:Dist(2) = Dist.makeBlock(BigR, 0, new SparsePlaceGroup(livePlaces.toRail())); printDist(BigD);
        var SmallD:Dist(2) = BigD | SmallR;
        var D_Base:Dist = Dist.makeUnique(SmallD.places());
        
        /* Resilient DistArrays */
        val A = ResilientDistArray.make[Double](BigD,(p:Point)=>{ LastRow.contains(p) ? 1.0 : 0.0 });
        val Temp = ResilientDistArray.make[Double](BigD);
        val Scratch = ResilientDistArray.make[Double](BigD);
        
        /*
         * Do the computation
         */
        A.snapshot();
        var snapshot_iter:Int = 0;
        var delta:Double = 1.0;
        for (i in 1..ITERATIONS) {
            Console.OUT.println("---- Iteration: "+i);
            try {
                /*
                 * Restore if necessary
                 */
                if (restore_needed()) {
                    /* Create new Dist on available places */
                    Console.OUT.println("Create new Dist over available " + (Place.MAX_PLACES-Place.numDead()) + " places");
                    BigD = Dist.makeBlock(BigR, 0, new SparsePlaceGroup(livePlaces.toRail())); printDist(BigD);
                    SmallD = BigD | SmallR;
                    D_Base = Dist.makeUnique(SmallD.places());
                    /* Restore from a snapshot */
                    Console.OUT.println("Restore from a snapshot at iteration " + snapshot_iter);
                    A.restore(BigD); // RESTORE with new Dist!
                    Temp.remake(BigD);
                    Scratch.remake(BigD);
                    restore_needed() = false;
                }
                
                /*
                 * Core part of the calculation
                 */
                val D = SmallD;
                finish ateach (z in D_Base) {
                    for (p:Point(2) in D|here) {
                        Temp(p) = stencil_1(A, p);
                    }
                }
                delta = A.map(Scratch, Temp, D.region, (x:Double,y:Double)=>Math.abs(x-y))
                         .reduce((x:Double,y:Double)=>Math.max(x,y), 0.0);
                finish ateach (p in D) {
                    A(p) = Temp(p);
                }
                Console.OUT.println("delta=" + delta);
                if (delta <= epsilon) {
                    Console.OUT.println("Result converged"); break;
                }
                
                /*
                 * Create a snapshot at every 10th iteration
                 */
                if (i % 10 == 0) {
                    Console.OUT.println("Create a snapshot at iteration " + i);
                    A.snapshot(); snapshot_iter = i; // SNAPSHOT!
                }
                
            } catch (e:DeadPlaceException) {
                processDeadPlaceException(e);
            } catch (es:MultipleExceptions) {
                for (e in es.exceptions()) {
                    if (e instanceof DeadPlaceException) processDeadPlaceException(e);
                    else throw e;
                }
            } /* try */
        } /* for (i) */
        
        /*
         * Pretty print the result
         */
        Console.OUT.println("---- Result");
        prettyPrint(A);
    }
    
    /**
     * Process a DeadPlaceException
     */
    private static def processDeadPlaceException(e:Exception) {
        val deadPlace = (e as DeadPlaceException).place;
        Console.OUT.println("DeadPlaceException thrown from " + deadPlace);
        livePlaces.remove(deadPlace);
        restore_needed() = true;
    }
    
    // private static def stencil_1(A:ResilientDistArray[Double](2), [x,y]:Point(2)): Double {
    //     return ((at(A.dist(x-1,y)) A(x-1,y)) + 
    //             (at(A.dist(x+1,y)) A(x+1,y)) + 
    //             (at(A.dist(x,y-1)) A(x,y-1)) + 
    //             (at(A.dist(x,y+1)) A(x,y+1))) / 4;
    // }
    /* Tentative workaround since DeatPlaceException is not thrown for "at (p) ..." */
    private static def stencil_1(A:ResilientDistArray[Double](2), [x,y]:Point(2)): Double {
        val a:Double, b:Double, c:Double, d:Double;
        finish a = at(A.dist(x-1,y)) A(x-1,y);
        finish b = at(A.dist(x+1,y)) A(x+1,y);
        finish c = at(A.dist(x,y-1)) A(x,y-1);
        finish d = at(A.dist(x,y+1)) A(x,y+1);
        return (a+b+c+d)/4;
    }
    
    private static def prettyPrint(A:ResilientDistArray[Double](2)) {
        for ([i] in A.region.projection(0)) {
            for ([j] in A.region.projection(1)) {
                val tmp = at (A.dist(i,j)) A(i,j);
                Console.OUT.printf("%5.3f ", tmp);
            }
            Console.OUT.println();
        }
    }
    
    private static def printDist(D:Dist(2)) {
        for ([i] in D.region.projection(0)) {
            for ([j] in D.region.projection(1))
                Console.OUT.print(D(i,j).id+" ");
            Console.OUT.println();
        }
    }
}
