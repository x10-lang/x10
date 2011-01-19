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

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.util.IndexedMemoryChunk;
import x10.util.Pair;

/** A 1-dimensional 0-based sequence of elements, each of the same type,
 * supporting constant-time access by integer index.  This is analogous to
 * arrays in other languages, but not the same as an X10 array.  X10 arrays are
 * more powerful, supporting distribution and regions.  Rail is meant to be an
 * internal interface that maps down to the C++ or Java notion of array.  In
 * future Rail may be deprecated in favor of single-place, zero-based linear
 * specialization of the array library.
 */
@NativeRep("java", "x10.core.Rail<#1>", null, "new x10.rtt.ParameterizedType(x10.core.Rail._RTT, #2)")
@NativeRep("c++", "x10aux::ref<x10::lang::Rail<#1 > >", "x10::lang::Rail<#1 >", null)
public final class Rail[T](length: Int)
    implements Settable[Int,T], Iterable[T]
{
    // need to declare a constructor to shut up the initialization checker
    private native def this(n: Int): Rail[T]{self.length==n};

    /**
     * Create a Rail and initialize it by evaluating the given closure at each index.
     *
     * @param length The number of elements.
     * @param init Evaluated once per element to initialize the Rail.
     * @return The reference to the new Rail.
     */
    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4, #5)")
    @Native("c++", "x10::lang::Rail<void>::make<#1 >(#4, #5)")
    public native static def make[S](length: Int, init: (Int) => S): Rail[S]{self.length==length};

    /**
     * Create a Rail and initialize it by evaluating the given closure at each index;
     * the backing storage for the Rail will be allocated in pinned memory.
     *
     * @param length The number of elements.
     * @param init Evaluated once per element to initialize the Rail.
     * @return The reference to the new Rail.
     */
    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4, #5)")
    @Native("c++", "x10::lang::Rail<void>::makePinned<#1 >(#4, #5)")
    public native static def makePinned[S](length: Int, init: (Int) => S): Rail[S]{self.length==length};


    /**
     * Create an appropriately aligned Rail and initialize it by evaluating the given closure at each index.
     *
     * @param length The number of elements.
     * @param init Evaluated once per element to initialize the Rail.
     * @param alignment The 0th element will be located at an address that is an integer multiple of this param (must be power of 2).
     * @return The reference to the new Rail.
     */
    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4, #5)")
    @Native("c++", "x10::lang::Rail<void>::makeAligned<#1 >(#4, #5, #6)")
    public native static def makeAligned[S](length: Int, init: (Int) => S, alignment: Int): Rail[S]{self.length==length};

    /**
     * Create a Rail and initialize it by copying elements from another Rail.
     *
     * @param length The number of Rail elements.
     * @param off Start copying elements from this offset of the given Rail.
     * @param init The Rail to initialize from.
     * @return The reference to the new Rail.
     */
    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4, #5, #6)")
    @Native("c++", "x10::lang::Rail<void>::make<#1 >(#4, #5, #6)")
    public native static def make[S](length: Int, off:Int, init:Rail[S]): Rail[S]{self.length==length};


    /**
     * Create an appropriately aligned Rail and initialize it by copying elements from
     * another Rail.  Note that there is no connection between the alignment of the two rails involved.
     *
     * @param length The number of Rail elements.
     * @param off Start copying elements from this offset of the given Rail.
     * @param init The Rail to initialize from.
     * @param alignment The 0th element will be located at an address that is an integer multiple of this param (must be power of 2).
     * @return The reference to the new Rail.
     */

    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4, #5, #6)")
    @Native("c++", "x10::lang::Rail<void>::makeAligned<#1 >(#4, #5, #6, #7)")
    public native static def makeAligned[S](length: Int, off:Int, init:Rail[S], alignment:Int): Rail[S]{self.length==length};

    /**
     * Creates an Rail whose contents are zero-initialized; in future releases
     * of X10, this method will only be callable of sizeof(T) bytes of zeros
     * is a valid value of type T.
     *
     * @param length The number of Rail elements.
     * @return The reference to the new Rail.
     */
    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4)")
    @Native("c++", "x10::lang::Rail<void>::make<#1 >(#4)")
    public native static def make[S](length: Int): Rail[S]{self.length==length,S haszero};

    /**
     * Creates an appropriately aligned Rail whose contents are zero-initialized;
     * in future releases of X10, this method will only be callable of sizeof(T)
     * bytes of zeros is a valid value of type T.
     *
     * @param length The number of Rail elements.
     * @param alignment The 0th element will be located at an address that is an integer multiple of this param (must be power of 2).
     * @return The reference to the new Rail.
     */
    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4)")
    @Native("c++", "x10::lang::Rail<void>::makeAligned<#1 >(#4, #5)")
    public native static def makeAligned[S](length: Int, alignment:Int): Rail[S]{self.length==length,S haszero};

    /**
     * Creates an Rail whose contents are initialized to init.
     *
     * @param length The number of Rail elements.
     * @param init The value with which to initialize all values of the Rail.
     * @return The reference to the new Rail.
     */
    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4, #5)")
    @Native("c++", "x10::lang::Rail<void>::make<#1 >(#4, #5)")
    public native static def make[S](length: Int, init:S): Rail[S]{self.length==length};

    /**
     * Creates an appropriately aligned Rail whose contents are initialized to init.
     *
     * @param length The number of Rail elements.
     * @param alignment The 0th element will be located at an address that is an integer multiple of this param (must be power of 2).
     * @param init The value with which to initialize all values of the Rail.
     * @return The reference to the new Rail.
     */
    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4, #5)")
    @Native("c++", "x10::lang::Rail<void>::makeAligned<#1 >(#4, #5, #6)")
    public native static def makeAligned[S](length: Int, init:S, alignment:Int): Rail[S]{self.length==length};

    /**
     * Re-initializes a Rail.
     *
     * @param init Evaluated once per element to reinitialize the Rail.
     */
    @Native("java", "(#0).reset(#1)")
    @Native("c++", "(#0)->reset(#1)")
    public native def reset(init: (Int) => T): void;

    /**
     * Re-initializes a Rail to a constant value.
     *
     * @param init Every element will be set to this value
     */
    @Native("java", "(#0).reset(#1)")
    @Native("c++", "(#0)->reset(#1)")
    public native def reset(init: T): void;

    /**
     * Operator that allows access of Rail elements by index.
     *
     * @param i The index to retreive.
     * @return The value at that index.
     */
    @Native("java", "(#0).$apply$G(#1)")
    @Native("c++", "(#0)->__apply(#1)")
    @Native("cuda", "(#0)[#1]")
    public native operator this(i: Int): T;

    /**
     * Operator that allows assignment of Rail elements by index.
     *
     * @param v The value to assign.
     * @param i The index of the element to be changed.
     * @return The new value.
     */
    @Native("java", "(#0).$set$G(#1, #2)")
    @Native("c++", "(#0)->__set(#1, #2)")
    @Native("cuda", "(#0)[#2] = #1") // FIXME: evaluation order
    public native operator this(i: Int)=(v: T): T;

    /**
     * Get an iterator over this Rail.
     *
     * @return A new iterator instance.
     */
    @Native("java", "(#0).iterator()")
    @Native("c++", "(#0)->iterator()")
    public native def iterator(): Iterator[T];

    private static class RailIterator[S] implements Iterator[S] {
        private var curIndex:int = 0;
        private val rail:Rail[S];

        private def this(r:Rail[S]) { rail = r; }
        public def hasNext() = curIndex < rail.length;
        public def next() = rail(curIndex++);
    }

    /**
     * Return an IndexedMemoryChunk[T] that is wrapping the backing storage for the rail.
     * This method is primarily intended to be used to interface with native libraries 
     * (eg BLAS, ESSL). <p>
     *
     * @return an IndexedMemoryChunk[T] that is wrapping the backing storage for the Rail object.
     */
    @Native("java", "(#0).raw()")
    @Native("c++", "(#0)->indexedMemoryChunk()")
    public native def raw(): IndexedMemoryChunk[T];
}
