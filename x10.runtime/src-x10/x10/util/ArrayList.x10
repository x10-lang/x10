// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.util;

public class ArrayList[T] extends AbstractCollection[T] implements List[T] {

    private var a: GrowableRail[T]{self.at(this)};

    public static def make[T](c: Container[T]) {
	val a = new ArrayList[T]();
	a.addAll(c);
	return a;
    }
    private def this(c: Container[T]) {
    }
    
    public def contains(v: T): Boolean {
        for (var i: Int = 0; i < a.length(); i++) {
            if (v == null ? a(i) == null : v.equals(a(i))) {
                return true;
            }
        }
        return false;
    }
    
    public def clone(): ArrayList[T] {
        return new ArrayList[T](this);
    }
    
    public def add(v: T): Boolean {
        a.add(v);
        return true;
    }
    
    public def remove(v: T): Boolean {
        for (var i: Int = 0; i < a.length(); i++) {
            if (v == null ? a(i) == null : v.equals(a(i))) {
                removeAt(i);
                return true;
            }
        }
        return false;
    }
    
    public def addBefore(i: int, v: T): Void {
        a.add(v);
        for (var j: int = i+1; j < a.length(); j++) {
            a(j) = a(j-1);
        }
        a(i) = v;
    }

    public def set(v: T, i: int): T {
        a(i) = v;
        return v;
    }

    public def removeAt(i: int): T {
        val v = a(i);
        for (var j: int = i+1; j < a.length(); j++) {
            a(j-1) = a(j);
        }
        a.removeLast();
        return v;
    }
            
    public def apply(i: nat) = a(i);

    public def get(i: int): T = a(i);

    public def size(): int = a.length();
    
    public def isEmpty(): Boolean = size() == 0;

    // DEPRECATED
    public def toArray() = a.toRail();
    public def toRail() = a.toRail();
    public def toValRail() = a.toValRail();

    public def this() {
        a = new GrowableRail[T]();
    }
    
    public def this(size: Int) {
        a = new GrowableRail[T](size);
    }
    
    public def removeFirst(): T = removeAt(0);
    public def removeLast(): T = removeAt(a.length()-1);
    public def getFirst(): T = get(0);
    public def getLast(): T = get(a.length()-1);

    public def indices(): List[Int] {
        val l = new ArrayList[Int]();
        for (var i: Int = 0; i < a.length(); i++) {
            l.add(i);
        }
        return l;
    }
    
    public def subList(begin: Int, end: Int): List[T] {
        val l = new ArrayList[T]();
        for (var i: Int = begin; i < a.length() && i < end; i++) {
           l.add(a(i));
        }
        return l;
    }
    
    public def indexOf(v: T): Int {
        return indexOf(0, v);
    }
    
    public def indexOf(index: Int, v: T): Int {
        for (var i: Int = index; i < a.length(); i++) {
            if (v==null ? a(i)==null : v.equals(a(i)))
            	return i;
        }
        return -1;
    }
    
    public def lastIndexOf(v: T): Int {
        return lastIndexOf(a.length()-1, v);
    }
    
    public def lastIndexOf(index: Int, v: T): Int {
        for (var i: Int = index; i >= 0; i--) {
            if (v==null ? a(i)==null : v.equals(a(i)))
            	return i;
        }
        return -1;
    }
    
    //
    // iterator
    //

// BIZARRE BUG: renaming S to T causes compiler to fail at isImplicitCastValid at end of X10MethodInstance_c.instantiate
    private static class It[S] implements ListIterator[S] {
        
        private var i: int;
        private val al: ArrayList[S]{self.at(this)};
        
        def this(al: ArrayList[S]) {
            this(al, -1);
        }

        def this(al: ArrayList[S], i: int) {
            this.al = al;
            this.i = i;
        }
        
        public def hasNext(): boolean {
            return i+1 < al.size();
        }

        public def nextIndex(): Int {
            return ++i;
        }
        
        public def next(): S {
            return al.a(++i);
        }

        public def hasPrevious(): boolean {
            return i-1 >= 0;
        }

        public def previousIndex(): Int {
            return --i;
        }
        
        public def previous(): S {
            return al.a(--i);
        }
        
        public def remove(): Void {
            al.removeAt(i);
        }
        
        public def set(v: S): Void {
            al.set(v, i);
        }
        
        public def add(v: S): Void {
            al.addBefore(i, v);
        }
    }

    public def iterator(): ListIterator[T] {
        return new It[T](this);
    }
    
    public def iteratorFrom(i: Int): ListIterator[T] {
        return new It[T](this, i);
    }
    
    public def reverse(): Void {
        val length = a.length();
        for (var i: Int = 0; i < length/2; i++) {
            exch(a, i, length-1-i);
        }
    }

    // [NN]: should not need to cast x to Comparable[T]
    public def sort() {T <: Comparable[T]} = sort((x:T, y:T) => (x as Comparable[T]).compareTo(y));
    public def sort(cmp: (T,T)=>Int) = qsort(a, 0, 
         a.length()-1, 
         cmp
    );

    // public def sort(lessThan: (T,T)=>Boolean) = qsort(a, 0, a.length()-1, (x:T,y:T) => lessThan(x,y) ? -1 : (lessThan(y,x) ? 1 : 0));
    
    //
    // quick&dirty sort
    //

    private def qsort(a: GrowableRail[T], lo: int, hi: int, cmp: (T,T)=>Int) {
        if (hi <= lo) return;
        var l: int = lo - 1;
        var h: int = hi;
        while (true) {
            while (cmp(a(++l), a(hi))<0);
            while (cmp(a(hi), a(--h))<0 && h>lo);
            if (l >= h) break;
            exch(a, l, h);
        }
        exch(a, l, hi);
        qsort(a, lo, l-1, cmp);
        qsort(a, l+1, hi, cmp);
    }

    private def exch(a: GrowableRail[T]{self.at(here)}, i: int, j: int): void {
        val temp = a(i);
        a(i) = a(j);
        a(j) = temp;
    }
}
