/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.util;

public class Stack[T] extends ArrayList[T] {
    public def this() { super(); }
    
    /** Add the element to the top of the stack. */
    public def push(v: T) = add(v);
    
    /** Remove and return the top element of the stack. */
    public def pop(): T = removeLast();
    
    /** Return, but do not remove, the top element of the stack. */
    public def peek(): T = getLast();
    
    /**
     * Returns the 1-based position where an object is on this stack.
     * If the v is in the stack, returns the distance from the top of
     * the stack of the occurrence nearest the top of the stack.
     * The topmost item on the stack is considered to be at distance 1.
     * The equals  method is used to compare o to the items in this stack. 
     */
    public def search(v:T) {
        val i = lastIndexOf(v);
        if (i >= 0)
            return size() - i;
        else
            return -1;
    }
}
