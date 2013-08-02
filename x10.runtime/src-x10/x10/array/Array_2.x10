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

package x10.array;

import x10.compiler.CompilerFlags;
import x10.compiler.Inline;
import x10.util.StringBuilder;

/**
 * Implementation of 2-dimensional Array.
 */
public final class Array_2[T] (
        /**
         * The number of elements in rank 1 (indexed 0..(numElems_1-1))
         */
        numElems_1:Long,

        /**
         * The number of elements in rank 2 (indexed 0..(numElems_2-1)).
         */
        numElems_2:Long
) extends Array[T]{this.rank()==2n} implements (Long,Long)=>T {

    public property rank() = 2n;

    /**
     * Construct a 2-dimensional array with indices [0..m-1][0..n-1] whose elements are zero-initialized.
     */
    public def this(m:Long, n:Long) {
        super(m*n, true);
        property(m, n);
    }

    /**
     * Construct a 2-dimensional array with indices [0..m-1][0..n-1] whose elements are initialized to init.
     */
    public def this(m:Long, n:Long, init:T) {
        super(m*n, false);
        property(m, n);
        raw.fill(init);
    }

    /**
     * Construct a 2-dimensional array with indices [0..m-1][0..n-1] whose elements are initialized 
     * to the value returned by the init closure for each index.
     */
    public @Inline def this(m:Long, n:Long, init:(long,long)=>T) {
        super(m*n, false);
        property(m, n);
        for (i in 0..(m-1)) {
            for (j in 0..(n-1)) {
                raw(offset(i,j)) = init(i,j);
            }
        }
    }

    /**
     * Construct a new 2-dimensional array by copying all elements of src
     * @param src The source array to copy
     */
    public def this(src:Array_2[T]) {
        super(new Rail[T](src.raw));
        property(src.numElems_1, src.numElems_2);
    }

    // Intentionally private: only for use of makeView factory method.
    private def this(r:Rail[T]{self!=null}, m:Long, n:Long) {
        super(r);
        property(m,n);
    }

    /**
     * Construct an Array_2 view over an existing Rail
     */
    public static def makeView[T](r:Rail[T]{self!=null}, m:Long, n:Long) {
        val size = m * n;
        if (size != r.size) throw new IllegalOperationException("size mismatch: "+m+" * "+n+" != "+r.size);
        return new Array_2[T](r, m, n);
    }


    /**
     * Return the string representation of this array.
     * 
     * @return the string representation of this array.
     */
    public def toString():String {
        val sb = new StringBuilder();
        sb.add("[");
        val limit = 10L;
        var printed:long = 0L;
        outer: for (i in 0L..(numElems_1 - 1)) {
            for (j in 0L..(numElems_2 - 1)) {
                if (j != 0L) sb.add(", ");
                sb.add(this(i,j));
                if (++printed > limit) break outer;
            }
            sb.add("; ");
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
    public def indices():DenseIterationSpace_2{self!=null} {
        return new DenseIterationSpace_2(0L, 0L, numElems_1-1, numElems_2-1);
    }

    /**
     * Return the element of this array corresponding to the given pair of indices.
     * 
     * @param i the given index in the first dimension
     * @param j the given index in the second dimension
     * @return the element of this array corresponding to the given pair of indices.
     * @see #set(T, Int, Int)
     */
    public @Inline operator this(i:long, j:long):T {
        if (CompilerFlags.checkBounds() && (i < 0 || i >= numElems_1 || 
                                            j < 0 || j >= numElems_2)) {
            raiseBoundsError(i, j);
        }
        return Unsafe.uncheckedRailApply(raw, offset(i, j));
    }

    public @Inline operator this(p:Point(2n)):T  = this(p(0n), p(1n));
    
    /**
     * Set the element of this array corresponding to the given pair of indices to the given value.
     * Return the new value of the element.
     * 
     * @param v the given value
     * @param i the given index in the first dimension
     * @param j the given index in the second dimension
     * @return the new value of the element of this array corresponding to the given pair of indices.
     * @see #operator(Int, Int)
     */
    public @Inline operator this(i:long,j:long)=(v:T):T{self==v} {
        if (CompilerFlags.checkBounds() && (i < 0 || i >= numElems_1 || 
                                            j < 0 || j >= numElems_2)) {
            raiseBoundsError(i, j);
        }
        Unsafe.uncheckedRailSet(raw, offset(i, j), v);
        return v;
    }

    public @Inline operator this(p:Point(2n))=(v:T):T{self==v} = this(p(0n), p(1n)) = v;
    
    private @Inline def offset(i:long, j:long) {
         return j + (i * numElems_2);
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
