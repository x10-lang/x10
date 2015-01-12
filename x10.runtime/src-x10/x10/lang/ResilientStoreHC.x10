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
package x10.lang;
import x10.util.resilient.ResilientMap; // ResilientMap using Hazelcast

public class ResilientStoreHC[K,V] {V haszero} extends ResilientStore[K,V] {V haszero} {
    private static val verbose = ResilientStore.verbose;

    private transient val map:ResilientMap[K,V];
    private val lockKey:K; // Dummy key for lock/unlock

    private def this(name:String, lockKey:K) { //TODO: any good way to pass lockKey?
        map = ResilientMap.getMap[K,V](name);
        this.lockKey = lockKey;
    }
    public static def make2[K,V](name:Any, lockKey:K){V haszero}:ResilientStoreHC[K,V] { // renamed to avoid XTENLANG-3396
        if (verbose>=3) debug("ResilientStoreHC.make called, name="+name);
        val r = new ResilientStoreHC[K,V](name.toString(),lockKey);
        if (verbose>=3) debug("ResilientStoreHC.make returning result="+r);
        return r;
    }
    
    public static def delete(name:Any):void {
        if (verbose>=3) debug("delete called, name="+name);
        if (verbose>=1) debug("ResilientStoreHC.delete: do nothing"); //TODO
        if (verbose>=3) debug("delete returning");
    }
    
    public def create(key:K, value:V):void { put(key, value); }
    public def put(key:K, value:V):void {
        if (verbose>=3) debug("put called, key="+key + " value="+value);
        map.put(key, value);
        if (verbose>=3) debug("put returning");
    }
    
    public def getOrElse(key:K, orelse:V):V {
        if (verbose>=3) debug("getOrElse called, key="+key);
        val r = map.get(key);
        if (verbose>=3) debug("getOrElse returning, result="+r);
        return r;
    }
    
    public def remove(key:K):void {
        if (verbose>=3) debug("remove called, key="+key);
        map.remove(key);
        if (verbose>=3) debug("remove returning");
    }
    
    public def lock():void { //TODO: should support recursive lock?
        if (verbose>=3) debug("lock called");
        map.lock(lockKey);
        if (verbose>=3) debug("lock returning (locked)");
    }
    
    public def unlock():void {
        if (verbose>=3) debug("unlock called");
        map.unlock(lockKey);
        if (verbose>=3) debug("unlock returning (unlocked)");
    }
}
