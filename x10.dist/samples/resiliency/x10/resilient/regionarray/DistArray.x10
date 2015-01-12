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
package x10.resilient.regionarray;

import x10.regionarray.*;
import x10.resilient.util.ResilientStoreForApp;

/**
 * Resilient DistArray which supports snapshot and restore
 * Currently limited methods necessary for ResilientHeatTransfer are implemented
 * @author kawatiya
 */
public class DistArray[T](region:Region) implements (Point(region.rank))=>T {
    public property rank():Long = region.rank;
    public var dist:Dist; // mutable (but should not be set from outside)
    private var da:x10.regionarray.DistArray[T](rank);
    private def this(dist:Dist, da:x10.regionarray.DistArray[T](dist.rank)) {
        property(dist.region);
        this.da = da;
        this.dist = dist;
        this.savedDist = null;
    }
    public static def make[T](dist:Dist, init:(Point(dist.rank))=>T) = new DistArray[T](dist, x10.regionarray.DistArray.make[T](dist, init));
    public static def make[T](dist:Dist){T haszero} = new DistArray[T](dist, x10.regionarray.DistArray.make[T](dist));
    public final operator this(pt:Point(rank)) = da(pt);
    public final operator this(i0:long, i1:long){rank==2} = da(i0, i1);
    public final operator this(pt:Point(rank))=(v:T):T { da(pt) = v; return v; }
    public final operator this(i0:long, i1:long)=(v:T){rank==2}:T { da(i0, i1) = v; return v; }
    public final def map[S,U](dst:DistArray[S](rank), src:DistArray[U](rank), filter:Region(rank), op:(T,U)=>S):DistArray[S](rank) {
        da.map(dst.da, src.da, filter, op); return dst;
    }
    public final def reduce(op:(T,T)=>T, unit:T):T =  da.reduce(op, unit);
    
    /*
     * Snapshot mechanism
     */
    /**
     * Remake the Resilient DistArray over a new Dist using specified initializer
     */
    public def remake(dist:Dist, init:(Point(dist.rank))=>T){dist.region==region} {
        this.da = x10.regionarray.DistArray.make[T](dist, init);
        this.dist = dist;
        this.savedDist = null;
    }
    /**
     * Remake the Resilient DistArray over a new Dist with zero
     */
    public def remake(dist:Dist){dist.region==region,T haszero} {
        this.da = x10.regionarray.DistArray.make[T](dist);
        this.dist = dist;
        this.savedDist = null;
    }
    
    // /*
    //  * Simple implementation of snapshot/restore (just for reference)
    //  */
    // private val ss = at (Place.FIRST_PLACE) GlobalRef[Cell[Array[T](region)]](new Cell[Array[T](region)](null));
    // public def snapshot() {
    //     at (ss) {
    //         val a = new Array[T](region, (p:Point)=>at (dist(p)) da(p));
    //         ss()() = a; // replace only when "a" is created successfully
    //     }
    // }
    // public def restore(dist:Dist){dist.region==region} {
    //     this.da = x10.regionarray.DistArray.make[T](dist, (p:Point)=>at (ss) ss()()(p));
    //     this.dist = dist;
    // }
    
    /*
     * Note that snapshot/restore should be executed for the master copy of Resilient DistArray
     */
    private val snapshots = new Rail[ResilientStoreForApp[Place,Rail[T]]](2, (Long)=>ResilientStoreForApp.make[Place,Rail[T]]());
    private transient var commit_count:Long = 0L;
    private transient var savedDist:Dist = null; // declared as transient to make restore possible only in master
    
    /**
     * Create and commit a snapshot
     */
    public def snapshot() {
        snapshot_try(); // may fail with DeadPlaceException
        snapshot_commit();
    }
    /**
     * Try to create a snapshot (not committed yet)
     */
    public def snapshot_try() {
        val idx = (commit_count+1) % 2;
        val snapshot = snapshots(idx);
        finish for (pl in dist.places()) {
            at (pl) async {
                val raw = da.raw();
                snapshot.save(pl, raw); // the data should be copied inside the call
            }
        }
    }
    /**
     * Commit the snapshot created by snapshot_try
     */
    public def snapshot_commit() {
        val idx = commit_count % 2;
        commit_count++; savedDist = dist; // swith to the new snapshot
        // delete the old snapshot
        val old_snapshot = snapshots(idx);
        try { old_snapshot.deleteAll(); } catch (e:Exception) { /* ignore errors */ }
    }
    /**
     * Restore from the snapshot with new Dist
     */
    public def restore(newDist:Dist){newDist.region==region} {
        if (savedDist == null) {
            throw new Exception("No saved_dist, maybe not a master?");
        }
        val oldDist = savedDist; // to make the transient savedDist copyable
        val idx = commit_count % 2;
        val snapshot = snapshots(idx); // get the active snapshot
        val cached_raw = PlaceLocalHandle.make[Cell[Rail[T]]](newDist.places(), ()=>new Cell[Rail[T]](null));
        val cached_place = PlaceLocalHandle.make[Cell[Place]](newDist.places(), ()=>new Cell[Place](Place.FIRST_PLACE)); // dummy data
        val init = (pt:Point)=>{
            // val offset = oldDist.offset(pt); // This does not work at different place
            val place = oldDist(pt);
            val region = oldDist.get(place);
            val offset = region.indexOf(pt); //TODO: This may not be general
            // val raw = snapshot.load(place); // Better to reduce the access by caching
            if (cached_raw()()==null || cached_place()()!=place) {
                cached_raw()() = snapshot.load(place); // load from snapshot, only if not cached
                cached_place()() = place;
            }
            val raw = cached_raw()();
            return raw(offset);
        };
        this.da = x10.regionarray.DistArray.make[T](newDist, init);
        this.dist = newDist;
        PlaceLocalHandle.destroy(newDist.places(), cached_raw);
        PlaceLocalHandle.destroy(newDist.places(), cached_place);
    }
    
    /**
     * Test program, should print "0 1 2 3 4 5 6 7 8 9" without Exception
     * 
     * Usage: [X10_RESILIENT_STORE_MODE=1] [X10_RESILIENT_STORE_VERBOSE=1] \
     *         X10_RESILIENT_MODE=1 X10_NPLACES=4 \
     *         x10 x10.resilient.regionarray.DistArray
     */
    public static def main(ars:Rail[String]) {
        val livePlaces = new x10.util.ArrayList[Place]();
        for (pl in Place.places()) livePlaces.add(pl);
        if (livePlaces.size() < 2) throw new Exception("numPlaces should be >=2");
        val R = Region.make(0..9);
        val D = Dist.makeBlock(R, 0, new SparsePlaceGroup(livePlaces.toRail()));
        val A = DistArray.make[Long](D, ([x]:Point(1))=>x);
        A.snapshot();
        livePlaces.remove(Place(1));
        val newD = Dist.makeBlock(R, 0, new SparsePlaceGroup(livePlaces.toRail()));
        A.restore(newD);
        for (pt:Point(1) in A.region) Console.OUT.print(at(A.dist(pt))A(pt) + " ");
        Console.OUT.println();
    }
}
static public type DistArray[T](r:Long) = DistArray[T]{self.rank==r};
