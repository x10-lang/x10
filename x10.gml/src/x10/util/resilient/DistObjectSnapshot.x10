/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 *  (C) Copyright Sara Salem Hamouda 2014.
 */
package x10.util.resilient;

public abstract class DistObjectSnapshot[K,V] {V haszero} {
    static val mode = getEnvInt("X10_RESILIENT_STORE_MODE");
    static val verbose = getEnvInt("X10_RESILIENT_STORE_VERBOSE");
    static def getEnvInt(name:String) {
        val env = System.getenv(name);
        val v = (env!=null) ? Int.parseInt(env) : 0N;
        if (here==Place.FIRST_PLACE) Console.OUT.println(name + "=" + v);
        return v;
    }
    public static def make[K,V](){V haszero}:DistObjectSnapshot[K,V] {
        switch (mode) {
        case 0N: return new DistObjectSnapshotPlace0[K,V]();
        case 1N: return new DistObjectSnapshotDistributed[K,V]();
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
    static class DistObjectSnapshotPlace0[K,V] {V haszero} extends DistObjectSnapshot[K,V] {
        val hm = at (Place.FIRST_PLACE) GlobalRef(new x10.util.HashMap[K,V]());
        private def DEBUG(msg:String) { Console.OUT.println(msg); Console.OUT.flush(); }
        public def save(key:K, value:V) {
            if (verbose>=1) DEBUG("save: key=" + key);
           finish //TODO: remove this workaround (see XTENLANG-3260)
            at (hm) hm().put(key,value); // value is deep-copied by "at"
        }
        public def load(key:K) {
            if (verbose>=1) DEBUG("load: key=" + key);
            var value:V;
           finish //TODO: remove this workaround (see XTENLANG-3260)
            value = at (hm) hm().getOrThrow(key); // value is deep-copied by "at"
            return value;
        }
        public def delete(key:K) {
            if (verbose>=1) DEBUG("delete: key=" + key);
           finish //TODO: remove this workaround (see XTENLANG-3260)
            at (hm) hm().remove(key);
        }
        public def deleteAll() {
            if (verbose>=1) DEBUG("deleteAll");
           finish //TODO: remove this workaround (see XTENLANG-3260)
            at (hm) hm().clear();
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
    static class DistObjectSnapshotDistributed[K,V] {V haszero} extends DistObjectSnapshot[K,V] {
        val hm = PlaceLocalHandle.make[x10.util.HashMap[K,V]](Place.places(), ()=>new x10.util.HashMap[K,V]());
        private def DEBUG(key:K, msg:String) { Console.OUT.println("At " + here + ": key=" + key + ": " + msg); }
        public def save(key:K, value:V) {
            if (verbose>=1) DEBUG(key, "save called");
            /* Store the copy of value locally */
           finish //TODO: remove this workaround (see XTENLANG-3260)
            at (here) hm().put(key, value); // value is deep-copied by "at"
            if (verbose>=1) DEBUG(key, "backed up locally");
            /* Backup the value in another place */
            var backupPlace:Long = key.hashCode() % Place.numPlaces();
            var trial:Long;
            for (trial = 0L; trial < Place.numPlaces(); trial++) {
                if (backupPlace != here.id && !Place.isDead(backupPlace)) break; // found appropriate place
                backupPlace = (backupPlace+1) % Place.numPlaces();
            }
            if (trial == Place.numPlaces()) {
                /* no backup place available */
                if (verbose>=1) DEBUG(key, "no backup place available");
            } else {
               finish //TODO: remove this workaround (see XTENLANG-3260)
                at (Place(backupPlace)) hm().put(key, value);
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
                value = at (here) hm().getOrThrow(key); // value is deep-copied by "at"
                if (verbose>=1) DEBUG(key, "restored locally");
                if (verbose>=1) DEBUG(key, "load returning");
                return value;
            } catch (e:Exception) {
                if (verbose>=1) DEBUG(key, "local restore failed with exception " + e);
                /* falls through, check other places */
            }
            /* Try to load from another place */
            var backupPlace:Long = key.hashCode() % Place.numPlaces();
            var trial:Long;
            for (trial = 0L; trial < Place.numPlaces(); trial++) {
                if (backupPlace != here.id && !Place.isDead(backupPlace)) {
                    if (verbose>=1) DEBUG(key, "checking backup place " + backupPlace);
                    try {
                        var value:V;
                       finish //TODO: remove this workaround (see XTENLANG-3260)
                        value = at (Place(backupPlace)) hm().getOrThrow(key);
                        if (verbose>=1) DEBUG(key, "restored from backup place " + backupPlace);
                        if (verbose>=1) DEBUG(key, "load returning");
                        return value;
                    } catch (e:Exception) {
                        if (verbose>=1) DEBUG(key, "failed with exception " + e);
                        /* falls through, try next place */
                    }
                }
                backupPlace = (backupPlace+1) % Place.numPlaces();
            }
            if (verbose>=1) DEBUG(key, "no backup found, ERROR");
            if (verbose>=1) DEBUG(key, "load throwing exception");
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
     * 
     * Usage: [X10_RESILIENT_STORE_MODE=1] [X10_RESILIENT_STORE_VERBOSE=1] \
     *         X10_RESILIENT_MODE=1 X10_NPLACES=4 \
     *         x10 x10.resilient.util.DistObjectSnapshot
     */
    public static def main(ars:Rail[String]) {
        val MAX_PLACES = Place.numPlaces();
        if (MAX_PLACES < 3) throw new Exception("numPlaces should be >=3");
       
        val rs = DistObjectSnapshot.make[Place,Rail[String]]();
        
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
