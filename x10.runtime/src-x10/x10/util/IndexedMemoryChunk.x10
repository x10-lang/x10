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
 * values of type T.<p>
 *
 * This abstraction is provide to enable other higher-level
 * abstractions (such as Array) to be implemented efficiently
 * and to allow low-level programming of memory regions at the
 * X10 level when absolutely required for performance. Most of the API
 * of this class is safe, but there are some loopholes that can be 
 * used when absolutely necessary for performance..<p>
 */
@NativeRep("java", "x10.core.IndexedMemoryChunk<#1>", null, "new x10.rtt.ParameterizedType(x10.core.IndexedMemoryChunk._RTT, #2)")
@NativeRep("c++", "x10::util::IndexedMemoryChunk<#1 >", "x10::util::IndexedMemoryChunk<#1 >", null)
public struct IndexedMemoryChunk[T] {

    @Native("java", "null")
    @Native("c++", "null")
    private native def this(); // unused; prevent instantiaton outside of native code

    @Native("java", "x10.core.IndexedMemoryChunk.<#2>allocate(#3, #4, false)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#1 >(#4, 8, false, false)")
    public static native def allocate[T](numElements:int):IndexedMemoryChunk[T];

    @Native("java", "x10.core.IndexedMemoryChunk.<#2>allocate(#3, #4, #5)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#1 >(#4, 8, false, #5)")
    public static native def allocate[T](numElements:int, zeroed:boolean):IndexedMemoryChunk[T]{!zeroed || T haszero};

    @Native("java", "x10.core.IndexedMemoryChunk.<#2>allocate(#3, #4, #7)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#1 >(#4, #5, #6, #7)")
    public static native def allocate[T](numElements:int, alignment:int, congruent:boolean, zeroed:boolean):IndexedMemoryChunk[T]{!zeroed || T haszero};

    @Native("java", "x10.core.IndexedMemoryChunk.<#2>allocate(#3, #4, false)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#1 >(#4, 8, false, false)")
    public static native def allocate[T](numElements:long):IndexedMemoryChunk[T];

    @Native("java", "x10.core.IndexedMemoryChunk.<#2>allocate(#3, #4, #5)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#1 >(#4, 8, false, #5)")
    public static native def allocate[T](numElements:long, zeroed:boolean):IndexedMemoryChunk[T]{!zeroed || T haszero};

    @Native("java", "x10.core.IndexedMemoryChunk.<#2>allocate(#3, #4, #7)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#1 >(#4, #5, #6, #7)")
    public static native def allocate[T](numElements:long, alignment:int, congruent:boolean, zeroed:boolean):IndexedMemoryChunk[T]{!zeroed || T haszero};


    /**
     * Operator that allows access of IndexedMemoryChunk elements by index.
     *
     * @param i The index to retreive.
     * @return The value at that index.
     */
    @Native("java", "(#0).apply$G(#1)")
    @Native("c++", "(#0)->apply(#1)")
    public native operator this(index:int):T;


    /**
     * Operator that allows access of IndexedMemoryChunk elements by index.
     *
     * @param i The index to retreive.
     * @return The value at that index.
     */
    @Native("java", "(#0).apply$G((int)(#1))")
    @Native("c++", "(#0)->apply(#1)")
    public native operator this(index:long):T;


    /**
     * Operator that allows assignment of IndexedMemoryChunk elements by index.
     *
     * @param v The value to assign.
     * @param i The index of the element to be changed.
     * @return The new value.
     */
    @Native("java", "(#0).set(#1, #2)")
    @Native("c++", "(#0)->set(#1, #2)")
    public native operator this(index:int)=(value:T):void;


    /**
     * Operator that allows assignment of IndexedMemoryChunk elements by index.
     *
     * @param v The value to assign.
     * @param i The index of the element to be changed.
     * @return The new value.
     */
    @Native("java", "(#0).set(#1, (int)(#2))")
    @Native("c++", "(#0)->set(#1, #2)")
    public native operator this(index:long)=(value:T):void;


    /**
     * Operator that allows UNSAFE access of IndexedMemoryChunk elements by index.
     *
     * @param i The index to retreive.
     * @return The value at that index.
     */
    @Native("java", "(#0).apply$G(#1)")
    @Native("c++", "(#0)->apply_unsafe(#1)")
    public native def apply_unsafe(index:int):T;


    /**
     * Operator that allows UNSAFE access of IndexedMemoryChunk elements by index.
     *
     * @param i The index to retreive.
     * @return The value at that index.
     */
    @Native("java", "(#0).apply$G((int)(#1))")
    @Native("c++", "(#0)->apply_unsafe(#1)")
    public native def apply_unsafe(index:long):T;


    /**
     * Operator that allows UNSAFE assignment of IndexedMemoryChunk elements by index.
     *
     * @param v The value to assign.
     * @param i The index of the element to be changed.
     * @return The new value.
     */
    @Native("java", "(#0).set(#1, #2)")
    @Native("c++", "(#0)->set_unsafe(#1, #2)")
    public native def set_unsafe(value:T, index:int):void;


    /**
     * Operator that allows UNSAFE assignment of IndexedMemoryChunk elements by index.
     *
     * @param v The value to assign.
     * @param i The index of the element to be changed.
     * @return The new value.
     */
    @Native("java", "(#0).set(#1, (int)(#2))")
    @Native("c++", "(#0)->set_unsafe(#1, #2)")
    public native def set_unsafe(value:T, index:long):void;


    /**
     * Return the size of the IndexedMemoryChunk (in elements)
     *
     * @return the size of the IndexedMemoryChunk (in elements)
     */
    @Native("java", "((#0).length)")
    @Native("c++", "(#0)->length()")
    public native def length():int; /* TODO: We need to convert this to returning a long */


    /**
     * Copies a contiguous portion of a local IndexedMemoryChunk 
     * to a destination RemoteIndexedMemoryChunk at the specified place.
     * If the destination place is the current place, then the copy happens synchronously.
     * If the destination place is not the same as the current place, then
     * the copy happens asynchronously and the created remote activity will be 
     * registered with the dynamically enclosing finish of the activity that invoked 
     * asyncCopyTo.</p>
     *
     * Note: This copy is a "raw" copy of the bytes from one indexed memory chunk
     *       to another. If elements of type T contain references to class instances,
     *       they will not be properly serialized.  This method is intended only for use
     *       on non-pointer containing data structures.
     *
     * @param src the source IndexedMemoryChunk.
     * @param srcIndex the index of the first element to copy in the source.
     * @param dst the destination RemoteIndexedMemoryChunk.
     * @param dstIndex the index of the first element to store in the destination.
     * @param numElems the number of elements to copy.
     */
    @Native("java", "x10.core.IndexedMemoryChunk.<#2>asyncCopy(#4,#5,#6,#7,#8)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::asyncCopy<#1 >(#4,#5,#6,#7,#8)")
    public static native def asyncCopy[T](src:IndexedMemoryChunk[T], srcIndex:int, 
                                          dst:RemoteIndexedMemoryChunk[T], dstIndex:int, 
                                          numElems:int):void;

    @Native("java", "x10.core.IndexedMemoryChunk.<#2>asyncCopy(#4,#5,#6,#7,#8,#9)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::asyncCopy<#1 >(#4,#5,#6,#7,#8,#9)")
    public static native def uncountedCopy[T](src:IndexedMemoryChunk[T], srcIndex:int, 
                                              dst:RemoteIndexedMemoryChunk[T], dstIndex:int, 
                                              numElems:int,
                                              notifier:()=>void):void;

    /**
     * Copies a contiguous portion of the remote src RemoteIndexedMemoryChunk 
     * into the local destination IndexedMemoryChunk.
     * If the source place is the current place, then the copy happens synchronously.
     * If the source place is not the same as the current place, then
     * the copy happens asynchronously and the created remote activity will be 
     * registered with the dynamically enclosing finish of the activity that invoked 
     * asyncCopyFrom.<p>
     *
     * Note: This copy is a "raw" copy of the bytes from one indexed memory chunk
     *       to another. If elements of type T contain references to class instances,
     *       they will not be properly serialized.  This method is intended only for use
     *       on non-pointer containing data structures.
     *
     * @param src the source RemoteIndexedMemoryChunk.
     * @param srcIndex the index of the first element to copy in the source.
     * @param dst the destination IndexedMemoryChunk.
     * @param dstIndex the index of the first element to store in the destination.
     * @param numElems the number of elements to copy.
     */
    @Native("java", "x10.core.IndexedMemoryChunk.<#2>asyncCopy(#4,#5,#6,#7,#8)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::asyncCopy<#1 >(#4,#5,#6,#7,#8)")
    public static native def asyncCopy[T](src:RemoteIndexedMemoryChunk[T], srcIndex:int, 
                                          dst:IndexedMemoryChunk[T], dstIndex:int, 
                                          numElems:int):void;

    @Native("java", "x10.core.IndexedMemoryChunk.<#2>asyncCopy(#4,#5,#6,#7,#8,#9)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::asyncCopy<#1 >(#4,#5,#6,#7,#8,#9)")
    public static native def uncountedCopy[T](src:RemoteIndexedMemoryChunk[T], srcIndex:int, 
                                              dst:IndexedMemoryChunk[T], dstIndex:int, 
                                              numElems:int,
                                              notifier:()=>void):void;

    /**
     * Synchronously copy a contiguous portion of the src IndexedMemoryChunk 
     * into the destination IndexedMemoryChunk. Both src and dst are local.
     * If the source place is the current place, then the copy happens synchronously.
     * If the source place is not the same as the current place, then
     * the copy happens asynchronously and the created remote activity will be 
     * registered with the dynamically enclosing finish of the activity that invoked 
     * asyncCopyFrom.<p>
     *
     * @param src the source IndexedMemoryChunk.
     * @param srcIndex the index of the first element to copy in the source.
     * @param dst the destination IndexedMemoryChunk.
     * @param dstIndex the index of the first element to store in the destination.
     * @param numElems the number of elements to copy.
     */
    @Native("java", "x10.core.IndexedMemoryChunk.<#2>copy(#4,#5,#6,#7,#8)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::copy<#1 >(#4,#5,#6,#7,#8)")
    public static native def copy[T](src:IndexedMemoryChunk[T], srcIndex:int, 
                                     dst:IndexedMemoryChunk[T], dstIndex:int, 
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

    @Native("java", "(#0).getCongruentSibling(#1)")
    @Native("c++", "(#0)->getCongruentSibling(#1)")
    public native def getCongruentSibling(Place):RemoteIndexedMemoryChunk[T];
}

// vim:shiftwidth=4:tabstop=4:expandtab
