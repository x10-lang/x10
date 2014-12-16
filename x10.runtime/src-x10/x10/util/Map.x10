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

package x10.util;

/**
 * A map from keys to values.
 */
public interface Map[K,V] {V haszero} {
    /**
     * Does the map contain an entry for the argument key?
     */
    public def containsKey(k:K):Boolean;

    /**
     * Return the value mapped to the argument key.
     * Will return Zero.get[T]() if !containsKey(k).
     */
    public def get(k:K):V;

    /**
     * Shorthand for @link{#get}
     */
    public operator this(k:K):V;

    /**
     * If containsKey(k), return the value mapped to k; 
     * otherwise return orlese
     */
    public def getOrElse(k:K, orelse:V):V;

    /**
     * If containsKey(k), return the value mapped to k; 
     * otherwise throw a NoSuchElementException.
     */
    public def getOrThrow(k:K):V;

    /**
     * Map k to v; returns either the old value mapped to k 
     *  or Zero.get[T]() if k was not previously mapped to a value.
     */
    public def put(k:K, v:V):V;

    /**
     * Shorthand for @link{#put}
     */
    public operator this(k:K)=(v:V):V;

    /**
     * Remove the mapping for k from the map, returning
     * a boolean to indicate whether or not k was actually
     * in the map before the call to delete. 
     */
    public def delete(k:K):boolean;

    /**
     * Remove the mapping for k from the map, returning
     * the value that was mapped to k or Zero.get[T] if
     * k was not mapped to a value.
     */
    public def remove(k:K):V;

    /**
     * Remove all entries in the map
     */
    public def clear():void;
    
    /**
     * Return a set of all keys in the map.
     */
    public def keySet():Set[K];

    /**
     * Return an entry set for the map containing all
     * keys and values.
     */
    public def entries():Set[Entry[K,V]];
    
    /**
     * Return the number of entries in the map.
     */
    public def size():Long;

    public static interface Entry[Key,Val] {
        public def getKey():Key;
        public def getValue():Val;
        public def setValue(v:Val):void;
    }
}
