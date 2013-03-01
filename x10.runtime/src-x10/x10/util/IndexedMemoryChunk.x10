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
 * abstractions (such as Rail) to be implemented efficiently
 * and to allow low-level programming of memory regions at the
 * X10 level when absolutely required for performance. Most of the API
 * of this class is safe, but there are some loopholes that can be 
 * used when absolutely necessary for performance..<p>
 */
@NativeRep("java", "x10.core.IndexedMemoryChunk<#T$box>", null, "x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, #T$rtt)")
@NativeRep("c++", "x10::util::IndexedMemoryChunk<#T >", "x10::util::IndexedMemoryChunk<#T >", null)
public struct IndexedMemoryChunk[T] {

    @Native("java", "false")
    @Native("c++", "x10aux::congruent_huge")
    public static native def hugePages():Boolean;

    @Native("java", "null")
    @Native("c++", "null")
    private native def this(); // unused; prevent instantiaton outside of native code

    @Native("java", "x10.core.IndexedMemoryChunk.<#T$box>allocate(#T$rtt, #numElements, false)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#T >(#numElements, 8, false, false)")
    public static native def allocateUninitialized[T](numElements:int):IndexedMemoryChunk[T];

    @Native("java", "x10.core.IndexedMemoryChunk.<#T$box>allocate(#T$rtt, #numElements, true)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#T >(#numElements, 8, false, true)")
    public static native def allocateZeroed[T](numElements:int):IndexedMemoryChunk[T]{T haszero};

    @Native("java", "x10.core.IndexedMemoryChunk.<#T$box>allocate(#T$rtt, #numElements, false)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#T >(#numElements, #alignment, #congruent, false)")
    public static native def allocateUninitialized[T](numElements:int, alignment:int, congruent:boolean):IndexedMemoryChunk[T];

    @Native("java", "x10.core.IndexedMemoryChunk.<#T$box>allocate(#T$rtt, #numElements, true)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#T >(#numElements, #alignment, #congruent, true)")
    public static native def allocateZeroed[T](numElements:int, alignment:int, congruent:boolean):IndexedMemoryChunk[T]{T haszero};

    @Native("java", "x10.core.IndexedMemoryChunk.<#T$box>allocate(#T$rtt, #numElements, false)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#T >(#numElements, 8, false, false)")
    public static native def allocateUninitialized[T](numElements:long):IndexedMemoryChunk[T];

    @Native("java", "x10.core.IndexedMemoryChunk.<#T$box>allocate(#T$rtt, #numElements, true)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#T >(#numElements, 8, false, true)")
    public static native def allocateZeroed[T](numElements:long):IndexedMemoryChunk[T]{T haszero};

    @Native("java", "x10.core.IndexedMemoryChunk.<#T$box>allocate(#T$rtt, #numElements, false)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#T >(#numElements, #alignment, #congruent, false)")
    public static native def allocateUninitialized[T](numElements:long, alignment:int, congruent:boolean):IndexedMemoryChunk[T];

    @Native("java", "x10.core.IndexedMemoryChunk.<#T$box>allocate(#T$rtt, #numElements, true)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::allocate<#T >(#numElements, #alignment, #congruent, true)")
    public static native def allocateZeroed[T](numElements:long, alignment:int, congruent:boolean):IndexedMemoryChunk[T]{T haszero};

    /**
     * Deallocate the backing storage for the IndexedMemoryChunk and
     * set its length to 0.  This is an unsafe operation, as other
     * IndexedMemoryChunks might have been created by copying this
     * IndexedMemoryChunk and will contain dangling pointers to the 
     * freed memory.  This operation should only be called when 
     * the caller is certain that no such copies of the IMC exist.
     */
    @Native("java", "(#this).deallocate()")
    @Native("c++", "(#this)->deallocate()")
    public native def deallocate():void;

    /**
     * Operator that allows access of IndexedMemoryChunk elements by index.
     *
     * @param i The index to retreive.
     * @return The value at that index.
     */
    @Native("java", "(#this).$apply$G(#index)")
    @Native("c++", "(#this)->__apply(#index)")
    public native operator this(index:int):T;


    /**
     * Operator that allows access of IndexedMemoryChunk elements by index.
     *
     * @param i The index to retreive.
     * @return The value at that index.
     */
    @Native("java", "(#this).$apply$G((int)(#index))")
    @Native("c++", "(#this)->__apply(#index)")
    public native operator this(index:long):T;


    /**
     * Operator that allows assignment of IndexedMemoryChunk elements by index.
     *
     * @param v The value to assign.
     * @param i The index of the element to be changed.
     * @return The new value.
     */
    @Native("java", "(#this).$set(#index, #value)")
    @Native("c++", "(#this)->__set(#index, #value)")
    public native operator this(index:int)=(value:T):void;


    /**
     * Operator that allows assignment of IndexedMemoryChunk elements by index.
     *
     * @param v The value to assign.
     * @param i The index of the element to be changed.
     * @return The new value.
     */
    @Native("java", "(#this).$set((int)(#index), #value)")
    @Native("c++", "(#this)->__set(#index, #value)")
    public native operator this(index:long)=(value:T):void;


    /**
     * Clears numElems of the backing storage starting at index start
     * by zeroing the storage.  Note that this is intentionally not
     * type safe because it does require T hasZero.
     */
    @Native("java", "(#this).clear(#index, #numElems)")
    @Native("c++", "(#this)->clear(#index, #numElems)")
    public native def clear(index:int, numElems:int):void;


    /**
     * Clears numElems of the backing storage starting at index start
     * by zeroing the storage.  Note that this is intentionally not
     * type safe because it does require T hasZero.
     */
    @Native("java", "(#this).clear((int)(#index), (int)(#numElems))")
    @Native("c++", "(#this)->clear(#index, #numElems)")
    public native def clear(index:long, numElems:long):void;


    /**
     * Operator that allows UNSAFE access of IndexedMemoryChunk elements by index.
     *
     * @param i The index to retreive.
     * @return The value at that index.
     */
    @Native("java", "(#this).$apply$G(#index)")
    @Native("c++", "(#this)->apply_unsafe(#index)")
    public native def apply_unsafe(index:int):T;


    /**
     * Operator that allows UNSAFE access of IndexedMemoryChunk elements by index.
     *
     * @param i The index to retreive.
     * @return The value at that index.
     */
    @Native("java", "(#this).$apply$G((int)(#index))")
    @Native("c++", "(#this)->apply_unsafe(#index)")
    public native def apply_unsafe(index:long):T;


    /**
     * Operator that allows UNSAFE assignment of IndexedMemoryChunk elements by index.
     *
     * @param v The value to assign.
     * @param i The index of the element to be changed.
     * @return The new value.
     */
    @Native("java", "(#this).set_unsafe(#value, #index)")
    @Native("c++", "(#this)->set_unsafe(#value, #index)")
    public native def set_unsafe(value:T, index:int):void;


    /**
     * Operator that allows UNSAFE assignment of IndexedMemoryChunk elements by index.
     *
     * @param v The value to assign.
     * @param i The index of the element to be changed.
     * @return The new value.
     */
    @Native("java", "(#this).set_unsafe(#value, (int)(#index))")
    @Native("c++", "(#this)->set_unsafe(#value, #index)")
    public native def set_unsafe(value:T, index:long):void;


    /**
     * Return the size of the IndexedMemoryChunk (in elements)
     *
     * @return the size of the IndexedMemoryChunk (in elements)
     */
    @Native("java", "((#this).length)")
    @Native("c++", "(#this)->length()")
    public native def length():int; /* TODO: We need to convert this to returning a long */


    /**
     * Copies a contiguous portion of a local IndexedMemoryChunk 
     * to a destination RemoteIndexedMemoryChunk at the specified place.
     * If the destination place is the current place, then the copy happens synchronously.
     * If the destination place is not the same as the current place, then
     * the copy happens asynchronously and the created remote activity will be 
     * registered with the dynamically enclosing finish of the activity that invoked 
     * asyncCopy.</p>
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
    @Native("java", "x10.core.IndexedMemoryChunk.<#T$box>asyncCopy(#src,#srcIndex,#dst,#dstIndex,#numElems)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::asyncCopy<#T >(#src,#srcIndex,#dst,#dstIndex,#numElems)")
    public static native def asyncCopy[T](src:IndexedMemoryChunk[T], srcIndex:int, 
                                          dst:RemoteIndexedMemoryChunk[T], dstIndex:int, 
                                          numElems:int):void;

    @Native("java", "x10.core.IndexedMemoryChunk.<#T$box>asyncCopy(#src,#srcIndex,#dst,#dstIndex,#numElems,#notifier)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::asyncCopy<#T >(#src,#srcIndex,#dst,#dstIndex,#numElems,#notifier)")
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
     * asyncCopy.<p>
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
    @Native("java", "x10.core.IndexedMemoryChunk.<#T$box>asyncCopy(#src,#srcIndex,#dst,#dstIndex,#numElems)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::asyncCopy<#T >(#src,#srcIndex,#dst,#dstIndex,#numElems)")
    public static native def asyncCopy[T](src:RemoteIndexedMemoryChunk[T], srcIndex:int, 
                                          dst:IndexedMemoryChunk[T], dstIndex:int, 
                                          numElems:int):void;

    @Native("java", "x10.core.IndexedMemoryChunk.<#T$box>asyncCopy(#src,#srcIndex,#dst,#dstIndex,#numElems,#notifier)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::asyncCopy<#T >(#src,#srcIndex,#dst,#dstIndex,#numElems,#notifier)")
    public static native def uncountedCopy[T](src:RemoteIndexedMemoryChunk[T], srcIndex:int, 
                                              dst:IndexedMemoryChunk[T], dstIndex:int, 
                                              numElems:int,
                                              notifier:()=>void):void;

    /**
     * Synchronously copy a contiguous portion of the src IndexedMemoryChunk 
     * into the destination IndexedMemoryChunk. Both src and dst are local.
     *
     * @param src the source IndexedMemoryChunk.
     * @param srcIndex the index of the first element to copy in the source.
     * @param dst the destination IndexedMemoryChunk.
     * @param dstIndex the index of the first element to store in the destination.
     * @param numElems the number of elements to copy.
     */
    @Native("java", "x10.core.IndexedMemoryChunk.<#T$box>copy(#src,#srcIndex,#dst,#dstIndex,#numElems)")
    @Native("c++", "x10::util::IndexedMemoryChunk<void>::copy<#T >(#src,#srcIndex,#dst,#dstIndex,#numElems)")
    public static native def copy[T](src:IndexedMemoryChunk[T], srcIndex:int, 
                                     dst:IndexedMemoryChunk[T], dstIndex:int, 
                                     numElems:int):void;


   /*
    * @Native methods from Any because the handwritten C++ code doesn't 100% match 
    * what the compiler would have generated.
    */

    @Native("java", "(#this).toString()")
    @Native("c++", "(#this)->toString()")
    public native def  toString():String;

    @Native("java", "(#this).equals(#that)")
    @Native("c++", "(#this)->equals(#that)")
    public native def equals(that:Any):Boolean;

    @Native("java", "(#this).hashCode()")
    @Native("c++", "(#this)->hashCode()")
    public native def  hashCode():Int;

    @Native("java", "(#this).getCongruentSibling(#p)")
    @Native("c++", "(#this)->getCongruentSibling(#p)")
    public native def getCongruentSibling(p:Place):RemoteIndexedMemoryChunk[T];
}

// vim:shiftwidth=4:tabstop=4:expandtab
