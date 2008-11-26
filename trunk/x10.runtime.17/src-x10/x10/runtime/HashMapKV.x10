package x10.runtime;

import x10.util.GrowableRail;
import x10.util.Container;
import x10.util.Set;

abstract class AbstractContainerT /*implements Container[/T//Key/Clock_c]*/ {

    public abstract def size(): Int;
  
    public def isEmpty(): Boolean = size() == 0;
        
    public abstract def contains(y: /*T*//*Key*/Clock_c): Boolean;
    //public abstract def clone(): Container[/*T*//*Key*/Clock_c];
    public abstract def iterator(): Iterator[/*T*//*Key*/Clock_c];
        
    public def toValRail(): ValRail[/*T*//*Key*/Clock_c] {
        val g = new GrowableRail[/*T*//*Key*/Clock_c](size());
        for (x: /*T*//*Key*/Clock_c in this) {
            g.add(x);
        }
        return g.toValRail();
    }
        
    public def toRail(): Rail[/*T*//*Key*/Clock_c] {
        val g = new GrowableRail[/*T*//*Key*/Clock_c](size());
        for (x: /*T*//*Key*/Clock_c in this) {
            g.add(x);
        }
        return g.toRail();
    }
        
    public def containsAll(c: Container[/*T*//*Key*/Clock_c]): Boolean {
        for (x: /*T*//*Key*/Clock_c in c) {
            if (! contains(x))
                return false;
        }
        return true;
    }
}

abstract class AbstractCollectionT extends AbstractContainerT /*implements Collection[/T//Key/Clock_c]*/ {

    public abstract def add(/*T*//*Key*/Clock_c): Boolean;
    public abstract def remove(/*T*//*Key*/Clock_c): Boolean;
  
    public def addAll(c: Container[/*T*//*Key*/Clock_c]): Boolean = addAllWhere(c, (/*T*//*Key*/Clock_c) => true);
//  public def retainAll(c: Container[/*T*//*Key*/Clock_c]): Boolean = removeAllWhere((x:/*T*//*Key*/Clock_c) => !c.contains(x));
//  public def removeAll(c: Container[/*T*//*Key*/Clock_c]): Boolean = removeAllWhere((x:/*T*//*Key*/Clock_c) => c.contains(x));

    public def addAllWhere(c: Container[/*T*//*Key*/Clock_c], p: (/*T*//*Key*/Clock_c) => Boolean): Boolean {
        var result: Boolean = false;
        for (x: /*T*//*Key*/Clock_c in c) {
            if (p(x))
                result |= add(x);
        }
        return result;
    }
  
//    public def removeAllWhere(p: (/*T*//*Key*/Clock_c) => Boolean): Boolean {
//        var result: Boolean = false;
//        for (x: /*T*//*Key*/Clock_c in this.clone()) {
//            if (p(x))
//                result |= remove(x);
//        }
//        return result;
//    }
  
//  public def clear(): Void { removeAllWhere((/*T*//*Key*/Clock_c)=>true); }
  
    //public abstract def clone(): Collection[/*T*//*Key*/Clock_c];
}

class HashEntryKV /*implements Map.Entry[/Key/Clock_c,/Value/Int]*/ {

    public def getKey() = key;
    public def getValue() = value;
    public def setValue(v: /*Value*/Int) { value = v; }
        
    val key: /*Key*/Clock_c;
    var value: /*Value*/Int;
    var removed: Boolean; 
    val hash: Int;
        
    def this(key: /*Key*/Clock_c, value: /*Value*/Int, h: Int) {
        this.key = key;
        this.value = value;
        this.hash = h;
        this.removed = false;
    }
}
    
//static type S = HashEntryKV;
//static type T = /*Key*/Clock_c;

class MapIteratorST implements Iterator[/*T*//*Key*/Clock_c] {

    val i: Iterator[/*S*/HashEntryKV];
    val f: (/*S*/HashEntryKV) => /*T*//*Key*/Clock_c;

    def this(i: Iterator[/*S*/HashEntryKV], f: (/*S*/HashEntryKV) => /*T*//*Key*/Clock_c) {
        this.i = i;
        this.f = f;
    }
	    
    public def hasNext(): Boolean = i.hasNext();
    public def next(): /*T*//*Key*/Clock_c = f(i.next());
}

class KeySetKV extends AbstractCollectionT /*implements Set[/Key/Clock_c]*/ {

    val map: HashMapKV;
        
    def this(map: HashMapKV) { this.map = map; }
        
    public def iterator(): Iterator[/*Key*/Clock_c] {
        return new MapIteratorST(map.entriesIterator(), (e: HashEntryKV) => e.key);
    }
        
    public def contains(k: /*Key*/Clock_c) {
        return map.containsKey(k);
    }
        
    public def add(k: /*Key*/Clock_c): Boolean { throw new UnsupportedOperationException(); }
    public def remove(k: /*Key*/Clock_c): Boolean { throw new UnsupportedOperationException(); }
    public def clone(): KeySetKV { throw new UnsupportedOperationException(); }
    public def size(): Int = map.size();
}

class EntriesIteratorKV implements Iterator[HashEntryKV] {

    val map: HashMapKV;
    var i: Int = 0;
        
    def this(map: HashMapKV) { this.map = map; advance(); }
        
    def advance(): void {
        while (i < map.table.length) {
            if (map.table(i) != null && ! map.table(i).removed)
                return;
            i++;
        }
    }
        
    public def hasNext(): Boolean {
        return i < map.table.length;
    }
        
    public def next(): HashEntryKV {
        val j = i++;
        advance();
        return map.table(j);
    }
}
    

public class HashMapKV implements (Clock_c)=>Box[Int] /*implements Map[/K/Clock_c,/V/Int]*/ {

    //static type Key = Clock_c;
    //static type Value = Int;
    //static type K = Clock_c;
    //static type V = Int;

    /** The actual table, must be of size 2**n */
    var table: Rail[HashEntryKV];
    
    /** Number of (non-null) entries in the table. */
    var size: Int;
    
    /** table.length - 1 */
    var mask: Int;
    
    var shouldRehash: Boolean;

    const MAX_PROBES = 3;
    const MIN_SIZE = 4;
    
    public def this() {
        init(MIN_SIZE);
    }
    
    public def this(var sz: int) {
        var pow2: int = MIN_SIZE;
        while (pow2 < sz)
            pow2 <<= 1;
        init(pow2);
    }
    
    protected def init(sz: int): void {
        // check that sz is a power of 2
        assert (sz & -sz) == sz;
        assert sz >= MIN_SIZE;
    
        table = Rail.makeVar[HashEntryKV](sz);
        mask = table.length - 1;
        size = 0;
        shouldRehash = false;
    }
    
    public def clear(): void {
        init(MIN_SIZE);
    }
    
    protected def hash(k: /*K*/Clock_c): Int {
        return /* k == null ? 0 : XXXX */ (k.hashCode() * 17);
    }
    
    public safe def apply(k: /*K*/Clock_c): Box[/*V*/Int] = get(k);
    
    public safe def get(k: /*K*/Clock_c): Box[/*V*/Int] {
        val e = getEntry(k);
        if (e == null) return null;
        return e.value to Box[/*V*/Int];
    }    
    
    protected def getEntry(k: /*K*/Clock_c): HashEntryKV {
        if (size == 0)
            return null;
            
        if (shouldRehash)
            rehash();
            
        val h = hash(k);

        var i: int = h;

        while (true) {        
            val j = i & mask;
            i++;
            
            val e = table(j);
            if (e == null) {
                if (i - h > MAX_PROBES)
                    shouldRehash = true;
                return null;
            }
            if (e != null) {
                if (e.hash == h && (/*k == null ? e.key == null : XXXX */ k.equals(e.key))) {
                    if (i - h > MAX_PROBES)
                        shouldRehash = true;
                    return e;
                }
            }
        }
    }
    
    public safe def put(k: /*K*/Clock_c, v: /*V*/Int): Box[/*V*/Int] {
        if (size == table.length || shouldRehash)
            rehash();
        
        val h = hash(k);
        var i: int = h;

        while (true) {
            val j = i & mask;
            i++;
            
            val e = table(j);
            if (e == null) {
                if (i - h > MAX_PROBES)
                    shouldRehash = true;
                table(j) = new HashEntryKV(k, v, h);
                size++;
                return null;
            }
            else if (e.hash == h && (/*k == null ? e.key == null : XXXX */ k.equals(e.key))) {
                if (i - h > MAX_PROBES)
                    shouldRehash = true;
                val old = e.value;
                e.value = v;
                if (e.removed)
                    return null;
                return (old to /*V*/Int) to Box[/*V*/Int];
            }
        }
    }
    
    public def rehash(): void {
        val t = table;
        val oldSize = size;
        table = Rail.makeVar[HashEntryKV](t.length*2);
        mask = table.length - 1;
        size = 0;
        shouldRehash = false;

        for (var i: int = 0; i < t.length; i++) {
            if (t(i) != null && ! t(i).removed) {
                put(t(i).key, t(i).value);
            }
        }
        
        assert size == oldSize;
    }
    
    public def containsKey(k: /*K*/Clock_c): boolean {
        val e = getEntry(k);
        return e != null && ! e.removed;
    }
    
    public def remove(k: /*K*/Clock_c): Box[/*V*/Int] {
        val e = getEntry(k);
        if (e != null && ! e.removed) {
            size--;
            e.removed = true;
            return e.value to Box[/*V*/Int];
        }
        return null;
    }
    
    public def keySet(): KeySetKV /*Set[/K/Clock_c]*/ {
        return new KeySetKV(this);
    }
    
    //public incomplete def entries(): Set[Map.Entry[/*K*/Clock_c,/*V*/Int]];
    //public incomplete def entries(): Set[Map.Entry[/*K*/Clock_c,/*V*/Int]];
    
    protected def entriesIterator(): Iterator[HashEntryKV] {
        return new EntriesIteratorKV(this);
    }
    
    public def size() = size;
}
