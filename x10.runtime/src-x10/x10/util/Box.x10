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

public final class Box[T] implements ()=> T {
    public val value:T;
    public def this(x:T) { value = x; }

    public operator this() = value;
    public def hashCode():Int = value.hashCode();

    public def toString():String = value.toString();

    public def equals(x:Any):Boolean {
        if (x == null) {
            return false;
        }
        if (x instanceof T) {
            val y = x as T;
            return value.equals(y);
        }
        if (x instanceof Box[T]) {
            val y = (x as Box[T]).value;
            return value.equals(y);
        }
        return false;
    }

    //public static operator[T](x:T):Box[T] = new Box[T](x);
}
