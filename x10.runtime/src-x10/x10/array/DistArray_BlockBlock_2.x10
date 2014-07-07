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

import x10.compiler.Inline;
import x10.compiler.CompilerFlags;
import x10.compiler.NonEscaping;
import x10.compiler.TransientInitExpr;

/**
 * Implementation of a 2-D DistArray that distributes its data elements
 * over the places in its PlaceGroup in a 2-D blocked fashion.
 */
public class DistArray_BlockBlock_2[T] extends DistArray[T]{this.rank()==2} implements (Long,Long)=>T {
    
    public property rank() = 2;

    protected val globalIndices:DenseIterationSpace_2{self!=null};

    protected val numElems_1:Long;

    protected val numElems_2:Long;

    @TransientInitExpr(reloadLocalIndices())
    protected transient val localIndices:DenseIterationSpace_2{self!=null};
    @NonEscaping
    protected final def reloadLocalIndices():DenseIterationSpace_2{self!=null} {
        val ls = localHandle() as LocalState_BB2[T];
        return ls != null ? ls.localIndices : new DenseIterationSpace_2(0,-1, 0, -1);
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
     * Construct a m by n block-block distributed DistArray
     * whose data is distrbuted over pg and initialized using
     * the init function.
     *
     * @param m number of elements in the first dimension
     * @param n number of elements in the second dimension
     * @param pg the PlaceGroup to use to distibute the elements.
     * @param init the element initialization function
     */
    public def this(m:Long, n:Long, pg:PlaceGroup{self!=null}, init:(Long,Long)=>T) {
        super(pg, () => LocalState_BB2.make[T](pg, m, n, init), validateSize(m,n));
        globalIndices = new DenseIterationSpace_2(0, 0, m-1, n-1);
        numElems_1 = globalIndices.max(0) - globalIndices.min(0) + 1;
        numElems_2 = globalIndices.max(1) - globalIndices.min(1) + 1;
        localIndices = reloadLocalIndices();
        minIndex_1 = reloadMinIndex_1();
        minIndex_2 = reloadMinIndex_2();
        numElemsLocal_1 = reloadNumElemsLocal_1();
        numElemsLocal_2 = reloadNumElemsLocal_2();
    }


    /**
     * Construct a m by n block-block distributed DistArray
     * whose data is distrbuted over Place.places() and 
     * initialized using the provided init closure.
     *
     * @param m number of elements in the first dimension
     * @param n number of elements in the second dimension
     * @param init the element initialization function
     */
    public def this(m:Long, n:Long, init:(Long,Long)=>T) {
        this(m, n, Place.places(), init);
    }


    /**
     * Construct a m by n block-block distributed DistArray
     * whose data is distrbuted over pg and zero-initialized.
     *
     * @param m number of elements in the first dimension
     * @param n number of elements in the second dimension
     * @param pg the PlaceGroup to use to distibute the elements.
     */
    public def this(m:Long, n:Long, pg:PlaceGroup{self!=null}){T haszero} {
        this(m, n, pg, (Long,Long)=>Zero.get[T]());
    }


    /**
     * Construct a m by n block-block distributed DistArray
     * whose data is distrbuted over Place.places() and 
     * zero-initialized.
     *
     * @param m number of elements in the first dimension
     * @param n number of elements in the second dimension
     */
    public def this(m:Long, n:Long){T haszero} {
        this(m, n, Place.places(), (Long,Long)=>Zero.get[T]());
    }


    /**
     * Get an IterationSpace that represents all Points contained in
     * the global iteration space (valid indices) of the DistArray.
     * @return an IterationSpace for the DistArray
     */
    public @Inline final def globalIndices():DenseIterationSpace_2{self!=null} = globalIndices;


    /**
     * Get an IterationSpace that represents all Points contained in
     * the local iteration space (valid indices) of the DistArray at the current Place.
     * @return an IterationSpace for the local portion of the DistArray
     */
    public @Inline final def localIndices():DenseIterationSpace_2{self!=null} = localIndices;


    /**
     * Return the Place which contains the data for the argument
     * index or Place.INVALID_PLACE if the Point is not in the globalIndices
     * of this DistArray
     *
     * @param i the index in the first dimension
     * @param j the index in the second dimension
     * @return the Place where (i,j) is a valid index in the DistArray; 
     *          will return Place.INVALID_PLACE if (i,j) is not contained in globalIndices
     */
    public final def place(i:Long, j:Long):Place {
        val tmp = BlockingUtils.mapIndexToBlockBlockPartition(globalIndices, placeGroup.size(), i, j);
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
    public final def place(p:Point(2)):Place = place(p(0), p(1));


    /**
     * Return the element of this array corresponding to the given index.
     * 
     * @param i the given index in the first dimension
     * @param j the given index in the second dimension
     * @return the element of this array corresponding to the given index.
     * @see #set(T, Long, Long)
     */
    public final @Inline operator this(i:Long, j:Long):T {
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
    public final @Inline operator this(p:Point(2)):T  = this(p(0), p(1));

    
    /**
     * Set the element of this array corresponding to the given index to the given value.
     * Return the new value of the element.
     * 
     * @param v the given value
     * @param i the given index in the first dimension
     * @param j the given index in the second dimension
     * @return the new value of the element of this array corresponding to the given index.
     * @see #operator(Long, Long)
     */
    public final @Inline operator this(i:Long,j:Long)=(v:T):T{self==v} {
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
     * @see #operator(Point)
     */
    public final @Inline operator this(p:Point(2))=(v:T):T{self==v} = this(p(0),p(1)) = v;


    /*
     * Order of tests is designed to minimize the dynamic number of comparisons
     * on valid access, while still preferring to raise a bounds error rather than
     * a place error when an index is not even contained in the globalIndices of the array.
     */
    protected final def validateIndex(i:Long, j:Long) {
        if (CompilerFlags.checkPlace() || CompilerFlags.checkBounds()) {
            if (i < minIndex_1 || i >= (minIndex_1 + numElemsLocal_1) || 
                j < minIndex_2 || j >= (minIndex_2 + numElemsLocal_2)) {
                if (CompilerFlags.checkBounds() && (i < 0 || i >= numElems_1 || j < 0 || j >= numElems_2)) {
                    raiseBoundsError(i, j);
                }
                if (CompilerFlags.checkPlace()) raisePlaceError(i,j);
            }
        }
    }

    protected final @Inline def offset(i:Long, j:Long) {
         return (j - minIndex_2) + ((i - minIndex_1) * numElemsLocal_2);
    }

    private @Inline static def validateSize(m:Long, n:Long):Long {
        if (m < 0 || n < 0) raiseNegativeArraySizeException();
        return m*n;
    }
}

// TODO:  Would prefer this to be a protected static nested class, but 
//        when written that way we non-deterministically fail compilation.
class LocalState_BB2[S] extends LocalState[S] {
    val globalIndices:DenseIterationSpace_2{self!=null};
    val localIndices:DenseIterationSpace_2{self!=null};

    def this(pg:PlaceGroup{self!=null}, data:Rail[S]{self!=null}, size:Long, 
             gs:DenseIterationSpace_2{self!=null}, ls:DenseIterationSpace_2{self!=null}) {
        super(pg, data, size);
        globalIndices = gs;
        localIndices = ls;
    }

    static def make[S](pg:PlaceGroup{self!=null}, m:Long, n:Long, init:(Long,Long)=>S):LocalState_BB2[S] {
        val globalSpace = new DenseIterationSpace_2(0, 0, m-1, n-1);
        val localSpace = BlockingUtils.partitionBlockBlock(globalSpace, pg.numPlaces(), pg.indexOf(here));
	val data:Rail[S]{self!=null};
	if (localSpace.isEmpty()) { 
            data = new Rail[S]();
        } else {            
            val low1 = localSpace.min(0);
            val hi1 = localSpace.max(0);
            val low2 = localSpace.min(1);
            val hi2 = localSpace.max(1);
            val localSize1 = hi1 - low1 + 1;
            val localSize2 = hi2 - low2 + 1;
            val dataSize = localSize1 * localSize2;
            data = Unsafe.allocRailUninitialized[S](dataSize);
            for (i in low1..hi1) {
                for (j in low2..hi2) {
                    val offset = j - low2 + ((i - low1) * localSize2);
                    data(offset) = init(i,j);
                }
            }
        }
        return new LocalState_BB2[S](pg, data, m*n, globalSpace, localSpace);
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
