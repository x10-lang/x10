/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.util;

public interface Map[-K,V] extends (K)=>Box[V] {
	public def containsKey(k: K): boolean;

	public def get(k: K): Box[V];

	public def put(k: K, v: V): Box[V];

	public def remove(k: K): Box[V];

	public def keySet(): Set[K];

	public def clear(): void;
	
	public def entries(): Set[Entry[K,V]];
	
	public interface Entry[K,V] {
	    public def getKey(): K;
	    public def getValue(): V;
	    public def setValue(V): void;
	}
}
