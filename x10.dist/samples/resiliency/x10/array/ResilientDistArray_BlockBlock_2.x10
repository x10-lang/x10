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
// package x10.regionarray;
package x10.array;

/**
 * ResilientDistArray which supports snapshot and restore
 * Currently limited methods necessary for ResilientSimpleHeatTransfer are implemented
 * @author kawatiya
 */
public class ResilientDistArray_BlockBlock_2[T] implements (Long,Long)=>T {
    public property rank() = 2;
    private var da:DistArray_BlockBlock_2[T];
    public def this(m:long, n:long, pg:PlaceGroup{self!=null}, init:(long,long)=>T) {
        this.da = new DistArray_BlockBlock_2[T](m, n, pg, init);
        this.savedPg = null;
    }
    public def this(m:long, n:long, init:(long,long)=>T) { this(m, n, PlaceGroup.WORLD, init); }
    public def this(m:long, n:long, pg:PlaceGroup{self!=null}){T haszero} { this(m, n, pg, (long,long)=>Zero.get[T]()); }
    public def this(m:long, n:long){T haszero} { this(m, n, PlaceGroup.WORLD, (long,long)=>Zero.get[T]()); }
    
    public final def placeGroup():PlaceGroup = da.placeGroup();
    public final def globalIndices():DenseIterationSpace_2{self!=null} = da.globalIndices();
    public final def localIndices():DenseIterationSpace_2{self!=null} = da.localIndices();
    public final def place(i:long,j:long):Place = da.place(i,j);
    public final def place(p:Point(2)):Place = place(p(0), p(1));
    
    public final operator this(i:long, j:long):T = da(i,j);
    public final operator this(p:Point(2)):T  = this(p(0),p(1));
    public final operator this(i:long,j:long)=(v:T):T{self==v} = da(i,j)=v;
    public final operator this(p:Point(2))=(v:T):T{self==v} = this(p(0),p(1))=v;
    
    /*
     * Snapshot mechanism
     */
    /**
     * Remake the ResilientDistArray over a new PlaceGroup using specified initializer
     */
    public def remake(pg:PlaceGroup{self!=null}, init:(long,long)=>T) {
        val m = da.numElems_1;
        val n = da.numElems_2;
        this.da = new DistArray_BlockBlock_2[T](m, n, pg, init);
        this.savedPg = null;
    }
    /**
     * Remake the ResilientDistArray over a new PlaceGroup with zero
     */
    public def remake(pg:PlaceGroup{self!=null}){T haszero} {
        val m = da.numElems_1;
        val n = da.numElems_2;
        this.da = new DistArray_BlockBlock_2[T](m, n, pg);
        this.savedPg = null;
    }
    
    /*
     * Note that snapshot/restore should be executed for the master copy of DistArray
     */
    static class LocalInfo[T](place:Place,raw:Rail[T],localIndices:DenseIterationSpace_2{self!=null}) { }
    private val snapshots = new Rail[ResilientStore[Place,LocalInfo[T]]](2, (Long)=>ResilientStore.make[Place,LocalInfo[T]]());
    private transient var commit_count:Long = 0L;
    private transient var savedPg:PlaceGroup = null; // declared as transient to make restore possible only in master

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
        finish for (pl in da.placeGroup()) {
            at (pl) async {
                val localInfo = new LocalInfo[T](pl, da.raw(), da.localIndices());
                snapshot.save(pl, localInfo); // the data should be copied inside the call
            }
        }
    }
    /**
     * Commit the snapshot created by snapshot_try
     */
    public def snapshot_commit() {
        val idx = commit_count % 2;
        commit_count++; savedPg = da.placeGroup(); // swith to the new snapshot
        // delete the old snapshot
        val old_snapshot = snapshots(idx);
        try { old_snapshot.deleteAll(); } catch (e:Exception) { /* ignore errors */ }
    }
    /**
     * Restore from the snapshot with new Dist
     */
    public def restore(newPg:PlaceGroup{self!=null}) {
        if (savedPg == null) {
            throw new Exception("No savedPg, maybe not a master?");
        }
        val oldPg = savedPg; // to make the transient savedPg copyable
        val idx = commit_count % 2;	
        val snapshot = snapshots(idx); // get the active snapshot
        val cached = PlaceLocalHandle.make[Cell[LocalInfo[T]]](newPg, ()=>new Cell[LocalInfo[T]](null));
        val init = (i:Long,j:Long)=>{
            /*
             * calculate saved place for a point (i,j) from oldPg
             */
            val globalIndices = da.globalIndices; // this should not change if pg is changed
            //TODO: remove this bug workaround...
            if (i<globalIndices.min0||i>globalIndices.max0||j<globalIndices.min1||j>globalIndices.max1) {
                return 0 as T;
            }
            // code from DistArray_BlockBlock_2.place(i,j)
            val tmp = BlockingUtils.mapIndexToBlockBlockPartition(globalIndices, oldPg.size(), i, j);
            val place = oldPg(tmp);
            /*
             * get (and cache) saved info for the place
             */
            // val info= snapshot.load(place);
            // val raw = info.raw, localIndices = info.localIndices;
            if (cached()()==null || cached()().place!=place) cached()() = snapshot.load(place);
            val raw = cached()().raw;
            val localIndices = cached()().localIndices;
            /*
             * calculate local offset for a point (i,j)
             */
            // code from DistArray_BlockBlock_2.offset(i,j)
            val minIndex_1 = localIndices.min(0n);
            val minIndex_2 = localIndices.min(1n);
            val numElemsLocal_2 = localIndices.max(1n) - minIndex_2 + 1L;
            val offset = (j - minIndex_2) + ((i - minIndex_1) * numElemsLocal_2);
            /*
             * return the saved data
             */
            return raw(offset);
        };
        val m = da.numElems_1;
        val n = da.numElems_2;
        this.da = new DistArray_BlockBlock_2[T](m, n, newPg, init);
        PlaceLocalHandle.destroy(newPg, cached);
    }
    
    /**
     * Test program, should print "0 1 2 3 4 5 6 7 8 9" without Exception
     */
    public static def main(ars:Rail[String]) {
        val livePlaces = new x10.util.ArrayList[Place]();
        for (pl in Place.places()) livePlaces.add(pl);
        if (livePlaces.size() < 2) throw new Exception("numPlaces should be >=2");
        val pg = new SparsePlaceGroup(livePlaces.toRail());
        val A = new ResilientDistArray_BlockBlock_2[Long](10, 1, pg, (x:Long,y:Long)=>x);
        A.snapshot();
        livePlaces.remove(here.next());
        val newPg = new SparsePlaceGroup(livePlaces.toRail());
        A.restore(newPg);
        for (pt:Point(2) in A.globalIndices()) Console.OUT.print(at(A.place(pt))A(pt) + " ");
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
        private def DEBUG(msg:String) { Console.OUT.println(msg); Console.OUT.flush(); }
        public def save(key:K, value:V) {
            if (verbose>=1) DEBUG("save: key=" + key);
            at (hm) hm().put(key,value); // value is deep-copied by "at"
        }
        public def load(key:K) {
            if (verbose>=1) DEBUG("load: key=" + key);
            return at (hm) hm().getOrThrow(key); // value is deep-copied by "at"
        }
        public def delete(key:K) {
            if (verbose>=1) DEBUG("delete: key=" + key);
            at (hm) hm().remove(key);
        }
        public def deleteAll() {
            if (verbose>=1) DEBUG("deleteAll");
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
// static public type ResilientDistArray[T](r:Long) = ResilientDistArray[T]{self.rank==r};
