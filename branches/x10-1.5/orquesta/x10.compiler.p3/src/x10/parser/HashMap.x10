package x10.util;

public class HashMap[K,V](keq: (K,K) => Boolean, hash: K => Int){K <: x10.lang.Object} {
    var table: Rail[Entry[K,V]];
    
    static class Entry[K,V] {
        val key: K;
        var value: V;
        var next: Entry[K,V];
        
        Entry(k: K, v: V, n: entry[K,V]) {
        	key = k;
        	value = v;
        	next = n;
        }
    }
    
    def this[K,V](){K <: x10.lang.Object} {
        property[K,V](K.equals.(Object), K.hashCode.());
    }
    
    public def get(k: K): V = {
        val h = hash(k);
        val h2 = h % table.length();
        
        for (val e = table[h2]; e != null; e = e.next) {
            if (keq(k, e.key)) return e.value;
        }
        
        return null;
    }
    
    public def apply(k: K): V = get(k);
    public def set(k: K, v: V): V = { put(k, v); return v; }
    
    public def get(k: K): V = {
        val h = hash(k);
        val h2 = h % table.length();
        
        for (val e = table[h2]; e != null; e = e.next) {
            if (keq(k, e.key)) return e.value;
        }
        
        return null; // Hmm...
    }
    
    public def put(k: K, v: V) = {
        val h = hash(k);
        val h2 = h % table.length();
        
        for (val e = table[h2]; e != null; e = e.next) {
            if (keq(k, e.key)) {
                e.value = v;
                return;
            }
        }
        
        val e = new Entry(k, v, table[h2]);
        table[h2] = e;
    }
    
    public def remove(k: K) = {
        val h = hash(k);
        val h2 = h % table.length();
        
        var prev = null;
        
        for (val e = table[h2]; e != null; e = e.next) {
            if (keq(k, e.key)) {
            	if (prev == null)
            		table[h2].next = e.next;
            	else
                    prev.next = e.next;
                return;
            }
            
            prev = e;
        }
    }
}    