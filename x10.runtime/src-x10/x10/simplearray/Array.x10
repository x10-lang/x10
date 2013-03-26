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

import x10.compiler.CompilerFlags;
import x10.compiler.Inline;
import x10.compiler.NoInline;
import x10.compiler.NoReturn;

    /*
     * TODO: Add Destructurable and associated methods (and compiler support...)
     */

/**
 * <p> This class provides a high-performance implementation of
 * a simple multi-dimensional Array that maps indices to values.  
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
 * a Array(1) and a Rail should have very similar performance characteristics,
 * although the Array(1) has an additional wrapper object and thus may have
 * marginally lower performance in some situations.</p>
 * 
 * <p>A more general, but lower performance, array abstraction is provided 
 * by {@link x10.array.Array} and {@link x10.array.DistArray} which support 
 * more general indexing operations via user-extensible {@link x10.array.Region}s 
 * and {@link x10.array.Dist}s. See the package documentation of {@link x10.array}
 * for more details.</p>
 */
public final class Array[T] (
        /**
         * The number of data values in the array.
         */
        size:Long,

        /**
         * The number of elements in rank 1 (indexed 0..(numElems_1-1))
         */
        numElems_1:Long,

        /**
         * The number of elements in rank 2 (indexed 0..(numElems_2-1)).
         * Will be zero if rank < 2.
         */
        numElems_2:Long,

        /**
         * The number of elements in rank 3 (indexed 0..(numElems_3-1)).
         * Will be zero if rank < 3.
         */
        numElems_3:Long,

        /**
         * The rank of this array.
         */
        rank:int
) implements Iterable[T] {
    
    /**
     * The backing storage for the array's elements
     */
    private val raw:Rail[T]{self!=null, self.size==this.size};

    /**
     * <p>Return the Rail[T] that is providing the backing storage for the array.
     * This method is primarily intended to be used to interface with native libraries 
     * (eg BLAS, ESSL). </p>
     * 
     * This method should be used sparingly, since it may make client code dependent on the layout
     * algorithm used to map rank-dimensional array indicies to 1-dimensional indicies in the backing Rail.
     * The actual layout algorithm is the standard row-major layout (like C, unlike Fortran)
     * and is compatible with the layout expected by platform BLAS libraries that operate on row-major
     * C arrays.
     * 
     * @return the Rail[T] that is the backing storage for the Array object.
     */
    public @Inline def raw() = raw;

    /**
     * Construct a rank-1 array with indices 0..n-1 whose elements are zero-initialized.
     */
    public def this(n:Long) { T haszero } {
        property(n, n, 0L, 0L, 1);
        raw = new Rail[T](n);
    }

    /**
     * Construct a rank-2 array with indices [0..m-1][0..n-1] whose elements are zero-initialized.
     */
    public def this(m:Long, n:Long) { T haszero } {
        val rawSize = n*m;
        property(rawSize, m, n, 0L, 2);
        raw = new Rail[T](rawSize);
    }

    /**
     * Construct a rank-3 array with indices [0..m-1][0..n-1][0..p-1] whose elements are zero-initialized.
     */
    public def this(m:Long, n:Long, p:Long) { T haszero } {
        val rawSize = n*m*p;
        property(rawSize, m, n, p, 3);
        raw = new Rail[T](rawSize);
    }

    /**
     * Construct a rank-1 array with indices 0..n-1 whose elements are initialized 
     * by evaluating the initialization closure init for each element.
     */
    public @Inline def this(n:Long, init:()=>T) {
        property(n, n, 0L, 0L, 1);
        raw = Unsafe.allocRailUninitialized[T](n);
	for (i in raw.range()) {
            raw(i) = init();
        }
    }

    /**
     * Construct a rank-2 array with indices [0..m-1][0..n-1] whose elements are initialized
     * by evaluating the initialization closure init for each element.
     */
    public @Inline def this(m:Long, n:Long, init:()=>T) {
        val rawSize = n*m;
        property(rawSize, m, n, 0L, 2);
        raw = Unsafe.allocRailUninitialized[T](rawSize);
	for (i in raw.range()) {
            raw(i) = init();
        }
    }

    /**
     * Construct a rank-3 array with indices [0..m-1][0..n-1][0..p-1] whose elements are initialized
     * by evaluating the initialization closure init for each element.
     */
    public @Inline def this(m:Long, n:Long, p:Long, init:()=>T) {
        val rawSize = n*m*p;
        property(rawSize, m, n, p, 3);
        raw = Unsafe.allocRailUninitialized[T](rawSize);
	for (i in raw.range()) {
            raw(i) = init();
        }
    }

    /**
     * Construct a rank-1 array with indices 0..n-1 whose elements are initialized to 
     * the value computed by the init closure when applied to the element index.
     */
    public @Inline def this(n:Long, init:(long)=>T) {
        property(n, n, 0L, 0L, 1);
        raw = new Rail[T](n, init);
    }

    /**
     * Construct a rank-2 array with indices [0..m-1][0..n-1] whose elements are initialized to
     * the value computed by the init closure when applied to the element index.
     */
    public @Inline def this(m:Long, n:Long, init:(long,long)=>T) {
        val rawSize = n*m;
        property(rawSize, m, n, 0L, 2);
        raw = Unsafe.allocRailUninitialized[T](rawSize);
        for (i in 0..(m-1)) {
            for (j in 0..(n-1)) {
                raw(offset(i,j)) = init(i,j);
            }
        }
    }

    /**
     * Construct a rank-3 array with indices [0..m-1][0..n-1][0..p-1] whose elements are initialized
     * the value computed by the init closure when applied to the element index.
     */
    public @Inline def this(m:Long, n:Long, p:Long, init:(long,long,long)=>T) {
        val rawSize = n*m*p;
        property(rawSize, m, n, p, 3);
        raw = Unsafe.allocRailUninitialized[T](rawSize);
        for (i in 0..(m-1)) {
            for (j in 0..(n-1)) {
                for (k in 0..(p-1)) {
                    raw(offset(i,j,k)) = init(i,j,k);
                }
            }
        }
    }

    /**
     * Return the string representation of this array.
     * 
     * @return the string representation of this array.
     */
    public def toString():String {
        switch(rank) {
            case 1:
                return raw.toString();
            default:
                return "Array: TODO implement pretty print for rank >1";
        }
    }
    
    /**
     * Return an iterator over the values of this array.
     * 
     * @return an iterator over the values of this array.
     * @see x10.lang.Iterable[T]#iterator()
     */
    public def iterator():Iterator[T] = raw.iterator(); 
    
    
    /**
     * Return the element of this array corresponding to the given index.
     * Only applies to one-dimensional arrays.
     * 
     * @param i the given index in the first dimension
     * @return the element of this array corresponding to the given index.
     * @see #set(T, Int)
     */
    public @Inline operator this(i:long){rank==1}:T {
        // Bounds checking by backing Rail is sufficient
        return raw(i);
    }
    
    /**
     * Return the element of this array corresponding to the given pair of indices.
     * Only applies to two-dimensional arrays.
     * 
     * @param i the given index in the first dimension
     * @param j the given index in the second dimension
     * @return the element of this array corresponding to the given pair of indices.
     * @see #set(T, Int, Int)
     */
    public @Inline operator this(i:long, j:long){rank==2}:T {
        if (CompilerFlags.checkBounds() && (i < 0 || i >= numElems_1 || 
                                            j < 0 || j >= numElems_2)) {
            raiseBoundsError(i, j);
        }
        return raw(offset(i, j));
    }
    
    /**
     * Return the element of this array corresponding to the given triple of indices.
     * Only applies to three-dimensional arrays.
     * 
     * @param i the given index in the first dimension
     * @param j the given index in the second dimension
     * @param k the given index in the third dimension
     * @return the element of this array corresponding to the given triple of indices.
     * @see #set(T, Int, Int, Int)
     */
    public @Inline operator this(i:long, j:long, k:long){rank==3}:T {
        if (CompilerFlags.checkBounds() && (i <0 || i >= numElems_1 || 
                                            j <0 || j >= numElems_2 || 
                                            k <0 || k >= numElems_3)) {
            raiseBoundsError(i, j, k);
        }
        return raw(offset(i, j, k));
    }
    
    
    /**
     * Set the element of this array corresponding to the given index to the given value.
     * Return the new value of the element.
     * Only applies to one-dimensional arrays.
     * 
     * @param v the given value
     * @param i the given index in the first dimension
     * @return the new value of the element of this array corresponding to the given index.
     * @see #operator(Int)
     */
    public @Inline operator this(i:long)=(v:T){rank==1}:T{self==v} {
        // Bounds checking by backing Rail is sufficient
        raw(i) = v;
        return v;
    }
    
    /**
     * Set the element of this array corresponding to the given pair of indices to the given value.
     * Return the new value of the element.
     * Only applies to two-dimensional arrays.
     * 
     * @param v the given value
     * @param i the given index in the first dimension
     * @param j the given index in the second dimension
     * @return the new value of the element of this array corresponding to the given pair of indices.
     * @see #operator(Int, Int)
     */
    public @Inline operator this(i:long,j:long)=(v:T){rank==2}:T{self==v} {
        if (CompilerFlags.checkBounds() && (i < 0 || i >= numElems_1 || 
                                            j < 0 || j >= numElems_2)) {
            raiseBoundsError(i, j);
        }
        raw(offset(i, j)) = v;
        return v;
    }
    
    /**
     * Set the element of this array corresponding to the given triple of indices to the given value.
     * Return the new value of the element.
     * Only applies to three-dimensional arrays.
     * 
     * @param v the given value
     * @param i the given index in the first dimension
     * @param j the given index in the second dimension
     * @param k the given index in the third dimension
     * @return the new value of the element of this array corresponding to the given triple of indices.
     * @see #operator(Int, Int, Int)
     */
    public @Inline operator this(i:long, j:long, k:long)=(v:T){rank==3}:T{self==v} {
        if (CompilerFlags.checkBounds() && (i <0 || i >= numElems_1 || 
                                            j <0 || j >= numElems_2 || 
                                            k <0 || k >= numElems_3)) {
            raiseBoundsError(i, j, k);
        }
        raw(offset(i, j, k)) = v;
        return v;
    }
    
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


    /*
     * TODO: Consider adding methods for map, scan, reduce operations
     */    


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

    private static @NoInline @NoReturn def raiseBoundsError(i:long) {
        throw new ArrayIndexOutOfBoundsException("(" + i + ") not contained in array");
    }    
    private static @NoInline @NoReturn def raiseBoundsError(i:long, j:long) {
        throw new ArrayIndexOutOfBoundsException("(" + i + ", "+j+") not contained in array");
    }    
    private static @NoInline @NoReturn def raiseBoundsError(i:long, j:long, k:long) {
        throw new ArrayIndexOutOfBoundsException("(" + i + ", "+j+", "+k+") not contained in array");
    }    

    private @Inline def offset(i:long, j:long) {
         return j + (i * numElems_2);
    }
        
    private @Inline def offset(i:long, j:long, k:long) {
        return k + numElems_3 * (j + (i * numElems_2));
    }
}

public type Array[T](r:Int) = Array[T]{self.rank==r};

// vim:tabstop=4:shiftwidth=4:expandtab
