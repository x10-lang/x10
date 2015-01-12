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

package x10.runtime.impl.java;


public abstract class ArrayUtils {

    public static <T> x10.regionarray.Array<T> makeArrayFromJavaArray(x10.rtt.Type type, Object value) {
        int length = type.arrayLength(value);
        x10.regionarray.Array<T> array = new x10.regionarray.Array<T>((java.lang.System[]) null, type).x10$regionarray$Array$$init$S(length);
        java.lang.System.arraycopy(value, 0, array.raw.value, 0, length);
        return array;
    }

    public static <T> x10.core.Rail<T> makeRailFromJavaArray(x10.rtt.Type type, Object value, boolean copy) {
        int length = type.arrayLength(value);
        x10.core.Rail<T> rail;
        if (copy) {
            rail = new x10.core.Rail<T>(type, length);
            java.lang.System.arraycopy(value, 0, rail.value, 0, length);
        } else {
            rail = new x10.core.Rail<T>(type, length, value);
        }
        return rail;
    }

    public static <T> x10.core.Rail<T> makeRailFromJavaArray(x10.rtt.Type type, Object value) {
        return makeRailFromJavaArray(type, value, true);
    }

}
