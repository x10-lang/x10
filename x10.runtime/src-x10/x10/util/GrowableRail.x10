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

import x10.compiler.CompilerFlags;
import x10.compiler.Inline;
import x10.compiler.NoInline;
import x10.compiler.NoReturn;
import x10.io.CustomSerialization;
import x10.io.SerialData;

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
    private var size:long;

    /** 
     * Create a GrowableRail with an initial 
     * capacity of 0.
     */
    public def this() {
        this(0L);
    }

    /** 
     * Create a GrowableRail with an initial 
     * capacity of cap.
     */
    public def this(cap:long) {
        data = Unsafe.allocRailZeroed[T](cap);
        size = 0L;
    }

    private def this(sd:SerialData) {
        val src = sd.data as Rail[T];
        data = Unsafe.allocRailUninitialized[T](src.size);
        Rail.copy(src, 0L, data, 0L, src.size);
        size = src.size;
    }

    public def serialize():SerialData {
        val tmp = Unsafe.allocRailUninitialized[T](size);
        Rail.copy(data, 0L, tmp, 0L, size);
        return new SerialData(tmp, null);
    }

    /**
     * Store v as the size element, growing the backing store if needed.
     */
    public def add(v:T):void {
        if (size+1 > capacity()) grow(size+1);
        data(size++) = v;
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
    public def insert(p:long, items:Rail[T]):void {
        val addLen = items.size;
        val newLen = size + addLen;
        val movLen = size - p;
        if (CompilerFlags.checkBounds() && movLen < 0L) illegalGap(p, size);
        if (newLen > capacity()) grow(newLen);
        if (movLen > 0L) {
            Rail.copy(data, p, data, p+addLen, movLen);
        }
        Rail.copy(items, 0L, data, p, items.size);
        size = newLen;
    }

    public @Inline operator this(idx:long):T {
        if (CompilerFlags.checkBounds() && idx >= size) raiseIndexOutOfBounds(idx, size);
        return data(idx);
    }

    public @Inline operator this(idx:long)=(v:T):void {
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
            for (i in 0L..(size()-1)) {
                if (data(i) == null) return true;
            }
        } else {
            for (i in 0L..(size()-1)) {
                if (v.equals(data(i))) return true;
            }
        }
        return false;
    }

    /**
     * Is the GrowableRail empty? 
     * @return the value of the expession size() == 0L
     */
    public def isEmpty():Boolean = size == 0L;

    /** 
     * Get the current size; indices from 0..size-1 are currently valid 
     * values of type T and may be accessed.
     */
    public def size():long = size;

    /**
     * What is the current capacity (size of backing storage)
     */
    public def capacity():long = data.size;

    /** 
     * Remove the last element and return it. May shrink backing storage.
     */
    public def removeLast():T {
        val res = this(size-1);
        Unsafe.clearRail(data, size-1, 1L);
        size = size-1;
        shrink(size+1);
        return res;
    }

    /** 
     * Remove all elements.
     */
    public def clear():void {
        Unsafe.clearRail(data, 0L, size-1);
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
    public def moveSectionToRail(i:long, j:long):Rail[T] {
        val len = j - i + 1;
        if (len < 1) return Unsafe.allocRailUninitialized[T](0L);
	val tmp = Unsafe.allocRailUninitialized[T](len);
        Rail.copy(data, i, tmp, 0L, len);
        Rail.copy(data, j+1, data, i, size-j-1);
        Unsafe.clearRail(data, size-len, len);
        size-=len;
        shrink(size+1);
        return tmp;
    }

    /**
     * Copy current data into Rail
     */
    public def toRail():Rail[T] {
       val ans = Unsafe.allocRailUninitialized[T](size);
       Rail.copy(data, 0L, ans, 0L, size);
       return ans;
    }

    public def grow(var newCapacity:long):void {
        var oldCapacity:long = capacity();
        if (newCapacity < oldCapacity*2) {
            newCapacity = oldCapacity*2;
        } 
        if (newCapacity < 8) {
            newCapacity = 8;
        }
        
        val tmp = Unsafe.allocRailUninitialized[T](newCapacity);
        Rail.copy(data, 0L, tmp, 0L, size);
        Unsafe.clearRail(tmp, size, newCapacity-size);
	Unsafe.dealloc(data);
        data = tmp;
    }

    public def shrink(var newCapacity:long):void {
        if (newCapacity > capacity()/4 || newCapacity < 8)
            return;
        newCapacity = x10.lang.Math.max(newCapacity, size);
        newCapacity = x10.lang.Math.max(newCapacity, 8L);
        val tmp = Unsafe.allocRailUninitialized[T](newCapacity);        
        Rail.copy(data, 0L, tmp, 0L, size);
        Unsafe.clearRail(tmp, size, newCapacity-size);
        Unsafe.dealloc(data);
        data = tmp;
    }

    private static @NoInline @NoReturn def raiseIndexOutOfBounds(idx:long, size:long) {
        throw new ArrayIndexOutOfBoundsException("Index is "+idx+"; size is "+size);
    }

    private static @NoInline @NoReturn def illegalGap(idx:long, size:long) {
        throw new UnsupportedOperationException("Insert at "+idx+" would have created gap (size = "+size+")");
    }
}
