/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

package x10.util.resilient.managed;

import x10.interop.Java;

import java.util.concurrent.Future;

import x10.util.AbstractCollection;
import x10.util.Collection;
import x10.util.HashSet;
import x10.util.Map;
import x10.util.Set;

public class InteropUtils {
	// create an X10 closure wrapping a java Future and register it with the enclosing Finish
	public static def createFuture[T](future:java.util.concurrent.Future) {T haszero} {
		val cl = wrapFuture[T](future);
		registerFuture(cl);
		return cl;
	}
		
	// register a future closure on the enclosing finish
	public static def registerFuture[T](future:()=>T) {
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
	public static def wrapFuture[T](future:java.util.concurrent.Future) {T haszero} {
		return ()=>{
            var result:T = Zero.get[T]();
			val evaluatedFuture = force(future);
			if (evaluatedFuture != null) {
				result = evaluatedFuture as T;
			}
            return result;
        };
	}

	public static def force(future:java.util.concurrent.Future):Any {
		try {
			return future.get();
		} catch (e:java.lang.InterruptedException) {
			throw new WrappedThrowable(e);
		} catch (e:java.util.concurrent.ExecutionException) {
			throw new WrappedThrowable(e);
		}
	}

	public static def convert[T](iter:java.util.Iterator):Iterator[T] {
		return new Iterator[T]() {
			public def hasNext():Boolean = iter.hasNext();
			public def next():T = iter.next() as T;
		};
	}

	public static def convert[T](iter:java.util.Iterator, from:(Any)=>T):Iterator[T] {
		return new Iterator[T]() {
			public def hasNext():Boolean = iter.hasNext();
			public def next():T = from(iter.next());
		};
	}

	public static def convert[K,V](entry:java.util.Map.Entry):Map[K,V].Entry[K,V] {
		return new Map.Entry[K,V]() {
			public def getKey():K = entry.getKey() as K;
			public def getValue():V = entry.getValue() as V;
			public def setValue(v:V) {
				entry.setValue(v);
			}
		};
	}
	
	public static def convert[K,V](entry:Map[K,V].Entry[K,V]):java.util.Map.Entry {
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

	public static def convert[T](coll:java.util.Collection):Set[T] {
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

	public static def convert[T](coll:java.util.Collection, from:(Any)=>T, to:(T)=>Any):Set[T] {
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

	public static def convert[K,V](predicate:(Map[K,V].Entry[K,V])=>boolean):com.hazelcast.query.Predicate {
		return new com.hazelcast.query.Predicate() {
			public def apply(mapEntry:java.util.Map.Entry):boolean {
				return predicate(convert[K,V](mapEntry));
			}
		};
	}
}
