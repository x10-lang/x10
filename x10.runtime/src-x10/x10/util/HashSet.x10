/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.util;

public class HashSet[T] extends MapSet[T] {
    public def this() { super(new HashMap[T,Boolean]()); }
    public def this(sz:Int) { super(new HashMap[T,Boolean](sz)); }
    public def this(map:HashMap[T,Boolean]) { super(map); }
    public def clone():HashSet[T] {
        val tmp = new HashMap[T,Boolean](map.size());
        for (e in map.entries()) {
           tmp.put(e.getKey(), e.getValue());
        }
        return new HashSet[T](tmp);
    }
}
