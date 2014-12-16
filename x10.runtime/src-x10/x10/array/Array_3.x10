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

package x10.array;

import x10.compiler.CompilerFlags;
import x10.compiler.Inline;
import x10.util.StringBuilder;

/**
 * Implementation of 3-dimensional Array using row-major ordering.
 */
public final class Array_3[T] (
        /**
         * The number of elements in rank 1 (indexed 0..(numElems_1-1))
         */
        numElems_1:Long,

        /**
         * The number of elements in rank 2 (indexed 0..(numElems_2-1)).
         */
        numElems_2:Long,

        /**
         * The number of elements in rank 3 (indexed 0..(numElems_3-1)).
         */
        numElems_3:Long
) extends Array[T]{this.rank()==3} implements (Long,Long,Long)=>T {
    
    /**
     * @return the rank (dimensionality) of the Array
     */
    public property rank() = 3;

    /**
     * Construct a 3-dimensional array with indices [0..m-1][0..n-1][0..p-1] 
     * whose elements are zero-initialized.
     */
    public def this(m:Long, n:Long, p:Long) {
        super(validateSize(m, n, p), true);
        property(m, n, p);
    }

    /**
     * Construct a 3-dimensional array with indices [0..m-1][0..n-1][0..p-1] 
     * whose elements are initialized to init.
     */
    public def this(m:Long, n:Long, p:Long, init:T) {
        super(validateSize(m, n, p), false);
        property(m, n, p);
        raw.fill(init);
    }

    /**
     * Construct a 3-dimensional array with indices [0..m-1][0..n-1][0..p-1] 
     * whose elements are initialized to the value returned by the init closure for each index.
     */
    public @Inline def this(m:Long, n:Long, p:Long, init:(Long,Long,Long)=>T) {
        super(validateSize(m, n, p), false);
        property(m, n, p);
        for (i in 0..(m-1)) {
            for (j in 0..(n-1)) {
                for (k in 0..(p-1)) {
                    raw(offset(i,j,k)) = init(i,j,k);
                }
            }
        }
    }

    /**
     * Construct a new 3-dimensional array by copying all elements of src
     * @param src The source array to copy
     */
    public def this(src:Array_3[T]) {
        super(new Rail[T](src.raw));
        property(src.numElems_1, src.numElems_2, src.numElems_3);
    }

    // Intentionally private: only for use of makeView factory method.
    private def this(r:Rail[T]{self!=null}, m:Long, n:Long, p:Long) {
        super(r);
        property(m,n,p);
    }

    /**
     * Construct an Array_3 view over an existing Rail
     */
    public static def makeView[T](r:Rail[T]{self!=null}, m:Long, n:Long, p:Long) {
        val size = n * m *p;
        if (size != r.size) throw new IllegalOperationException("size mismatch: "+m+" * "+n+" * "+p+" != "+r.size);
        return new Array_3[T](r, m, n, p);
    }


    /**
     * Return the string representation of this array.
     * 
     * @return the string representation of this array.
     */
    public def toString():String {
        val sb = new StringBuilder();
        sb.add("[");
        val limit = 10;
        var printed:Long = 0;
        outer: for (i in 0..(numElems_1 - 1)) {
            for (j in 0..(numElems_2 - 1)) {
                for (k in 0..(numElems_3 - 1)) {
                    if (k != 0) sb.add(", ");
                    sb.add(this(i,j,k));
                    if (++printed > limit) break outer;
                 }
                 sb.add(j==numElems_2-1 ? ";; " : "; ");
            }
        }
        if (limit < size) {
            sb.add("...(omitted " + (size - limit) + " elements)");
        }
        sb.add("]");
        return sb.toString();
    }

    /**
     * @return an IterationSpace containing all valid Points for indexing this Array.
     */  
    public def indices():DenseIterationSpace_3{self!=null} {
        return new DenseIterationSpace_3(0, 0, 0, numElems_1-1, numElems_2-1, numElems_3-1);
    }
    
    /**
     * Map a 3-D (i,j,k) index into a 1-D index into the backing Rail
     * returned by raw(). Uses row-major ordering.
     */
    public @Inline def offset(i:Long, j:Long, k:Long) {
        return k + numElems_3 * (j + (i * numElems_2));
    }

    /**
     * Return the element of this array corresponding to the given triple of indices.
     * 
     * @param i the given index in the first dimension
     * @param j the given index in the second dimension
     * @param k the given index in the third dimension
     * @return the element of this array corresponding to the given triple of indices.
     * @see #set(T, Long, Long, Long)
     */
    public @Inline operator this(i:Long, j:Long, k:Long):T {
        if (CompilerFlags.checkBounds() && (i < 0 || i >= numElems_1 || 
                                            j < 0 || j >= numElems_2 || 
                                            k < 0 || k >= numElems_3)) {
            raiseBoundsError(i, j, k);
        }
        return Unsafe.uncheckedRailApply(raw, offset(i, j, k));
    }

    /**
     * Return the element of this array corresponding to the given Point.
     * 
     * @param p the given Point
     * @return the element of this array corresponding to the given Point.
     * @see #set(T, Point)
     */
    public @Inline operator this(p:Point(3)):T  = this(p(0), p(1), p(2));
    
    /**
     * Set the element of this array corresponding to the given triple of indices to the given value.
     * Return the new value of the element.
     * 
     * @param v the given value
     * @param i the given index in the first dimension
     * @param j the given index in the second dimension
     * @param k the given index in the third dimension
     * @return the new value of the element of this array corresponding to the given triple of indices.
     * @see #operator(Long, Long, Long)
     */
    public @Inline operator this(i:Long,j:Long,k:Long)=(v:T):T{self==v} {
        if (CompilerFlags.checkBounds() && (i < 0 || i >= numElems_1 || 
                                            j < 0 || j >= numElems_2 || 
                                            k < 0 || k >= numElems_3)) {
            raiseBoundsError(i, j, k);
        }
        Unsafe.uncheckedRailSet(raw, offset(i, j, k), v);
        return v;
    }

    /**
     * Set the element of this array corresponding to the given Point to the given value.
     * Return the new value of the element.
     * 
     * @param v the given value
     * @param p the given Point
     * @return the new value of the element of this array corresponding to the given Point.
     * @see #operator(Point)
     */
    public @Inline operator this(p:Point(3))=(v:T):T{self==v} = this(p(0), p(1), p(2)) = v;

    
    private @Inline static def validateSize(m:Long, n:Long, p:Long):Long {
        if (m < 0 || n < 0 || p < 0) raiseNegativeArraySizeException();
        return m*n*p;
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
