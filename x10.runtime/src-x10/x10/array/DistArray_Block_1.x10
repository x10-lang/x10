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
 * Implementation of a 1-D DistArray that distributes its data elements
 * over the places in its PlaceGroup in a 1-D blocked fashion.
 */
public class DistArray_Block_1[T] extends DistArray[T]{this.rank()==1} implements (Long)=>T {
    
    public property rank() = 1;

    protected val globalIndices:DenseIterationSpace_1{self!=null};

    @TransientInitExpr(reloadLocalIndices())
    protected transient val localIndices:DenseIterationSpace_1{self!=null};
    @NonEscaping protected final def reloadLocalIndices():DenseIterationSpace_1{self!=null} {
        val ls = localHandle() as LocalState_B1[T];
        return ls != null ? ls.localIndices : new DenseIterationSpace_1(0,-1);
    }
    
    @TransientInitExpr(reloadMinLocalIndex())
    protected transient val minLocalIndex:Long;
    @NonEscaping protected final def reloadMinLocalIndex() = localIndices.min(0);
 
    @TransientInitExpr(reloadMaxLocalIndex())
    protected transient val maxLocalIndex:Long;
    @NonEscaping protected final def reloadMaxLocalIndex() = localIndices.max(0);

    /**
     * Construct a n-element block distributed DistArray
     * whose data is distrbuted over pg and initialized using
     * the init function.
     *
     * @param n number of elements 
     * @param pg the PlaceGroup to use to distibute the elements.
     * @param init the element initialization function
     */
    public def this(n:Long, pg:PlaceGroup{self!=null}, init:(Long)=>T) {
        super(pg, () => LocalState_B1.make[T](pg, n, init), validateSize(n));
        globalIndices = new DenseIterationSpace_1(0, n-1);
        localIndices = reloadLocalIndices();
        minLocalIndex = reloadMinLocalIndex();
        maxLocalIndex = reloadMaxLocalIndex();
    }


    /**
     * Construct a n-element block distributed DistArray
     * whose data is distrbuted over Place.places() and 
     * initialized using the provided init closure.
     *
     * @param n number of elements
     * @param init the element initialization function
     */
    public def this(n:Long, init:(Long)=>T) {
        this(n, Place.places(), init);
    }


    /**
     * Construct a n-elmenent block distributed DistArray
     * whose data is distrbuted over pg and zero-initialized.
     *
     * @param n number of elements 
     * @param pg the PlaceGroup to use to distibute the elements.
     */
    public def this(n:Long, pg:PlaceGroup{self!=null}){T haszero} {
        this(n, pg, (Long)=>Zero.get[T]());
    }


    /**
     * Construct a n-element block distributed DistArray
     * whose data is distrbuted over Place.places() and 
     * zero-initialized.
     *
     * @param n number of elements
     */
    public def this(n:Long){T haszero} {
        this(n, Place.places(), (Long)=>Zero.get[T]());
    }


    /**
     * Get an IterationSpace that represents all Points contained in
     * the global iteration space (valid indices) of the DistArray.
     * @return an IterationSpace for the DistArray
     */
    public @Inline final def globalIndices():DenseIterationSpace_1{self!=null} = globalIndices;


    /**
     * Get an IterationSpace that represents all Points contained in
     * the local iteration space (valid indices) of the DistArray at the current Place.
     * @return an IterationSpace for the local portion of the DistArray
     */
    public @Inline final def localIndices():DenseIterationSpace_1{self!=null} = localIndices;


    /**
     * Return the Place which contains the data for the argument
     * index or Place.INVALID_PLACE if the Point is not in the globalIndices
     * of this DistArray
     *
     * @param i the index 
     * @return the Place where i is a valid index in the DistArray; 
     *          will return Place.INVALID_PLACE if i is not contained in globalIndices
     */
    public final def place(i:Long):Place {
        val tmp = BlockingUtils.mapIndexToBlockPartition(globalIndices, placeGroup.size(), i);
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
    public final def place(p:Point(1)):Place = place(p(0));


    /**
     * Return the element of this array corresponding to the given index.
     * 
     * @param i the given index
     * @return the element of this array corresponding to the given index.
     * @see #set(T, Long)
     */
    public final @Inline operator this(i:Long):T {
        if (CompilerFlags.checkPlace() || CompilerFlags.checkBounds()) {
            if (i < minLocalIndex || i > maxLocalIndex) {
                if (CompilerFlags.checkBounds() && (i < 0 || i >= size)) raiseBoundsError(i);
                if (CompilerFlags.checkPlace()) raisePlaceError(i);
            }
        }
        return Unsafe.uncheckedRailApply(raw, i-minLocalIndex);
    }


    /**
     * Return the element of this array corresponding to the given Point.
     * 
     * @param p the given Point
     * @return the element of this array corresponding to the given Point.
     * @see #set(T, Point)
     */
    public final @Inline operator this(p:Point(1)):T  = this(p(0));

    
    /**
     * Set the element of this array corresponding to the given index to the given value.
     * Return the new value of the element.
     * 
     * @param v the given value
     * @param i the given index 
     * @return the new value of the element of this array corresponding to the given index.
     * @see #operator(Long)
     */
    public final @Inline operator this(i:Long)=(v:T):T{self==v} {
        if (CompilerFlags.checkPlace() || CompilerFlags.checkBounds()) {
            if (i < minLocalIndex || i > maxLocalIndex) {
                if (CompilerFlags.checkBounds() && (i < 0 || i >= size)) raiseBoundsError(i);
                if (CompilerFlags.checkPlace()) raisePlaceError(i);
            }
        }
        Unsafe.uncheckedRailSet(raw, i - minLocalIndex, v);
        return v;
    }


    /**
     * Set the element of this array corresponding to the given Point to the given value.
     * Return the new value of the element.
     * 
     * @param v the given value
     * @param p the given Point
     * @return the new value of the element of this array corresponding to the given Point.
     * @see #operator(Long)
     */
    public final @Inline operator this(p:Point(1))=(v:T):T{self==v} = this(p(0)) = v;


    private @Inline static def validateSize(n:Long):Long {
        if (n < 0) raiseNegativeArraySizeException();
        return n;
    }
}


// TODO:  Would prefer this to be a protected static nested class, but 
//        when written that way we non-deterministically fail compilation.
class LocalState_B1[S] extends LocalState[S] {
    val globalIndices:DenseIterationSpace_1{self!=null};
    val localIndices:DenseIterationSpace_1{self!=null};

    def this(pg:PlaceGroup{self!=null}, data:Rail[S]{self!=null}, size:Long, 
             gs:DenseIterationSpace_1{self!=null}, ls:DenseIterationSpace_1{self!=null}) {
        super(pg, data, size);
        globalIndices = gs;
        localIndices = ls;
    }

    static def make[S](pg:PlaceGroup{self!=null}, n:Long, init:(Long)=>S):LocalState_B1[S] {
        val globalSpace = new DenseIterationSpace_1(0, n-1);
        val localSpace = BlockingUtils.partitionBlock(globalSpace, pg.numPlaces(), pg.indexOf(here));

	val data:Rail[S]{self!=null};
	if (localSpace.isEmpty()) { 
            data = new Rail[S]();
        } else {            
            val low = localSpace.min(0);
            val hi = localSpace.max(0);
            val dataSize = hi - low + 1;
            data = Unsafe.allocRailUninitialized[S](dataSize);
            for (i in low..hi) {
                val offset = i - low;
                data(offset) = init(i);
            }
        }
        return new LocalState_B1[S](pg, data, n, globalSpace, localSpace);
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
