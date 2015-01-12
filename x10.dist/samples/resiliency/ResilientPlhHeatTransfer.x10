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
import x10.resilient.lang.PlaceLocalHandle;
import x10.util.ArrayList;

/**
 * Resilient HeatTransfer which uses Resilient PlaceLocalHandle
 * Wrote from scratch
 * Uses Resilient PlaceLocalHandle
 * @author kawatiya
 * 
 * For Managed X10:
 *   $ x10 ResilientPlhHeatTransfer.x10
 *   $ X10_RESILIENT_MODE=1 X10_NPLACES=8 x10 ResilientPlhHeatTransfer [size]
 * For Native X10:
 *   $ x10c++ ResilientPlhHeatTransfer.x10 -o ResilientPlheatTransfer
 *   $ X10_RESILIENT_MODE=1 X10_NPLACES=8 runx10 ResilientPlhHeatTransfer [size]
 */
public class ResilientPlhHeatTransfer {
    static val epsilon = 1.0e-5;
    static val ITERATIONS=1000L;
    static val NUM_BACKUP = 2; // number of backup places
    
    static val MAX_PLACES = Place.numPlaces();
    static val livePlaces = new ArrayList[Place]();
    static val restore_needed = new Cell[Boolean](false);
    
    /* Part of large 2-D array, divided by x */
    static class PartialArray[T](xStart:Long, xEnd:Long, ySize:Long){T haszero} { // xEnd is inclusive
        private val data:Rail[T];
        def this(xStart:Long, xEnd:Long, ySize:Long, init:(Long,Long)=>T) {
            property(xStart, xEnd, ySize);
            data = new Rail[T]((xEnd+1-xStart) * ySize);
            for (var x:Long = xStart; x <= xEnd; ++x)
                for (var y:Long = 0; y < ySize; ++y)
                    data((x-xStart) * ySize + y) = init(x, y);
        }
        operator this(x:Long, y:Long):T = data((x-xStart) * ySize + y);
        operator this(x:Long, y:Long)=(v:T) = (data((x-xStart) * ySize + y) = v);
    }
    
    public static def main(args:Rail[String]) {
        val n = (args.size>=1) ? Long.parseLong(args(0)) : 10L;
        Console.OUT.println("HeatTransfer for " + n + "x" + n + ", epsilon=" + epsilon);
        for (p in Place.places()) at (p) Console.OUT.println(here+" running in "+Runtime.getName());
        
        /*
         * Initialize the data
         */
        val num_use = MAX_PLACES - NUM_BACKUP; // number of places to be used
        Console.OUT.println("Use " + num_use + " places for calculation");
        if (num_use <= 0) throw new Exception("need more places to run");
        for (pl in Place.places()) livePlaces.add(pl); // livePlaces should be sorted
        
        /* Divide [0..n+1] rows into num_use partitions */
        val part = new Rail[Long](num_use+1);
        for (var i:Long = 0; i <= num_use; ++i) part(i) = (n*i)/num_use + 1;
        for (var i:Long = 0; i < num_use; ++i) {
            Console.OUT.printf("  partition %2d: (%2d)", i, part(i)-1); // ghost row
            for (var x:Long = part(i); x < part(i+1); ++x) Console.OUT.printf(" %2d", x);
            Console.OUT.printf(" (%2d)\n", part(i+1)); // ghost row
        }
        
        /* Create PLH arrays over usePlaces */
        val usePlaces = new ArrayList[Place](); // list of places currently used, should not be declared as static because this value is passed to another place
        for (var i:Long = 0; i < num_use; ++i) usePlaces.add(livePlaces(i)); // use first num_use place
        Console.OUT.println("usePlaces: " + usePlaces);
        val pg = new SparsePlaceGroup(usePlaces.toRail());
        val A = PlaceLocalHandle.make[PartialArray[Double]](pg, ()=>{
            val i = usePlaces.indexOf(here); // get my index
            return new PartialArray[Double](part(i)-1, part(i+1), n+2, (x:Long, y:Long)=>( (x==0) ? 1.0 : 0.0 ));
        });
        val B = PlaceLocalHandle.make[PartialArray[Double]](pg, ()=>{ // temporary array not to be backed up
            val i = usePlaces.indexOf(here); // get my index
            return new PartialArray[Double](part(i)-1, part(i+1), n+2, (x:Long, y:Long)=>( (x==0) ? 1.0 : 0.0 ));
        });
        
        /*
         * Do the computation
         */
        A.snapshot();
        var snapshot_iter:Long = 0;
        var countFromRestore:Long = 0; // even value indicates that A contains the latest values
        for (iter in 1..ITERATIONS) {
            Console.OUT.println("---- Iteration: "+iter);
            try {
                /*
                 * Restore if necessary
                 */
                if (restore_needed()) {
                    /* Create new usePlaces */
                    if (livePlaces.size() < num_use) throw new Exception("no more place to replace");
                    usePlaces.clear();
                    for (var i:Long = 0; i < num_use; ++i) usePlaces.add(livePlaces(i)); // use first num_use place
                    Console.OUT.println("usePlaces: " + usePlaces);
                    val newPg = new SparsePlaceGroup(usePlaces.toRail());
                    /* Restore from a snapshot */
                    Console.OUT.println("Restore from a snapshot at iteration " + snapshot_iter);
                    A.restore(newPg);
                    B.remake(newPg, ()=>{
                        val i = usePlaces.indexOf(here); // get my index
                        return new PartialArray[Double](part(i)-1, part(i+1), n+2, (x:Long, y:Long)=>( (x==0) ? 1.0 : 0.0 ));
                    });
                    restore_needed() = false;
                    countFromRestore = 0;
                }
                
                /*
                 * Core part of the calculation
                 */
                val From = ((countFromRestore % 2) == 0) ? A : B; // array which contains the latest values
                val To   = ((countFromRestore % 2) == 0) ? B : A; // array which will contain new values
            //  val delta = finish(Reducible.MaxReducer[Double](0.0)) { // collective finish may not work with Resilient X10
                val deltaCell = new Cell[Double](0.0), deltaGref = GlobalRef(deltaCell);
                finish {
                    for (pl in usePlaces) at (pl) async {
                        val f = From(), t = To();
                        /* stencil calculation */
                        var maxDiff:Double = 0.0;
                        for (var x:Long = t.xStart+1; x <= t.xEnd-1; ++x) {
                            for (var y:Long = 1; y < t.ySize-1; ++y) {
                                t(x,y) = (f(x-1,y) + f(x+1,y) + f(x,y-1) + f(x,y+1)) / 4;
                                maxDiff = Math.max(maxDiff, Math.abs(t(x,y)-f(x,y)));
                            }
                        }
            //          offer maxDiff; // max delta in this place
                        val d = maxDiff; at (deltaGref) atomic { if (d>deltaGref()()) deltaGref()()=d; }
                        
                        /* send to ghost rows */
                        val i = usePlaces.indexOf(here);
                        if (i > 0) {
                            val x = t.xStart+1;
                            val ghost_values = new Rail[Double](n, (j:Long)=>t(x,j+1));
                            at (usePlaces(i-1)) async {
                                val _t = To(); assert x==_t.xEnd;
                                for (var j:Long = 0; j < n; ++j) _t(x,j+1) = ghost_values(j);
                            }
                        }
                        if (i < num_use-1) {
                            val x = t.xEnd-1;
                            val ghost_values = new Rail[Double](n, (j:Long)=>t(x,j+1));
                            at (usePlaces(i+1)) async {
                                val _t = To(); assert x==_t.xStart;
                                for (var j:Long = 0; j < n; ++j) _t(x,j+1) = ghost_values(j);
                            }
                        }
                    }
            //  }; /* end of collective finish */
                } val delta = deltaCell();
                
                Console.OUT.println("delta=" + delta);
                if (delta <= epsilon) {
                    Console.OUT.println("Result converged");
                    Console.OUT.println("---- Result");
                    prettyPrint(To, usePlaces);
                    break;
                }
                
                /*
                 * Create a snapshot at every 10th iteration
                 */
                if (++countFromRestore % 10 == 0) {
                    assert To==A;
                    Console.OUT.println("Create a snapshot at iteration " + iter);
                    A.snapshot(); snapshot_iter = iter; // SNAPSHOT!
                }
                
            } catch (e:Exception) {
                processException(e, 0);
            } /* try */
        } /* for (i) */
    }
    
    /**
     * Pretty print a PLH array
     */
    private static def prettyPrint(A:PlaceLocalHandle[PartialArray[Double]], usePlaces:ArrayList[Place]) {
        /* usePlaces should be sorted */
        for (pl in usePlaces) {
            val pa = at (pl) A(); // PartialArray
            for (var x:Long = pa.xStart+1; x <= pa.xEnd-1; ++x) {
                Console.OUT.printf(pl + " %4d:", x);
                for (var y:Long = 1; y < pa.ySize-1; ++y)
                    Console.OUT.printf(" %5.3f", pa(x,y));
                Console.OUT.println();
            }
        }
    }
    
    /**
     * Process Exception(s)
     * l is the nest level of MultipleExceptions (for pretty print)
     */
    private static def processException(e:Exception, l:Long) {
        if (e instanceof DeadPlaceException) {
            val deadPlace = (e as DeadPlaceException).place;
            Console.OUT.println(new String(new Rail[Char](l,' ')) + "DeadPlaceException thrown from " + deadPlace);
            livePlaces.remove(deadPlace); // may be removed multiple times
            restore_needed() = true; // TODO: better to check usePlaces.contains(deadPlace)
        }  else if (e instanceof MultipleExceptions) {
            val exceptions = (e as MultipleExceptions).exceptions();
            Console.OUT.println(new String(new Rail[Char](l,' ')) + "MultipleExceptions size=" + exceptions.size);
            val deadPlaceExceptions = (e as MultipleExceptions).getExceptionsOfType[DeadPlaceException]();
            for (dpe in deadPlaceExceptions) {
                processException(dpe, l+1);
            }
            val filtered = (e as MultipleExceptions).filterExceptionsOfType[DeadPlaceException]();
            if (filtered != null) throw filtered;
        } else {
            Console.ERR.println("unhandled exception " + e);
        }
    }
}
