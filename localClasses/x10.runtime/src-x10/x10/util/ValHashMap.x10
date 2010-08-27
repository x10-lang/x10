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

public class ValHashMap[K,V] implements ValMap[K,V] {
	public static def make[Key,Value](map:Map[Key,Value]!):ValHashMap[Key,Value] {
		var hashMap:HashMap[Key,Value]! = new HashMap[Key,Value]();
		val entries:Iterable[Map.Entry[Key,Value]!]! = map.entries();
        for (val entry:Map.Entry[Key,Value]! in entries) {
			if (entry!=null)
			    hashMap.put(entry.getKey(), entry.getValue());
		}
		return make(hashMap);
	}
	public static def make[Key,Value](map:HashMap[Key,Value]!):ValHashMap[Key,Value] {
		return new ValHashMap[Key,Value](map);
	}

    static class HashEntry[Key,Value] implements ValMap.Entry[Key,Value] {
        public global safe def getKey() = key;
        public global safe def getValue() = value;
        
        global val key: Key;
        global val value: Value;
		global val hash:Int;
        
        def this(key: Key, value: Value) {
            this.key = key;
            this.value = value;
			this.hash = hash(key);
        }
    }
    
    global val table: ValRail[HashEntry[K,V]!]!;
        
    private def this(map:HashMap[K,V]!) {
		val t:Rail[HashMap.HashEntry[K,V]!]! = map.table;
		table = ValRail.make[HashEntry[K,V]!](t.length, (index:Int)=> {
		    val entry:HashMap.HashEntry[K,V]! = t(index);
			var res:HashEntry[K,V]! = null;
			if (entry!=null) res = new HashEntry[K,V](entry.getKey(), entry.getValue());
			return res;
		});
    }    
    
    protected static global safe def hash[T](k:T): Int {
        return k.hashCode() * 17;
    }    
    public global safe def apply(k: K): Box[V] = get(k);    
    public global safe def get(k: K): Box[V] {
        val e = getEntry(k);
        if (e == null) return null;
        return e.value as Box[V];
    }
    
    public global safe def getOrElse(k: K, orelse: V): V {
        val e = getEntry(k);
        if (e == null) return orelse;
        return e.value;
    }
    
    public global safe def findMax(var index:K, var max:V, cmp:(V,V)=>Boolean):K {
	   for (key in keySet()) {
	     val count = this(key).apply();
         if (cmp(count, max))  { max = count ; index = key ;}
	   }
	   return index;
    }
    
    public global safe def getOrThrow(k: K): V throws NoSuchElementException {
        val e = getEntry(k);
        if (e == null) throw new NoSuchElementException("Not found");
        return e.value;
    }    
    protected global safe def getEntry(k: K): HashEntry[K,V] {
		val size = table.length;
		val mask = size - 1;

        if (size == 0)
            return null;
 
            
        val h = hash(k);

        var i: int = h;

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
                if (i - h > table.length) {
                    return null;
                }
            }
        }
    }
    
    public global safe def containsKey(k: K): boolean {
        val e = getEntry(k);
        return e != null;
    }
    
    public global safe def keySet(): Set[K]! = new KeySet[K,V](this);
    public global safe def entries(): Set[ValMap.Entry[K,V]]! = new EntrySet[K,V](this);
    public global safe def size() = table.length;
    
    protected global safe def entriesIterator(): Iterator[HashEntry[K,V]]! {
    val iterator = new EntriesIterator[K,V](this);
    iterator.advance();
    return iterator;
    }

    protected static class EntriesIterator[Key,Value] implements Iterator[HashEntry[Key,Value]] {
        val map: ValHashMap[Key,Value];
        var i: Int;
        
        def this(map: ValHashMap[Key,Value]) { this.map = map; this.i = 0; } // you call advance() after the ctor

        def advance(): void {
            while (i < map.table.length) {
               if (map.table(i) != null)
                   return;
               i++;
            }
        }
        
        public def hasNext(): Boolean {
            if (i < map.table.length) {
                return true;
            }
            return false;
        }
        
        public def next(): HashEntry[Key,Value] {
            val j = i;
            i++;
            advance();
            return map.table(j) ;
        }
    }
    
    
    protected static class KeySet[Key,Value] extends AbstractCollection[Key] implements Set[Key] {
        val map: ValHashMap[Key,Value];
        
        def this(map: ValHashMap[Key,Value]) { this.map = map; }
        
        public def iterator(): Iterator[Key] {
            return new MapIterator[HashEntry[Key,Value],Key](map.entriesIterator(), (e: HashEntry[Key,Value]) => e.key);
        }
        
        public def contains(k: Key) {
            return map.containsKey(k);
        }
        
        public def add(k: Key): Boolean { throw new UnsupportedOperationException(); }
        public def remove(k: Key): Boolean { throw new UnsupportedOperationException(); }
        public def clone(): KeySet[Key,Value]! { throw new UnsupportedOperationException(); }
        public def size(): Int = map.size();
    }

    protected static class EntrySet[Key,Value] extends AbstractCollection[ValMap.Entry[Key,Value]] implements Set[ValMap.Entry[Key,Value]] {
        val map: ValHashMap[Key,Value];
        
        def this(map: ValHashMap[Key,Value]) { this.map = map; }
        
        public def iterator(): Iterator[ValMap.Entry[Key,Value]] {
            return new MapIterator[HashEntry[Key,Value],ValMap.Entry[Key,Value]](map.entriesIterator(), (e: HashEntry[Key,Value]):ValMap.Entry[Key,Value] => e);
        }
        
        public def contains(k: ValMap.Entry[Key,Value]): Boolean { throw new UnsupportedOperationException(); }
        public def add(k: ValMap.Entry[Key,Value]): Boolean { throw new UnsupportedOperationException(); }
        public def remove(k: ValMap.Entry[Key,Value]): Boolean { throw new UnsupportedOperationException(); }
        public def clone(): EntrySet[Key,Value]! { throw new UnsupportedOperationException(); }
        public def size(): Int = map.size();
    }
}
