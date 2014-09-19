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

package x10.util.resilient.managed;

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.interop.Java;
import x10.io.Console;
import x10.io.CustomSerialization;
import x10.io.Deserializer;
import x10.io.Serializer;
import x10.util.resilient.ResilientMap;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * The HazelcastMap class implements a resilient Map using Hazelcast as the underlying implementation.
 */

public class HazelcastMap[K,V] {V haszero} extends ResilientMap[K,V] implements
CustomSerialization {

    protected var entrySet: java.util.Set;  // for creating Java Set view of
                                             // all elements in map
    protected var iterator: java.util.Iterator;  // for iterating over all
                                               // entries in key-value map
    protected val keyValueMap: com.hazelcast.core.IMap; // Java data structure
                                                   // storing key-value map.
    protected var mapEntry: java.util.Map.Entry; // Stores map entries when
                                        // iterating over elements in map
    protected val mapName: String;  // name of key-value map


    /** First methods are for creating the Hazelcast resilient map **/

    /**
     * Factory method to create Hazelcast map.
     */
    public static def getMap[K,V](mapName:String){V haszero}:ResilientMap[K,V] = new HazelcastMap[K,V](mapName);

    /**
     * Constructor method.  This is called by factory method getMap
     * and is not directly invoked by applications.
     */
    protected def this(mapName: String) {
        this.keyValueMap = getResilientMap(mapName);
        if (keyValueMap == null) 
	    throw new Exception("HazelcastMap.this("+mapName+"): HazelcastMap is not supported in this configuration");
        this.mapName = mapName;
	this.entrySet = null;
	this.iterator = null;
	this.mapEntry = null;
    }

    /**
     * Helper method called by constructor method to get Java data structure
     * storing the map.
     */    
    @Native("java", "x10.x10rt.X10RT.getResilientMap(#mapName)")
    private native static def getResilientMap(mapName:String): com.hazelcast.core.IMap;



    /** Next set of methods comprise the core API and are listed in alphabetical order */

    /**
     * Remove all key-value pairs from the resilient map.
     */
    public def clear(): void {
        keyValueMap.clear();
    }	
	
    /**
     * Check if the resilient map contains key k.
     */
    public def containsKey(k: K): Boolean = keyValueMap.containsKey(k);

    /**
     * Check if the resilient map contains value v.
     */
    public def containsValue(v: V): Boolean = keyValueMap.containsValue(v);

    /**
     * Applies the user defined EntryProcessor to the all entries in the map.
     * Returns the results mapped by each key in the map. 
     */
/*
    public def executeOnEntries(entryProcessor: com.hazelcast.map.EntryProcessor, 
                           predicate: com.hazelcast.query.Predicate): java.util.Map {
        return keyValueMap.executeOnEntries(entryProcessor, predicate);
    };
*/

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
    public def get(k: K):V {
        val v = keyValueMap.get(k);
        return v as V;
    };


    /**
     * Check if the resilient map contains any mappings.
     */
    public def isEmpty(): Boolean{
        return keyValueMap.isEmpty();
    };

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
     * Return native Java IMap of the map.  This is used for making SQL
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
     * Asynchronously put value v with key k in the resilient map returning
     * a future that when forced will return the previous value (if any) 
     * that was stored for key k.  
     */
    public def asyncPutFuture(k:K, v:V):()=>V {
        val future = keyValueMap.putAsync(k, v);
        return ()=>{
            var result:V = Zero.get[V]();
            try {
                val evaluatedFuture = future.get();
                if (evaluatedFuture != null)
		    result = evaluatedFuture as V;
            } catch (e:java.lang.InterruptedException) {
                throw new WrappedThrowable(e);
            } catch (e:java.util.concurrent.ExecutionException) {
                throw new WrappedThrowable(e);
            }
            return result;
        };
    }

    /**
     * Asynchronously put value v with key k in the resilient map.
     * The activity created to do the put will be registered with the
     * dynamically enclosing finish.
     */
    public def asyncPut(k:K, v:V):void {
        async { put(k,v); };
    }


    /**
     * Remove any value associated with key k from the resilient map.
     */
    public def remove(k: K):V {
        val v = keyValueMap.remove(k);
        return v as V;
    };

    /**
     * Asynchronously remove the given key from the resilient map returning
     * a future that when forced will return the previous value (if any) 
     * that was stored for key k.  
     */
    public def asyncRemoveFuture(k:K):()=>V {
        val future = keyValueMap.removeAsync(k);
        return ()=>{
            var result: V = Zero.get[V]();
            try {
                val evaluatedFuture = future.get();
                if (evaluatedFuture != null) {
		    result = evaluatedFuture as  V;
                }
            } catch (e:java.lang.InterruptedException) {
                throw new WrappedThrowable(e);
            } catch (e:java.util.concurrent.ExecutionException) {
                throw new WrappedThrowable(e);
            }
            return result;
        };
    }

    /**
     * Asynchronously remove the given key.
     * The activity created to do the remove will be registered with the
     * dynamically enclosing finish.
     */
    public def asyncRemove(k:K):void {
        async { remove(k); }
    }

    /**
     * Return number of key-value pairs in the resilient map.
     */
    public def size(): Long {
        return keyValueMap.size();
    };

    /**
     * Applies the user defined EntryProcessor to the entry mapped by the key
     * with specified ExecutionCallback to listen event status and returns 
     * immediately. 
     */
    public def submitToKey(k:K, entryProcessor:(Entry[K,V])=>Any, callback:(Any)=>void):void {
        val p = new com.hazelcast.map.EntryProcessor() {
            public def process(entry:java.util.Map.Entry):Any {
                return entryProcessor(entry as Entry[K,V]);
            }
            public def getBackupProcessor():com.hazelcast.map.EntryBackupProcessor = null;
        };
        val c = new com.hazelcast.core.ExecutionCallback() {
            public def onFailure(CheckedThrowable):void { Runtime.println("submitToKey failed"); } // TODO
            public def onResponse(result:Any):void { callback(result); } // TODO: submit callback to thread pool
        };
        keyValueMap.submitToKey(k, p, c);
    }

    /**
     * Releases the lock for the specified key.
     */
    public def unlock(k: K): void {
        keyValueMap.unlock(k);
    };

    /** The following methods are for iterating over/examining all entries in the map */

    /**
     * Get key from map entry.
     */
    public def getKey(): K {
        if (mapEntry == null)
	    throw new Exception("HazelcastMap.getKey(): Error: mapEntry is null.  mapEntry must be instantiated (e.g. by iterator.next) before it can be used.");
	return mapEntry.getKey() as K;
    };

    /**
     * Get value from map entry.
     */
    public def getValue(): V {
        if (mapEntry == null)
	    throw new Exception("HazelcastMap.getValue(): Error: mapEntry is null.  mapEntry must be instantiated (e.g. by iterator.next) before it can be used.");
        return mapEntry.getValue() as V;
    };

    /**
     * Initialize iterator for iterating over elements of map.
     */
    public def initializeIterator(): void {
        entrySet = keyValueMap.entrySet();
	iterator = entrySet.iterator();
    };

    /**
     * Determine if iteration has finished yet.
     */
    public def iteratorHasNext(): Boolean {
        if (iterator == null)
	    return false;
        return iterator.hasNext();
    };

    /**
     * Get next element of iterator.
     */
    public def iteratorNext(): void {
        if (iterator == null)
	    throw new Exception("HazelcastMap.iterator.next(): Error: Null iterator.  Iterator must be initialized (e.g. by initializeIterator) before it can be used.");
	mapEntry = iterator.next() as java.util.Map.Entry;
    };




    /** The following methods handle serialization and deserialization */


    /*
     * Custom serialization
     */
    public def serialize(s:Serializer) {
        s.writeAny(mapName);
    }

    /*
     * Custom deserialization
     */
    public def this(ds:Deserializer) {
        this(ds.readAny() as String);
    }

}
