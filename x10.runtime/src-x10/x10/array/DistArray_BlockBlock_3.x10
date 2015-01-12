/*
 *  This file is part of the X10 project (http://x10-lang.org).
 * 
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 * 
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.array;

import x10.compiler.Inline;
import x10.compiler.CompilerFlags;
import x10.compiler.NonEscaping;
import x10.compiler.TransientInitExpr;

/**
 * Implementation of a 3-D DistArray that distributes its data elements
 * over the places in its PlaceGroup in a 2-D blocked fashion.
 */
public class DistArray_BlockBlock_3[T] extends DistArray[T]{this.rank()==3} implements (Long,Long,Long)=>T {
    
    public property rank() = 3;

    protected val globalIndices:DenseIterationSpace_3{self!=null};

    protected val numElems_1:Long;

    protected val numElems_2:Long;

    protected val numElems_3:Long;

    @TransientInitExpr(reloadLocalIndices())
    protected transient val localIndices:DenseIterationSpace_3{self!=null};
    @NonEscaping
    protected final def reloadLocalIndices():DenseIterationSpace_3{self!=null} {
        val ls = localHandle() as LocalState_BB3[T];
        return ls != null ? ls.dist.localIndices : new DenseIterationSpace_3(0, 0, 0, -1, -1, -1);
    }
    
    @TransientInitExpr(reloadMinIndex_1())
    protected transient val minIndex_1:Long;
    @NonEscaping protected final def reloadMinIndex_1() = localIndices.min(0);
 
    @TransientInitExpr(reloadMinIndex_2())
    protected transient val minIndex_2:Long;
    @NonEscaping protected final def reloadMinIndex_2() = localIndices.min(1);

    @TransientInitExpr(reloadNumElemsLocal_1())
    protected transient val numElemsLocal_1:Long;
    @NonEscaping protected final def reloadNumElemsLocal_1() = localIndices.max(0) - minIndex_1 + 1;

    @TransientInitExpr(reloadNumElemsLocal_2())
    protected transient val numElemsLocal_2:Long;
    @NonEscaping protected final def reloadNumElemsLocal_2() = localIndices.max(1) - minIndex_2 + 1;

    /**
     * Construct a m by n by p block-block distributed DistArray
     * whose data is distributed over pg and initialized using
     * the init function.
     *
     * @param m number of elements in the first dimension
     * @param n number of elements in the second dimension
     * @param p number of elements in the third dimension
     * @param pg the PlaceGroup to use to distibute the elements.
     * @param init the element initialization function
     */
    public def this(m:Long, n:Long, p:Long, pg:PlaceGroup{self!=null}, init:(Long,Long,Long)=>T) {
        super(pg, () => LocalState_BB3.make[T](pg, m, n, p, init), validateSize(m,n,p));
        globalIndices = new DenseIterationSpace_3(0, 0, 0, m-1, n-1, p-1);
        numElems_1 = m;
        numElems_2 = n;
        numElems_3 = p;
        localIndices = reloadLocalIndices();
        minIndex_1 = reloadMinIndex_1();
        minIndex_2 = reloadMinIndex_2();
        numElemsLocal_1 = reloadNumElemsLocal_1();
        numElemsLocal_2 = reloadNumElemsLocal_2();
    }


    /**
     * Construct a m by n by p block-block distributed DistArray
     * whose data is distributed over Place.places() and 
     * initialized using the provided init closure.
     *
     * @param m number of elements in the first dimension
     * @param n number of elements in the second dimension
     * @param p number of elements in the third dimension
     * @param init the element initialization function
     */
    public def this(m:Long, n:Long, p:Long, init:(Long,Long,Long)=>T) {
        this(m, n, p, Place.places(), init);
    }


    /**
     * Construct a m by n by p block-block distributed DistArray
     * whose data is distributed over pg and zero-initialized.
     *
     * @param m number of elements in the first dimension
     * @param n number of elements in the second dimension
     * @param p number of elements in the third dimension
     * @param pg the PlaceGroup to use to distibute the elements.
     */
    public def this(m:Long, n:Long, p:Long, pg:PlaceGroup{self!=null}){T haszero} {
        this(m, n, p, pg, (Long,Long,Long)=>Zero.get[T]());
    }


    /**
     * Construct a m by n by p block-block distributed DistArray
     * whose data is distributed over Place.places() and 
     * zero-initialized.
     *
     * @param m number of elements in the first dimension
     * @param n number of elements in the second dimension
     * @param p number of elements in the third dimension
     */
    public def this(m:Long, n:Long, p:Long){T haszero} {
        this(m, n, p, Place.places(), (Long,Long,Long)=>Zero.get[T]());
    }


    /**
     * Get an IterationSpace that represents all Points contained in
     * the global iteration space (valid indices) of the DistArray.
     * @return an IterationSpace for the DistArray
     */
    public @Inline final def globalIndices():DenseIterationSpace_3{self!=null} = globalIndices;


    /**
     * Get an IterationSpace that represents all Points contained in
     * the local iteration space (valid indices) of the DistArray at the current Place.
     * @return an IterationSpace for the local portion of the DistArray
     */
    public @Inline final def localIndices():DenseIterationSpace_3{self!=null} = localIndices;


    /**
     * Return the Place which contains the data for the argument
     * index or Place.INVALID_PLACE if the Point is not in the globalIndices
     * of this DistArray
     *
     * @param i the index in the first dimension
     * @param j the index in the second dimension
     * @param k the index in the third dimension
     * @return the Place where (i,j,k) is a valid index in the DistArray; 
     *          will return Place.INVALID_PLACE if (i,j,k) is not contained in globalIndices
     */
    public final def place(i:Long, j:Long, k:Long):Place {
        if (k < 0 || k >= numElems_3) return Place.INVALID_PLACE;
        val tmp = BlockingUtils.mapIndexToBlockBlockPartition(0, 0, numElems_1-1, numElems_2-1, placeGroup.size(), i, j);
	return tmp == -1 ? Place.INVALID_PLACE : placeGroup(tmp);
    }


    /**
     * Return the Place which contains the data for the argument
     * Point or Place.INVALID_PLACE if the Point is not in the globalIndices
     * of this DistArray
     *
     * @param p the Point to lookup
     * @return the Place where p is a valid index in the DistArray; 
     *          will return Place.INVALID_PLACE if p is not contained in globalIndices
     */
    public final def place(p:Point(3)):Place = place(p(0), p(1), p(2));


    /**
     * Return the element of this array corresponding to the given index.
     * 
     * @param i the given index in the first dimension
     * @param j the given index in the second dimension
     * @param k the given index in the third dimension
     * @return the element of this array corresponding to the given index.
     * @see #set(T, Long, Long, Long)
     */
    public final @Inline operator this(i:Long, j:Long, k:Long):T {
        if (CompilerFlags.checkPlace() || CompilerFlags.checkBounds()) validateIndex(i, j, k);
        return Unsafe.uncheckedRailApply(raw, offset(i, j, k));
    }


    /**
     * Return the element of this array corresponding to the given Point.
     * 
     * @param p the given Point
     * @return the element of this array corresponding to the given Point.
     * @see #set(T, Point)
     */
    public final @Inline operator this(p:Point(3)):T  = this(p(0), p(1), p(2));

    
    /**
     * Set the element of this array corresponding to the given index to the given value.
     * Return the new value of the element.
     * 
     * @param v the given value
     * @param i the given index in the first dimension
     * @param j the given index in the second dimension
     * @param k the given index in the third dimension
     * @return the new value of the element of this array corresponding to the given index.
     * @see #operator(Long, Long, Long)
     */
    public final @Inline operator this(i:Long,j:Long,k:Long)=(v:T):T{self==v} {
        if (CompilerFlags.checkPlace() || CompilerFlags.checkBounds()) validateIndex(i,j,k);
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
    public final @Inline operator this(p:Point(3))=(v:T):T{self==v} = this(p(0),p(1),p(2)) = v;


    /*
     * Order of tests is designed to minimize the dynamic number of comparisons
     * on valid access, while still preferring to raise a bounds error rather than
     * a place error when an index is not even contained in the globalIndices of the array.
     */
    protected final def validateIndex(i:Long, j:Long, k:Long) {
        if (CompilerFlags.checkPlace() || CompilerFlags.checkBounds()) {
            if (CompilerFlags.checkBounds() && (k < 0 || k >= numElems_3)) {
                raiseBoundsError(i, j, k);
            }
            if (i < minIndex_1 || i >= (minIndex_1 + numElemsLocal_1) || 
                j < minIndex_2 || j >= (minIndex_2 + numElemsLocal_2)) {
                if (CompilerFlags.checkBounds() && (i < 0 || i >= numElems_1 || j < 0 || j >= numElems_2)) {
                    raiseBoundsError(i, j, k);
                }
                if (CompilerFlags.checkPlace()) raisePlaceError(i,j,k);
            }
        }
    }

    protected final @Inline def offset(i:Long, j:Long, k:Long) {
         return k + numElems_3 * (j - minIndex_2 + numElemsLocal_2 * (i - minIndex_1));
    }

    /**
     * Returns the specified rectangular patch of this array as a Rail.
     * 
     * @param space the IterationSpace representing the portion of this array to copy
     * @see offset
     * @throws ArrayIndexOutOfBoundsException if the specified region is not
     *        contained in this array
     */
    public def getPatch(space:IterationSpace(3){self.rect}):Rail[T] {
        val r = space as DenseIterationSpace_3;

        if (CompilerFlags.checkBounds() &&
          !(localIndices.min0 <= r.min0 && r.max0 <= localIndices.max0
         && localIndices.min1 <= r.min1 && r.max1 <= localIndices.max1
         && localIndices.min2 <= r.min2 && r.max2 <= localIndices.max2)) {
            throw new ArrayIndexOutOfBoundsException("patch to copy: " + r + " not contained in local indices: " + localIndices);
        }

        val patch = Unsafe.allocRailUninitialized[T](r.size());
        var patchIndex:Long = 0;
        for ([i0,i1,i2] in r) {
            patch(patchIndex++) = raw(offset(i0,i1,i2));
        }
        return patch;
    }

    private @Inline static def validateSize(m:Long, n:Long, p:Long):Long {
        if (m < 0 || n < 0 || p < 0) raiseNegativeArraySizeException();
        return m*n*p;
    }
}

// TODO:  Would prefer this to be a protected static nested class, but 
//        when written that way we non-deterministically fail compilation.
class LocalState_BB3[S] extends LocalState[S] {
    val dist:Dist_BlockBlock_3{self!=null};

    def this(pg:PlaceGroup{self!=null}, data:Rail[S]{self!=null}, size:Long, 
             d:Dist_BlockBlock_3{self!=null}) {
        super(pg, data, size);
        dist = d;
    }

    static def make[S](pg:PlaceGroup{self!=null}, m:Long, n:Long, p:Long, init:(Long,Long,Long)=>S):LocalState_BB3[S] {
        val globalSpace = new DenseIterationSpace_3(0, 0, 0, m-1, n-1, p-1);
        val dist = new Dist_BlockBlock_3(pg, globalSpace);

	val data:Rail[S]{self!=null};
	if (dist.localIndices.isEmpty()) { 
            data = new Rail[S]();
        } else {            
            val low1 = dist.localIndices.min(0);
            val hi1 = dist.localIndices.max(0);
            val low2 = dist.localIndices.min(1);
            val hi2 = dist.localIndices.max(1);
            val low3 = 0;
            val hi3 = p-1;
            val localSize1 = hi1 - low1 + 1;
            val localSize2 = hi2 - low2 + 1;
            val localSize3 = p;
            val dataSize = localSize1 * localSize2 * p;
            data = Unsafe.allocRailUninitialized[S](dataSize);
            for (i in low1..hi1) {
                for (j in low2..hi2) {
                    for (k in low3..hi3) {
                        val offset = k + localSize3 * (j - low2 + localSize2 * (i - low1));
                        data(offset) = init(i,j,k);
                    }
                }
            }
        }
        return new LocalState_BB3[S](pg, data, m*n, dist);
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
