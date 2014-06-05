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

package x10.util.resilient;

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.interop.Java;
import x10.util.Box;

import java.util.Set;
import java.util.concurrent.Future;

/**
 * The ResilientMap class implements a resilient Map using Hazelcast as the underlying implementation.
 */

public class ResilientMap[K,V] {

    var keyValueMap: com.hazelcast.core.IMap;


    /**
     * Get the key-value map.
     */
    @Native("java", "x10.x10rt.X10RT.getResilientMap(#mapName)")
    native def getMap(mapName: String): com.hazelcast.core.IMap;


    /**
     * Associate key-value map with mapName.
     */
    public def associate(mapName: String): void {
        keyValueMap = this.getMap(mapName);
    }

    /**
     * Remove all key-value pairs from the resilient map.
     */
    @Native("java", "(keyValueMap).clear()")
    public native def clear(): void;
	
	
    /**
     * Check if the resilient map contains key k.
     */
    @Native("java", "(keyValueMap).containsKey(#k)")
    public native def containsKey(k: K): Boolean;

    /**
     * Check if the resilient map contains value v.
     */
    @Native("java", "(keyValueMap).containsValue(#v)")
    public native def containsValue(v: V): Boolean;

    /**
     * Return a set view of the mappings contained in this map.
     */
    public def entrySet(): java.util.Set {
        return keyValueMap.entrySet();
    };

    /**
     * Applies the user defined EntryProcessor to the all entries in the map.
     * Returns the results mapped by each key in the map. 
     */
    public def executeOnEntries(entryProcessor: com.hazelcast.map.EntryProcessor, 
                           predicate: com.hazelcast.query.Predicate): java.util.Map {
        return keyValueMap.executeOnEntries(entryProcessor, predicate);
    };

    /**
     * Release the lock for the specified key regardless of the lock owner.
     * It always successfully unlocks the key, never blocks and returns
     * immediately (according to Hazelcast javadoc). 
     * 
     */
    public def forceUnlock(k: K): void {
        keyValueMap.forceUnlock(k);
    };

    /**
     * Get the value of key k in the resilient map.
     */
//    @Native("java", "(#V)((keyValueMap).get(#k))")
    public def get(k: K): Box[V] {
        val v = keyValueMap.get(k);
        return v == null ? null : new Box[V](Java.deserialize(v as Java.array[Byte]) as V);
    };


    /**
     * Check if the resilient map contains any mappings.
     */
    @Native("java", "(keyValueMap).isEmpty()")
    public native def isEmpty(): Boolean;

    /**
     * Check lock for key k.  Return true iff lock for key k is acquired.
     * 
     */
    public def isLocked(k: K): boolean {
        return keyValueMap.isLocked(k);
    };

    /**
     * Acquires the lock for the specified key.
     */
    public def lock(k: K): void {
        keyValueMap.lock(k);
    };

    /**

    /**
     * Return native Java IMap of the map.
     */
    public def nativeMap(): com.hazelcast.core.IMap {
        return keyValueMap;
    };

    /**
     * Return a key that is specific to the current place.  Useful for
     * storing place local handles.
     */
    public static def placeSpecificKey(key: String): String {
        return key + here.id();
    }

    /**
     * Return a key that is specific to place "placeID".  Useful for
     * storing place local handles.
     */
    public static def placeSpecificKey(key: String, placeID: Long): String {
        return key + placeID;
    }

    /**
     * Associate value v with key k in the resilient map.
     */
//    @Native("java", "(#V)((keyValueMap).put(#k, #v))")
    public def put(k: K, v: V): Box[V] {
        val oldv = keyValueMap.put(k, Java.serialize(v));
        return oldv == null ? null : new Box[V](Java.deserialize(oldv as Java.array[Byte]) as V);

    };

    /**
     * Asynchronously put value v with key k in the resilient map.  Return
     * a future.  Thread can continue executing before put completes.
     */
    public def putAsync(k: K, v: V): java.util.concurrent.Future {
        return keyValueMap.putAsync(k, Java.serialize(v));
    };

    /**
     * Remove any value associated with key k from the resilient map.
     */
//    @Native("java", "(#V)((keyValueMap).remove(#k))")
    public def remove(k: K): Box[V] {
        val v = keyValueMap.remove(k);
        return v == null ? null : new Box[V](Java.deserialize(v as Java.array[Byte]) as V);
    };

    /**
     * Asynchronously remove the given key.
     */
    public def removeAsync(k: K): java.util.concurrent.Future {
        return keyValueMap.removeAsync(k);
    };

    /**
     * Return number of key-value pairs in the resilient map.
     */
    @Native("java", "(keyValueMap).size()")
    public native def size(): Long;

    /**
     * Applies the user defined EntryProcessor to the entry mapped by the key
     * with specified ExecutionCallback to listen event status and returns 
     * immediately. 
     */
    public def submitToKey(k: K, entryProcessor: com.hazelcast.map.EntryProcessor, 
                           callback: com.hazelcast.core.ExecutionCallback): void {
        keyValueMap.submitToKey(k, entryProcessor, callback);
    };

    /**
     * Acquires the lock for the specified key.
     */
    public def unlock(k: K): void {
        keyValueMap.unlock(k);
    };

    /**
     * Query map based on a predicate, return values of matching entries.
    public def values(predicate: SqlPredicate): SetJava {
        return new SetJava((keyValueMap.values(predicate.get()) as java.util.Set));
    };
*/

}
