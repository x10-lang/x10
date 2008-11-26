// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.runtime;

import x10.util.GrowableRail;

public class StackActivity {

    private var a: GrowableRail[Activity];
    
    /* XXXX
    public def this(c: Container[Activity]) {
        addAll(c);
    }
    */

    public def contains(v: Activity): Boolean {
        for (var i: Int = 0; i < a.length(); i++) {
            if (/*v == null ? a(i) == null : XXXX */ v.equals(a(i))) {
                return true;
            }
        }
        return false;
    }
    
    /* XXXX
    public def clone(): StackActivity {
        return new StackActivity(this);
    }
    */
    
    public def add(v: Activity): Boolean {
        a.add(v);
        return true;
    }

    public def remove(v: Activity): Boolean {
        for (var i: Int = 0; i < a.length(); i++) {
            if (/*v == null ? a(i) == null : XXXX */ v.equals(a(i))) {
                removeAt(i);
                return true;
            }
        }
        return false;
    }
    
    public def addBefore(i: int, v: Activity): Void {
        a.add(v);
        for (var j: int = i+1; j < a.length(); j++) {
            a(j) = a(j-1);
        }
        a(i) = v;
    }

    public def set(v: Activity, i: int): Activity {
        a(i) = v;
        return v;
    }

    public def removeAt(i: int): Activity {
        val v = a(i);
        for (var j: int = i+1; j < a.length(); j++) {
            a(j-1) = a(j);
        }
        a.removeLast();
        return v;
    }
            
    public def apply(i: nat) = a(i);

    public def get(i: int): Activity = a(i);

    public def size(): int = a.length();
    
    public def isEmpty(): Boolean = size() == 0;

    // DEPRECAActivityED
    public def toArray(): Rail[Activity] = a.toRail();

    public def toRail(): Rail[Activity] = a.toRail();
    public def toValRail(): ValRail[Activity] = a.toValRail();

    public def this() {
        a = new GrowableRail[Activity]();
    }
    

    public def this(size: Int) {
        a = new GrowableRail[Activity](size);
    }
    
    public def removeFirst(): Activity = removeAt(0);
    public def removeLast(): Activity = removeAt(a.length()-1);
    public def getFirst(): Activity = get(0);
    public def getLast(): Activity = get(a.length()-1);

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
    public def subList(begin: Int, end: Int): List[Activity] {
        val l = new ArrayList[Activity]();
        for (var i: Int = begin; i < a.length() && i < end; i++) {
           l.add(a(i));
        }
        return l;
    }
    */
    
    public def indexOf(v: Activity): Int {
        return indexOf(0, v);
    }
    
    public def indexOf(index: Int, v: Activity): Int {
        for (var i: Int = index; i < a.length(); i++) {
            if (/*v == null ? a(i) == null : XXXX */ v.equals(a(i))) return i;
        }
        return -1;
    }
    
    public def lastIndexOf(v: Activity): Int {
        return lastIndexOf(a.length()-1, v);
    }
    
    public def lastIndexOf(index: Int, v: Activity): Int {
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
    private static class ItActivity implements ListIterator[Activity] {
        
        private var i: int;
        private val al: StackActivity;
        
        def this(al: StackActivity) {
            this(al, -1);
        }

        def this(al: StackActivity, i: int) {
            this.al = al;
            this.i = i;
        }
        
        public def hasNext(): boolean {
            return i+1 < al.size();
        }

        public def nextIndex(): Int {
            return ++i;
        }
        
        public def next(): Activity {
            return al.a(++i);
        }

        public def hasPrevious(): boolean {
            return i-1 >= 0;
        }

        public def previousIndex(): Int {
            return --i;
        }
        
        public def previous(): Activity {
            return al.a(--i);
        }
        
        public def remove(): Void {
            al.removeAt(i);
        }
        
        public def set(v: Activity): Void {
            al.set(v, i);
        }
        
        public def add(v: Activity): Void {
            al.addBefore(i, v);
        }
    }

    public def iterator(): ListIterator[Activity] {
        return new ItActivity(this);
    }
    
    public def iteratorFrom(i: Int): ListIterator[Activity] {
        return new ItActivity(this, i);
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
    public def push(v: Activity) = add(v);
    
    /** Remove and return the top element of the stack. */
    public def pop(): Activity = removeLast();
    
    /** Return, but do not remove, the top element of the stack. */
    public def peek(): Activity = getLast();
    
    /**
     * Returns the 1-based position where an object is on this stack.
     * If the v is in the stack, returns the distance from the top of
     * the stack of the occurrence nearest the top of the stack.
     * The topmost item on the stack is considered to be at distance 1.
     * The equals  method is used to compare o to the items in this stack. 
     */
    public def search(v:Activity) {
        val i = lastIndexOf(v);
        if (i >= 0)
            return size() - i;
        else
            return -1;
    }
}