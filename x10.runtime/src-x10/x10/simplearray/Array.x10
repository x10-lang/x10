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

/**
 * FIXME: Update header comment for this implmentation.
 * <p>An array defines a mapping from {@link Point}s to data values of some type T.
 * The Points in the Array's domain are defined by specifying a {@link Region}
 * over which the Array is defined.  Attempting to access a data value
 * at a Point not included in the Array's Region will result in a 
 * {@link ArrayIndexOutOfBoundsException} being raised.</p>
 * 
 * <p>All of the data in an Array is stored in a single Place, the 
 * Array's object home.  Data values may only be accessed at
 * the Array's home place.</p>
 * 
 * <p>The Array implementation is optimized for relatively dense 
 * region of points. In particular, to compute the storage required
 * to store an array instance's data, the array's Region is asked
 * for its bounding box (n-dimensional box such that all points in
 * the Region are contained within the bounding box). Backing storage 
 * is allocated for every Point in the bounding box of the array's Region.
 * Using the Array with partially defined Regions (ie, Regions that do 
 * not include every point in the Region's bounding box) is supported
 * and will operate as expected, however if the Region is sparse and large
 * there will be significant space overheads incurred for defining an Array
 * over the Region.  In future versions of X10, we may support a more 
 * space efficient implementation of Arrays over sparse regions, but 
 * such an implementation is not yet available as part of the x10.array package.</p>
 * 
 * <p>The closely related class {@link DistArray} is used to define 
 * distributed arrays where the data values for the Points in the 
 * array's domain are distributed over multiple places.</p>
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
    private val raw:Rail[T]/*{self!=null, size.size==this.size}*/;

    /**
     * Return the Rail[T] that is providing the backing storage for the array.
     * This method is primarily intended to be used to interface with native libraries 
     * (eg BLAS, ESSL). <p>
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
    public def this(n:Long, init:()=>T) {
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
    public def this(m:Long, n:Long, init:()=>T) {
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
    public def this(m:Long, n:Long, p:Long, init:()=>T) {
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
    public def this(n:Long, init:(long)=>T) {
        property(n, n, 0L, 0L, 1);
        raw = new Rail[T](n, init);
    }

    /**
     * Construct a rank-2 array with indices [0..m-1][0..n-1] whose elements are initialized to
     * the value computed by the init closure when applied to the element index.
     */
    public def this(m:Long, n:Long, init:(long,long)=>T) {
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
    public def this(m:Long, n:Long, p:Long, init:(long,long,long)=>T) {
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

    /*
     * TODO: Add copy methods (both local & remote)
     */
    
    /*
     * TODO: Add a range method that will return a IndexSpace structure
     *       for use in for loop comrhensions.  
     *       Key design question: how best to make this general so we don't have
     *         lots of special cases in the ForLoopOptimizer (and so users can do
     *         it for their classes just as easily).
     *
     *       for ([i,j] in a.range) S
     *
     *  should be transformable by the compiler to        
     *
     *       for (i in 0..(a.numElems_1-1)) 
     *           for (j in 0..a.numElems_2-1) S
     */
    
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
