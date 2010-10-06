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

import x10.compiler.Header;
import x10.compiler.Inline;
import x10.compiler.Native;
import x10.compiler.NativeRep;


/**
 * A low-level abstraction of a chunk of memory that
 * contains a dense, indexed from 0 collection of 
 * values of type T.  No bounds checking or other
 * error checking is performed on read/write access to
 * this memory.<p>
 *
 * This abstraction is provide to enable other higher-level
 * abstractions (such as Array) to be implemented efficiently
 * and to allow low-level programming of memory regions at the
 * X10 level when absolutely required for performance. This class
 * is not intended for general usage, since it is inherently unsafe.<p>
 */
@NativeRep("java", "x10.core.IndexedMemoryChunk<#1>", null, "new x10.rtt.ParameterizedType(x10.core.IndexedMemoryChunk._RTT, #2)")
@NativeRep("c++", "x10::util::IndexedMemoryChunk<#1 >", "x10::util::IndexedMemoryChunk<#1 >", null)
public struct IndexedMemoryChunk[T] {

    @Native("java", "null")
    @Native("c++", "null")
    private native def this(); // unused; prevent instantiaton outside of native code

    @Native("java", "x10.core.IndexedMemoryChunk.<#2>allocate(#3, #4)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#1 >(#4, 8, false, false)")
    public static native def allocate[T](numElements:int):IndexedMemoryChunk[T];

    @Native("java", "x10.core.IndexedMemoryChunk.<#2>allocate(#3, #4)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#1 >(#4, 8, false, #5)")
    public static native def allocate[T](numElements:int, zeroed:boolean):IndexedMemoryChunk[T];

    @Native("java", "x10.core.IndexedMemoryChunk.<#2>allocate(#3, #4)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#1 >(#4, #5, #6, #7)")
    public static native def allocate[T](numElements:int, alignment:int, pinned:boolean, zeroed:boolean):IndexedMemoryChunk[T];

    @Native("java", "x10.core.IndexedMemoryChunk.<#2>allocate(#3, #4)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#1 >(#4, 8, false, false)")
    public static native def allocate[T](numElements:long):IndexedMemoryChunk[T];

    @Native("java", "x10.core.IndexedMemoryChunk.<#2>allocate(#3, #4)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#1 >(#4, 8, false, #5)")
    public static native def allocate[T](numElements:long, zeroed:boolean):IndexedMemoryChunk[T];

    @Native("java", "x10.core.IndexedMemoryChunk.<#2>allocate(#3, #4)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#1 >(#4, #5, #6, #7)")
    public static native def allocate[T](numElements:long, alignment:int, pinned:boolean, zeroed:boolean):IndexedMemoryChunk[T];


    /**
     * Operator that allows access of IndexedMemoryChunk elements by index.
     *
     * @param i The index to retreive.
     * @return The value at that index.
     */
    @Native("java", "(#0).apply$G(#1)")
    @Native("c++", "(#0)->apply(#1)")
    public native def apply(index:int):T;


    /**
     * Operator that allows access of IndexedMemoryChunk elements by index.
     *
     * @param i The index to retreive.
     * @return The value at that index.
     */
    @Native("java", "(#0).apply$G((int)(#1))")
    @Native("c++", "(#0)->apply(#1)")
    public native def apply(index:long):T;


    /**
     * Operator that allows assignment of IndexedMemoryChunk elements by index.
     *
     * @param v The value to assign.
     * @param i The index of the element to be changed.
     * @return The new value.
     */
    @Native("java", "(#0).set(#1, #2)")
    @Native("c++", "(#0)->set(#1, #2)")
    public native def set(value:T, index:int):void;


    /**
     * Operator that allows assignment of IndexedMemoryChunk elements by index.
     *
     * @param v The value to assign.
     * @param i The index of the element to be changed.
     * @return The new value.
     */
    @Native("java", "(#0).set(#1, (int)(#2))")
    @Native("c++", "(#0)->set(#1, #2)")
    public native def set(value:T, index:long):void;


    /**
     * Copies a contiguous portion of this IndexedMemoryChunk 
     * to a destination IndexedMemoryChunk at the specified place.
     * If the destination place is the current place, then the copy happens synchronously.
     * If the destination place is not the same as the current place, then
     * the copy happens asynchronously and the created remote activity will be 
     * registered with the dynamically enclosing finish of the activity that invoked 
     * asyncCopyTo.</p>
     *
     * Note: No checking is performed to verify that this operation is safe;
     * it is the responsibility of higher-level abstractions built on top of 
     * IndexedMemoryChunk to ensure memory, type, and place safety.
     *
     * @param srcIndex the index of the first element to copy in the source.
     * @param dstPlace the destination place (must be the real home of dst).
     * @param dst the destination IndexedMemoryChunk.
     * @param dstIndex the index of the first element to store in the destination.
     * @param numElems the number of elements to copy.
     */
    @Native("java", "x10.util.IndexedMemoryChunk__NativeRep.copyTo_0_$_x10$util$IndexedMemoryChunk__NativeRep_T_$_3_$_x10$util$IndexedMemoryChunk__NativeRep_T_$(#8, #0,#1,#2,#3,#4,#5)")
    @Native("c++", "(#0)->copyTo(#1,#2,#3,#4,#5)")
    public native def asyncCopyTo (srcIndex:int, 
                                   dstPlace:Place, dst:IndexedMemoryChunk[T], dstIndex:int, 
                                   numElems:int):void;


    /**
     * Copies a contiguous portion of the src IndexedMemoryChunk found
     * at the specified place into this IndexedMemoryChunk.
     * If the source place is the current place, then the copy happens synchronously.
     * If the source place is not the same as the current place, then
     * the copy happens asynchronously and the created remote activity will be 
     * registered with the dynamically enclosing finish of the activity that invoked 
     * asyncCopyFrom.<p>
     *
     * Note: No checking is performed to verify that this operation is safe;
     * it is the responsibility of higher-level abstractions built on top of 
     * IndexedMemoryChunk to ensure memory, type, and place safety.
     *
     * @param dstIndex the index of the first element to store in the destination.
     * @param srcPlace the source place (must be the real home of src).
     * @param src the destination IndexedMemoryChunk.
     * @param srcIndex the index of the first element to copy in the source.
     * @param numElems the number of elements to copy.
     */
    @Native("java", "x10.util.IndexedMemoryChunk__NativeRep.copyFrom_0_$_x10$util$IndexedMemoryChunk__NativeRep_T_$_3_$_x10$util$IndexedMemoryChunk__NativeRep_T_$(#8, #0,#1,#2,#3,#4,#5)")
    @Native("c++", "(#0)->copyFrom(#1,#2,#3,#4,#5)")
    public native def asyncCopyFrom(dstIndex:int,
                                    srcPlace:Place, src:IndexedMemoryChunk[T], srcIndex:int,
                                    numElems:int):void;


   /*
    * @Native methods from Any because the handwritten C++ code doesn't 100% match 
    * what the compiler would have generated.
    */

    @Native("java", "(#0).toString()")
    @Native("c++", "(#0)->toString()")
    public native def  toString():String;

    @Native("java", "(#0).equals(#1)")
    @Native("c++", "(#0)->equals(#1)")
    public native def equals(that:Any):Boolean;

    @Native("java", "(#0).hashCode()")
    @Native("c++", "(#0)->hash_code()")
    public native def  hashCode():Int;
}

// vim:shiftwidth=4:tabstop=4:expandtab
