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
 * A GrowableIndexedMemoryChunk provides the abstraction of 
 * a variable size IndexedMemoryChunk.  It is implemented 
 * by wrapping a backing IndexedMemoryChunk and automatically 
 * growing the backing store as needed to support the requested
 * insertion operations. 
 * 
 * @See ArrayList, which provides a similar abstraction of a 
 *      Growable Array. Like Array, ArrayList is type safe.
 */
public final class GrowableIndexedMemoryChunk[T] implements CustomSerialization {
    private var imc:IndexedMemoryChunk[T];

   /**
    * Elements 0..length-1 have valid entries of type T.
    * Elements length..imc.length-1 may not be valid 
    * values of type T.  
    * It is an invariant of this class, that such elements
    * will never be accessed.
    */
    private var length:int;

    /** 
     * Create a GrowableIndexedMemoryChunk with an initial 
     * capacity of 0.
     */
    public def this() {
        this(0);
    }

    /** 
     * Create a GrowableIndexedMemoryChunk with an initial 
     * capacity of cap.
     */
    public def this(cap:Int) {
        imc = IndexedMemoryChunk.allocateZeroed[T](cap);
        length = 0;
    }

    private def this(sd:SerialData) {
        val data = sd.data as IndexedMemoryChunk[T];
        imc = IndexedMemoryChunk.allocateUninitialized[T](data.length());
        IndexedMemoryChunk.copy(data, 0, imc, 0, data.length());
        length = data.length();
    }

    public def serialize():SerialData {
        val data = IndexedMemoryChunk.allocateUninitialized[T](length);
        IndexedMemoryChunk.copy(imc, 0, data, 0, length);
        return new SerialData(data, null);
    }

    /**
     * Store v as the length element, growing the backing store if needed.
     */
    public def add(v:T):void {
        if (length+1 > capacity()) grow(length+1);
        imc(length++) = v;
    }

    /** 
     * Insert all elements of items starting at the specified index.
     * This will raise an UnsupportedOperationException if p > length.
     * If p<length, then the elements from p..length-1 will first
     * be slide out of the way (growing the backing storage if needed)
     * and then the items inserted.
     * If p==length, then insert is equivalent to calling add for
     * each element of items in turn.
     */
    public def insert(p:Int, items:IndexedMemoryChunk[T]):void {
        val addLen = items.length();
        val newLen = length + addLen;
        val movLen = length - p;
        if (CompilerFlags.checkBounds() && movLen < 0) illegalGap(p, length);
        if (newLen > capacity()) grow(newLen);
        if (movLen > 0) {
            IndexedMemoryChunk.copy(imc, p, imc, p+addLen, movLen);
        }
        IndexedMemoryChunk.copy(items, 0, imc, p, items.length());
        length = newLen;
    }

    public @Inline operator this(idx:Int):T {
        if (CompilerFlags.checkBounds() && idx >= length) raiseIndexOutOfBounds(idx, length);
        return imc(idx);
    }

    public @Inline operator this(idx:Int)=(v:T):void {
        if (CompilerFlags.checkBounds() && idx > length) illegalGap(idx, length);
        if (idx == length) {
            add(v);
        } else {
            imc(idx) = v;
        }
    }

    def imc() = imc;

    /** 
     * Get the current length; indices from 0..length-1 are currently valid 
     * values of type T and may be accessed.
     */
    public def length():Int = length;

    /**
     * What is the current capacity (size of backing storage)
     */
    public def capacity():Int = imc.length();

    /** 
     * Remove the last element. May shrink backing storage.
     */
    public def removeLast():void {
        imc.clear(length-1,1);
        length = length-1;
        shrink(length+1);
    }

    /** 
     * Remove all elements.
     */
    public def clear():void {
        imc.clear(0,length-1);
        length = 0;
    }

    /**
     * Transfer elements between i and j (inclusive) into a new IMC,
     * in the order in which they appear in this rail.  The elements
     * following element j are shifted over to position i.
     * (j-i+1) must be no greater than s, the size of the rail.
     * On return the rail has s-(j-i+1) elements.
     * May shrink backing storage.
     */
    public def moveSectionToIndexedMemoryChunk(i:Int, j:Int):IndexedMemoryChunk[T] {
        val len = j - i + 1;
        if (len < 1) return IndexedMemoryChunk.allocateUninitialized[T](0);
	val tmp = IndexedMemoryChunk.allocateUninitialized[T](len);
        IndexedMemoryChunk.copy(imc, i, tmp, 0, len);
        IndexedMemoryChunk.copy(imc, j+1, imc, i, length-j-1);
        imc.clear(length-len, len);
        length-=len;
        shrink(length+1);
        return tmp;
    }

    public def moveSectionToArray(i:Int, j:Int):Rail[T] {
        val len = j - i + 1;
        if (len < 1) return new Rail[T]();
        val tmp = new Rail[T](len, imc(i));
        for (var k:Int=1; k<len; ++k) {
//          IndexedMemoryChunk.copy(imc, i, tmp, 0, len);
            tmp(k) = imc(i+k);
        }
        IndexedMemoryChunk.copy(imc, j+1, imc, i, length-j-1);
        imc.clear(length-len, len);
        length-=len;
        shrink(length+1);
        return tmp;
    }

    /**
     * Copy current data into IndexedMemoryChunk
     */
    public def toIndexedMemoryChunk():IndexedMemoryChunk[T] {
       val ans = IndexedMemoryChunk.allocateUninitialized[T](length);
       IndexedMemoryChunk.copy(imc, 0, ans, 0, length);
       return ans;
    }

    /** 
     * Copy current data into an Array.
     */
    public def toArray():Rail[T] {
        val ans = length > 0 ? new Rail[T](length, imc(0)) : new Rail[T]();
        for (var i:Int=1; i<length; ++i) {
            ans(i) = imc(i);
        }
        return ans;
    }

    public def grow(var newCapacity:int):void {
        var oldCapacity:int = capacity();
        if (newCapacity < oldCapacity*2) {
            newCapacity = oldCapacity*2;
        } 
        if (newCapacity < 8) {
            newCapacity = 8;
        }
        
        val tmp = IndexedMemoryChunk.allocateUninitialized[T](newCapacity);
        IndexedMemoryChunk.copy(imc, 0, tmp, 0, length);
        tmp.clear(length, newCapacity-length);
        imc.deallocate();
        imc = tmp;
    }

    public def shrink(var newCapacity:int):void {
        if (newCapacity > capacity()/4 || newCapacity < 8)
            return;
        newCapacity = x10.lang.Math.max(newCapacity, length);
        newCapacity = x10.lang.Math.max(newCapacity, 8);
        val tmp = IndexedMemoryChunk.allocateUninitialized[T](newCapacity);        
        IndexedMemoryChunk.copy(imc, 0, tmp, 0, length);
        tmp.clear(length, newCapacity-length);
        imc.deallocate();
        imc = tmp;
    }

    private static @NoInline @NoReturn def raiseIndexOutOfBounds(idx:int, length:int) {
        throw new ArrayIndexOutOfBoundsException("Index is "+idx+"; length is "+length);
    }

    private static @NoInline @NoReturn def illegalGap(idx:int, length:int) {
        throw new UnsupportedOperationException("Insert at "+idx+" would have created gap (length = "+length+")");
    }
}

