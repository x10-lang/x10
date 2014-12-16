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

/**
 * The ResilientMap abstract class defines an interface for resilient stores.
 * There is currently a Hazelcast implementation of a resilient store which
 * implements this interface for managed X10.  Native implementations are also
 * possible (but not yet implemented).
 */
public abstract class ResilientMap[K,V] {V haszero} {

    /**
     * Factory method to create resilient map.
     */
    @Native("java", "x10.util.resilient.managed.HazelcastMap.getMap(#K$rtt, #V$rtt, #mapName)")
    public static def getMap[K,V](mapName:String){V haszero}:ResilientMap[K,V] {
        throw new Exception("ResilientMap is not supported in this configuration");
    }


    /** Next set of methods comprise the core API and are listed in alphabetical order */

    /**
     * Remove all key-value pairs from the resilient map.
     */
    public abstract def clear():void;

    /**
     * Check if the resilient map contains key k.
     */
    public abstract def containsKey(k:K):Boolean;

    /**
     * Check if the resilient map contains value v.
     */
    public abstract def containsValue(v:V):Boolean;

    /**
     * Applies the user defined EntryProcessor to the all entries in the map.
     * Returns the results mapped by each key in the map.
     */
//  TODO: add
//  public abstract def executeOnEntries(entryProcessor:(Entry[K,V])=>Any, predicate:(Entry[K,V])=>Boolean):void;

    /**
     * Release the lock for the specified key regardless of the lock owner.
     * It always successfully unlocks the key, never blocks and returns
     * immediately (according to Hazelcast javadoc).
     *
     */
    public abstract def forceUnlock(k:K):void;

    /**
     * Get the value of key k in the resilient map.
     */
    public abstract def get(k:K):V;

    /**
     * Check if the resilient map contains any mappings.
     */
    public abstract def isEmpty():Boolean;

    /**
     * Check lock for key k.  Return true iff lock for key k is acquired.
     *
     */
    public abstract def isLocked(k:K):Boolean;

    /**
     * Acquires the lock for the specified key.
     */
    public abstract def lock(k:K):void;

    /**
     * Return underlying representation of the map.  This is used by the
     * Hazelcast resilient store for making SQL queries to the map.
     */
    public abstract def nativeMap(): Any;

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
    public abstract def put(k:K, v:V):V;

    /**
     * Asynchronously put value v with key k in the resilient map returning
     * a future that when forced will return the previous value (if any) 
     * that was stored for key k.  
     */
    public abstract def asyncPutFuture(k:K, v:V):()=>V;

    /**
     * Asynchronously put value v with key k in the resilient map.
     * The activity created to do the put will be registered with the
     * dynamically enclosing finish.
     */
    public abstract def asyncPut(k:K, v:V):void;

    /**
     * Remove any value associated with key k from the resilient map.
     */
    public abstract def remove(k:K):V;

    /**
     * Asynchronously remove the given key from the resilient map returning
     * a future that when forced will return the previous value (if any) 
     * that was stored for key k.  
     */
    public abstract def asyncRemoveFuture(k:K):()=>V;

    /**
     * Asynchronously remove the given key.
     * The activity created to do the remove will be registered with the
     * dynamically enclosing finish.
     */
    public abstract def asyncRemove(k:K):void;

    /**
     * Return number of key-value pairs in the resilient map.
     */
    public abstract def size(): Long;

    /**
     * Applies the user defined EntryProcessor to the entry mapped by the key
     * with specified ExecutionCallback to listen event status and returns
     * immediately.
     */
    public abstract def submitToKey(k:K, entryProcessor:(Entry[K,V])=>Any, callback:(Any)=>void):void;

    /**
     * Releases the lock for the specified key.
     */
    public abstract def unlock(k:K):void;


    /** The following methods are for iterating over/examining all entries in the map */

    /**
     * Get key from map entry.
     */
    public abstract def getKey(): K;

    /**
     * Get value from map entry.
     */
    public abstract def getValue(): V;

    /**
     * Initialize iterator for iterating over elements of map.
     */
    public abstract def initializeIterator(): void;

    /**
     * Determine if iteration has finished yet.
     */
    public abstract def iteratorHasNext(): Boolean;

    /**
     * Get next element of iterator.
     */
    public abstract def iteratorNext(): void;


    @NativeRep("java", "java.util.Map.Entry<#K$box,#V$box>", null, null) // TODO: fix rtt
    public static interface Entry[K,V] {
        @Native("java", "#this.getKey()")
        public def getKey():K;

        @Native("java", "#this.getValue()")
        public def getValue():V;

        @Native("java", "#this.setValue(#v)")
        public def setValue(v:V):V;
    }
}