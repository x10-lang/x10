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

public class ArrayList[T] extends AbstractCollection[T] implements List[T] {

    private val a: GrowableRail[T];

    public static def make[T](c: Container[T]) {
	val a = new ArrayList[T]();
	a.addAll(c);
	return a;
    }
    
    public def contains(v: T): Boolean {
        for (var i: Int = 0; i < a.size(); i++) {
            if (v == null ? a(i) == null : v.equals(a(i))) {
                return true;
            }
        }
        return false;
    }
    
    public def clear(): void {
        a.clear();
    }

    public def clone() {
        val a = new ArrayList[T]();
        a.addAll(this);
        return a;
    }

    public def add(v: T): Boolean {
        a.add(v);
        return true;
    }
    
    public def remove(v: T): Boolean {
        for (var i: Int = 0; i < a.size(); i++) {
            if (v == null ? a(i) == null : v.equals(a(i))) {
                removeAt(i);
                return true;
            }
        }
        return false;
    }
    
    public def addBefore(i: int, v: T): void {
        a.add(v);
        for (var j:int = (a.size() as int)-1; j > i; j--) {
            a(j) = a(j-1);
        }
        a(i) = v;
    }

    public operator this(i: int)=(v: T) : T = set(v,i);
    
    public def set(v: T, i: int): T {
        a(i) = v;
        return v;
    }

    public def removeAt(i: int): T {
        val v = a(i);
        for (var j: int = i+1; j < a.size(); j++) {
            a(j-1) = a(j);
        }
        a.removeLast();
        return v;
    }
            
    public operator this(i: int) = a(i);

    public def get(i: int): T = a(i);

    public def size(): int = (a.size() as int);
    
    public def isEmpty(): Boolean = size() == 0;

    public def toRail() = a.toRail();

    public def this() {
        a = new GrowableRail[T]();
    }
    
    public def this(size: Int) {
        a = new GrowableRail[T](size);
    }
    
    public def removeFirst(): T = removeAt(0);
    public def removeLast(): T = removeAt(a.size() as int -1);
    public def getFirst(): T = get(0);
    public def getLast(): T = get(a.size() as int -1);

    public def indices(): List[Int] {
        val l = new ArrayList[Int]();
        for (var i: Int = 0; i < a.size(); i++) {
            l.add(i);
        }
        return l;
    }
    
    public def subList(begin: Int, end: Int): List[T] {
        val l = new ArrayList[T]();
        for (var i: Int = begin; i < a.size() && i < end; i++) {
           l.add(a(i));
        }
        return l;
    }
    
    public def indexOf(v: T): Int {
        return indexOf(0, v);
    }
    
    public def indexOf(index: Int, v: T): Int {
        for (var i: Int = index; i < a.size(); i++) {
            if (v==null ? a(i)==null : v.equals(a(i)))
            	return i;
        }
        return -1;
    }
    
    public def lastIndexOf(v: T): Int {
        return lastIndexOf(a.size() as int -1, v);
    }
    
    public def lastIndexOf(index: Int, v: T): Int {
        for (var i: Int = index; i >= 0; i--) {
            if (v==null ? a(i)==null : v.equals(a(i)))
            	return i;
        }
        return -1;
    }

    public def moveSectionToRail(i:Int, j:Int) = a.moveSectionToRail(i,j);

    //
    // iterator
    //

// BIZARRE BUG: renaming S to T causes compiler to fail at isImplicitCastValid at end of X10MethodInstance_c.instantiate
    private static class It[S] implements ListIterator[S] {
        
        private var i: int;
        private val al: ArrayList[S];
        
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
        
        public def remove(): void {
            al.removeAt(i);
        }
        
        public def set(v: S): void {
            al.set(v, i);
        }
        
        public def add(v: S): void {
            al.addBefore(i, v);
        }
    }

    public def iterator(): ListIterator[T] {
        return new It[T](this);
    }
    
    public def iteratorFrom(i: Int): ListIterator[T] {
        return new It[T](this, i);
    }
    
    public def reverse(): void {
        val length = a.size() as int;
        for (var i: Int = 0; i < length/2; i++) {
            exch(a, i, length-1-i);
        }
    }

    // [NN]: should not need to cast x to Comparable[T]
    public def sort() {T <: Comparable[T]} { sort((x:T, y:T) => (x as Comparable[T]).compareTo(y)); }
    public def sort(cmp: (T,T)=>Int) { ArrayUtils.qsort[T](a.rail(), 0, a.size()-1, cmp); }

    // public def sort(lessThan: (T,T)=>Boolean) = qsort(a, 0, a.size()-1, (x:T,y:T) => lessThan(x,y) ? -1 : (lessThan(y,x) ? 1 : 0));

    private def exch(a:GrowableRail[T], i: int, j: int): void {
        val temp = a(i);
        a(i) = a(j);
        a(j) = temp;
    }

    /**
     * Searches this ArrayList for the key using the binary search
     * algorithm.  This ArrayList must be sorted (e.g. by the sort method).
     * If the key is found, return its index in the list.
     * Otherwise, return (-(insertion point) -1), where insertion point is the
     * index at which the key would be inserted into the sorted list.
     * @param key the value to find
     * @param cmp the comparison function to use
     */
    public def binarySearch(key:T, cmp:(T,T)=>Int):Int = ArrayUtils.binarySearch[T](a.rail(), key, 0, a.size(), cmp) as Int;
    public def binarySearch(key:T){T <: Comparable[T]}:Int { 
        return ArrayUtils.binarySearch[T](a.rail(), key, 0, a.size(), (x:T, y:T) => (x as Comparable[T]).compareTo(y)) as Int;
    }

    /**
     * Return the string representation of this ArrayList.
     * 
     * @return the string representation of this ArrayList.
     */
    public def toString(): String {
        val sb = new x10.util.StringBuilder();
        sb.add("[");
        val sz = Math.min(size(), 10);
        for (var i:Int = 0; i < sz; ++i) {
            if (i > 0) sb.add(",");
            sb.add("" + this(i));
        }
        if (sz < size()) sb.add("...(omitted " + (size() - sz) + " elements)");
        sb.add("]");
        return sb.toString();
    }
}
