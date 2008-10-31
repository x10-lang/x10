/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "x10.runtime.impl.java.NativeHashMap<#1,#2>")
public class HashMap[K,V] implements Map[K,V] {
	public native def this();
	
	@Native("java", "#0.containsKey(#1)")
	public native def containsKey(k: K): boolean;
	
	@Native("java", "#0.get(#1)")
	public native def apply(k: K): V;

	@Native("java", "#0.get(#1)")
	public native def get(k: K): V;

	@Native("java", "#0.put(#1, #2)")
	public native def put(k: K, v: V): void;

	@Native("java", "#0.remove(#1)")
	public native def remove(k: K): V;

	@Native("java", "#0.keySet()")
	public native def keySet(): Set[K];

	@Native("java", "#0.clear()")
	public native def clear(): void;
}
