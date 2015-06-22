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

package x10.util.resilient;

import x10.compiler.Native;
import x10.util.NoSuchElementException;

import x10.util.Collection;
import x10.util.Map;
import x10.util.Set;

/**
 * The ResilientMap abstract class defines an interface for resilient stores.
 * There is currently a Hazelcast implementation of a resilient store which
 * implements this interface for managed X10.  Native implementations are also
 * possible (but not yet implemented).
 */
public abstract class ResilientMap[K,V] {V haszero} implements ResilientBaseMap[K,V], Map[K,V] {

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
     * Remove any value associated with key k from the resilient map.
	 * Unlike {@link #remove(K)}, this methods does not return the old value,
     * and so may be more efficient.
	 * @see #remove(K)
     */
	public abstract def deleteVoid(k:K):void;

	// Note that deleteVoid is more efficient
	public def delete(k:K):boolean {
		return remove(k) != null;
	}

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
     * Shorthand for {@link #get}
     */
    public operator this(k:K):V
		= get(k);

	public def getOrElse(k:K, orElse:V):V {
		val v = get(k);
		return v == null ? orElse : v;
	}

	public def getOrThrow(k:K):V {
		val v = get(k);
		if(v == null) {
			throw new NoSuchElementException();
		}
		return v;
	}

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
     * Shorthand for {@link #put}
     */
    public operator this(k:K)=(v:V):V
		= put(k,v);
	
    /**
     * Asynchronously put value v with key k in the resilient map returning
     * a future that when forced will return the previous value (if any) 
     * that was stored for key k.  
     * The activity created to do the remove will be registered with the
     * dynamically enclosing finish.
     */
    public abstract def asyncPut(k:K, v:V):()=>V;

	/**
     * If key k does not have a value, associate value v with key k
	 * in the resilient map.
     */
    public abstract def putIfAbsent(k:K, v:V):V;

    /**
     * Remove any value associated with key k from the resilient map.
     */
    public abstract def remove(k:K):V;

    /**
     * Asynchronously remove the given key from the resilient map returning
     * a future that when forced will return the previous value (if any) 
     * that was stored for key k.
     * The activity created to do the remove will be registered with the
     * dynamically enclosing finish.
     */
    public abstract def asyncRemove(k:K):()=>V;


	/**
     * Remove any value associated with key k from the resilient map if the
	 * associate value is equal to value v.
     */
    public abstract def remove(k:K, v:V):boolean;

	/**
     * If key k is associate with a value, update the resilient map,
	 * associating key k with value v.
     */
    public abstract def replace(k:K, v:V):V;

	/**
     * If key k is associate with value oldValue, update the resilient map,
	 * associating key k with value newValue.
     */
    public abstract def replace(k:K, oldValue:V, newValue:V):boolean;

	/**
     * Associate value v with key k in the resilient map.
     * Similar to {@link #put(K,V)}, but does not return 
	 * the old value (and so can be more efficient).
     * @see #put(K,V)
     */
    public abstract def set(k: K, v: V):void;

    /**
     * Return number of key-value pairs in the resilient map.
     */
    public abstract def size(): Long;

    /**
     * Applies the user defined EntryProcessor to the entry mapped by the key
     * with specified ExecutionCallback to listen event status and returns
     * immediately.
     */
    public abstract def submitToKey(k:K, entryProcessor:(Entry[K,V])=>Any):Any;

	/**
     * Applies the user defined EntryProcessor to the entry mapped by the key
     * with specified ExecutionCallback to listen event status and returns
     * immediately with a future. When forced, it will wait until the operation is done
	 * and return the result of the entryProcessor.
     *
     */
    public abstract def asyncSubmitToKey(k:K, entryProcessor:(Entry[K,V])=>Any):()=>Any;

    /**
     * Releases the lock for the specified key.
     */
    public abstract def unlock(k:K):void;

	/**
     * Return a set of all keys in the map.
     */
    public abstract def keySet():Set[K];

	/**
	 * Returns a set of all the entries in the map.
	 */
    public abstract def entrySet():Set[Entry[K,V]];

	/**
	 * Returns a set of all the entries in the map.
	 */
	public def entries():Set[Entry[K,V]]
		= entrySet();

	/**
	 * Returns a set of all the values in the map.
	 */
	public abstract def values():Collection[V];

	/**
     * Return a set of all the keys in the map whose entry satisfies a given predicate.
     */
	public abstract def keySet(predicate:(Entry[K,V])=>boolean):Set[K];

	/**
     * Return a set of all the entries that satisfy a given predicate.
     */
    public abstract def entrySet(predicate:(Entry[K,V])=>boolean):Set[Entry[K,V]];

	/**
     * Return a set of all the entries that satisfy a given predicate.
     */
	public def entries(predicate:(Entry[K,V])=>boolean):Set[Entry[K,V]]
		= entrySet(predicate);

	/**
     * Return a set of all the values in the map whose entry satisfies a given predicate.
     */
	public abstract def values(predicate:(Entry[K,V])=>boolean):Collection[V];
}
