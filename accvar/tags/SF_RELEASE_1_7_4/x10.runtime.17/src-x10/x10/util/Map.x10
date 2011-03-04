/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.util;

public interface Map[-K,V] /* @EQ {K <: Equals[K]} */ {
	public def containsKey(k: K): boolean;

	public def get(k: K): Box[V];

	public def put(k: K, v: V): Box[V];

	public def remove(k: K): Box[V];

	public def keySet(): Set[K];

	public def clear(): void;
	
	public def entries(): Set[Entry[K,V]];
	
	public interface Entry[Key,Val] {
	    public def getKey(): Key;
	    public def getValue(): Val;
	    public def setValue(Val): void;
	}
}
