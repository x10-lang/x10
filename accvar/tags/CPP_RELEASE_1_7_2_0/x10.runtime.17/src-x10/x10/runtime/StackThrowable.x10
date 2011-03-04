// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.runtime;

import x10.util.GrowableRail;

public class StackThrowable {

    private var a: GrowableRail[Throwable];
    
    /* XXXX
    public def this(c: Container[Throwable]) {
        addAll(c);
    }
    */

    public def contains(v: Throwable): Boolean {
        for (var i: Int = 0; i < a.length(); i++) {
            if (/*v == null ? a(i) == null : XXXX */ v.equals(a(i))) {
                return true;
            }
        }
        return false;
    }
    
    /* XXXX
    public def clone(): StackThrowable {
        return new StackThrowable(this);
    }
    */
    
    public def add(v: Throwable): Boolean {
        a.add(v);
        return true;
    }

    public def remove(v: Throwable): Boolean {
        for (var i: Int = 0; i < a.length(); i++) {
            if (/*v == null ? a(i) == null : XXXX */ v.equals(a(i))) {
                removeAt(i);
                return true;
            }
        }
        return false;
    }
    
    public def addBefore(i: int, v: Throwable): Void {
        a.add(v);
        for (var j: int = i+1; j < a.length(); j++) {
            a(j) = a(j-1);
        }
        a(i) = v;
    }

    public def set(v: Throwable, i: int): Throwable {
        a(i) = v;
        return v;
    }

    public def removeAt(i: int): Throwable {
        val v = a(i);
        for (var j: int = i+1; j < a.length(); j++) {
            a(j-1) = a(j);
        }
        a.removeLast();
        return v;
    }
            
    public def apply(i: nat) = a(i);

    public def get(i: int): Throwable = a(i);

    public def size(): int = a.length();
    
    public def isEmpty(): Boolean = size() == 0;

    // DEPRECAThrowableED
    public def toArray(): Rail[Throwable] = a.toRail();

    public def toRail(): Rail[Throwable] = a.toRail();
    public def toValRail(): ValRail[Throwable] = a.toValRail();

    public def this() {
        a = new GrowableRail[Throwable]();
    }
    

    public def this(size: Int) {
        a = new GrowableRail[Throwable](size);
    }
    
    public def removeFirst(): Throwable = removeAt(0);
    public def removeLast(): Throwable = removeAt(a.length()-1);
    public def getFirst(): Throwable = get(0);
    public def getLast(): Throwable = get(a.length()-1);

    /* XXXX
    public def indices(): List[Int] {
        val l = new ArrayList[int]();
        for (var i: Int = 0; i < a.length(); i++) {
            l.add(i);
        }
        return l;
    }
    */
    
    /* XXXX
    public def subList(begin: Int, end: Int): List[Throwable] {
        val l = new ArrayList[Throwable]();
        for (var i: Int = begin; i < a.length() && i < end; i++) {
           l.add(a(i));
        }
        return l;
    }
    */
    
    public def indexOf(v: Throwable): Int {
        return indexOf(0, v);
    }
    
    public def indexOf(index: Int, v: Throwable): Int {
        for (var i: Int = index; i < a.length(); i++) {
            if (/*v == null ? a(i) == null : XXXX */ v.equals(a(i))) return i;
        }
        return -1;
    }
    
    public def lastIndexOf(v: Throwable): Int {
        return lastIndexOf(a.length()-1, v);
    }
    
    public def lastIndexOf(index: Int, v: Throwable): Int {
        for (var i: Int = index; i >= 0; i--) {
            if (/*v == null ? a(i) == null : XXXX */ v.equals(a(i))) return i;
        }
        return -1;
    }
    
    //
    // iterator
    //

    /* XXXX
    // BIZARRE BUG: renaming S to T causes compiler to fail at isImplicitCastValid at end of X10MethodInstance_c.instantiate
    private static class ItThrowable implements ListIterator[Throwable] {
        
        private var i: int;
        private val al: StackThrowable;
        
        def this(al: StackThrowable) {
            this(al, -1);
        }

        def this(al: StackThrowable, i: int) {
            this.al = al;
            this.i = i;
        }
        
        public def hasNext(): boolean {
            return i+1 < al.size();
        }

        public def nextIndex(): Int {
            return ++i;
        }
        
        public def next(): Throwable {
            return al.a(++i);
        }

        public def hasPrevious(): boolean {
            return i-1 >= 0;
        }

        public def previousIndex(): Int {
            return --i;
        }
        
        public def previous(): Throwable {
            return al.a(--i);
        }
        
        public def remove(): Void {
            al.removeAt(i);
        }
        
        public def set(v: Throwable): Void {
            al.set(v, i);
        }
        
        public def add(v: Throwable): Void {
            al.addBefore(i, v);
        }
    }

    public def iterator(): ListIterator[Throwable] {
        return new ItThrowable(this);
    }
    
    public def iteratorFrom(i: Int): ListIterator[Throwable] {
        return new ItThrowable(this, i);
    }
    */
    
    /* XXXX
    public def reverse(): Void {
        val length = a.length();
        for (var i: Int = 0; i < length/2; i++) {
            exch(a, i, length-1-i);
        }
    }
    */


    /** Add the element to the top of the stack. */
    public def push(v: Throwable) = add(v);
    
    /** Remove and return the top element of the stack. */
    public def pop(): Throwable = removeLast();
    
    /** Return, but do not remove, the top element of the stack. */
    public def peek(): Throwable = getLast();
    
    /**
     * Returns the 1-based position where an object is on this stack.
     * If the v is in the stack, returns the distance from the top of
     * the stack of the occurrence nearest the top of the stack.
     * The topmost item on the stack is considered to be at distance 1.
     * The equals  method is used to compare o to the items in this stack. 
     */
    public def search(v:Throwable) {
        val i = lastIndexOf(v);
        if (i >= 0)
            return size() - i;
        else
            return -1;
    }
}