/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.util;

public interface Map[-K,V] /* @EQ {K <: Equals[K]} */ {
	public safe def containsKey(k: K): boolean;

	public safe def get(k: K): Box[V];

    public safe def getOrElse(k: K, orelse: V): V;

    public safe def getOrThrow(k: K): V throws NoSuchElementException;

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
