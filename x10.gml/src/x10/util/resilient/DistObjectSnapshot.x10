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

import x10.util.HashMap;
import x10.util.Timer;
import x10.xrx.Runtime;

/**
 * A distributed snapshot of an object, which can be used to restore the
 * object to a previous correct state in case of a place failure.
 *
 * TODO this type should be parametrized by [Key,Value] types, but this is
 * not possible due to limitation of Native X10: XTENLANG-3472
 */
public abstract class DistObjectSnapshot {
    static val mode = getEnvInt("X10_RESILIENT_STORE_MODE");
    static val verbose = getEnvInt("X10_RESILIENT_STORE_VERBOSE");
    
    static val localCopy = getEnvString("X10_RESILIENT_STORE_LOCAL_COPY", "clone"); // at, deep, clone
    static val remoteCopy = getEnvString("X10_RESILIENT_STORE_REMOTE_COPY", "at"); // at, dma
    static val forceDeepCopy = 0N;
    
    static def getEnvInt(name:String) {
        val env = System.getenv(name);
        val v = (env!=null) ? Int.parseInt(env) : 0N;
        if (here==Place.FIRST_PLACE) Console.OUT.println(name + "=" + v);
        return v;
    }
    static def getEnvString(name:String, defaultValue:String) {
        val env = System.getenv(name);
        val v = (env!=null) ? env : defaultValue;
        if (here==Place.FIRST_PLACE) Console.OUT.println(name + "=" + v);
        return v;
    }

    public static def make():DistObjectSnapshot {
        switch (mode) {
            case 0N: return new DistObjectSnapshotPlace0();
            case 1N: return new DistObjectSnapshotDistributed();
            default: throw new Exception("unknown mode");
        }
    }

    public abstract def save(key:Any, value:Any):void;
    public abstract def load(key:Any):Any;
    public abstract def delete(key:Any):void;
    public abstract def deleteAll():void;

    /**
     * Place0 implementation of ResilientStore
     */
    static class DistObjectSnapshotPlace0 extends DistObjectSnapshot {
        val hm = at(Place.FIRST_PLACE) GlobalRef(new HashMap[Any,Any]());
        private def DEBUG(msg:String) { Console.OUT.println(msg); Console.OUT.flush(); }

        public def save(key:Any, value:Any) {
            if (verbose>=1) DEBUG("save: key=" + key);
            if (hm.home == here)
                saveLocal(key, value);
            else
                saveRemote(key, value);
        }

        public def load(key:Any) {
            if (verbose>=1) DEBUG("load: key=" + key);
            if (hm.home == here)
                return loadLocal(key);
            else
                return loadRemote(key);
        }

        public def delete(key:Any) {
            if (verbose>=1) DEBUG("delete: key=" + key);
            at(hm) atomic { hm().remove(key); }
        }

        public def deleteAll() {
            if (verbose>=1) DEBUG("deleteAll");
            at(hm) atomic { hm().clear(); }
        }
        
        private def saveLocal(key:Any, value:Any){hm.home==here} {
            if (localCopy.equals("at")) {
                at(hm) atomic { hm().put(key,value); } // value is deep-copied by "at"
            } else if (localCopy.equals("deep")) {
                val copiedValue = Runtime.deepCopy(value);
                atomic { hm().put(key, copiedValue ); }
            } else if (localCopy.equals("clone")) {
                val copiedValue = (value as Snapshot).clone();
                atomic { hm().put(key, copiedValue); }
            } else {
                throw new Exception("unknown local copy mode");
            }
        }
        
        private def saveRemote(key:Any, value:Any) {
            if (remoteCopy.equals("at")) {
                at(hm) atomic hm().put(key,value); // value is deep-copied by "at"
            } else if (remoteCopy.equals("dma")) {
                val hmGR = at(hm.home) GlobalRef[HashMap[Any,Any]](hm());
                (value as Snapshot).remoteCopyAndSave(key, hmGR);
            } else {
                throw new Exception("unknown remote copy mode");
            }
        }
        
        public def loadLocal(key:Any){hm.home==here}:Any {
            if (localCopy.equals("at")) {
                val value = at(hm) {
                    var v:Any; atomic { v = hm().getOrThrow(key); } v
                }; // value is deep-copied by "at"
                return value;
            } else if (localCopy.equals("deep")) {
                var v:Any;
                atomic { v = hm().getOrThrow(key); }
                return Runtime.deepCopy(v);
            } else if (localCopy.equals("clone")) {
                var v:Any;
                atomic { v = hm().getOrThrow(key); }
                return (v as Snapshot).clone();
            } else {
                throw new Exception("unknown local copy mode");
            }
        }
        
        public def loadRemote(key:Any):Any {
            if (remoteCopy.equals("at")) {
                val value = at(hm) {
                    var v:Any; atomic { v = hm().getOrThrow(key); } v
                };
                return value;
            } else if (remoteCopy.equals("dma")) { // dma
                val destPlace = here;
                val gr = at(hm) {
                    var v:Any;
                    atomic { v = hm().getOrThrow(key); }
                    (v as Snapshot).remoteClone(destPlace)
                };
                val value = gr();
                return value;
            } else {
                throw new Exception("unknown remote copy mode");
            }
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
    static class DistObjectSnapshotDistributed extends DistObjectSnapshot {
        val hm = PlaceLocalHandle.make[HashMap[Any,Any]](Place.places(), ()=>new x10.util.HashMap[Any,Any]());
        private def DEBUG(key:Any, msg:String) { Console.OUT.println("At " + here + ": key=" + key + ": " + msg); }
        public def save(key:Any, value:Any) {
            if (verbose>=1) DEBUG(key, "save called");
            /* Store the copy of value locally */
            saveLocal(key, value);
            if (verbose>=1) DEBUG(key, "backed up locally");
            /* Backup the value in another place */
            var backupPlace:Long = Math.abs(key.hashCode()) % Place.numPlaces();
            var trial:Long;
            for (trial = 0L; trial < Place.numPlaces(); trial++) {
                if (backupPlace != here.id && !Place.isDead(backupPlace)) break; // found appropriate place
                backupPlace = (backupPlace+1) % Place.numPlaces();
            }
            if (trial == Place.numPlaces()) {
                /* no backup place available */
                if (verbose>=1) DEBUG(key, "no backup place available");
            } else {
                saveRemote(key, value, backupPlace);
                if (verbose>=1) DEBUG(key, "backed up to place " + backupPlace);
            }
            if (verbose>=1) DEBUG(key, "save returning");
        }

        public def load(key:Any) {
            if (verbose>=1) DEBUG(key, "load called");
            /* First, try to load locally */
            try {
                return loadLocal(key);
            } catch (e:Exception) {
                if (verbose>=1) DEBUG(key, "local restore failed with exception " + e);
                /* falls through, check other places */
            }
            /* Try to load from another place */
            var backupPlace:Long = Math.abs(key.hashCode()) % Place.numPlaces();
            var trial:Long;
            for (trial = 0L; trial < Place.numPlaces(); trial++) {
                if (backupPlace != here.id && !Place.isDead(backupPlace)) {
                    if (verbose>=1) DEBUG(key, "checking backup place " + backupPlace);
                    try {
                        return loadRemote(key, backupPlace);
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
        public def delete(key:Any) {
            finish for (pl in Place.places()) {
                if (pl.isDead()) continue;
                at(pl) async atomic { hm().remove(key); }
            }
        }
        public def deleteAll() {
            finish for (pl in Place.places()) {
                if (pl.isDead()) continue;
                at(pl) async atomic { hm().clear(); }
            }
        }
        
        private def saveLocal(key:Any, value:Any) {
            if (localCopy.equals("at")) {
                at(here) atomic { hm().put(key, value); } // value is deep-copied by "at"
            }                
            else if (localCopy.equals("deep")) {
                val copiedValue = Runtime.deepCopy(value);
                atomic { hm().put(key, copiedValue); }
            }                    
            else if (localCopy.equals("clone")) {
                val copiedValue = (value as Snapshot).clone();
                atomic { hm().put(key, copiedValue); }
            }
            else
                throw new Exception("unknown local copy mode");
        }
        
        private def saveRemote(key:Any, value:Any, backupPlace:Long) {
            if (remoteCopy.equals("at")) {
                at(Place(backupPlace)) atomic { hm().put(key, value); }
            } else if (remoteCopy.equals("dma")) {
                val hmGR = at(Place(backupPlace)) GlobalRef[HashMap[Any,Any]](hm());
                (value as Snapshot).remoteCopyAndSave(key, hmGR);
            } else {
                throw new Exception("unknown remote copy mode");
            }
        }

        public def loadLocal(key:Any):Any {
            if (localCopy.equals("at")) {
                val value = at(here) {
                    var v:Any; atomic { v = hm().getOrThrow(key); } v
                };
                return value;
            } else if (localCopy.equals("deep")) {
                var v:Any;
                atomic { v = hm().getOrThrow(key); }
                return Runtime.deepCopy(v);
            } else if (localCopy.equals("clone")) {
                var v:Any;
                atomic { v = hm().getOrThrow(key); }
                return (v as Snapshot).clone();
            } else {
                throw new Exception("unknown local copy mode");
            }
        }
        
        public def loadRemote(key:Any, backupPlace:Long):Any {
            if (remoteCopy.equals("at")) {
                val value = at(Place(backupPlace)) {
                    var v:Any; atomic { v = hm().getOrThrow(key); } v
                };
                return value;
            } else if (remoteCopy.equals("dma")) {
                val destPlace = here;
                val gr = at(Place(backupPlace)) {
                    var v:Any;
                    atomic { v = hm().getOrThrow(key); }
                    (v as Snapshot).remoteClone(destPlace)
                };
                val value = gr();
                return value;
            } else {
                throw new Exception("unknown remote copy mode");
            }
        }
    }
}
