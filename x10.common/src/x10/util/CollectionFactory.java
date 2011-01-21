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

class WallaSmallMap<K,V> extends com.ibm.wala.util.collections.SmallMap<K,V> {
    /*
    java.lang.UnsupportedOperationException
        at com.ibm.wala.util.collections.SmallMap.putAll(Unknown Source)
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> map) throws UnsupportedOperationException {
        /*
        com.ibm.wala.util.debug.UnimplementedError: must implement entrySet
            at com.ibm.wala.util.debug.Assertions.UNREACHABLE(Unknown Source)
            at com.ibm.wala.util.collections.SmallMap.entrySet(Unknown Source)
         */
        for (Map.Entry en : map.entrySet())
            put(en.getKey(),en.getValue());
    }
}

final class SmallMap<K,V> implements Map<K,V> {
    // we must support null key and values (TypeCheckFragmentGoal.runTask puts a null value)
    // keys[0] .. keys[curIndex-1] contains distinct elements, and there might be additional elements in overflow
    private Entry<K,V>[] entries; // because of entrySet().iterator(), it's more efficient to keep entries instead of two arrays of keys&values (which is more compact)
    private int curIndex;
    private LinkedHashMap<K,V> overflow = null;

    public SmallMap() {
        this(CollectionFactory.DEFAULT_SIZE);
    }
    public SmallMap(int size) {
        entries = (Entry<K,V>[])new Entry[size];
    }
    public SmallMap(Map<K,V> map) {
        this(map.size());
        putAll(map);
    }


    public V put(K key, V value) {
        // if it's contained, then we need to change the value
        for (int i=0; i<curIndex; i++) {
            final Entry<K, V> en = entries[i];
            if (CollectionFactory.equals(en.getKey(),key)) {
                V old = en.getValue();
                en.setValue(value);
                return old;
            }
        }
        if (overflow!=null && overflow.containsKey(key)) return overflow.put(key,value);

        // it isn't contained, so let's add it
        if (curIndex<entries.length) {
            entries[curIndex++] = new Entry<K,V>(key,value);
        } else {
            if (overflow==null) overflow = new LinkedHashMap<K,V>();
            overflow.put(key,value);
        }
        return null;
    }
    private void removeIndex(int i) {
        for (int j=i; j<curIndex-1; j++)
            entries[j] = entries[j+1];
        entries[--curIndex] = null; // for garbage collection
    }

    public V remove(Object o) {
        for (int i=0; i<curIndex; i++) {
            final Entry<K, V> en = entries[i];
            if (CollectionFactory.equals(en.getKey(),o)) {
                removeIndex(i);
                return en.getValue();
            }
        }
        return overflow==null ? null : overflow.remove(o);
    }


    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Map))
            return false;
        Map<K,V> m = (Map<K,V>) o;
        if (m.size() != size())
            return false;

        for (Map.Entry<K,V> en : m.entrySet())
            if (!containsPair(en.getKey(),en.getValue())) return false;

        return true;
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        if (m instanceof SmallMap) {
            SmallMap<K,V> s = (SmallMap) m;
            for (int i=0; i<s.curIndex; i++) {
                final Entry<K, V> en = s.entries[i];
                put(en.getKey(),en.getValue());
            }
            if (s.overflow!=null)
                for (Map.Entry<K,V> en : s.overflow.entrySet())
                    put(en.getKey(),en.getValue());
            return;
        }
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
            put(e.getKey(), e.getValue());
    }

    public boolean isEmpty() {
        return size()==0;
    }

    public int hashCode() {
        int h = 0;
        for (int i=0; i<curIndex; i++)
            h += entries[i].hashCode();
        if (overflow!=null) h+=overflow.hashCode();
        return h;
    }


    public V get(Object o) {
        for (int i=0; i<curIndex; i++) {
            final Entry<K, V> en = entries[i];
            if (CollectionFactory.equals(en.getKey(),o)) return en.getValue();
        }
        return overflow==null ? null : overflow.get(o);
    }

    public boolean containsKey(Object o) {
        for (int i=0; i<curIndex; i++)
            if (CollectionFactory.equals(entries[i].getKey(),o)) return true;
        return overflow==null ? false : overflow.containsKey(o);
    }

    public boolean containsValue(Object o) {
        for (int i=0; i<curIndex; i++)
            if (CollectionFactory.equals(entries[i].getValue(),o)) return true;
        return overflow==null ? false : overflow.containsValue(o);
    }

    public boolean containsPair(Object key, Object value) {
        for (int i=0; i<curIndex; i++) {
            final Entry<K, V> en = entries[i];
            if (CollectionFactory.equals(en.getKey(),key) && CollectionFactory.equals(en.getValue(),value)) return true;
        }
        if (overflow==null) return false;
        if (value == null) {
            return overflow.get(key)==null && overflow.containsKey(key);
        } else {
            return value.equals(overflow.get(key));
        }
    }

    public void clear() {
        for (int i=0; i<curIndex; i++)
            entries[i] = null;
        overflow = null;
        curIndex = 0;
    }


    public int size() {
        return curIndex+(overflow==null?0:overflow.size());
    }


    static class Entry<K,V> implements Map.Entry<K,V> {
        final K key;
        V value;

        /**
         * Creates new entry.
         */
        Entry(K k, V v) {
            value = v;
            key = k;
        }

        public final K getKey() {
            return key;
        }

        public final V getValue() {
            return value;
        }

        public final V setValue(V newValue) {
	        V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry e = (Map.Entry)o;
            Object k1 = getKey();
            Object k2 = e.getKey();
            if (k1 == k2 || (k1 != null && k1.equals(k2))) {
                Object v1 = getValue();
                Object v2 = e.getValue();
                if (v1 == v2 || (v1 != null && v1.equals(v2)))
                    return true;
            }
            return false;
        }

        public final int hashCode() {
            return (key==null   ? 0 : key.hashCode()) ^
                   (value==null ? 0 : value.hashCode());
        }

        public final String toString() {
            return getKey() + "=" + getValue();
        }
    }


    /**
     * Iterator part
     */
    private abstract class SmallIterator<E> implements Iterator<E> {
        int pos;
        Iterator<Map.Entry<K,V>> overflowIt;
        public boolean hasNext() {
            return pos<curIndex || (
                    overflowIt==null ? (overflow!=null && !overflow.isEmpty())
                        : overflowIt.hasNext());
        }

        public Map.Entry<K,V> nextEntry() {
            if (pos<curIndex) {
                return entries[pos++];
            } else {
                if (overflowIt==null) overflowIt = overflow.entrySet().iterator();
                return overflowIt.next();
            }
        }

        public void remove() {
            if (overflowIt==null) {
                removeIndex(--pos);
            } else
                overflowIt.remove();
        }
    }

    private final class ValueIterator extends SmallIterator<V> {
        public V next() {
            return nextEntry().getValue();
        }
    }

    private final class KeyIterator extends SmallIterator<K> {
        public K next() {
            return nextEntry().getKey();
        }
    }

    private final class EntryIterator extends SmallIterator<Map.Entry<K,V>> {
        public Map.Entry<K,V> next() {
            return nextEntry();
        }
    }

    private Set<Map.Entry<K,V>> entrySet = null;
    private Set<K>        keySet = null;
    private Collection<V> valuesSet = null;

    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K,V>> es = entrySet;
        return es != null ? es : (entrySet = new EntrySet());
    }
    public Collection<V> values() {
        Collection<V> vs = valuesSet;
        return (vs != null ? vs : (valuesSet = new Values()));
    }
    public Set<K> keySet() {
        Set<K> ks = keySet;
        return (ks != null ? ks : (keySet = new KeySet()));
    }

    private final class KeySet extends AbstractSet<K> {
        public Iterator<K> iterator() {
            return new KeyIterator();
        }
        public int size() {
            return SmallMap.this.size();
        }
        public boolean contains(Object o) {
            return containsKey(o);
        }
        public boolean remove(Object o) {
            return SmallMap.this.remove(o) != null;
        }
        public void clear() {
            SmallMap.this.clear();
        }
    }
    private final class Values extends java.util.AbstractCollection<V> {
        public Iterator<V> iterator() {
            return new ValueIterator();
        }
        public int size() {
            return SmallMap.this.size();
        }
        public boolean contains(Object o) {
            return containsValue(o);
        }
        public void clear() {
            SmallMap.this.clear();
        }
    }

    private final class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        public Iterator<Map.Entry<K,V>> iterator() {
            return new EntryIterator();
        }
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<K,V> en = (Map.Entry<K,V>) o;
            return containsPair(en.getKey(), en.getValue());
        }
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<K,V> en = (Map.Entry) o;
            final V value = en.getValue();
            final K key = en.getKey();
            boolean contained = containsPair(key, value);
            if (contained) remove(key);
            return contained;
        }
        public int size() {
            return SmallMap.this.size();
        }
        public void clear() {
            SmallMap.this.clear();
        }
    }

}














final class SmallSet<K> extends AbstractSet<K> {
    // elems[0] .. elems[curIndex-1] contains distinct elements, and there might be additional elements in overflow
    private K[] elems; // we must support null entries
    private int curIndex;
    private LinkedHashSet<K> overflow = null;

    public SmallSet() {
        this(CollectionFactory.DEFAULT_SIZE);
    }
    public SmallSet(int size) {
        elems = (K[])new Object[size];
    }
    public SmallSet(Collection<K> c) {
        this(c.size());
        addAll(c);
    }
    @Override
    public boolean add(K k) {
        // X10TypeEnv_c.bounds adds a null entry: if (k==null) throw new RuntimeException("SmallSet doesn't support null entries");
        if (contains(k)) return false;
        if (curIndex<elems.length) {
            elems[curIndex++] = k;
        } else {
            if (overflow==null) overflow = new LinkedHashSet<K>();
            overflow.add(k);
        }
        return true;
    }
    private void removeIndex(int i) {
        for (int j=i; j<curIndex-1; j++)
            elems[j] = elems[j+1];
        elems[--curIndex] = null; // for garbage collection
    }
    @Override
    public boolean remove(Object o) {
        for (int i=0; i<curIndex; i++)
            if (CollectionFactory.equals(elems[i],o)) {
                removeIndex(i);
                return true;
            }
        return overflow==null ? false : overflow.remove(o);
    }


    // no need to override since the implementation in AbstractSet is efficient.
    @Override
    public boolean equals(Object o) {
        return super.equals(o); // uses containsAll
    }
    @Override
    public boolean containsAll(Collection<?> c) {
        if (c instanceof SmallSet) {
            SmallSet<K> s = (SmallSet) c;
            for (int i=0; i<s.curIndex; i++)
                if (!contains(s.elems[i])) return false;
            if (s.overflow!=null)
                for (K o : s.overflow)
                    if (!contains(o)) return false;
            return true;
        }
        return super.containsAll(c); // uses c.iterator() and contains
    }
    @Override
    public boolean addAll(Collection<? extends K> c) {
        if (c instanceof SmallSet) {
            boolean modified = false;
            SmallSet<K> s = (SmallSet) c;
            for (int i=0; i<s.curIndex; i++)
                if (add(s.elems[i])) modified = true;
            if (s.overflow!=null)
                for (K o : s.overflow)
                    if (add(o)) modified = true;
            return modified;
        }
        return super.addAll(c);    // uses c.iterator() and add
    }
    @Override
     public boolean removeAll(Collection<?> c) {
        return super.removeAll(c);    // iterates over the smaller collection
    }
    @Override
    public boolean isEmpty() {
        return super.isEmpty();    // uses size
    }
    @Override
    public Object[] toArray() {
        if (overflow==null) {
            return CollectionFactory.copyOf(elems,curIndex);
        }
        return super.toArray(); // uses an iterator
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (overflow==null) {
            if (a.length>=curIndex) {
                System.arraycopy(elems,0,a,0,curIndex);
                return a;
            }
            return (T[])CollectionFactory.copyOf(elems,curIndex,a.getClass());
        }
        return super.toArray(a); 
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        for (int i=0; i<curIndex; i++)
            if (!c.contains(elems[i])) {
                modified = true;
                removeIndex(i);
                i--;
            }
        if (overflow!=null) modified |=overflow.retainAll(c);
        return modified;
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (int i=0; i<curIndex; i++)
            h += CollectionFactory.hashCode(elems[i]);
        if (overflow!=null) h+=overflow.hashCode();
        return h;
    }

    @Override
    public boolean contains(Object o) {
        for (int i=0; i<curIndex; i++)
            if (CollectionFactory.equals(elems[i],o)) return true;
        return overflow==null ? false : overflow.contains(o);
    }


    @Override
    public void clear() {
        for (int i=0; i<curIndex; i++)
            elems[i] = null;
        overflow = null;
        curIndex = 0;
    }

    public Iterator<K> iterator() {
        return new Iterator<K>() {
            int pos;
            Iterator<K> overflowIt;
            public boolean hasNext() {
                return pos<curIndex || (
                        overflowIt==null ? (overflow!=null && !overflow.isEmpty())
                            : overflowIt.hasNext());
            }

            public K next() {
                if (pos<curIndex) {
                    return elems[pos++];
                } else {
                    if (overflowIt==null) overflowIt = overflow.iterator();
                    return overflowIt.next();
                }
            }

            public void remove() {
                if (overflowIt==null) {
                    removeIndex(--pos);
                } else
                    overflowIt.remove();
            }
        }; 
    }

    public int size() {
        return curIndex+(overflow==null?0:overflow.size());
    }
}