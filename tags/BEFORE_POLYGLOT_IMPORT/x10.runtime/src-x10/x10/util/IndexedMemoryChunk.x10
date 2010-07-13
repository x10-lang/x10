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


/*
 * Implementation note.
 *
 * For C++ we are mapping IndexedChunk to an optimized hand-written class that provides
 * the minimal required functionality.
 * 
 * For Java, we are currently mapping to Rail. I think the plan here should be to 
 * continue mapping to Rail.  Once Rail disappears as an X10-level source consturct,
 * we can then simplify the Java implementation of Rail accordingly.
 * Alternatively, we could go ahead with the customized implementation of IndexedChunk<T>
 * in Java eagerly and then get rid of the Java implementation of Rail at a later date
 * when we kill the X10 version of same.
 */

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
@NativeRep("java", "x10.core.Rail<#1>", null, "new x10.rtt.ParameterizedType(x10.core.Rail._RTT, #2)")
@NativeRep("c++", "x10::util::IndexedMemoryChunk<#1 >", "x10::util::IndexedMemoryChunk<#1 >", null)
public struct IndexedMemoryChunk[T] {

    @Native("java", "null")
    @Native("c++", "null")
    private native def this(); // unused; prevent instantiaton outside of native code

    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#1 >(#4, 8, false, false)")
    public static native def allocate[T](numElements:int):IndexedMemoryChunk[T];

    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#1 >(#4, 8, false, #5)")
    public static native def allocate[T](numElements:int, zeroed:boolean):IndexedMemoryChunk[T];

    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#1 >(#4, #5, #6, #7)")
    public static native def allocate[T](numElements:int, alignment:int, pinned:boolean, zeroed:boolean):IndexedMemoryChunk[T];


    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#1 >(#4, 8, false, false)")
    public static native def allocate[T](numElements:long):IndexedMemoryChunk[T];

    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#1 >(#4, 8, false, #5)")
    public static native def allocate[T](numElements:long, zeroed:boolean):IndexedMemoryChunk[T];

    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4)")
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
    public native safe def apply(index:int):T;


    /**
     * Operator that allows access of IndexedMemoryChunk elements by index.
     *
     * @param i The index to retreive.
     * @return The value at that index.
     */
    @Native("java", "(#0).apply$G((int)(#1))")
    @Native("c++", "(#0)->apply(#1)")
    public native safe def apply(index:long):T;


    /**
     * Operator that allows assignment of IndexedMemoryChunk elements by index.
     *
     * @param v The value to assign.
     * @param i The index of the element to be changed.
     * @return The new value.
     */
    @Native("java", "(#0).set$G(#1, #2)")
    @Native("c++", "(#0)->set(#1, #2)")
    public native safe def set(value:T, index:int):void;


    /**
     * Operator that allows assignment of IndexedMemoryChunk elements by index.
     *
     * @param v The value to assign.
     * @param i The index of the element to be changed.
     * @return The new value.
     */
    @Native("java", "(#0).set$G(#1, (int)(#2))")
    @Native("c++", "(#0)->set(#1, #2)")
    public native safe def set(value:T, index:long):void;


   /* TODO: Java codegen doesn't support static overloading on signed vs. unsigned.
    native safe def apply(index:uint):T;
    native safe def apply(index:ulong):T;
    native safe def set(value:T, index:uint):void;
    native safe def set(value:T, index:ulong):void;
    */
}
