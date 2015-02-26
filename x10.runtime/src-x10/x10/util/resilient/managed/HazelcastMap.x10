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
import x10.io.CustomSerialization;
import x10.io.Deserializer;
import x10.io.Serializer;
import x10.util.resilient.ResilientMap;

import x10.util.AbstractCollection;
import x10.util.Collection;
import x10.util.HashSet;
import x10.util.Map;
import x10.util.Set;

/**
 * The HazelcastMap class implements a resilient Map using Hazelcast as the underlying implementation.
 */

public class HazelcastMap[K,V] {V haszero} extends ResilientMap[K,V] implements
CustomSerialization {

    protected val keyValueMap: com.hazelcast.core.IMap; // Java data structure
                                                   // storing key-value map.
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
     * Remove any value associated with key k from the resilient map.
	 * Unlike {@link #remove(K)}, this methods does not return the old value,
     * and so may be more efficient.
	 * @see #remove(K)
     */
	public def deleteVoid(k:K) {
		keyValueMap.delete(k);
	}

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
    public def asyncPut(k:K, v:V):()=>V {
        val future = keyValueMap.putAsync(k, v);
		return createFuture[V](future);
    }

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
     * Asynchronously remove the given key from the resilient map returning
     * a future that when forced will return the previous value (if any) 
     * that was stored for key k.  
     */
    public def asyncRemove(k:K):()=>V {
        val future = keyValueMap.removeAsync(k);
        return createFuture[V](future);
    }

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

	 /**
     * Applies the user defined EntryProcessor to the entry mapped by the key
     */
    public def submitToKey(k:K, entryProcessor:(Entry[K,V])=>Any):Any {
		val p = new com.hazelcast.map.EntryProcessor() {
            public def process(entry:java.util.Map.Entry):Any {
                return entryProcessor(convert[K,V](entry));
            }
            public def getBackupProcessor():com.hazelcast.map.EntryBackupProcessor = null;
        };
		val future = keyValueMap.submitToKey(k, p);
		return force(future);
	}

	/**
     * Applies the user defined EntryProcessor to the entry mapped by the key
     * with specified ExecutionCallback to listen event status and returns
     * immediately with a future. When forced, it will wait until the operation is done
	 * and return the result of the entryProcessor.
     * The activity created to do the remove will be registered with the
     * dynamically enclosing finish.
     */
    public def asyncSubmitToKey(k:K, entryProcessor:(Entry[K,V])=>Any):()=>Any {
		val p = new com.hazelcast.map.EntryProcessor() {
            public def process(entry:java.util.Map.Entry):Any {
                return entryProcessor(convert[K,V](entry));
            }
            public def getBackupProcessor():com.hazelcast.map.EntryBackupProcessor = null;
        };
		val future = keyValueMap.submitToKey(k, p);
		return createFuture[Any](future);
	}
	
    /**
     * Releases the lock for the specified key.
     */
    public def unlock(k: K): void {
        keyValueMap.unlock(k);
    };

	/**
     * Return a set of all keys in the map.
     */
    public def keySet():Set[K] {
		val keySet = keyValueMap.keySet();
		return convert[K](keySet);
	}

    public def entrySet():Set[Entry[K,V]] {
		val entrySet = keyValueMap.entrySet();
		return convert[Entry[K,V]](entrySet,
								   (en:Any)=>convert[K,V](en as java.util.Map.Entry),
								   (en:Entry[K,V])=>convert[K,V](en));
	}
	
	public def values():Collection[V] {
		val values = keyValueMap.values();
		return convert[V](values);
	}

	public def keySet(predicate:(Entry[K,V])=>boolean):Set[K] {
		val keySet = keyValueMap.keySet(convert(predicate));
		return convert[K](keySet);
	}

    public def entrySet(predicate:(Entry[K,V])=>boolean):Set[Entry[K,V]] {
		val entrySet = keyValueMap.entrySet(convert(predicate));
		return convert[Entry[K,V]](entrySet,
								   (en:Any)=>convert[K,V](en as java.util.Map.Entry),
								   (en:Entry[K,V])=>convert[K,V](en));
	}

	public def values(predicate:(Entry[K,V])=>boolean):Collection[V] {
		val values = keyValueMap.values(convert(predicate));
		return convert[V](values);
	}

	// create an X10 closure wrapping a java Future and register it with the enclosing Finish
	private static def createFuture[T](future:java.util.concurrent.Future) {T haszero} {
		val cl = wrapFuture[T](future);
		registerFuture(cl);
		return cl;
	}
		
	// register a future closure on the enclosing finish
	private static def registerFuture[T](future:()=>T) {
		async {
			x10.xrx.Runtime.increaseParallelism();
			try {
				// force the future inside of an async
				future();
			} finally {
				x10.xrx.Runtime.decreaseParallelism(1n);
			}
		}
	}

	// create a future
	private static def wrapFuture[T](future:java.util.concurrent.Future) {T haszero} {
		return ()=>{
            var result:T = Zero.get[T]();
			val evaluatedFuture = force(future);
			if (evaluatedFuture != null) {
				result = evaluatedFuture as T;
			}
            return result;
        };
	}

	private static def force(future:java.util.concurrent.Future):Any {
		try {
			return future.get();
		} catch (e:java.lang.InterruptedException) {
			throw new WrappedThrowable(e);
		} catch (e:java.util.concurrent.ExecutionException) {
			throw new WrappedThrowable(e);
		}
	}

	private static def convert[T](iter:java.util.Iterator):Iterator[T] {
		return new Iterator[T]() {
			public def hasNext():Boolean = iter.hasNext();
			public def next():T = iter.next() as T;
		};
	}

	private static def convert[T](iter:java.util.Iterator, from:(Any)=>T):Iterator[T] {
		return new Iterator[T]() {
			public def hasNext():Boolean = iter.hasNext();
			public def next():T = from(iter.next());
		};
	}

	private static def convert[K,V](entry:java.util.Map.Entry):Entry[K,V] {
		return new Entry[K,V]() {
			public def getKey():K = entry.getKey() as K;
			public def getValue():V = entry.getValue() as V;
			public def setValue(v:V) {
				entry.setValue(v);
			}
		};
	}
	
	private static def convert[K,V](entry:Entry[K,V]):java.util.Map.Entry {
		return new java.util.Map.Entry() {
			public def getKey():Any = entry.getKey();
			public def getValue():Any = entry.getValue();
			public def setValue(v:Any) {
				val old = getValue();
				entry.setValue(v as V);
				return old;
			}
		};
	}

	private static def convert[T](coll:java.util.Collection):Set[T] {
		class SetWrapper extends AbstractCollection[T] implements Set[T] {
			// From Iterable
			public def iterator(): Iterator[T] = convert[T](coll.iterator());
			// from Container
			public def size(): Long = coll.size();
			public def isEmpty(): Boolean = coll.isEmpty();
			public def contains(y:T): Boolean = coll.contains(y);

			// public def containsAll(c:Container[T]): Boolean;
			
			 // from Collection
			public def add(v:T): Boolean = coll.add(v);
			public def remove(v:T): Boolean = coll.remove(v);
			// public def addAll(c:Container[T]): Boolean;
			// public def retainAll(c:Container[T]): Boolean;
			// public def removeAll(c:Container[T]): Boolean;
			// public def addAllWhere(c:Container[T], p:(T) => Boolean): Boolean;
			// public def removeAllWhere(p:(T) => Boolean): Boolean;
			public def clear(): void {
				coll.clear();
			}
				
			public def clone(): Set[T] {
				val s = new HashSet[T](size() as Int);
				s.addAll(this);
				return s;
			}
		};

		return new SetWrapper();
	}

	private static def convert[T](coll:java.util.Collection, from:(Any)=>T, to:(T)=>Any):Set[T] {
		class SetWrapper extends AbstractCollection[T] implements Set[T] {
			// From Iterable
			public def iterator(): Iterator[T] = convert[T](coll.iterator(), from);
			// from Container
			public def size(): Long = coll.size();
			public def isEmpty(): Boolean = coll.isEmpty();
			public def contains(y:T): Boolean = coll.contains(to(y));

			// public def containsAll(c:Container[T]): Boolean;
			
			 // from Collection
			public def add(v:T): Boolean = coll.add(to(v));
			public def remove(v:T): Boolean = coll.remove(to(v));
			// public def addAll(c:Container[T]): Boolean;
			// public def retainAll(c:Container[T]): Boolean;
			// public def removeAll(c:Container[T]): Boolean;
			// public def addAllWhere(c:Container[T], p:(T) => Boolean): Boolean;
			// public def removeAllWhere(p:(T) => Boolean): Boolean;
			public def clear(): void {
				coll.clear();
			}
				
			public def clone(): Set[T] {
				val s = new HashSet[T](size() as Int);
				s.addAll(this);
				return s;
			}
		};

		return new SetWrapper();
	}

	private static def convert[K,V](predicate:(Entry[K,V])=>boolean):com.hazelcast.query.Predicate {
		return new com.hazelcast.query.Predicate() {
			public def apply(mapEntry:java.util.Map.Entry):boolean {
				return predicate(convert[K,V](mapEntry));
			}
		};
	}

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
