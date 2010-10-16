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
import x10.util.Pair;

/** A 1-dimensional 0-based sequence of immutable elements, each of the same type,
 * supporting constant-time access by integer index.  See x10.lang.Rail for a comparison with
 * arrays in X10 and other languages.
 */
@NativeRep("java", "x10.core.ValRail<#1>", null, "new x10.rtt.ParameterizedType(x10.core.ValRail._RTT, #2)")
@NativeRep("c++", "x10aux::ref<x10::lang::ValRail<#1 > >", "x10::lang::ValRail<#1 >", null)
public final class ValRail[+T](length: Int) implements (Int) => T, Iterable[T] {

    // need to declare a constructor to shut up the initialization checker
    private native def this(n: Int): ValRail[T]{self.length==n};
    
    /**
     * Create a ValRail and initialize it by evaluating the given closure at each index.
     *
     * @param length The number of elements.
     * @param init Evaluated once per element to initialize the ValRail.
     * @return The reference to the new ValRail.
     */
    @Native("java", "x10.core.RailFactory.<#2>makeValRail(#3, #4, #5)")
    @Native("c++", "x10::lang::ValRail<#1 >::make(#4, #5)")
    public native static def make[T](length: Int, init: (Int) => T): ValRail[T](length);
    
    /**
     * Create an appropriately aligned ValRail and initialize it by evaluating the given closure at each index.
     *
     * @param length The number of elements.
     * @param init Evaluated once per element to initialize the ValRail.
     * @param alignment The 0th element will be located at an address that is an integer multiple of this param (must be power of 2).
     * @return The reference to the new ValRail.
     */
    @Native("java", "x10.core.RailFactory.<#2>makeValRail(#3, #4, #5)")
    @Native("c++", "x10::lang::ValRail<#1 >::makeAligned(#4, #5, #6)")
    public native static def makeAligned[T](length: Int, init: (Int) => T, alignment: Int): ValRail[T](length);

    /**
     * Create a ValRail from a Rail.
     *
     * @param init The length and elements will be copied from this Rail.
     */
    @Native("java", "x10.core.RailFactory.<#2>makeValRailFromRail(#3, #4)")
    @Native("c++", "x10::lang::ValRail<#1 >::make(#4)")
    public native static def make[U](r: Rail[U]): ValRail[U]{self.length==r.length};

    /**
     * Operator that allows access of ValRail elements by index.
     *
     * @param i The index to retreive.
     * @return The value at that index.
     */
    @Native("java", "(#0).apply$G(#1)")
    @Native("c++", "(#0)->apply(#1)")
    @Native("cuda", "(#0)[#1]")
    public native def apply(i: Int): T;
    
    /**
     * Get an iterator over this ValRail.
     *
     * @return A new iterator instance.
     */
    @Native("java", "(#0).iterator()")
    @Native("c++", "(#0)->iterator()")
    public native def iterator(): Iterator[T];

    /**
     * Equals on ValRail is defined by (1) equal length and
     * (2) pairwise comparision by equals of the elements of the
     * two ValRails.
     *
     * @param other the ValRail[T] to compare this to.
     * @return <code>true</code> if <code>this</code> is equal
     * to <code>other</code> and <code>false</code> otherwise.
     */
    @Native("java", "(#0).equals(#1)")
    @Native("c++", "(#0)->equals(#1)")
    public native def equals(other:Any):boolean;

    private static class RailIterator[S] implements Iterator[S] {
        private var curIndex:int = 0;
        private val rail:ValRail[S];
	
        private def this(r:ValRail[S]) { rail = r; }
        public def hasNext() = curIndex < rail.length;
        public def next() = rail(curIndex++);
    }


}
