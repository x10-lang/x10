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
// import x10.resilient.regionarray.DistArray;
// import x10.regionarray.Dist;
// import x10.regionarray.Region;
// import x10.resilient.array.DistArray_BlockBlock_2;
import x10.array.DistArray_BlockBlock_2;
import x10.util.ArrayList;

/**
 * Non-Resilient HeatTransfer which uses simple resilient DistArray
 * Should be maintained together with ResilientSimpleHeatTransfer.x10
 * @author kawatiya
 * 
 * For Managed X10:
 *   $ x10 NonResilientSimpleHeatTransfer.x10
 *   $ X10_RESILIENT_MODE=1 X10_NPLACES=4 x10 NonResilientSimpleHeatTransfer [size]
 * For Native X10:
 *   $ x10c++ NonResilientSimpleHeatTransfer.x10 -o NonResilientSimpleHeatTransfer
 *   $ X10_RESILIENT_MODE=1 X10_NPLACES=4 runx10 NonResilientSimpleHeatTransfer [size]
 */
public class NonResilientSimpleHeatTransfer {
    static val epsilon = 1.0e-5;
    static val ITERATIONS=200L;
    
    static val livePlaces = new ArrayList[Place]();
//  static val restore_needed = new Cell[Boolean](false);
    
    public static def main(args:Rail[String]) {
        val n = (args.size>=1) ? Long.parseLong(args(0)) : 10L;
        Console.OUT.println("HeatTransfer for " + n + "x" + n + ", epsilon=" + epsilon);
        for (p in Place.places()) at (p) Console.OUT.println(here+" running in "+Runtime.getName());
        
        /*
         * Initialize the data
         */
        // val BigR = Region.make(0..(n+1), 0..(n+1));
        // val SmallR = Region.make(1..n, 1..n);
        // val LastRow = Region.make(0..0, 1..n);
        
        /* Variables to be recalculated when place dies */
        for (pl in Place.places()) livePlaces.add(pl); // livePlaces should be sorted
        // var BigD:Dist(2) = Dist.makeBlock(BigR, 0, new SparsePlaceGroup(livePlaces.toRail())); printDist(BigD);
        // var SmallD:Dist(2) = BigD | SmallR;
        // var D_Base:Dist = Dist.makeUnique(SmallD.places());
        var pg:PlaceGroup = new SparsePlaceGroup(livePlaces.toRail());
        
        /* Resilient DistArrays */
        // val A = DistArray.make[Double](BigD,(p:Point)=>{ LastRow.contains(p) ? 1.0 : 0.0 });
        // val Temp = DistArray.make[Double](BigD);
        // val Scratch = DistArray.make[Double](BigD);
        val A = new DistArray_BlockBlock_2[Double](n+2, n+2, pg, (x:Long,y:Long)=>{ x==0 ? 1.0 : 0.0 });
        val Temp = new DistArray_BlockBlock_2[Double](n+2, n+2, pg);
        val Scratch = new DistArray_BlockBlock_2[Double](n+2, n+2, pg); // necessary to use map()
        printDist(A, n+2, n+2);
        
        /*
         * Do the computation
         */
//      A.snapshot();
//      var snapshot_iter:Long = 0L;
        var delta:Double = 1.0;
        for (i in 1..ITERATIONS) {
            Console.OUT.println("---- Iteration: "+i);
//          try {
//              /*
//               * Restore if necessary
//               */
//              if (restore_needed()) {
//                  /* Create new PlaceGroup on available places */
//                  Console.OUT.println("Create new PlaceGroup over available " + (Place.numPlaces()-Place.numDead()) + " places");
//                  // BigD = Dist.makeBlock(BigR, 0, new SparsePlaceGroup(livePlaces.toRail())); printDist(BigD);
//                  // SmallD = BigD | SmallR;
//                  // D_Base = Dist.makeUnique(SmallD.places());
//                  pg = new SparsePlaceGroup(livePlaces.toRail());
//                  /* Restore from a snapshot */
//                  Console.OUT.println("Restore from a snapshot at iteration " + snapshot_iter);
//                  // A.restore(BigD); // RESTORE with new Dist!
//                  // Temp.remake(BigD);
//                  // Scratch.remake(BigD);
//                  A.restore(pg);
//                  Temp.remake(pg);
//                  Scratch.remake(pg);
//                  printDist(A, n+2, n+2);
//                  restore_needed() = false;
//              }
                
                /*
                 * Core part of the calculation
                 */
                // val D = SmallD;
                // finish ateach (z in D_Base) {
                finish for (pl in A.placeGroup()) at (pl) async {
                    // for (p:Point(2) in D|here) {
                    for (p in A.localIndices()) {
                        // if (isEdge(p,n)) continue;
                        if (isEdge(p,n)) Temp(p) = A(p); // necessary to use map()
                        else Temp(p) = stencil_1(A, p);
                    }
                }
                // delta = A.map(Scratch, Temp, D.region, (x:Double,y:Double)=>Math.abs(x-y))
                //          .reduce((x:Double,y:Double)=>Math.max(x,y), 0.0);
                delta = A.map[Double,Double](Temp, Scratch, (x:Double,y:Double)=>Math.abs(x-y))
                         .reduce((x:Double,y:Double)=>Math.max(x,y), 0.0);
                         // Note that abs(x-y) becomes 0.0 for "isEdge" elements
            /** Old code when map/reduce were not available
                delta = finish(Reducible.MaxReducer[Double](0.0)) {
                    for (pl in A.placeGroup()) {
                        at (pl) async {
                            var maxDiff:Double = 0.0;
                            for (p in A.localIndices()) {
                                if (isEdge(p,n)) continue;
                                maxDiff = Math.max(maxDiff, Math.abs(A(p)-Temp(p)));
                            }
                            offer(maxDiff);
                        }
                    }
                };
             **/
                // finish ateach (p in D) {
                //     A(p) = Temp(p);
                // }
                finish for (pl in A.placeGroup()) at (pl) async {
                    for (p in A.localIndices()) {
                        if (isEdge(p,n)) continue;
                        A(p) = Temp(p);
                    }
                }
                Console.OUT.println("delta=" + delta);
                if (delta <= epsilon) {
                    Console.OUT.println("Result converged"); break;
                }
                
//              /*
//               * Create a snapshot at every 10th iteration
//               */
//              if (i % 10 == 0) {
//                  Console.OUT.println("Create a snapshot at iteration " + i);
//                  A.snapshot(); snapshot_iter = i; // SNAPSHOT!
//              }
//              
//          } catch (e:Exception) {
//              processException(e, 0);
//          } /* try */
        } /* for (i) */
        
        /*
         * Pretty print the result
         */
        Console.OUT.println("---- Result");
        // prettyPrint(A);
        prettyPrint(A, n+2, n+2);
    }
    
//  /**
//   * Process Exception(s)
//   * l is the nest level of MultipleExceptions (for pretty print)
//   */
//  private static def processException(e:Exception, l:Long) {
//      if (e instanceof DeadPlaceException) {
//          val deadPlace = (e as DeadPlaceException).place;
//          Console.OUT.println(new String(new Rail[Char](l,' ')) + "DeadPlaceException thrown from " + deadPlace);
//          livePlaces.remove(deadPlace); // may be removed multiple times
//          restore_needed() = true;
//      } else if (e instanceof MultipleExceptions) {
//          val exceptions = (e as MultipleExceptions).exceptions();
//          Console.OUT.println(new String(new Rail[Char](l,' ')) + "MultipleExceptions size=" + exceptions.size);
//          for (ec in exceptions) processException(ec, l+1);
//      } else {
//          Console.OUT.println(new String(new Rail[Char](l,' ')) + e);
//          throw e;
//      }
//  }
    
    // private static def stencil_1(A:DistArray[Double](2), [x,y]:Point(2)): Double {
    private static def stencil_1(A:DistArray_BlockBlock_2[Double], [x,y]:Point(2)): Double {
        // return ((at(A.dist(x-1,y)) A(x-1,y)) + 
        //         (at(A.dist(x+1,y)) A(x+1,y)) + 
        //         (at(A.dist(x,y-1)) A(x,y-1)) + 
        //         (at(A.dist(x,y+1)) A(x,y+1))) / 4;
        return ((at(A.place(x-1,y)) A(x-1,y)) + 
                (at(A.place(x+1,y)) A(x+1,y)) + 
                (at(A.place(x,y-1)) A(x,y-1)) + 
                (at(A.place(x,y+1)) A(x,y+1))) / 4;
    }
    // /* Tentative workaround since DeatPlaceException is not thrown for "at (p) ..." */
    // private static def stencil_1(A:DistArray[Double](2), [x,y]:Point(2)): Double {
    //     val a:Double, b:Double, c:Double, d:Double;
    //     finish a = at(A.dist(x-1,y)) A(x-1,y);
    //     finish b = at(A.dist(x+1,y)) A(x+1,y);
    //     finish c = at(A.dist(x,y-1)) A(x,y-1);
    //     finish d = at(A.dist(x,y+1)) A(x,y+1);
    //     return (a+b+c+d)/4;
    // }
    
    // private static def prettyPrint(A:DistArray[Double](2)) {
    //     for ([i] in A.region.projection(0)) {
    //         for ([j] in A.region.projection(1)) {
    //             val tmp = at (A.dist(i,j)) A(i,j);
    //             Console.OUT.printf("%5.3f ", tmp);
    //         }
    //         Console.OUT.println();
    //     }
    // }
    // 
    // private static def printDist(D:Dist(2)) {
    //     for ([i] in D.region.projection(0)) {
    //         for ([j] in D.region.projection(1))
    //             Console.OUT.print(D(i,j).id+" ");
    //         Console.OUT.println();
    //     }
    // }
    private static def prettyPrint(A:DistArray_BlockBlock_2[Double], x:Long, y:Long) {
        for (i in 0..(x-1)) {
            for (j in 0..(y-1)) {
                val tmp = at (A.place([i,j])) A([i,j]);
                Console.OUT.printf("%5.3f ", tmp);
            }
            Console.OUT.println();
        }
    }
    private static def printDist(A:DistArray_BlockBlock_2[Double], x:Long, y:Long) {
        for (i in 0..(x-1)) {
            for (j in 0..(y-1)) {
                Console.OUT.print(A.place([i,j]).id+" ");
            }
            Console.OUT.println();
        }
    }
    private static def isEdge([x,y]:Point(2), n:Long) {
        return (x==0||x==n+1||y==0||y==n+1);
    }
}
