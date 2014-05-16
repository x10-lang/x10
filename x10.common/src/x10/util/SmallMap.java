package x10.util;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class SmallMap<K,V> implements Map<K,V>, Cloneable {
    // we must support null key and values (TypeCheckFragmentGoal.runTask puts a null value)
    // keys[0] .. keys[curIndex-1] contains distinct elements, and there might be additional elements in overflow
    private Entry<K,V>[] entries; // because of entrySet().iterator(), it's more efficient to keep entries instead of two arrays of keys&values (which is more compact)
    private int curIndex;
    private LinkedHashMap<K,V> overflow = null;

    public SmallMap() {
        this(CollectionFactory.DEFAULT_SIZE);
    }
    @SuppressWarnings("unchecked")
    public SmallMap(int size) {
        entries = (Entry<K,V>[])new Entry[size];
    }
    public SmallMap(Map<K,V> map) {
        this(map.size());
        putAll(map);
    }

    @SuppressWarnings("unchecked")
    public SmallMap<K,V> clone() {
        try {
            return (SmallMap<K,V>) super.clone();
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            return null;
        }
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
        @SuppressWarnings("unchecked")
        Map<K,V> m = (Map<K,V>) o;
        if (m.size() != size())
            return false;

        for (Map.Entry<K,V> en : m.entrySet())
            if (!containsPair(en.getKey(),en.getValue())) return false;

        return true;
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        if (m instanceof SmallMap) {
            @SuppressWarnings("unchecked")
            SmallMap<K,V> s = (SmallMap<K,V>) m;
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


    // Code copied from AbstractMap. I don't want to inherit from AbstractMap because it has two fields (keySet, values) and it's a waste of space becasue we do not use them. 
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("{");

        Iterator<Map.Entry<K,V>> i = entrySet().iterator();
            boolean hasNext = i.hasNext();
            while (hasNext) {
            Map.Entry<K,V> e = i.next();
            K key = e.getKey();
                V value = e.getValue();
            if (key == this)
            buf.append("(this Map)");
            else
            buf.append(key);
            buf.append("=");
            if (value == this)
            buf.append("(this Map)");
            else
            buf.append(value);
                hasNext = i.hasNext();
                if (hasNext)
                    buf.append(", ");
            }

        buf.append("}");
        return buf.toString();
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
            @SuppressWarnings("rawtypes")
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
            @SuppressWarnings("unchecked")
            Map.Entry<K,V> en = (Map.Entry<K,V>) o;
            return containsPair(en.getKey(), en.getValue());
        }
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            @SuppressWarnings("unchecked")
            Map.Entry<K,V> en = (Map.Entry<K,V>) o;
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
