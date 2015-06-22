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

import x10.compiler.NonEscaping;
import x10.io.CustomSerialization;
import x10.io.Deserializer;
import x10.io.Serializer;

public class HashMap[K,V] {V haszero} implements Map[K,V], CustomSerialization {
    static class HashEntry[Key,Value] {Value haszero} implements Map.Entry[Key,Value] {
        public def getKey() = key;
        public def getValue() = value;
        public def setValue(v:Value) { value = v; }
        
        val key:Key;
        var value:Value;
        var removed:Boolean; 
        val hash:Int;
        
        def this(key:Key, value:Value, h:Int) {
            this.key = key;
            this.value = value;
            this.hash = h;
            this.removed = false;
        }
    }
    
    /** The actual table, must be of size 2**n */
    var table:Rail[HashEntry[K,V]];
    
    /** Number of non-null, non-removed entries in the table. */
    var size:Long;

    /** Number of non-null entries in the table. */
    var occupation:Long;
    
    /** table.length - 1 */
    var mask:Long;

    var modCount:Long = 0; // to discover concurrent modifications
    
    static val MAX_PROBES = 3;
    static val MIN_SIZE = 4;
    
    public def this() {
        init(MIN_SIZE);
    }
    
    public def this(var sz:Long) {
        if (sz > (1<<60)) throw new OutOfMemoryError("Cannot allocate backing Rail of size "+sz);
        var pow2:Long = MIN_SIZE;
        while (pow2 < sz)
            pow2 <<= 1n;
        init(pow2);
    }
    
    @NonEscaping final def init(sz:Long):void {
        // check that sz is a power of 2
        assert (sz & -sz) == sz;
        assert sz >= MIN_SIZE;
    
        table = new Rail[HashEntry[K,V]](sz);
        mask = sz - 1;
        size = 0;
        occupation = 0;
    }

    public def clear():void {
        modCount++;
        init(MIN_SIZE);
    }
    
    protected def hash(k:K):Int = hashInternal(k);
    @NonEscaping protected final def hashInternal(k:K):Int {
        return k.hashCode() * 17n;
    }
    
    public operator this(k:K):V = get(k);
    
    public def get(k:K):V {
        val e = getEntry(k);
        if (e == null || e.removed) return Zero.get[V]();
        return e.value;
    }
    
    public def getOrElse(k:K, orelse:V):V {
        val e = getEntry(k);
        if (e == null || e.removed) return orelse;
        return e.value;
    }
    
    public def getOrThrow(k:K):V //throws NoSuchElementException
    {
        val e = getEntry(k);
        if (e == null || e.removed) throw new NoSuchElementException("Not found:" + k);
        return e.value;
    }
    
    protected def getEntry(k:K):HashEntry[K,V] {
        if (size == 0)
            return null;

        val h = hash(k);
        var i:Long = h;

        while (true) {        
            val j = i & mask;
            i++;
            
            val e = table(j);
            if (e == null) {
                return null;
            }
            if (e != null) {
                if (e.hash == h && k.equals(e.key)) {
                    return e;
                }
                if (i - h > table.size) {
                    return null;
                }
            }
        }
    }

    public operator this(k:K)=(v:V):V = putInternal(k,v,true);
    
    public def put(k:K, v:V):V = putInternal(k,v,true);
    @NonEscaping protected final def putInternal(k:K, v:V, mayRehash:Boolean):V {

        val h = hashInternal(k);
        var i:Long = h;

        while (true) {
            val j = i & mask;
            i++;
            
            val e = table(j);
            if (e == null) {
                modCount++;
                table(j) = new HashEntry[K,V](k, v, h);
                size++;
                occupation++;
                if (mayRehash && (((i - h > MAX_PROBES) && (occupation >= table.size / 2)) || (occupation == table.size))) {
                    rehashInternal();
                }
                return Zero.get[V]();
            } else if (e.hash == h && k.equals(e.key)) {
                val old = e.value;
                e.value = v;
                if (e.removed) {
                    e.removed = false;
                    size++;
                    return Zero.get[V]();
                }
                return old;
            }
        }
    }
    
    public def rehash():void { rehashInternal(); }
    @NonEscaping protected final def rehashInternal():void {
        modCount++;
        val t = table;
        val oldSize = size;
        table = new Rail[HashEntry[K,V]](t.size*2);
        mask = table.size - 1;
        size = 0;
        occupation = 0;

        for (var i:Long = 0; i < t.size; i++) {
            if (t(i) != null && ! t(i).removed) {
                putInternal(t(i).key, t(i).value, false);
            }
        }
        assert size == oldSize;
    }
    
    public def containsKey(k:K):Boolean {
        val e = getEntry(k);
        return e != null && ! e.removed;
    }
    
    public def remove(k:K):V {
        modCount++;
        val e = getEntry(k);
        if (e != null && ! e.removed) {
            size--;
            e.removed = true;
            val ans = e.value;
            e.value = Zero.get[V]();
            return ans;
        }
        return Zero.get[V]();
    }

    public def delete(k:K):Boolean {
        modCount++;
        val e = getEntry(k);
        if (e != null && ! e.removed) {
            size--;
            e.removed = true;
            val ans = e.value;
            e.value = Zero.get[V]();
            return true;
        }
        return false;
    }

    
    public def keySet():Set[K] = new KeySet[K,V](this);
    public def entries():Set[Map.Entry[K,V]] = new EntrySet[K,V](this);
    
    protected def entriesIterator():Iterator[Map.Entry[K,V]] {
        val iterator = new EntriesIterator[K,V](this);
        iterator.advance();
        return iterator;
    }

    protected static class EntriesIterator[Key,Value] {Value haszero} implements Iterator[Map.Entry[Key,Value]] {
        val map:HashMap[Key,Value];
        var i:Long;
        var originalModCount:Long;
        
        def this(map:HashMap[Key,Value]) { this.map = map; this.i = 0; originalModCount = map.modCount; } // you call advance() after the ctor

        def advance():void {
            while (i < map.table.size) {
               if (map.table(i) != null && ! map.table(i).removed)
                   return;
               i++;
            }
        }
        
        public def hasNext():Boolean {
            if (i < map.table.size) {
//              assert map.table(i) != null && ! map.table(i).removed :"map entry " + i + " is null or removed";
                return true;
            }
            return false;
        }
        
        public def next():Map.Entry[Key,Value] {
            if (originalModCount!=map.modCount) throw new Exception("Your code has a concurrency bug! You updated the hashmap "+(map.modCount-originalModCount)+" times since you created the iterator.");
            val j = i;
//            assert map.table(j) != null && ! map.table(j).removed :"map entry " + j + " is null or removed";
            i++;
            advance();
            return map.table(j) ;
        }
    }
    
    public def size():Long = size;
    
    protected static class KeySet[Key,Value] {Value haszero} extends AbstractCollection[Key] implements Set[Key] {
        val map:HashMap[Key,Value];
        
        def this(map:HashMap[Key,Value]) { this.map = map; }
        
        public def iterator():Iterator[Key] {
            return new MapIterator[Map.Entry[Key,Value],Key](map.entriesIterator(), (e:Map.Entry[Key,Value]) => e.getKey());
        }
        
        public def contains(k:Key):Boolean {
            return map.containsKey(k);
        }
        
        public def add(k:Key):Boolean { throw new UnsupportedOperationException(); }
        public def remove(k:Key):Boolean { throw new UnsupportedOperationException(); }
        public def clone():KeySet[Key,Value] { throw new UnsupportedOperationException(); }
        public def size():Long = map.size();
    }

    protected static class EntrySet[Key,Value] {Value haszero}
           extends AbstractCollection[Map.Entry[Key,Value]] 
           implements Set[Map.Entry[Key,Value]] {
        val map:HashMap[Key,Value];
        
        def this(map:HashMap[Key,Value]) { this.map = map; }
        
        public def iterator():Iterator[Map.Entry[Key,Value]] {
            return map.entriesIterator();
        }
        
        public def contains(k:Map.Entry[Key,Value]):Boolean { throw new UnsupportedOperationException(); }
        public def add(k:Map.Entry[Key,Value]):Boolean { throw new UnsupportedOperationException(); }
        public def remove(k:Map.Entry[Key,Value]):Boolean { throw new UnsupportedOperationException(); }
        public def clone():EntrySet[Key,Value] { throw new UnsupportedOperationException(); }
        public def size():Long = map.size();
    }

    /*
     * Custom deserialization
     */
    public def this(ds:Deserializer) {
        this();
        val numEntries = ds.readAny() as Long;
        for (1..numEntries) {
           val key = ds.readAny() as K;
           val value = ds.readAny() as V;
           putInternal(key, value, true);
        }
    }

    /*
     * Custom serialization
     */
    public def serialize(s:Serializer) {
        val it = entriesIterator();
        s.writeAny(size());
        while (it.hasNext()) {
            val entry = it.next();
            s.writeAny(entry.getKey());
            s.writeAny(entry.getValue());
        }
    }
}
