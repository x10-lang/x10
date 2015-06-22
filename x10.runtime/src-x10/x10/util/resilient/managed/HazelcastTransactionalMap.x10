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

package x10.util.resilient.managed;

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.interop.Java;
import x10.io.Console;
import x10.io.Unserializable;
import x10.io.Deserializer;
import x10.io.Serializer;
import x10.util.resilient.ResilientTransactionalMap;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * The HazelcastMap class implements a transactional view of a resilient Map using Hazelcast as the underlying implementation.
 */

public class HazelcastTransactionalMap[K,V] {V haszero}
		 extends ResilientTransactionalMap[K,V]
			 implements Unserializable {

    protected val keyValueMap: com.hazelcast.core.TransactionalMap; // Java data structure
                                                   // storing key-value map.

    /**
     * Constructor method.  This is called by factory method getMap
     * and is not directly invoked by applications.
     */
	def this(baseMap:com.hazelcast.core.TransactionalMap) {
        this.keyValueMap = baseMap;
    }


    /** Next set of methods comprise the core API and are listed in alphabetical order */
	
    /**
     * Check if the resilient map contains key k.
     */
    public def containsKey(k: K): Boolean = keyValueMap.containsKey(k);

	/**
     * Remove any value associated with key k from the resilient map.
	 * Unlike {@link #remove(K)}, this methods does not return the old value,
     * and so may be more efficient.
	 * @see #remove(K)
     */
	public def deleteVoid(k:K) {
		keyValueMap.delete(k);
	}

    /**
     * Get the value of key k in the resilient map.
     */
    public def get(k: K):V {
        val v = keyValueMap.get(k);
        return v as V;
    };


	/**
     * Get the value of key k in the resilient map. 
	 * Also ``hints'' to the implementation that we are likely to
	 * update the key later in the transaction.
     */
    public def getForUpdate(k:K):V
		= keyValueMap.getForUpdate(k) as V;


    /**
     * Check if the resilient map contains any mappings.
     */
    public def isEmpty(): Boolean{
        return keyValueMap.isEmpty();
    };

    /**
     * Return native Java TransactionalMap of the map.  This is used for making SQL
     * queries to the map.
     */
    public def nativeMap(): Any {
        return keyValueMap as Any;
    };

    /**
     * Associate value v with key k in the resilient map.
     */
    public def put(k: K, v: V):V {
        val oldv = keyValueMap.put(k, v);
        return oldv as V;
    };
	

	/**
     * If key k does not have a value, associate value v with key k
	 * in the resilient map.
     */
    public def putIfAbsent(k:K, v:V):V
		= keyValueMap.putIfAbsent(k,v) as V;

    /**
     * Remove any value associated with key k from the resilient map.
     */
    public def remove(k: K):V {
        val v = keyValueMap.remove(k);
        return v as V;
    };

	/**
     * Remove any value associated with key k from the resilient map if the
	 * associate value is equal to value v.
     */
    public def remove(k:K, v:V):boolean
		= keyValueMap.remove(k,v);

	/**
     * If key k is associate with a value, update the resilient map,
	 * associating key k with value v.
     */
    public def replace(k:K, v:V):V
		= keyValueMap.replace(k,v) as V;

	/**
     * If key k is associate with value oldValue, update the resilient map,
	 * associating key k with value newValue.
     */
    public def replace(k:K, oldValue:V, newValue:V):boolean
		= keyValueMap.replace(k, oldValue, newValue);


	/**
     * Associate value v with key k in the resilient map.
     * Similar to {@link #put(K,V)}, but does not return 
	 * the old value (and so is more efficient).
     * @see #put(K,V)
     */
    public def set(k: K, v: V):void {
		keyValueMap.set(k, v);
    };

    /**
     * Return number of key-value pairs in the resilient map.
     */
    public def size(): Long {
        return keyValueMap.size();
    };
}
