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

import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class CollectionFactory
{
    public static <K,V> LinkedHashMap<K,V> newHashMap() {
        return new LinkedHashMap<K,V>();
    }
    public static <K,V> LinkedHashMap<K,V> newHashMap(Map<K,V> map) {
        return new LinkedHashMap<K,V>(map);
    }
    public static <K,V> LinkedHashMap<K,V> newHashMap(int size) {
        return new LinkedHashMap<K,V>(size);
    }
    public static <K,V> LinkedHashMap<K,V> newHashMap(int size, float loadFactor) {
        return new LinkedHashMap<K,V>(size, loadFactor);
    }

    public static <K> LinkedHashSet<K> newHashSet() {
        return new LinkedHashSet<K>();
    }
    public static <K> LinkedHashSet<K> newHashSet(Collection<K> set) {
        return new LinkedHashSet<K>(set);
    }
    public static <K> LinkedHashSet<K> newHashSet(int size) {
        return new LinkedHashSet<K>(size);
    }
}
