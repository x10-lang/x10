/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * <p> The Rail class provides a basic abstraction of fixed-sized 
 * indexed storage, ie a dense zero-indexed array. 
 * A <code>Rail<T></code> contains <code>size</code> elements 
 * of type <code>T</code>.</p>
 * 
 * <p> Rails are intended both for direct use in programs,
 * where basic C or Java style one dimensional arrays are needed
 * and as fundamental building block from which more complex data
 * structures can be constructed.</p>
 * 
 * <p> Although the API of the Rail class is specified at the X10 level,
 * the implementation is provided via a combination of native classes defined
 * in the Managed X10 and Native X10 runtime libraries and by direct compiler
 * support for Rail literals and compiler intrinsic implementation of a subset
 * of the Rail API methods.</p>
 * 
 * <p> Although Rails are long-indexed, Managed X10 implements the Rail class 
 * using Java's arrays as a backing storage.  Therefore rails larger than 2^31
 * may not be created on Managed X10.</p>
 *
 * <p> The class x10.util.RailUtils provides static methods with additional
 * useful Rail functions such as sorting, binary search, map, and reduce.</p>
 *
 * @see x10.util.RailUtils
 */
@NativeRep("java", "x10.core.Rail<#T$box>", null, "x10.rtt.ParameterizedType.make(x10.core.Rail.$RTT, #T$rtt)")
@NativeRep("c++", "x10::lang::Rail< #T >*", "x10::lang::Rail< #T >", null)
public final class Rail[T](
    @Native("c++", "(x10_long)(::x10aux::nullCheck(#this)->FMGL(size))")
    @Native("cuda", "(#this).FMGL(size)")
    /** The number of elements in the Rail */
    size:Long
) implements Iterable[T],(Long)=>T {

    /** @return the LongRange 0..(size-1) */
    public native property def range():LongRange;

    /** @return an iterator over the elements of the Rail */
    public native def iterator():Iterator[T];

    /** @return a String suitable for printing representing the contents of the Rail */
    public native def toString():String;

    /**
     * Construct an empty (size 0) Rail
     */
    public native def this():Rail[T]{self.size==0};

    /**
     * Construct a new Rail by copying all elements of src.
     * @param src The source Rail to copy
     */
    public native def this(src:Rail[T]):Rail[T]{self.size==src.size};

    /**
     * Construct a zero-initialized Rail of size elements.
     * @param size the size of the Rail
     * @throws IllegalArgumentException if size exceeds Int.MAX_VALUE on ManagedX10
     */
    public native def this(size:Long){T haszero}:Rail[T]{self.size==size};

    /**
     * Construct a Rail of size elements initialized to init
     * @param size the size of the Rail
     * @param init the initial value for all elements
     * @throws IllegalArgumentException if size exceeds Int.MAX_VALUE
     */
    public native def this(size:Long, init:T):Rail[T]{self.size==size};

    /**
     * Construct a Rail of size elements initialized to the result of evaluating 
     * init for each index of the Rail
     * @param size the size of the Rail
     * @param init the function to use to compute the initial value of each element
     * @throws IllegalArgumentException if size exceeds Int.MAX_VALUE on ManagedX10
     */
    public native def this(size:Long, init:(Long)=>T):Rail[T]{self.size==size};


    /**
     * Construct a zero-initialized Rail of size elements using the
     * provided memory allocator to allocate the backing storage for the Rail.
     * @param size the size of the Rail
     * @param allocator the MemoryAllocator to use
     * @throws IllegalArgumentException if size exceeds Int.MAX_VALUE on ManagedX10
     */
    public native def this(size:Long, allocator:Runtime.MemoryAllocator){T haszero}:Rail[T]{self.size==size};


    /**
     * Return the element of this array corresponding to the given index.
     * 
     * @param i the given index in the first dimension
     * @return the element of this array corresponding to the given index.
     * @see #set(T, Int)
     */
    @Native("cuda", "(#this).raw[#index]")
    public native operator this(index:Long):T;

    /**
     * Set the element of this array corresponding to the given index to the given value.
     * Return the new value of the element.
     * 
     * @param v the given value
     * @param i the given index in the first dimension
     * @return the new value of the element of this array corresponding to the given index.
     * @see #operator(Int)
     */
    @Native("cuda", "(#this).raw[#index] = (#v)")
    public native operator this(index:Long)=(v:T):T{self==v};

    /**
     * Copy all of the values from the source Rail to the destination Rail.
     * The two arrays must be of equal size.
     *
     * @param src the source Rail.
     * @param dst the destination Rail.
     */
    public static native def copy[T](src:Rail[T], dst:Rail[T]){src.size==dst.size}:void;

    /**
     * Copy numElems values starting from srcIndex from the source Rail
     * to the destination Rail starting at dstIndex.
     *
     * @param src the source Rail.
     * @param srcIndex the index of the first element in this Rail
     *        to be copied.  
     * @param dst the destination Rail.
     * @param dstIndex the index of the first element in the destination
     *        Rail where copied data elements will be stored.
     * @param numElems the number of elements to be copied.
     */
    public static native def copy[T](src:Rail[T], srcIndex:Long, 
                                     dst:Rail[T], dstIndex:Long, numElems:Long):void;

    /**
     * Asynchronously copy the specified values from the source Rail to the 
     * specified portion of the Rail referenced by the destination GlobalRail.
     * The activity created to do the copying will be registered with the
     * dynamically enclosing finish.<p>
     * 
     * Warning: This method is only intended to be used on Rails containing
     *   non-Object data elements.  The elements are actually copied via an
     *   optimized DMA operation if available.  Therefore object-references will
     *   not be properly transferred. However, (arbitrarily nested) structs 
     *   containing only non-Object data elements are supported by this method.
     *   Ideally, future versions of the X10 type system would enable this 
     *   restriction to be checked statically.</p>
     * 
     * @param src the source rail.
     * @param srcIndex the index of the first element in src to be copied.  
     * @param dst a GlobalRail to the destination rail.  May actually be local or remote
     * @param dstIndex the index of the first element in the destination
     *        rail where copied data elements will be stored.
     * @param numElems the number of elements to be copied.
     */
    public static native def asyncCopy[T](src:Rail[T], srcIndex:Long, 
            dst:GlobalRail[T], dstIndex:Long, numElems:Long):void;

    /**
     * Asynchronously copy the specified values from the referenced source Rail to the 
     * specified portion of the destination Rail referenced.
     * The activity created to do the copying will be registered with the
     * dynamically enclosing finish.<p>
     * 
     * Warning: This method is only intended to be used on Rails containing
     *   non-Object data elements.  The elements are actually copied via an
     *   optimized DMA operation if available.  Therefore object-references will
     *   not be properly transferred. However, (arbitrarily nested) structs 
     *   containing only non-Object data elements are supported by this method.
     *   Ideally, future versions of the X10 type system would enable this 
     *   restriction to be checked statically.</p>
     * 
     * @param src a GlobalRail to the source rail. May actually be local or remote.
     * @param srcIndex the index of the first element in src to be copied.  
     * @param dst the destination rail.  
     * @param dstIndex the index of the first element in the destination
     *        rail where copied data elements will be stored.
     * @param numElems the number of elements to be copied.
     */
    public static native def asyncCopy[T](src:GlobalRail[T], srcIndex:Long, 
            dst:Rail[T], dstIndex:Long, numElems:Long):void;

    /**
     * Asynchronously copy the specified values from the source Rail to the 
     * specified portion of the Rail referenced by the destination GlobalRail.
     * The activity created to do the copying will be uncounted, ie not be registered 
     * with any finish.<p>
     * 
     * Warning: This method is only intended to be used on Rails containing
     *   non-Object data elements.  The elements are actually copied via an
     *   optimized DMA operation if available.  Therefore object-references will
     *   not be properly transferred. However, (arbitrarily nested) structs 
     *   containing only non-Object data elements are supported by this method.
     *   Ideally, future versions of the X10 type system would enable this 
     *   restriction to be checked statically.</p>
     *
     * @param src the source rail.
     * @param srcIndex the index of the first element in src to be copied.  
     * @param dst a GlobalRail to the destination rail.  May actually be local or remote
     * @param dstIndex the index of the first element in the destination
     *        rail where copied data elements will be stored.
     * @param numElems the number of elements to be copied.
     * @param notifier the function to call when the copy is complete
     */
    public static native def uncountedCopy[T](src:Rail[T], srcIndex:Long, 
            dst:GlobalRail[T], dstIndex:Long, numElems:Long, 
            notifier:()=>void):void;

    /**
     * Asynchronously copy the specified values from the referenced source Rail to the 
     * specified portion of the destination Rail referenced.
     * The activity created to do the copying will be uncounted, ie not be registered 
     * with any finish.<p>
     * 
     * Warning: This method is only intended to be used on Rails containing
     *   non-Object data elements.  The elements are actually copied via an
     *   optimized DMA operation if available.  Therefore object-references will
     *   not be properly transferred. However, (arbitrarily nested) structs 
     *   containing only non-Object data elements are supported by this method.
     *   Ideally, future versions of the X10 type system would enable this 
     *   restriction to be checked statically.</p>
     * 
     * @param src a GlobalRail to the source rail. May actually be local or remote.
     * @param srcIndex the index of the first element in src to be copied.  
     * @param dst the destination rail.  
     * @param dstIndex the index of the first element in the destination
     *        rail where copied data elements will be stored.
     * @param numElems the number of elements to be copied.
     * @param notifier the function to call when the copy is complete
     */
    public static native def uncountedCopy[T](src:GlobalRail[T], srcIndex:Long, 
            dst:Rail[T], dstIndex:Long, numElems:Long, 
            notifier:()=>void):void;


    /**
     * Fill all elements of the array to contain the argument value.
     * 
     * @param v the value with which to fill the array
     */
    public native def fill(v:T):void;


    /**
     * Clears the entire Rail by zeroing the storage.
     */
    public native def clear(){T haszero}:void;


    /**
     * Clears numElems of the backing storage begining at start by zeroing the storage.
     * @param start the first index to clear
     * @param numElems the number of elements to clear
     */
    public native def clear(start:Long, numElems:Long){T haszero}:void;
}
