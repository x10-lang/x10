/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime;

public class NativeHashMap<K,V> extends java.util.HashMap<K,V> {
	// HACK discard type parameters
	public NativeHashMap(Object typeK, Object typeV) { super(); }
}
