/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */
package x10.resilient.lang;

import x10.resilient.util.ResilientStoreForApp;

/**
 * Resilient PlaceLocalHandle which supports snapshot and restore
 * Currently limited methods necessary for ResilientPlhHeatTransfer are implemented
 * @author kawatiya
 */
public class PlaceLocalHandle[T]{T isref, T haszero} { // struct cannot have var field
    private var pg:PlaceGroup;
    private var plh:x10.lang.PlaceLocalHandle[T];
    private def this(pg:PlaceGroup, plh:x10.lang.PlaceLocalHandle[T]) {
        this.pg = pg;
        this.plh = plh;
    }
    public static def make[T](pg:PlaceGroup, init:()=>T){T isref, T haszero}
        = new PlaceLocalHandle[T](pg, x10.lang.PlaceLocalHandle.make[T](pg, init));
    public def hashCode() = plh.hashCode();
    public operator this() = plh();
    
    /*
     * Snapshot mechanism
     */
    /**
     * Remake the Resilient PlaceLocalHandle over a new PlaceGroup using specified initializer
     */
    public def remake(newPg:PlaceGroup, init:()=>T) {
        if (pg.size()!=newPg.size()) throw new Exception("PlaceGroup size should be same");
        this.pg = newPg;
        this.plh = x10.lang.PlaceLocalHandle.make[T](newPg, init);
    }
    
    /*
     * Note that snapshot/restore should be executed for the master copy of Resilient PlaceLocalHandle
     */
    private val snapshots = new Rail[ResilientStoreForApp[Long,T]](2, (Long)=>ResilientStoreForApp.make[Long,T]());
    private transient var commit_count:Long = 0L;
    
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

        finish for (pl in pg) {
            at (pl) async {
                val i = pg.indexOf(here);
                val data = plh();
                snapshot.save(i, data); // the data should be copied inside the call
            }
        }
    }
    /**
     * Commit the snapshot created by snapshot_try
     */
    public def snapshot_commit() {
        val idx = commit_count % 2;
        commit_count++; // swith to the new snapshot
        // delete the old snapshot
        val old_snapshot = snapshots(idx);
        try { old_snapshot.deleteAll(); } catch (e:Exception) { /* ignore errors */ }
    }
    /**
     * Restore from the snapshot with new PlaceGroup
     */
    public def restore(newPg:PlaceGroup) {
        if (pg.size()!=newPg.size()) throw new Exception("PlaceGroup size should be same");
        val idx = commit_count % 2;
        val snapshot = snapshots(idx); // get the active snapshot
        this.pg = newPg;
        this.plh = x10.lang.PlaceLocalHandle.make[T](newPg, ()=>{
            val i = newPg.indexOf(here);
            return snapshot.load(i);
        });
    }
    
    /**
     * Test program, should print "0 1 2 3 4 5 6" without Exception
     * 
     * Usage: [X10_RESILIENT_STORE_MODE=1] [X10_RESILIENT_STORE_VERBOSE=1] \
     *         X10_RESILIENT_MODE=1 X10_NPLACES=8 \
     *         x10 x10.resilient.lang.PlaceLocalHandle
     */
    public static def main(ars:Rail[String]) {
        if (Place.numPlaces() < 2) throw new Exception("numPlaces should be >=2");

        val places = new x10.util.ArrayList[Place]();
        for (pl in Place.places()) places.add(pl);
        places.remove(Place(0));
        val pg = new SparsePlaceGroup(places.toRail());
        val A = PlaceLocalHandle.make[Cell[Long]](pg, ()=>new Cell(pg.indexOf(here)));
        A.snapshot();
        
	places.clear();
        for (pl in Place.places()) places.add(pl);
        places.remove(Place(1));
        val newPg = new SparsePlaceGroup(places.toRail());
        A.restore(newPg);
        
        for (pl in newPg) Console.OUT.print(at (pl) A()() + " ");
        Console.OUT.println();
    }
}
