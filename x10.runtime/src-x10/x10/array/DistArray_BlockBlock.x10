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

import x10.compiler.Inline;
import x10.compiler.CompilerFlags;
import x10.io.CustomSerialization;
import x10.io.SerialData;

/**
 * Implementation of a 2-D DistArray that distributes its data elements
 * over the places in its PlaceGroup in a 2-D blocked fashion.
 */
public class DistArray_BlockBlock[T] extends DistArray[T] implements (Long,Long)=>T {
    
    public property rank() = 2;

    protected val numElems_1:Long;

    protected val numElems_2:Long;

    protected val minIndex_1:Long;
 
    protected val minIndex_2:Long;

    protected val numElemsLocal_1:Long;

    protected val numElemsLocal_2:Long;
    
    /**
     * Construct a m by n block-block distributed DistArray
     * whose data is distrbuted over pg and initialized using
     * the init function.
     *
     * @param m number of elements in the first dimension
     * @param n number of elements in the second dimension
     * @param pg the PlaceGroup to use to distibute the elements.
     * @param init the element initialization function
     */
    public def this(m:long, n:long, pg:PlaceGroup{self!=null}, init:(long,long)=>T) {
        super(pg, () => BBLocalState.make[T](pg, m, n, init));
        val bbls = localHandle() as BBLocalState[T];
        numElems_1 = bbls.numElems_1;
        numElems_2 = bbls.numElems_2;
        minIndex_1 = bbls.minIndex_1;
        minIndex_2 = bbls.minIndex_2;
        numElemsLocal_1 = bbls.numElemsLocal_1;
        numElemsLocal_2 = bbls.numElemsLocal_2;
    }

    /**
     * Construct a m by n block-block distributed DistArray
     * whose data is distrbuted over PlaceGroup.WORLD and 
     * initialized using the provided init closure.
     *
     * @param m number of elements in the first dimension
     * @param n number of elements in the second dimension
     * @param init the element initialization function
     */
    public def this(m:long, n:long, init:(long,long)=>T) {
        this(m, n, PlaceGroup.WORLD, init);
    }

    /**
     * Construct a m by n block-block distributed DistArray
     * whose data is distrbuted over pg and zero-initialized.
     *
     * @param m number of elements in the first dimension
     * @param n number of elements in the second dimension
     * @param pg the PlaceGroup to use to distibute the elements.
     */
    public def this(m:long, n:long, pg:PlaceGroup{self!=null}){T haszero} {
        this(m, n, pg, (long,long)=>Zero.get[T]());
    }

    /**
     * Construct a m by n block-block distributed DistArray
     * whose data is distrbuted over PlaceGroup.WORLD and 
     * zero-initialized.
     *
     * @param m number of elements in the first dimension
     * @param n number of elements in the second dimension
     */
    public def this(m:long, n:long){T haszero} {
        this(m, n, PlaceGroup.WORLD, (long,long)=>Zero.get[T]());
    }


    // Custom Serialization: Superclass handles serialization of localHandle
    public def serialize():SerialData = super.serialize();

    def this(sd:SerialData) { 
      super(sd.data as PlaceLocalHandle[LocalState[T]]);
      val bbls = localHandle() as BBLocalState[T];
      numElems_1 = bbls.numElems_1;
      numElems_2 = bbls.numElems_2;
      minIndex_1 = bbls.minIndex_1;
      minIndex_2 = bbls.minIndex_2;
      numElemsLocal_1 = bbls.numElemsLocal_1;
      numElemsLocal_2 = bbls.numElemsLocal_2;
    }


    public def globalIndices():IterationSpace{self.rank==this.rank(),self.rect,self!=null} {
        val is = new DenseIterationSpace_2(0L, 0L, numElems_1-1L, numElems_2-1L);
        return is as IterationSpace{self.rank==2,self.rect,self!=null}; // FIXME: Constraint system weakness. This cast should not be needed.
    }

    /**
     * Get an IterationSpace that represents all Points contained in
     * the local iteration space (valid indices) of the DistArray at the current Place.
     * @return an IterationSpace for the Array
     */
    public def localIndices():IterationSpace{self.rank==this.rank(),self.rect,self!=null} {
        val is = new DenseIterationSpace_2(minIndex_1, minIndex_2, minIndex_1 + numElemsLocal_1 - 1, minIndex_2 + numElemsLocal_2 - 1);
        return is as IterationSpace{self.rank==2,self.rect,self!=null}; // FIXME: Constraint system weakness. This cast should not be needed.
    }

    /**
     * Return the element of this array corresponding to the given index.
     * 
     * @param i the given index in the first dimension
     * @param j the given index in the second dimension
     * @return the element of this array corresponding to the given index.
     * @see #set(T, Long, Long)
     */
    public final @Inline operator this(i:long, j:long):T {
        if (CompilerFlags.checkPlace() || CompilerFlags.checkBounds()) validateIndex(i, j);
        return Unsafe.uncheckedRailApply(raw, offset(i, j));
    }

    /**
     * Return the element of this array corresponding to the given Point.
     * 
     * @param p the given Point
     * @return the element of this array corresponding to the given Point.
     * @see #set(T, Point)
     */
    public final @Inline operator this(p:Point(this.rank())):T  = this(p(0), p(1));

    
    /**
     * Set the element of this array corresponding to the given index to the given value.
     * Return the new value of the element.
     * 
     * @param v the given value
     * @param i the given index in the first dimension
     * @param j the given index in the second dimension
     * @return the new value of the element of this array corresponding to the given index.
     * @see #operator(Int)
     */
    public final @Inline operator this(i:long,j:long)=(v:T):T{self==v} {
        if (CompilerFlags.checkPlace() || CompilerFlags.checkBounds()) validateIndex(i,j);
        Unsafe.uncheckedRailSet(raw, offset(i, j), v);
        return v;
    }

    /**
     * Set the element of this array corresponding to the given Point to the given value.
     * Return the new value of the element.
     * 
     * @param v the given value
     * @param p the given Point
     * @return the new value of the element of this array corresponding to the given Point.
     * @see #operator(Int)
     */
    public final @Inline operator this(p:Point(this.rank()))=(v:T):T{self==v} = this(p(0),p(1)) = v;

    /*
     * Order of tests is designed to minimize the dynamic number of comparisons
     * on valid access, while still preferring to raise a bounds error rather than
     * a place error when an index is not even contained in the globalIndices of the array.
     */
    protected final def validateIndex(i:long, j:long) {
        if (CompilerFlags.checkPlace() || CompilerFlags.checkBounds()) {
            if (i < minIndex_1 || i >= (minIndex_1 + numElemsLocal_1) || 
                j < minIndex_2 || j >= (minIndex_2 + numElemsLocal_2)) {
                if (CompilerFlags.checkBounds() && (i < 0 || i >= numElems_1 || j < 0 || j >= numElems_2)) {
                    raiseBoundsError(i);
                }
                if (CompilerFlags.checkPlace()) raisePlaceError(i,j);
            }
        }
    }

    protected final @Inline def offset(i:long, j:long) {
         return (j - minIndex_2) + ((i - minIndex_1) * numElemsLocal_2);
    }
}

class BBLocalState[S] extends LocalState[S] {
    val numElems_1:Long;
    val numElems_2:Long;
    val minIndex_1:Long;
    val minIndex_2:Long;
    val numElemsLocal_1:Long;
    val numElemsLocal_2:Long;

    def this(pg:PlaceGroup{self!=null}, data:Rail[S]{self!=null}, m:long, n:long, min1:long, min2:long, localSize1:long, localSize2:long) {
        super(pg, data, m * n);
        numElems_1 = m;
        numElems_2 = n;
        minIndex_1 = min1;
        minIndex_2 = min2;
        numElemsLocal_1 = localSize1;
        numElemsLocal_2 = localSize2;
    }

    static def make[S](pg:PlaceGroup{self!=null}, m:long, n:long, init:(long,long)=>S):BBLocalState[S] {
        val globalSpace = new DenseIterationSpace_2(0L, 0L, m-1, n-1);
        val localSpace = BlockingUtils.partitionBlockBlock(globalSpace, pg.numPlaces(), pg.indexOf(here));

	if (localSpace.min(0) > localSpace.max(0)) {
            // TODO: add isEmpty() to IterationSpace API?
            return new BBLocalState[S](pg, new Rail[S](), m, n, 0L, 0L, 0L, 0L);
        } else {            
            val low1 = localSpace.min(0);
            val hi1 = localSpace.max(0);
            val low2 = localSpace.min(1);
            val hi2 = localSpace.max(1);
            val localSize1 = hi1 - low1 + 1;
            val localSize2 = hi2 - low2 + 1;
            val dataSize = localSize1 * localSize2;
            val data = Unsafe.allocRailUninitialized[S](dataSize);
            for (i in low1..hi1) {
                for (j in low2..hi2) {
                    val offset = j - low2 + ((i - low1) * localSize2);
                    data(offset) = init(i,j);
                }
            }
            return new BBLocalState[S](pg, data, m, n, low1, low2, localSize1, localSize2);
        }
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
