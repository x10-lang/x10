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
package x10.lang;

/**
 * ResilientStore interface used by ResilientDistArray.snapshot/restore
 * @author kawatiya
 */
public abstract class ResilientStoreForDistArray[K,V] {
    static val mode = getEnvInt("X10_RESILIENT_STORE_MODE");
    static val verbose = getEnvInt("X10_RESILIENT_STORE_VERBOSE");
    static def getEnvInt(name:String) {
        val env = System.getenv(name);
        val v = (env!=null) ? Int.parseInt(env) : 0N;
        if (here==Place.FIRST_PLACE) Console.OUT.println(name + "=" + v);
        return v;
    }
    public static def make[K,V]():ResilientStoreForDistArray[K,V] {
        switch (mode) {
        case 0N: return new ResilientStoreForDistArrayPlace0[K,V]();
        case 1N: return new ResilientStoreForDistArrayDistributed[K,V]();
        default: throw new Exception("unknown mode");
        }
    }
    public abstract def save(key:K, value:V):void;
    public abstract def load(key:K):V;
    public abstract def delete(key:K):void;
    public abstract def deleteAll():void;
 
    /**
     * Place0 implementation of ResilientStore
     */
    static class ResilientStoreForDistArrayPlace0[K,V] extends ResilientStoreForDistArray[K,V] {
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
     * Distributed (local+backup) implementation of ResilientStore, for reference
     * NOTE: This implementation is just for using with ResilientDistArray
     *       Currently, same key cannot be stored multiple times!
     *       For it, delete(key) or deleteAll() must be called first
     *       Racing between multiple places are not also considered.
     */
    static class ResilientStoreForDistArrayDistributed[K,V] extends ResilientStoreForDistArray[K,V] {
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
    
    /**
     * Test program, should print "0 1 2 ..."
     */
    public static def main(ars:Rail[String]) {
        if (Place.MAX_PLACES < 3) throw new Exception("numPlaces should be >=3");
       
        val rs = ResilientStoreForDistArray.make[Place,Rail[String]]();
        
        /* Store at each place */
        finish for (pl in Place.places()) {
            at (pl) async {
                val data = new Rail[String](1); data(0) = pl.id.toString();
                rs.save(pl, data);
            }
        }
        
        /* Kill place1 */
        try {
            at (Place(1)) System.killHere();
        } catch (e:DeadPlaceException) {
            Console.OUT.println("Place " + e.place + " died");
        }
        
        /* Load at place2 */
        at (Place(2)) {
            for (pl in Place.places()) {
                val data = rs.load(pl);
                Console.OUT.print(data(0) + " ");
            }
            Console.OUT.println();
        }
    }
}