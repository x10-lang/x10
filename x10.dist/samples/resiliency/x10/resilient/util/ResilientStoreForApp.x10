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
package x10.resilient.util;

/**
 * ResilientStore interface used by Resilient DistArray and PlaceLocalHandle
 * @author kawatiya
 */
public abstract class ResilientStoreForApp[K,V]{V haszero} {
    static val mode = getEnvInt("X10_RESILIENT_STORE_MODE");
    static val verbose = getEnvInt("X10_RESILIENT_STORE_VERBOSE");
    static def getEnvInt(name:String) {
        val env = System.getenv(name);
        val v = (env!=null) ? Int.parseInt(env) : 0N;
        if (here==Place.FIRST_PLACE) Console.OUT.println(name + "=" + v);
        return v;
    }
    public static def make[K,V](){V haszero}:ResilientStoreForApp[K,V] {
        if (verbose>=1) Console.OUT.println("ResilientStoreForApp.make: mode="+mode);
        switch (mode) {
        case 0N: return new ResilientStoreForAppPlace0[K,V]();
        case 1N: return new ResilientStoreForAppDistributed[K,V]();
        case 2N: return new ResilientStoreForAppHC[K,V]();
        default: throw new Exception("unknown mode");
        }
    }
    public abstract def save(key:K, value:V):void;
    public abstract def load(key:K):V;
    public abstract def delete(key:K):void;
    public abstract def deleteAll():void;

    // HashMap-like I/F
    public def put(key:K, value:V) { save(key, value); }
    public def getOrThrow(key:K) { return load(key); }
    public def get(key:K) { try { return load(key); } catch (e:x10.util.NoSuchElementException) { return Zero.get[V](); } }
    public def getOrElse(key:K, orelse:V) { try { return load(key); } catch (e:x10.util.NoSuchElementException) { return orelse; } }
    
    /**
     * Place0 implementation of ResilientStore
     */
    static class ResilientStoreForAppPlace0[K,V]{V haszero} extends ResilientStoreForApp[K,V] {
        val hm = at (Place.FIRST_PLACE) GlobalRef(new x10.util.HashMap[K,V]());
        private def DEBUG(msg:String) { Console.OUT.println(msg); Console.OUT.flush(); }
        public def save(key:K, value:V) {
            if (verbose>=1) DEBUG("save: key=" + key);
           finish //TODO: remove this workaround (see XTENLANG-3260)
            at (hm) atomic { hm().put(key,value); } // value is deep-copied by "at"
        }
        public def load(key:K) {
            if (verbose>=1) DEBUG("load: key=" + key);
            var value:V;
           finish //TODO: remove this workaround (see XTENLANG-3260)
            value = at (hm) {
              var v:V; atomic { v = hm().getOrThrow(key); } v
            }; // value is deep-copied by "at"
            return value;
        }
        public def delete(key:K) {
            if (verbose>=1) DEBUG("delete: key=" + key);
           finish //TODO: remove this workaround (see XTENLANG-3260)
            at (hm) atomic { hm().remove(key); }
        }
        public def deleteAll() {
            if (verbose>=1) DEBUG("deleteAll");
           finish //TODO: remove this workaround (see XTENLANG-3260)
            at (hm) atomic { hm().clear(); }
        }
    }
    
    /**
     * Distributed (local+backup) implementation of ResilientStore, for reference
     * 
     * NOTE: This implementation is just for using with Resilient DistArray
     *       Currently, same key cannot be stored multiple times!!!!
     *       For it, delete(key) is or deleteAll() must be called first.
     *       Racing between multiple places are not also considered.
     */
    static class ResilientStoreForAppDistributed[K,V]{V haszero} extends ResilientStoreForApp[K,V] {
        private static val MAX_PLACES = Place.numPlaces(); // TODO: remove the MAX_PLACES dependency to support elastic X10
        val hm = PlaceLocalHandle.make[x10.util.HashMap[K,V]](Place.places(), ()=>new x10.util.HashMap[K,V]());
        private def DEBUG(key:K, msg:String) { Console.OUT.println("At " + here + ": key=" + key + ": " + msg); }
        public def save(key:K, value:V) {
            if (verbose>=1) DEBUG(key, "save called");
            /* Store the copy of value locally */
           finish //TODO: remove this workaround (see XTENLANG-3260)
            at (here) atomic { hm().put(key, value); } // value is deep-copied by "at"
            if (verbose>=1) DEBUG(key, "backed up locally");
            /* Backup the value in another place */
            var backupPlace:Long = key.hashCode() % MAX_PLACES;
            if (backupPlace < 0) backupPlace += MAX_PLACES;
            var trial:Long;
            for (trial = 0L; trial < MAX_PLACES; trial++) {
                if (backupPlace != here.id && !Place.isDead(backupPlace)) break; // found appropriate place
                backupPlace = (backupPlace+1) % MAX_PLACES;
            }
            if (trial == MAX_PLACES) {
                /* no backup place available */
                if (verbose>=1) DEBUG(key, "no backup place available");
            } else {
               finish //TODO: remove this workaround (see XTENLANG-3260)
                at (Place(backupPlace)) atomic { hm().put(key, value); }
                if (verbose>=1) DEBUG(key, "backed up to place " + backupPlace);
            }
            if (verbose>=1) DEBUG(key, "save returning");
        }
        public def load(key:K) {
            if (verbose>=1) DEBUG(key, "load called");
            /* First, try to load locally */
            try {
                var value:V;
               finish //TODO: remove this workaround (see XTENLANG-3260)
                value = at (here) {
                  var v:V; atomic { v = hm().getOrThrow(key); } v
                }; // value is deep-copied by "at"
                if (verbose>=1) DEBUG(key, "restored locally");
                if (verbose>=1) DEBUG(key, "load returning");
                return value;
            } catch (e:Exception) {
                if (verbose>=1) DEBUG(key, "local restore failed with exception " + e);
                /* falls through, check other places */
            }
            /* Try to load from another place */
            var backupPlace:Long = key.hashCode() % MAX_PLACES;
            if (backupPlace < 0) backupPlace += MAX_PLACES;
            var trial:Long;
            for (trial = 0L; trial < MAX_PLACES; trial++) {
                if (backupPlace != here.id && !Place.isDead(backupPlace)) {
                    if (verbose>=1) DEBUG(key, "checking backup place " + backupPlace);
                    try {
                        var value:V;
                       finish //TODO: remove this workaround (see XTENLANG-3260)
                        value = at (Place(backupPlace)) {
                          var v:V; atomic { v = hm().getOrThrow(key); } v
                        };
                        if (verbose>=1) DEBUG(key, "restored from backup place " + backupPlace);
                        if (verbose>=1) DEBUG(key, "load returning");
                        return value;
                    } catch (e:Exception) {
                        if (verbose>=1) DEBUG(key, "failed with exception " + e);
                        /* falls through, try next place */
                    }
                }
                backupPlace = (backupPlace+1) % MAX_PLACES;
            }
            if (verbose>=1) DEBUG(key, "no backup found, ERROR");
            if (verbose>=1) DEBUG(key, "load throwing exception");
            throw new Exception("No data for key " + key);
        }
        public def delete(key:K) {
            finish for (pl in Place.places()) {
                if (pl.isDead()) continue;
                at (pl) async atomic { hm().remove(key); }
            }
        }
        public def deleteAll() {
            finish for (pl in Place.places()) {
                if (pl.isDead()) continue;
                at (pl) async atomic { hm().clear(); }
            }
        }
    }
    
    /**
     * Hazelcast-based implementation of ResilientStore
     */
    static class ResilientStoreForAppHC[K,V]{V haszero} extends ResilientStoreForApp[K,V] {
        private static seqNum = new x10.util.concurrent.AtomicLong();
        private def DEBUG(msg:String) { Console.OUT.println(msg); Console.OUT.flush(); }
        private val uniqueName:String;
        private transient var map:x10.util.resilient.ResilientMap[K,V] = null; // must be initialized at each place
        private def this() { uniqueName = "ResilientStoreForAppHC_"+here.id+"_"+seqNum.incrementAndGet(); }
        private def getMap() = (map!=null) ? map : x10.util.resilient.ResilientMap.getMap[K,V](uniqueName);

        public def save(key:K, value:V) {
            if (verbose>=1) DEBUG("save: key=" + key);
            getMap().put(key, value);
        }
        public def load(key:K) {
            if (verbose>=1) DEBUG("load: key=" + key);
            val value = getMap().get(key); //TODO: throw NoSuchElementException?
            if (verbose>=1) DEBUG("load returning "+value);
            return value; 
        }
        public def delete(key:K):void {
            if (verbose>=1) DEBUG("delete: key=" + key);
            getMap().remove(key);
        }
        public def deleteAll() {
            if (verbose>=1) DEBUG("deleteAll");
            getMap().clear();
        }
    }
    
    /**
     * Test program, should print "0 1 2 ..."
     * 
     * Usage: [X10_RESILIENT_STORE_MODE=1] [X10_RESILIENT_STORE_VERBOSE=1] \
     *         X10_RESILIENT_MODE=1 X10_NPLACES=4 \
     *         x10 x10.resilient.util.ResilientStoreForApp
     */
    public static def main(ars:Rail[String]) {
        val MAX_PLACES = Place.numPlaces();
        if (MAX_PLACES < 3) throw new Exception("numPlaces should be >=3");
       
        val rs = ResilientStoreForApp.make[Place,Rail[String]]();
        
        /* Store at each place */
        finish for (i in 0..(MAX_PLACES-1)) {
            val pl = Place(i);
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
            for (i in 0..(MAX_PLACES-1)) {
                val pl = Place(i);
                val data = rs.load(pl);
                Console.OUT.print(data(0) + " ");
            }
            Console.OUT.println();
        }
    }
}
