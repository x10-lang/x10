// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.runtime;

import x10.util.GrowableRail;

public class StackThread {

    private var a: GrowableRail[Thread];
    
    /* XXXX
    public def this(c: Container[Thread]) {
        addAll(c);
    }
    */

    public def contains(v: Thread): Boolean {
        for (var i: Int = 0; i < a.length(); i++) {
            if (/*v == null ? a(i) == null : XXXX */ v.equals(a(i))) {
                return true;
            }
        }
        return false;
    }
    
    /* XXXX
    public def clone(): StackThread {
        return new StackThread(this);
    }
    */
    
    public def add(v: Thread): Boolean {
        a.add(v);
        return true;
    }

    public def remove(v: Thread): Boolean {
        for (var i: Int = 0; i < a.length(); i++) {
            if (/*v == null ? a(i) == null : XXXX */ v.equals(a(i))) {
                removeAt(i);
                return true;
            }
        }
        return false;
    }
    
    public def addBefore(i: int, v: Thread): Void {
        a.add(v);
        for (var j: int = i+1; j < a.length(); j++) {
            a(j) = a(j-1);
        }
        a(i) = v;
    }

    public def set(v: Thread, i: int): Thread {
        a(i) = v;
        return v;
    }

    public def removeAt(i: int): Thread {
        val v = a(i);
        for (var j: int = i+1; j < a.length(); j++) {
            a(j-1) = a(j);
        }
        a.removeLast();
        return v;
    }
            
    public def apply(i: nat) = a(i);

    public def get(i: int): Thread = a(i);

    public def size(): int = a.length();
    
    public def isEmpty(): Boolean = size() == 0;

    // DEPRECAThreadED
    public def toArray(): Rail[Thread] = a.toRail();

    public def toRail(): Rail[Thread] = a.toRail();
    public def toValRail(): ValRail[Thread] = a.toValRail();

    public def this() {
        a = new GrowableRail[Thread]();
    }
    

    public def this(size: Int) {
        a = new GrowableRail[Thread](size);
    }
    
    public def removeFirst(): Thread = removeAt(0);
    public def removeLast(): Thread = removeAt(a.length()-1);
    public def getFirst(): Thread = get(0);
    public def getLast(): Thread = get(a.length()-1);

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
    public def subList(begin: Int, end: Int): List[Thread] {
        val l = new ArrayList[Thread]();
        for (var i: Int = begin; i < a.length() && i < end; i++) {
           l.add(a(i));
        }
        return l;
    }
    */
    
    public def indexOf(v: Thread): Int {
        return indexOf(0, v);
    }
    
    public def indexOf(index: Int, v: Thread): Int {
        for (var i: Int = index; i < a.length(); i++) {
            if (/*v == null ? a(i) == null : XXXX */ v.equals(a(i))) return i;
        }
        return -1;
    }
    
    public def lastIndexOf(v: Thread): Int {
        return lastIndexOf(a.length()-1, v);
    }
    
    public def lastIndexOf(index: Int, v: Thread): Int {
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
    private static class ItThread implements ListIterator[Thread] {
        
        private var i: int;
        private val al: StackThread;
        
        def this(al: StackThread) {
            this(al, -1);
        }

        def this(al: StackThread, i: int) {
            this.al = al;
            this.i = i;
        }
        
        public def hasNext(): boolean {
            return i+1 < al.size();
        }

        public def nextIndex(): Int {
            return ++i;
        }
        
        public def next(): Thread {
            return al.a(++i);
        }

        public def hasPrevious(): boolean {
            return i-1 >= 0;
        }

        public def previousIndex(): Int {
            return --i;
        }
        
        public def previous(): Thread {
            return al.a(--i);
        }
        
        public def remove(): Void {
            al.removeAt(i);
        }
        
        public def set(v: Thread): Void {
            al.set(v, i);
        }
        
        public def add(v: Thread): Void {
            al.addBefore(i, v);
        }
    }

    public def iterator(): ListIterator[Thread] {
        return new ItThread(this);
    }
    
    public def iteratorFrom(i: Int): ListIterator[Thread] {
        return new ItThread(this, i);
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
    public def push(v: Thread) = add(v);
    
    /** Remove and return the top element of the stack. */
    public def pop(): Thread = removeLast();
    
    /** Return, but do not remove, the top element of the stack. */
    public def peek(): Thread = getLast();
    
    /**
     * Returns the 1-based position where an object is on this stack.
     * If the v is in the stack, returns the distance from the top of
     * the stack of the occurrence nearest the top of the stack.
     * The topmost item on the stack is considered to be at distance 1.
     * The equals  method is used to compare o to the items in this stack. 
     */
    public def search(v:Thread) {
        val i = lastIndexOf(v);
        if (i >= 0)
            return size() - i;
        else
            return -1;
    }
}