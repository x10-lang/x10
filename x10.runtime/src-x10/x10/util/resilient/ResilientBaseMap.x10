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
import x10.compiler.NativeRep;

/**
 * The ResilientBaseMap interface provides simple operations on resilient stores
 * @see ResilientMap
 * @see ResilientTransactionalMap
 */
public interface ResilientBaseMap[K,V] {V haszero} {

    /**
     * Check if the resilient map contains key k.
     */
    public def containsKey(k:K):Boolean;

	/**
     * Remove any value associated with key k from the resilient map.
	 * Unlike {@link #remove(K)}, this methods does not return the old value,
     * and so may be more efficient.
	 * @see #remove(K)
     */
	public def deleteVoid(k:K):void;

    /**
     * Get the value of key k in the resilient map.
     */
    public def get(k:K):V;

	/**
     * Shorthand for {@link #get}
     */
    public operator this(k:K):V;

    /**
     * Check if the resilient map contains any mappings.
     */
    public def isEmpty():Boolean;

    /**
     * Associate value v with key k in the resilient map.
     */
    public def put(k:K, v:V):V;

	/**
     * Shorthand for {@link #put}
     */
    public operator this(k:K)=(v:V):V;
	
	/**
     * If key k does not have a value, associate value v with key k
	 * in the resilient map.
     */
    public def putIfAbsent(k:K, v:V):V;

    /**
     * Remove any value associated with key k from the resilient map.
     */
    public def remove(k:K):V;

	/**
     * Remove any value associated with key k from the resilient map if the
	 * associate value is equal to value v.
     */
    public def remove(k:K, v:V):boolean;

	/**
     * If key k is associate with a value, update the resilient map,
	 * associating key k with value v.
     */
    public def replace(k:K, v:V):V;

	/**
     * If key k is associate with value oldValue, update the resilient map,
	 * associating key k with value newValue.
     */
    public def replace(k:K, oldValue:V, newValue:V):boolean;

	/**
     * Associate value v with key k in the resilient map.
     * Similar to {@link #put(K,V)}, but does not return 
	 * the old value (and so can be more efficient).
     * @see #put(K,V)
     */
    public def set(k: K, v: V):void;

    /**
     * Return the number of key-value pairs in the resilient map.
     */
    public def size(): Long;
}
