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
package x10.regionarray;

/**
 * ResilientDistArray which supports snapshot and restore
 * Currently limited methods necessary for ResilientHeatTransfer are implemented
 * @author kawatiya
 */
public class ResilientDistArray[T](region:Region) implements (Point(region.rank))=>T {
    public property rank():Long = region.rank;
    public var dist:Dist; // mutable (but should not be set from outside)
    private var da:DistArray[T](rank);
    private def this(dist:Dist, da:DistArray[T](dist.rank)) {
        property(dist.region);
        this.da = da;
        this.dist = dist;
        this.savedDist = null;
    }
    public static def make[T](dist:Dist, init:(Point(dist.rank))=>T) = new ResilientDistArray[T](dist, DistArray.make[T](dist, init));
    public static def make[T](dist:Dist){T haszero} = new ResilientDistArray[T](dist, DistArray.make[T](dist));
    public final operator this(pt:Point(rank)) = da(pt);
    public final operator this(i0:long, i1:long){rank==2} = da(i0, i1);
    public final operator this(pt:Point(rank))=(v:T):T { da(pt) = v; return v; }
    public final operator this(i0:long, i1:long)=(v:T){rank==2}:T { da(i0, i1) = v; return v; }
    public final def map[S,U](dst:ResilientDistArray[S](rank), src:ResilientDistArray[U](rank), filter:Region(rank), op:(T,U)=>S):ResilientDistArray[S](rank) {
        da.map(dst.da, src.da, filter, op); return dst;
    }
    public final def reduce(op:(T,T)=>T, unit:T):T =  da.reduce(op, unit);
    
    /*
     * Snapshot mechanism
     */
    /**
     * Remake the ResilientDistArray over a new Dist using specified initializer
     */
    public def remake(dist:Dist, init:(Point(dist.rank))=>T){dist.region==region} {
        this.da = DistArray.make[T](dist, init);
        this.dist = dist;
        this.savedDist = null;
    }
    /**
     * Remake the ResilientDistArray over a new Dist with zero
     */
    public def remake(dist:Dist){dist.region==region,T haszero} {
        this.da = DistArray.make[T](dist);
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
    //     this.da = DistArray.make[T](dist, (p:Point)=>at (ss) ss()()(p));
    //     this.dist = dist;
    // }
    
    /*
     * Note that snapshot/restore should be executed for the master copy of DistArray
     */
    private val snapshots = new Rail[ResilientStore[Place,Rail[T]]](2, (Long)=>ResilientStore.make[Place,Rail[T]]());
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
        this.da = DistArray.make[T](newDist, init);
        this.dist = newDist;
        PlaceLocalHandle.destroy(newDist.places(), cached_raw);
        PlaceLocalHandle.destroy(newDist.places(), cached_place);
    }
    
    /**
     * Test program, should print "0 1 2 3 4 5 6 7 8 9" without Exception
     */
    public static def main(ars:Rail[String]) {
        val livePlaces = new x10.util.ArrayList[Place]();
        for (pl in Place.places()) livePlaces.add(pl);
        if (livePlaces.size() < 2) throw new Exception("numPlaces should be >=2");
        val R = Region.make(0..9);
        val D = Dist.makeBlock(R, 0, new SparsePlaceGroup(livePlaces.toRail()));
        val A = ResilientDistArray.make[Long](D, ([x]:Point(1))=>x);
        A.snapshot();
        livePlaces.remove(here.next());
        val newD = Dist.makeBlock(R, 0, new SparsePlaceGroup(livePlaces.toRail()));
        A.restore(newD);
        for (pt:Point(1) in A.region) Console.OUT.print(at(A.dist(pt))A(pt) + " ");
        Console.OUT.println();
    }
    
    /**
     * ResilientStore interface used by snapshot/restore
     */
    static abstract class ResilientStore[K,V] {
        static val mode = getEnvInt("X10_RESILIENT_STORE_MODE");
        static val verbose = getEnvInt("X10_RESILIENT_STORE_VERBOSE");
        static def getEnvInt(name:String) {
            val env = System.getenv(name);
            val v = (env!=null) ? Int.parseInt(env) : 0N;
            if (here==Place.FIRST_PLACE) Console.OUT.println(name + "=" + v);
            return v;
        }
        public static def make[K,V]():ResilientStore[K,V] {
            switch (mode) {
            case 0N: return new ResilientStorePlace0[K,V]();
            case 1N: return new ResilientStoreDistributed[K,V]();
            default: throw new Exception("unknown mode");
            }
        }
        public abstract def save(key:K, value:V):void;
        public abstract def load(key:K):V;
        public abstract def delete(key:K):void;
        public abstract def deleteAll():void;
    }
    /**
     * Place0 implementation of ResilientStore
     */
    static class ResilientStorePlace0[K,V] extends ResilientStore[K,V] {
        val hm = at (Place.FIRST_PLACE) GlobalRef(new x10.util.HashMap[K,V]());
        public def save(key:K, value:V) {
            at (hm) hm().put(key,value); // value is deep-copied by "at"
        }
        public def load(key:K) {
            return at (hm) hm().getOrThrow(key); // value is deep-copied by "at"
        }
        public def delete(key:K) {
            at (hm) hm().remove(key);
        }
        public def deleteAll() {
            at (hm) hm().clear();
        }
    }
    /**
     * Distributed (local+backup) implementation of ResilientStore
     */
    static class ResilientStoreDistributed[K,V] extends ResilientStore[K,V] {
        val hm = PlaceLocalHandle.make[x10.util.HashMap[K,V]](PlaceGroup.WORLD, ()=>new x10.util.HashMap[K,V]());
        private def DEBUG(key:K, msg:String) { Console.OUT.println("At " + here + ": key=" + key + ": " + msg); }
        public def save(key:K, value:V) {
            /* Store the copy of value locally */
            at (here) hm().put(key, value); // value is deep-copied by "at"
            if (verbose>=1) DEBUG(key, "backed up locally");
            /* Backup the value in another place */
            var backupPlace:Long = key.hashCode() % Place.MAX_PLACES;
            var trial:Long;
            for (trial = 0L; trial < Place.MAX_PLACES; trial++) {
                if (backupPlace != here.id && !Place.isDead(backupPlace)) break; // found appropriate place
                backupPlace = (backupPlace+1) % Place.MAX_PLACES;
            }
            if (trial == Place.MAX_PLACES) {
                /* no backup place available */
                if (verbose>=1) DEBUG(key, "no backup place available");
            } else {
                at (Place(backupPlace)) hm().put(key, value);
                if (verbose>=1) DEBUG(key, "backed up to place " + backupPlace);
            }
        }
        public def load(key:K) {
            /* First, try to load locally */
            try {
                val value = at (here) hm().getOrThrow(key); // value is deep-copied by "at"
                if (verbose>=1) DEBUG(key, "restored locally");
                return value;
            } catch (e:x10.util.NoSuchElementException) { /* falls through */ }
            /* Try to load from another place */
            var backupPlace:Long = key.hashCode() % Place.MAX_PLACES;
            var trial:Long;
            for (trial = 0L; trial < Place.MAX_PLACES; trial++) {
                if (backupPlace != here.id && !Place.isDead(backupPlace)) {
                    if (verbose>=1) DEBUG(key, "checking backup place " + backupPlace);
                    try {
                        val value = at (Place(backupPlace)) hm().getOrThrow(key);
                        if (verbose>=1) DEBUG(key, "restored from backup place " + backupPlace);
                        return value;
                    } catch (e:x10.util.NoSuchElementException) { /* falls through */ }
                }
                backupPlace = (backupPlace+1) % Place.MAX_PLACES;
            }
            if (verbose>=1) DEBUG(key, "no backup found, ERROR");
            throw new Exception("No data for key " + key);
        }
        public def delete(key:K) {
            finish for (pl in Place.places()) {
                if (pl.isDead()) continue;
                at (pl) async hm().remove(key);
            }
        }
        public def deleteAll() {
            finish for (pl in Place.places()) {
                if (pl.isDead()) continue;
                at (pl) async hm().clear();
            }
        }
    }
}
static public type ResilientDistArray[T](r:Long) = ResilientDistArray[T]{self.rank==r};
