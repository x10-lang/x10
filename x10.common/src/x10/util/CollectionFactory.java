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

import java.util.*;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.lang.reflect.Array;

public class CollectionFactory
{
    public static <K,V> Map<K,V> newHashMap() {
        return new SmallMap<K,V>();
    }
    public static <K,V> Map<K,V> newHashMap(Map<K,V> map) {
        return new SmallMap<K,V>(map);
    }
    public static <K,V> Map<K,V> newHashMap(int size) {
        return new SmallMap<K,V>(size);
    }
    public static <K,V> Map<K,V> newHashMap(int size, float loadFactor) { // only in two places in X10CPPContext_c
        return new LinkedHashMap<K,V>(size,loadFactor);
    }

    public static <K> Set<K> newHashSet() {
        return new SmallSet<K>();
    }
    public static <K> Set<K> newHashSet(Collection<K> set) {
        return new SmallSet<K>(set);
    }
    public static <K> Set<K> newHashSet(int size) {
        return new SmallSet<K>(size);
    }

    public static final int DEFAULT_SIZE = 10;
    public static int hashCode(Object o1) {
        return o1==null ? 41 : o1.hashCode();
    }
    public static boolean equals(Object o1, Object o2) {
        return o1 == o2 ||
                (o1!=null && o2!=null && o1.equals(o2));
    }


    // copyOf is only in Arrays since java 6.
    public static <T> T[] copyOf(T[] original, int newLength) {
        return (T[]) copyOf(original, newLength, original.getClass());
    }
    public static <T,U> T[] copyOf(U[] original, int newLength, Class<? extends T[]> newType) {
        T[] copy = ((Object)newType == (Object)Object[].class)
            ? (T[]) new Object[newLength]
            : (T[]) Array.newInstance(newType.getComponentType(), newLength);
        System.arraycopy(original, 0, copy, 0,
                         Math.min(original.length, newLength));
        return copy;
    }
}