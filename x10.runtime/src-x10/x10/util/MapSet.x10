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

public abstract class MapSet[T] extends AbstractCollection[T] implements Set[T] {
    val map: Map[T,boolean];

    public def this(map: Map[T,boolean]) { this.map = map; }

    public def size(): Int = map.keySet().size();
    public def contains(v: T): Boolean = map.containsKey(v);

    public def add(v: T): Boolean = map.put(v, true) == null;
    public def remove(v: T): Boolean = map.remove(v) != null;
    public def clear(): void { map.clear(); }
    public def iterator(): Iterator[T] = map.keySet().iterator();
}
