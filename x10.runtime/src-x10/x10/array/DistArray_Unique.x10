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

/**
 * Implementation of DistArray that has exactly one element at each 
 * place in its PlaceGroup such that element i is stored at the
 * ith place as computed by the {@link x10.lang.PlaceGroup#indexOf(Place)}.
 */
public final class DistArray_Unique[T] extends DistArray[T]{this.rank()==1} implements (Long)=>T {
    
    public property rank() = 1;

    /**
     * Construct a zero-initialized DistArray_Unique object over Place.places()
     */
    public def this(){T haszero} {
        this(Place.places());
    }

    /**
     * Construct a zero-initialized DistArray_Unique object over the given PlaceGroup
     */
    public def this(pg:PlaceGroup{self!=null}){T haszero} {
        super(pg, () => new LocalState[T](pg, new Rail[T](1), pg.size()), pg.size());
    }


    /**
     * Construct a DistArray_Unique object over Place.places() using the
     * given initialization function.
     */
    public def this(init:()=>T) {
        this(Place.places(), init);
    }

    /**
     * Construct a DistArray_Unique object using the given PlaceGroup and
     * initialization function.
     */
    public def this(pg:PlaceGroup{self!=null}, init:()=>T) {
        super(pg, () => new LocalState[T](pg, new Rail[T](1, init()), pg.size()), pg.size());
    }

    /**
     * Get an IterationSpace that represents all Points contained in
     * the global iteration space (valid indices) of the DistArray.
     * @return an IterationSpace for the DistArray
     */
    public def globalIndices():DenseIterationSpace_1{self!=null} {
        return new DenseIterationSpace_1(0, placeGroup.size()-1);
    }

    /**
     * Get an IterationSpace that represents all Points contained in
     * the local iteration space (valid indices) of the DistArray at the current Place.
     * @return an IterationSpace for the Array
     */
    public def localIndices():DenseIterationSpace_1{self!=null} {
        val idx = placeGroup.indexOf(here);
        return new DenseIterationSpace_1(idx, idx);
    }

    /**
     * Return the Place which contains the data for the argument
     * index or Place.INVALID_PLACE if the Point is not in the globalIndices
     * of this DistArray
     *
     * @param i the index to lookup
     * @return the Place where i is a valid index in the DistArray; 
     *          will return Place.INVALID_PLACE if i is not contained in globalIndices
     */
    public def place(i:Long):Place = (i>=0 && i<placeGroup.size()) ? placeGroup(i) : Place.INVALID_PLACE;


    /**
     * Return the Place which contains the data for the argument
     * Point or Place.INVALID_PLACE if the Point is not in the globalIndices
     * of this DistArray
     *
     * @param p the Point to lookup
     * @return the Place where p is a valid index in the DistArray; 
     *          will return Place.INVALID_PLACE if p is not contained in globalIndices
     */
    public def place(p:Point(this.rank())):Place = place(p(0));


    /**
     * Return the element of this array corresponding to the given index.
     * 
     * @param i the given index in the first dimension
     * @return the element of this array corresponding to the given index.
     * @see #set(T, Long)
     */
    public @Inline operator this(i:Long):T {
        if (CompilerFlags.checkPlace() || CompilerFlags.checkBounds()) validateIndex(i);
        return raw(0);
    }

    /**
     * Return the element of this array corresponding to the given Point.
     * 
     * @param p the given Point
     * @return the element of this array corresponding to the given Point.
     * @see #set(T, Point)
     */
    public @Inline operator this(p:Point(this.rank())):T  = this(p(0));

    
    /**
     * Set the element of this array corresponding to the given index to the given value.
     * Return the new value of the element.
     * 
     * @param v the given value
     * @param i the given index in the first dimension
     * @return the new value of the element of this array corresponding to the given index.
     * @see #operator(Long)
     */
    public @Inline operator this(i:Long)=(v:T):T{self==v} {
        if (CompilerFlags.checkPlace() || CompilerFlags.checkBounds()) validateIndex(i);
	raw(0) = v;
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
    public @Inline operator this(p:Point(this.rank()))=(v:T):T{self==v} = this(p(0)) = v;

    public def getPatch(space:IterationSpace(1){self.rect}):Rail[T] {
        throw new UnsupportedOperationException("getPatch not supported for DistArray_Unique");
    }

    /*
     * Order of tests is designed to minimize the dynamic number of comparisons
     * on valid access, while still preferring to raise a bounds error rather than
     * a place error when an index is not even contained in the globalIndices of the array.
     */
    private @Inline def validateIndex(i:Long) {
        if (CompilerFlags.checkBounds() || CompilerFlags.checkPlace()) {
            if (placeGroup.indexOf(here) != i) { 
                if (CompilerFlags.checkBounds() && (i < 0 || i >= placeGroup.size())) raiseBoundsError(i);
                if (CompilerFlags.checkPlace()) raisePlaceError(i);
            }
        }
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
