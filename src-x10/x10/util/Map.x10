/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.util;

public interface Map[K,V] extends (K)=>V {

	public def containsKey(k: K): boolean;

	public def get(k: K): V;

	public def put(k: K, v: V): void;

	public def remove(k: K): V;

	public def keySet(): Set[K];

	public def clear(): void;
}
