/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.util;

public interface Map[K,V]   {
	public def containsKey(k: K): Boolean;

	public def get(k: K): Box[V];

    public def getOrElse(k: K, orelse: V): V;

    public def getOrThrow(k: K): V; //throws NoSuchElementException;

	public def put(k: K, v: V): Box[V];

	public def remove(k: K): Box[V];

	public def keySet(): Set[K];

	public def clear(): void;
	
	public def entries(): Set[Entry[K,V]];
	
	public def size(): Long;

	public static interface Entry[Key,Val] {
	    public def getKey(): Key;
	    public def getValue(): Val;
	    public def setValue(Val): void;
	}
}
