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
import x10.compiler.ClockedVar;

/** A 1-dimensional 0-based sequence of elements, each of the same type,
 * supporting constant-time access by integer index.  This is analogous to
 * arrays in other languages, but not the same as an X10 array.  X10 arrays are
 * more powerful, supporting distribution and regions.  Rail is meant to be an
 * internal interface that maps down to the C++ or Java notion of array.  In
 * future Rail may be deprecated in favor of single-place, zero-based linear
 * specialization of the array library.
 */
@NativeRep("java", "x10.core.Rail<#1>", "x10.core.Rail.BoxedRail", "new x10.core.Rail.RTT(#2)")
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
    @Native("c++", "x10::lang::Rail<#1 >::make(#4, #5)")
    public native static safe def make[S](length: Int, init: (Int) => S): Rail[S]!{self.length==length};

    /**
     * Create a Rail and initialize it by copying elements from another Rail.
     *
     * @param length The number of Rail elements.
     * @param off Start copying elements from this offset of the given Rail.
     * @param init The Rail to initialize from.
     * @return The reference to the new Rail.
     */
    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4, #5, #6)")
    @Native("c++", "x10::lang::Rail<#1 >::make(#4, #5, #6)")
    public native static safe def make[S](length: Int, off:Int, init:Rail[S]): Rail[S]!{self.length==length};

    /**
     * Creates an unitiialized Rail, use with caution!
     *
     * @param length The number of Rail elements.
     * @return The reference to the new Rail.
     */
    @Native("java", "x10.core.RailFactory.<#2>makeVarRail(#3, #4)")
    @Native("c++", "x10::lang::Rail<#1 >::make(#4)")
    public native static safe def make[S](length: Int): Rail[S]!{self.length==length};

	 @Native("java", "x10.lang.Rail__NativeRep.<#2>makeClockedRail(#3, #4)")
    public native static safe def makeClockedRail[S](length: Int): Rail[ClockedVar[S]]!{self.length==length};
    
     @Native("java", "x10.lang.Rail__NativeRep.<#2>setClocked(#3, #0, #4, #5)")
     public native static safe def setClocked[T](index: Int, value: T): void;
     
      @Native("java", "x10.lang.Rail__NativeRep.<#2>getClocked(#3, #0, #4)")
     public native static safe def getClocked[T](index: Int): T;
 

    /**
     * Re-initializes a Rail.
     *
     * @param init Evaluated once per element to reinitialize the Rail.
     */
    @Native("java", "#0.reset(#1)")
    @Native("c++", "(#0)->reset(#1)")
    public native safe def reset(init: (Int) => T): Void;

    /**
     * Re-initializes a Rail to a constant value.
     *
     * @param init Every element will be set to this value
     */
    @Native("java", "#0.reset(#1)")
    @Native("c++", "(#0)->reset(#1)")
    public native safe def reset(init: T): Void;

    /**
     * Cast operator that creates a new Rail from a ValRail.
     *
     * @param init The length and elements will be copied from this ValRail.
     */
    @Native("java", "x10.core.RailFactory.<#2>makeRailFromValRail(#3, #4)")
    @Native("c++", "x10::lang::Rail<#1 >::make(#4)")
    public native static safe operator [U](r: ValRail[U]): Rail[U]!{self.length==r.length};

    /**
     * Operator that allows access of Rail elements by index.
     *
     * @param i The index to retreive.
     * @return The value at that index.
     */
    @Native("java", "#0.apply(#1)")
    @Native("c++", "(*#0)[#1]")
    @Native("cuda", "(#0)[#1]")
    public native safe def apply(i: Int): T;

    /**
     * Operator that allows assignment of Rail elements by index.
     *
     * @param v The value to assign.
     * @param i The index of the element to be changed.
     * @return The new value.
     */
    @Native("java", "#0.set(#1, #2)")
    @Native("c++", "(*#0)[#2] = #1")
    @Native("cuda", "(#0)[#2] = #1")
    public native safe def set(v: T, i: Int): T;

    /**
     * Get an iterator over this Rail.
     *
     * @return A new iterator instance.
     */
    @Native("java", "#0.iterator()")
    @Native("c++", "(#0)->iterator()")
    public native safe def iterator(): Iterator[T];


    /**
     * Create a Rail in a given place and initialize it using the given function.
     * This is a synchronous operation.
     *
     * @param p the location of the new Rail.
     * @param length the length of the new Rail.
     * @param init the initialization function.
     * @return The reference to a remote Rail.
     */
    @Native("java", "x10.lang.Rail__NativeRep.makeRemoteRail(#3, #4,#5,#6)")
    @Native("c++", "x10::lang::Rail__NativeRep::makeRemoteRail(#4,#5,#6)")
    public native static safe def makeRemote[T] (p:Place, length:Int, init: (Int) => T) : Rail[T]!p{self.length==length};

    /**
     * Create a Rail in a given place and initialize it by copying from a given Rail.
     * This is a synchronous operation.
     *
     * @param p the location of the new Rail.
     * @param length the length of the new Rail.
     * @param init the Rail whose contents will be used to initialize the new Rail.
     * @return The reference to a remote Rail.
     */
    @Native("java", "x10.lang.Rail__NativeRep.makeRemoteRail(#3, #4,#5,#6)")
    @Native("c++", "x10::lang::Rail__NativeRep::makeRemoteRail(#4,#5,#6)")
    public native static safe def makeRemote[T] (p:Place, length:Int, init: Rail[T]!) : Rail[T]!p{self.length==length};
   
   
 	
    
    // Transfer functions

    /**
     * Copies a portion of a given Rail into a given remote Rail.
     * Upon completion, notifies the enclosing finish.
     *
     * @param src the source Rail.
     * @param src_off the offset of the first element to copy in the source.
     * @param dst the destination Rail.
     * @param dst_off the offset of the first element store in the destination.
     * @param len the number of elements to copy.
     */
    @Native("java", "x10.lang.Rail__NativeRep.copyTo(#7, #0,#1,#2,#3,#4)")
    @Native("c++", "x10::lang::Rail__NativeRep::copyTo(#0,#1,#2,#3,#4)")
    public native def copyTo (src_off:Int, dst:Rail[T], dst_off:Int, len:Int) : Void;

    /**
     * Copies a portion of a given Rail into a remote Rail indicated by the given closure.
     * Upon completion, notifies the enclosing finish.
     *
     * @param src the source Rail.
     * @param src_off the offset of the first element to copy in the source.
     * @param dst_place the location of the destination Rail.
     * @param dst_finder a function returning a {@link x10.util.Pair} that consists
     *                   of the reference to the destination Rail and the offset of
     *                   the first element to store in the destination.
     * @param len the number of elements to copy.
     */
    @Native("java", "x10.lang.Rail__NativeRep.copyTo(#7, #0,#1,#2,#3,#4)")
    @Native("c++", "x10::lang::Rail__NativeRep::copyTo(#0,#1,#2,#3,#4)")
    public native def copyTo (src_off:Int,
                              dst_place:Place, dst_finder:()=>Pair[Rail[T],Int],
                              len:Int) : Void;

    /**
     * Copies a portion of a given Rail into a remote Rail.
     * Upon completion, invokes the notifier closure.  The enclosing finish is not affected.
     *
     * @param src the source Rail.
     * @param src_off the offset of the first element to copy in the source.
     * @param dst_place the location of the destination Rail.
     * @param dst_finder a function returning a {@link x10.util.Pair} that consists
     *                   of the reference to the destination Rail and the offset of
     *                   the first element to store in the destination.
     * @param len the number of elements to copy.
     * @param notifier the function to invoke upon completion.
     */
    @Native("java", "x10.lang.Rail__NativeRep.copyTo(#8, #0,#1,#2,#3,#4,#5)")
    @Native("c++", "x10::lang::Rail__NativeRep::copyTo(#0,#1,#2,#3,#4,#5)")
    public native def copyTo (src_off:Int, dst:Rail[T], dst_off:Int, len:Int, notifier:()=>Void) : Void;

    /**
     * Copies a portion of a given Rail into a remote Rail indicated by the given closure.
     * Upon completion, invokes the notifier closure.  The enclosing finish is not affected.
     *
     * @param src the source Rail.
     * @param src_off the offset of the first element to copy in the source.
     * @param dst_place the location of the destination Rail.
     * @param dst_finder a function returning a {@link x10.util.Pair} that consists
     *                   of the reference to the destination Rail and the offset of
     *                   the first element to store in the destination.
     * @param len the number of elements to copy.
     * @param notifier the function to invoke upon completion.
     */
    @Native("java", "x10.lang.Rail__NativeRep.copyTo(#8, #0,#1,#2,#3,#4,#5)")
    @Native("c++", "x10::lang::Rail__NativeRep::copyTo(#0,#1,#2,#3,#4,#5)")
    public native def copyTo (src_off:Int,
                              dst_place:Place, dst_finder:()=>Pair[Rail[T],Int],
                              len:Int, notifier:()=>Void) : Void;

    /**
     * Copies a portion of a given local Rail into the Rail stored in a given
     * place-local handle at a given place.
     * Upon completion, notifies the enclosing finish.
     *
     * @param src the source Rail.
     * @param src_off the offset of the first element to copy in the source.
     * @param dst_place the location of the destination Rail.
     * @param dst_handle the place-local handle that references the destination Rail.
     * @param dst_off the offset of the first element store in the destination.
     * @param len the number of elements to copy.
     */
    @Native("java", "x10.lang.Rail__NativeRep.copyTo(#8, #0,#1,#2,#3,#4,#5)")
    @Native("c++", "x10::lang::Rail__NativeRep::copyTo(#0,#1,#2,#3,#4,#5)")
    public native def copyTo (src_off:Int,
                              dst_place:Place, dst_handle:PlaceLocalHandle[Rail[T]], dst_off:Int,
                              len:Int) : Void;

    /**
     * Copies a portion of a given local Rail into the Rail stored in a given
     * place-local handle at a given place.
     * Upon completion, invokes the notifier closure.  The enclosing finish is not affected.
     *
     * @param src the source Rail.
     * @param src_off the offset of the first element to copy in the source.
     * @param dst_place the location of the destination Rail.
     * @param dst_handle the place-local handle that references the destination Rail.
     * @param dst_off the offset of the first element store in the destination.
     * @param len the number of elements to copy.
     * @param notifier the function to invoke upon completion.
     */
    @Native("java", "x10.lang.Rail__NativeRep.copyTo(#9, #0,#1,#2,#3,#4,#5,#6)")
    @Native("c++", "x10::lang::Rail__NativeRep::copyTo(#0,#1,#2,#3,#4,#5,#6)")
    public native def copyTo (src_off:Int,
                              dst_place:Place, dst_handle:PlaceLocalHandle[Rail[T]], dst_off:Int,
                              len:Int, notifier:()=>Void) : Void;

/* not implemented!
    /**
     * Copies a portion of a given Rail into a given remote Rail.
     * Upon completion, invokes the notifier closure.  The enclosing finish is not affected.
     *
     * @param src the source Rail.
     * @param src_off the offset of the first element to copy in the source.
     * @param dst the destination Rail.
     * @param dst_off the offset of the first element store in the destination.
     * @param len the number of elements to copy.
     * /
    @Native("java", "x10.lang.Rail__NativeRep.copyTo(#8, #0,#1,#2,#3,#4,#5)")
    @Native("c++", "x10::lang::Rail__NativeRep::copyTo(#0,#1,#2,#3,#4,#5)")
    public native def copyTo (src_off:Int, dst:Rail[T], dst_off:Int,
                              len:Int, notifier:()=>Void) : Void;
*/

    /**
     * Copies a portion of a given remote Rail into a given Rail (a DMA get).
     * Upon completion, notifies the enclosing finish.
     *
     * @param dst the destination Rail.
     * @param dst_off the offset of the first element store in the destination.
     * @param src the source Rail.
     * @param src_off the offset of the first element to copy in the source.
     * @param len the number of elements to copy.
     */
    @Native("java", "x10.lang.Rail__NativeRep.copyFrom(#7, #0,#1,#2,#3,#4)")
    @Native("c++", "x10::lang::Rail__NativeRep::copyFrom(#0,#1,#2,#3,#4)")
    public native def copyFrom (dst_off:Int, src:Rail[T], src_off:Int, len:Int) : Void;

    /**
     * Copies a portion of a remote Rail indicated by the given closure into a given Rail (a DMA get).
     * Upon completion, notifies the enclosing finish.
     *
     * @param dst the destination Rail.
     * @param dst_off the offset of the first element to store in the destination.
     * @param src_place the location of the source Rail.
     * @param src_finder a function returning a {@link x10.util.Pair} that consists
     *                   of the reference to the source Rail and the offset of
     *                   the first element to copy in the source.
     * @param len the number of elements to copy.
     */
    @Native("java", "x10.lang.Rail__NativeRep.copyFrom(#7, #0,#1,#2,#3,#4)")
    @Native("c++", "x10::lang::Rail__NativeRep::copyFrom(#0,#1,#2,#3,#4)")
    public native def copyFrom (dst_off:Int,
                                src_place:Place, src_finder:()=>Pair[Rail[T],Int],
                                len:Int) : Void;

    /**
     * Copies a portion of a given ValRail into a given Rail (not a DMA since this is a local operation).
     * Upon completion, notifies the enclosing finish.
     *
     * @param dst the destination Rail.
     * @param dst_off the offset of the first element store in the destination.
     * @param src the source ValRail.
     * @param src_off the offset of the first element to copy in the source.
     * @param len the number of elements to copy.
     */
    @Native("java", "x10.lang.Rail__NativeRep.copyFrom(#7, #0,#1,#2,#3,#4)")
    @Native("c++", "x10::lang::Rail__NativeRep::copyFrom(#0,#1,#2,#3,#4)")
    public native def copyFrom (dst_off:Int, src:ValRail[T], src_off:Int, len:Int):Void;

    /** 
     * Copies a portion of a ValRail indicated by the given closure into a given Rail (a DMA get). 
     * Upon completion, notifies the enclosing finish. 
     * 
     * @param dst the destination Rail. 
     * @param dst_off the offset of the first element to store in the destination. 
     * @param src_place the location of the source ValRail (to evaluate the closure). 
     * @param src_finder a function returning a {@link x10.util.Pair} that consists 
     *                   of the reference to the source ValRail and the offset of 
     *                   the first element to copy in the source. 
     * @param len the number of elements to copy. 
     */ 
    @Native("java", "x10.lang.Rail__NativeRep.copyFrom1(#7, #0,#1,#2,#3,#4)")
    @Native("c++", "x10::lang::Rail__NativeRep::copyFrom1(#0,#1,#2,#3,#4)")
    public native def copyFrom (dst_off:Int,
                                src_place:Place, src_finder:()=>Pair[ValRail[T],Int],
                                len:Int) : Void;

    /** Creates a ValRail that shares the data of the given Rail.  This is an
     * unsafe operation that may be removed in future X10 releases.  The ValRail
     * does not actually contain constant data, so use with caution!  The intended
     * use-case is to avoid duplicating the content of a Rail when serialising to a
     * remote place.  You may find copyTo/copyFrom are a better choice as they are
     * faster and safer.
     */ 
    @Native("java", "#0.view()")
    @Native("c++", "#0->view()")
    public native def view(): ValRail[T]{self.length==this.length};

    private static class RailIterator[S] implements Iterator[S] {
        private var curIndex:int = 0;
        private val rail:Rail[S]!;

        private def this(r:Rail[S]!) { rail = r; }
        public def hasNext() = curIndex < rail.length;
        public def next() = rail(curIndex++);
    }

}
