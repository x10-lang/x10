/*
 *  This file is part of the X10 project (http://x10-lang.org).
 * 
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 * 
 *  (C) Copyright IBM Corporation 2006-2013.
 */

package x10.simplearray;

import x10.compiler.Inline;
import x10.compiler.NoInline;
import x10.compiler.NoReturn;

/**
 * <p> This class provides a high-performance implementation of
 * a simple multi-dimensional Array that maps long indices to values.  
 * This array implementation only supports zero-based, dense indexing.
 * For example, a two dimensional array of N by M elements is indexed by 
 * (0..N-1,0..M-1).</p>
 * 
 * <p> Related classes in this package {@link DistArray} provide a similar
 * simple array abstraction whose data is distributed across multiple
 * places.</p>
 * 
 * <p>The {@link x10.lang.Rail} class also provides a similar, high-performance
 * implementation of a one dimensional array.  The expectation should be that
 * a Array_1 and a Rail should have very similar performance characteristics,
 * although the Array_1 has an additional wrapper object and thus may have
 * marginally lower performance in some situations.</p>
 * 
 * <p>A more general, but lower performance, array abstraction is provided 
 * by {@link x10.regionarray.Array} and {@link x10.regionarray.DistArray} which support 
 * more general indexing operations via user-extensible {@link x10.regionarray.Region}s 
 * and {@link x10.regionarray.Dist}s. See the package documentation of {@link x10.regionarray}
 * for more details.</p>
 */
public abstract class Array[T] (
        /**
         * The number of data values in the array.
         */
        size:Long
) implements Iterable[T] {

    /**
     * @return the rank (dimensionality) of the Array
     */
    public abstract property rank():int;

    /**
     * The backing storage for the array's elements
     */
    protected val raw:Rail[T]{self!=null, self.size==this.size};

    protected def this(s:Long, zero:boolean) {
        property(s);
        raw = zero ? Unsafe.allocRailZeroed[T](s) : Unsafe.allocRailUninitialized[T](s);
    }

    protected def this(r:Rail[T]{self!=null}) {
        property(r.size);
        raw = r;
    }


    /**
     * <p>Return the Rail[T] that is providing the backing storage for the array.
     * This method is primarily intended to be used to interface with native libraries 
     * (eg BLAS, ESSL) and to support bulk copy operations using the asyncCopy 
     * methods of Rail.</p>
     * 
     * This method should be used sparingly, since it may make client code dependent on the layout
     * algorithm used to map rank-dimensional array indicies to 1-dimensional indicies in the backing Rail.
     * 
     * @return the Rail[T] that is the backing storage for the Array object.
     */
    public final @Inline def raw() = raw;


    /**
     * Return an iterator over the values of this array.
     * 
     * @return an iterator over the values of this array.
     * @see x10.lang.Iterable[T]#iterator()
     */
    public final def iterator():Iterator[T] = raw.iterator(); 
    

    /**
     * Fill all elements of the array to contain the argument value.
     * 
     * @param v the value with which to fill the array
     */
    public def fill(v:T) {
        for (i in raw.range) {
            raw(i) = v;
        }
    }


    /**
     * Fill all elements of the array with the zero value of type T 
     * @see x10.lang.Zero.get[T]()
     */
    public def clear(){T haszero} {
        raw.clear();
    }


    /**
     * Reduce this array using the given function and the given initial value.
     * Each element of the array will be given as an argument to the reduction
     * function exactly once, but in an arbitrary order.  The reduction function
     * may be applied concurrently to implement a parallel reduction. 
     * 
     * @param op the reduction function
     * @param unit the given initial value
     * @return the final result of the reduction.
     */
    public final @Inline def reduce[U](op:(U,T)=>U, unit:U):U {
        // TODO: Once collecting finish is generalized to take
        //       a (U,T)=>U instead of (T,T)=>T function then
        //       rewrite this as a blocked parallel loop with 
        //       collecting finish to accumulate the final results
        var accum:U = unit;
        for (i in 0..(raw.size-1)) {
            accum = op(accum, raw(i));
        }          
        return accum;
    }
    

    /**
     * Get an IterationSpace that represents all Points contained in
     * iteration space (valid indices) of the Array.
     * @return an IterationSpace for the Array
     */
    // TODO: rank constraint commented out until XTENLANG-3210 is fixed
    public abstract def indices():IterationSpace/*{self.rank==this.rank(),self.rect,self!=null}*/;


    /**
     * Return the element of this array corresponding to the given point.
     * The rank of the given point has to be the same as the rank of this array.
     * 
     * @param p the given point
     * @return the element of this array corresponding to the given point.
     * @see #set(T, Point)
     */
    public abstract operator this(p:Point(this.rank())):T;


    /**
     * Set the element of this array corresponding to the given point to the given value.
     * Return the new value of the element.
     * The rank of the given point has to be the same as the rank of this array.
     * 
     * @param v the given value
     * @param p the given point
     * @return the new value of the element of this array corresponding to the given point.
     * @see #operator(Point)
     */
    // TODO: commented out until XTENLANG-3209 is fixed
    // public abstract operator this(p:Point(this.rank()))=(v:T):T{self==v};


    /**
     * Copy all of the values from the source Array to the destination Array.
     * The two arrays must be of equal size, but do not necessarily need
     * to be the same rank or have the same dimensions.
     *
     * @param src the source array.
     * @param dst the destination array.
     */
    public static def copy[T](src:Array[T], dst:Array[T]){src.size==dst.size} {
        Rail.copy(src.raw, 0L, dst.raw, 0L, src.raw.size);
    }


    /**
     * Copy numElems values starting from srcIndex from the source Array 
     * to the destination Array starting at dstIndex.
     *
     * @param src the source array.
     * @param srcIndex the index of the first element in this array
     *        to be copied.  
     * @param dst the destination array.
     * @param dstIndex the index of the first element in the destination
     *        array where copied data elements will be stored.
     * @param numElems the number of elements to be copied.
     */
    public static def copy[T](src:Array[T], srcIndex:long, 
                              dst:Array[T], dstIndex:long, numElems:long) {
        Rail.copy(src.raw, srcIndex, dst.raw, dstIndex, numElems);
    }


    protected static @NoInline @NoReturn def raiseBoundsError(i:long) {
        throw new ArrayIndexOutOfBoundsException("(" + i + ") not contained in array");
    }    
    protected static @NoInline @NoReturn def raiseBoundsError(i:long, j:long) {
        throw new ArrayIndexOutOfBoundsException("(" + i + ", "+j+") not contained in array");
    }    
    protected static @NoInline @NoReturn def raiseBoundsError(i:long, j:long, k:long) {
        throw new ArrayIndexOutOfBoundsException("(" + i + ", "+j+", "+k+") not contained in array");
    }    
}

// vim:tabstop=4:shiftwidth=4:expandtab
