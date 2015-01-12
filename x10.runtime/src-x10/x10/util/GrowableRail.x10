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

import x10.compiler.CompilerFlags;
import x10.compiler.Inline;
import x10.compiler.NoInline;
import x10.compiler.NoReturn;

import x10.io.CustomSerialization;
import x10.io.Serializer;
import x10.io.Deserializer;

/**
 * A GrowableRail provides the abstraction of a variable size Rail.  
 * It is implemented by wrapping a backing Rail and automatically 
 * growing the backing store as needed to support the requested
 * insertion operations. 
 * 
 * @See ArrayList, which provides a similar abstraction of a 
 *      Growable Array. Like Rail, ArrayList is type safe.
 */
public final class GrowableRail[T] implements CustomSerialization {
    private var data:Rail[T];

    def rail() = data; // for use by ArrayList and other classes in util package

   /**
    * Elements 0..size-1 have valid entries of type T.
    * Elements size..data.size-1 may not be valid values of type T.  
    * It is an invariant of this class, that such elements
    * will never be accessed.
    */
    private var size:Long;

    /** 
     * Create a GrowableRail with an initial 
     * capacity of 0.
     */
    public def this() {
        this(0);
    }

    /** 
     * Create a GrowableRail with an initial 
     * capacity of cap.
     */
    public def this(cap:Long) {
        data = Unsafe.allocRailZeroed[T](cap);
        size = 0;
    }

    private def this(ds:Deserializer) {
        val src = ds.readAny() as Rail[T];
        data = Unsafe.allocRailUninitialized[T](src.size);
        Rail.copy(src, 0, data, 0, src.size);
        size = src.size;
    }

    public def serialize(s:Serializer) {
        val tmp = Unsafe.allocRailUninitialized[T](size);
        Rail.copy(data, 0, tmp, 0, size);
	s.writeAny(tmp);
    }

    /**
     * Store v as the size element, growing the backing store if needed.
     */
    public def add(v:T):void {
        if (size+1 > capacity()) grow(size+1);
        data(size++) = v;
    }

    /**
     * Add all elements from the argument Rail to this.
     */
    public def addAll(x:Rail[T]) {
       if (size+x.size > capacity()) grow(size+x.size);
       for (i in 0..(x.size-1)) {
           data(size++) = x(i);
       }
    }

    /**
     * Add all elements from the argument GrowableRail to this.
     */
    public def addAll(x:GrowableRail[T]) {
       addAll(x.data);
    }

    /** 
     * Insert all elements of items starting at the specified index.
     * This will raise an UnsupportedOperationException if p > size.
     * If p<size, then the elements from p..size-1 will first
     * be slide out of the way (growing the backing storage if needed)
     * and then the items inserted.
     * If p==size, then insert is equivalent to calling add for
     * each element of items in turn.
     */
    public def insert(p:Long, items:Rail[T]):void {
        val addLen = items.size;
        val newLen = size + addLen;
        val movLen = size - p;
        if (CompilerFlags.checkBounds() && movLen < 0) illegalGap(p, size);
        if (newLen > capacity()) grow(newLen);
        if (movLen > 0) {
            Rail.copy(data, p, data, p+addLen, movLen);
        }
        Rail.copy(items, 0, data, p, items.size);
        size = newLen;
    }

    public operator this(idx:Long):T {
        if (CompilerFlags.checkBounds() && idx >= size) raiseIndexOutOfBounds(idx, size);
        return data(idx);
    }

    public operator this(idx:Long)=(v:T):void {
        if (CompilerFlags.checkBounds() && idx > size) illegalGap(idx, size);
        if (idx == size) {
            add(v);
        } else {
            data(idx) = v;
        }
    }

    /**
     * Does the GrowableRail contain an element that is equal to v?
     *
     * @param v the element to search for
     * @return <code>true</code> if the GrowableRail contains an
     * element that is equal to v, <code>false</code> otherwise.
     */
    public def contains(v:T):Boolean {
        if (v == null) {
            for (i in 0..(size()-1)) {
                if (data(i) == null) return true;
            }
        } else {
            for (i in 0..(size()-1)) {
                if (v.equals(data(i))) return true;
            }
        }
        return false;
    }

    /**
     * Is the GrowableRail empty? 
     * @return the value of the expession size() == 0
     */
    public def isEmpty():Boolean = size == 0;

    /** 
     * Get the current size; indices from 0..size-1 are currently valid 
     * values of type T and may be accessed.
     */
    public def size():Long = size;

    /**
     * What is the current capacity (size of backing storage)
     */
    public def capacity():Long = data.size;

    /** 
     * Remove the last element and return it. May shrink backing storage.
     */
    public def removeLast():T {
        val res = this(size-1);
        shrink(size-1);
        return res;
    }

    /** 
     * Remove all elements.
     */
    public def clear():void {
        Unsafe.clearRail(data);
        size = 0;
    }

    /**
     * Transfer elements between i and j (inclusive) into a new Rail,
     * in the order in which they appear in this rail.  The elements
     * following element j are shifted over to position i.
     * (j-i+1) must be no greater than s, the size of the rail.
     * On return the rail has s-(j-i+1) elements.
     * May shrink backing storage.
     */
    public def moveSectionToRail(i:Long, j:Long):Rail[T] {
        val len = j - i + 1;
        if (len < 1) return Unsafe.allocRailUninitialized[T](0);
        val tmp = Unsafe.allocRailUninitialized[T](len);
        Rail.copy(data, i, tmp, 0, len);
        Rail.copy(data, j+1, data, i, size-j-1);
        shrink(size-len);
        return tmp;
    }

    /**
     * Copy current data into Rail
     */
    public def toRail():Rail[T] {
       val ans = Unsafe.allocRailUninitialized[T](size);
       Rail.copy(data, 0, ans, 0, size);
       return ans;
    }

    /** 
     * Grow the capacity of this GrowableRail to at least 
     * <code>newCapacity</code>, automatically reallocating storage.
     * On return, capacity is max(newCapacity, oldCapacity*2, 8).
     * The size (number of elements) is unchanged.
     * @param newCapacity the minimum new capacity for this GrowableRail
     */
    public def grow(var newCapacity:Long):void {
        assert (newCapacity >= capacity());
        var oldCapacity:Long = capacity();
        if (newCapacity < oldCapacity*2) {
            newCapacity = oldCapacity*2;
        } 
        if (newCapacity < 8) {
            newCapacity = 8;
        }
        
        val tmp = Unsafe.allocRailUninitialized[T](newCapacity);
        Rail.copy(data, 0, tmp, 0, size);
        Unsafe.clearRail(tmp, size, newCapacity-size);
        Unsafe.dealloc(data);
        data = tmp;
    }

    /** 
     * Shrink the capacity of this GrowableRail and remove all elements
     * above <code>newCapacity</code>.
     * On return, capacity == max(newCapacity, 8) and size == newCapacity.
     * @param newCapacity the new capacity for this GrowableRail
     */
    public def shrink(newCapacity:Long):void {
        assert (newCapacity <= capacity());
        val oldSize = size;
        size = Math.min(size, newCapacity);

        val cap = Math.max(newCapacity, 8);
        if (cap <= capacity()/4) {
            val tmp = Unsafe.allocRailUninitialized[T](cap);        
            Rail.copy(data, 0, tmp, 0, cap);
            Unsafe.dealloc(data);
            data = tmp;
        } else if (size < oldSize) {
            Unsafe.clearRail(data, size, oldSize-size);
        }
    }

    private static @NoInline @NoReturn def raiseIndexOutOfBounds(idx:Long, size:Long) {
        throw new ArrayIndexOutOfBoundsException("Index is "+idx+"; size is "+size);
    }

    private static @NoInline @NoReturn def illegalGap(idx:Long, size:Long) {
        throw new UnsupportedOperationException("Insert at "+idx+" would have created gap (size = "+size+")");
    }

    public def toString():String {
        val sb = new StringBuilder();
        sb.add("[");
        val sz = size > 10 ? 10 : size;
        for (var i:Long = 0; i < sz; i++) {
            if (i > 0)
                sb.add(",");
            sb.add(data(i).toString());
        }
        if (sz < size)
            sb.add("...(omitted " + (size - sz) + " elements)");
        sb.add("]");
        return sb.toString();
    }
}

