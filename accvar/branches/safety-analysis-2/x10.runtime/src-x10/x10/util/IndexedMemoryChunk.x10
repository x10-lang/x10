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
 * For C++ we are mapping RawChunk to an optimized hand-written class that provides
 * the minimal required functionality.
 * 
 * For Java, we are currently mapping to Rail. I think the plan here should be to 
 * continue mapping to Rail.  Once Rail disappears as an X10-level source consturct,
 * we can then simplify the Java implementation of Rail accordingly.
 * Alternatively, we could go ahead with the customized implementation of RawChunk<T>
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
public struct IndexedMemoryChunk[T] {
    private val chunk:RawChunk[T];

    public def this(numElements:int) {
	this(numElements, 8, false, false);
    }

    public def this(numElements:long) {
	this(numElements, 8, false, false);
    }

    public def this(numElements:int, zeroed:boolean) {
	this(numElements, 8, false, zeroed);
    }

    public def this(numElements:long, zeroed:boolean) {
	this(numElements, 8, false, zeroed);
    }

    public def this(numElements:int, alignment:int, pinned:boolean, zeroed:boolean) {
        chunk = RawChunk.allocate[T](numElements, alignment, pinned, zeroed);
    }

    public def this(numElements:long, alignment:int, pinned:boolean, zeroed:boolean) {
        chunk = RawChunk.allocate[T](numElements, alignment, pinned, zeroed);
    }


    /**
     * Operator that allows access of IndexedMemoryChunk elements by index.
     *
     * @param i The index to retreive.
     * @return The value at that index.
     */
    public safe @Header @Inline def apply(index:int) = chunk.apply(index);


    /**
     * Operator that allows access of IndexedMemoryChunk elements by index.
     *
     * @param i The index to retreive.
     * @return The value at that index.
     */
    public safe @Header @Inline def apply(index:long) = chunk.apply(index);

    /**
     * Operator that allows assignment of IndexedMemoryChunk elements by index.
     *
     * @param v The value to assign.
     * @param i The index of the element to be changed.
     * @return The new value.
     */
    public safe @Header @Inline def set(value:T, index:int) = chunk.set(value, index);


    /**
     * Operator that allows assignment of IndexedMemoryChunk elements by index.
     *
     * @param v The value to assign.
     * @param i The index of the element to be changed.
     * @return The new value.
     */
    public safe @Header @Inline def set(value:T, index:long) = chunk.set(value, index);


    /* TODO: Java codegen doesn't support static overloading on signed vs. unsigned.
    public safe @Header @Inline def apply(index:uint) = chunk.apply(index);
    public safe @Header @Inline def apply(index:ulong) = chunk.apply(index);
    public safe @Header @Inline def set(value:T, index:uint) = chunk.set(value, index);
    public safe @Header @Inline def set(value:T, index:ulong) = chunk.set(value, index);
    */


    @NativeRep("java", "x10.core.Rail<#1>", null, "new x10.rtt.ParameterizedType(x10.core.Rail._RTT, #2)")
    @NativeRep("c++", "x10::util::IndexedMemoryChunk__RawChunk<#1 >", "x10::util::IndexedMemoryChunk__RawChunk<#1 >", null)
    private static struct RawChunk[T] {
	@Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4)")
        @Native("c++", "x10::util::IndexedMemoryChunk__RawChunk<#1 >::allocate(#4, #5, #6, #7)")
        static native def allocate[T](numElements:int, alignment:int, pinned:boolean, zeroed:boolean):RawChunk[T];

	@Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, (int)(#4))")
        @Native("c++", "x10::util::IndexedMemoryChunk__RawChunk<#1 >::allocate(#4, #5, #6, #7)")
        static native def allocate[T](numElements:long, alignment:int, pinned:boolean, zeroed:boolean):RawChunk[T];

	@Native("java", "null")
	@Native("c++", "null")
	private native def this(); // unused.

        @Native("java", "(#0).apply$G(#1)")
        @Native("c++", "(#0)->apply(#1)")
        native safe def apply(index:int):T;

        @Native("java", "(#0).apply$G((int)(#1))")
        @Native("c++", "(#0)->apply(#1)")
        native safe def apply(index:long):T;

        @Native("java", "(#0).set$G(#1, #2)")
        @Native("c++", "(#0)->set(#1, #2)")
        native safe def set(value:T, index:int):void;

        @Native("java", "(#0).set$G(#1, (int)(#2))")
        @Native("c++", "(#0)->set(#1, #2)")
        native safe def set(value:T, index:long):void;

       /* TODO: Java codegen doesn't support static overloading on signed vs. unsigned.
        native safe def apply(index:uint):T;
        native safe def apply(index:ulong):T;
        native safe def set(value:T, index:uint):void;
        native safe def set(value:T, index:ulong):void;
	*/
    }
}
