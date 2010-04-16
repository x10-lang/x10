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
@NativeRep("java", "x10.core.ValRail<#1>", "x10.core.ValRail.BoxedValRail", "x10.core.ValRail._RTT")
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
     * Cast operator that creates a new ValRail from a Rail.
     *
     * @param init The length and elements will be copied from this Rail.
     */
    @Native("java", "x10.core.RailFactory.<#2>makeValRailFromRail(#3, #4)")
    @Native("c++", "x10::lang::ValRail<#1 >::make(#4)")
    public native static operator[U](r: Rail[U]): ValRail[U]{self.length==r.length};

    /**
     * Operator that allows access of ValRail elements by index.
     *
     * @param i The index to retreive.
     * @return The value at that index.
     */
    @Native("java", "(#0).apply(#1)")
    @Native("c++", "(#0)->apply(#1)")
    @Native("cuda", "(#0)[#1]")
    public global native safe def apply(i: Int): T;
    
    /**
     * Get an iterator over this ValRail.
     *
     * @return A new iterator instance.
     */
    @Native("java", "(#0).iterator()")
    @Native("c++", "(#0)->iterator()")
    public global native def iterator(): Iterator[T];

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
    public global safe native def equals(other:Any):boolean;


    /**
     * Copies a portion of a given ValRail into a given remote Rail.
     * Upon completion, notifies the enclosing finish.
     *
     * @param src the source ValRail.
     * @param src_off the offset of the first element to copy in the source.
     * @param dst the destination Rail.
     * @param dst_off the offset of the first element store in the destination.
     * @param len the number of elements to copy.
     */
    @Native("java", "x10.lang.ValRail__NativeRep.copyTo(#0,#1,#2,#3,#4)")
    @Native("c++", "x10::lang::ValRail__NativeRep::copyTo(#0,#1,#2,#3,#4)")
    public global native def copyTo (src_off:Int, dst:Rail[T], dst_off:Int, len:Int) : Void;

    /**
     * Copies a portion of a given ValRail into a remote Rail indicated by the given closure.
     * Upon completion, notifies the enclosing finish.
     *
     * @param src the source ValRail.
     * @param src_off the offset of the first element to copy in the source.
     * @param dst_place the location of the destination Rail.
     * @param dst_finder a function returning a {@link x10.util.Pair} that consists
     *                   of the reference to the destination Rail and the offset of
     *                   the first element to copy to in the destination.
     * @param len the number of elements to copy.
     */
    @Native("java", "x10.lang.ValRail__NativeRep.copyTo(#0,#1,#2,#3,#4)")
    @Native("c++", "x10::lang::ValRail__NativeRep::copyTo(#0,#1,#2,#3,#4)")
    public global native def copyTo (src_off:Int,
                                     dst_place:Place, dst_finder:()=>Pair[Rail[T],Int],
                                     len:Int) : Void;

    private static class RailIterator[S] implements Iterator[S] {
        private var curIndex:int = 0;
        private val rail:ValRail[S]!;
	
        private def this(r:ValRail[S]!) { rail = r; }
        public def hasNext() = curIndex < rail.length;
        public def next() = rail(curIndex++);
    }


}
