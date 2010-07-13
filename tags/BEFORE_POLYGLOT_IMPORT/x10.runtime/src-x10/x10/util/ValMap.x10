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

public interface ValMap[K,V] {
	// All methods are global
	public global safe def containsKey(k: K): boolean;
	public global safe def get(k: K): Box[V];
    public global safe def getOrElse(k: K, orelse: V): V;
    public global safe def getOrThrow(k: K): V throws NoSuchElementException;
	public global safe def keySet(): Set[K]!;
	public global safe def entries(): Set[Entry[K,V]]!;

	public static interface Entry[Key,Val] {
	    public global safe def getKey(): Key;
	    public global safe def getValue(): Val;
	}
}
