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
import x10.compiler.NoInline;
import x10.compiler.NoReturn;
import x10.compiler.NonEscaping;
import x10.compiler.TransientInitExpr;
import x10.util.RailUtils;

/**
 * <p> This class hierarchy provides high-performance implementations of
 * distributed multi-dimensional Arrays that map long indices to values. 
 * This array implementation only supports zero-based, dense indexing.
 * For example, a two dimensional array of N by M elements is indexed by 
 * (0..N-1,0..M-1).  Different subclasses of this class implement different
 * algorithms for distibuting the elements of the DistArray across the 
 * Places of the X10 computation.</p>
 * 
 * <p> Related classes in this package {@link Array} provide a similar
 * simple array abstraction whose data is contained in a sinple Place.</p>
 * 
 * <p>A more general, but lower performance, distributed array abstraction is
 *  provided by {@link x10.regionarray.DistArray} which supports more 
 * general indexing operations via user-extensible 
 * {@link x10.regionarray.Region}s and {@link x10.regionarray.Dist}s. 
 * See the package documentation of {@link x10.regionarray} for more details.</p>
 */
public abstract class DistArray[T] (
        /**
         * The number of data values in the array.
         */
        size:Long
) implements Iterable[Point(this.rank())] {

    /**
     * @return the rank (dimensionality) of the DistArray
     */
    public abstract property rank():Long;
 
    /** 
     * The place-local state for the DistArray 
     */
    protected val localHandle:PlaceLocalHandle[LocalState[T]];

    /**
     * The place-local backing storage for elements of the DistArray.
     */
    @TransientInitExpr(getRailFromLocalHandle())
    protected transient val raw:Rail[T]{self!=null};
    @NonEscaping protected final def getRailFromLocalHandle():Rail[T]{self!=null} {
      val ls = localHandle();
      return ls != null ? ls.rail : new Rail[T]();
    }

    /**
     * The PlaceGroup over which this DistArray is defined.
     */
    protected val placeGroup:PlaceGroup;

    /**
     * Construct the DistArray using the argument PlaceGroup
     * and initialization closure to create the LocalState of
     * the DistArray in every Place in the PlaceGroup.
     * @param pg the PlaceGroup over which the DistArray is defined.
     * @param init the closure to pass to PlaceLocalHandle.make
     */
    protected def this(pg:PlaceGroup, init:()=>LocalState[T], sz:Long) {
        property(sz);
        val plh = PlaceLocalHandle.makeFlat[LocalState[T]](pg, init);
        val ls = plh();
        localHandle = plh;
        placeGroup = pg;
	raw = getRailFromLocalHandle();
    }

    /**
     * <p>Return the Rail[T] that is providing the backing storage for the array.
     * This method is primarily intended to be used to interface with native libraries 
     * (eg BLAS, ESSL) and to support bulk copy operations using the asyncCopy 
     * methods of Rail.</p>
     * 
     * This method should be used sparingly, since it may make client code dependent on the layout
     * algorithm used to map the local portion of the rank-dimensional array indicies to 
     * the 1-dimensional indicies in the backing Rail.
     * 
     * @return the Rail[T] that is the backing storage for the DistArray object in this Place.
     */
    public @Inline final def raw():Rail[T]{self!=null} = raw;

    /**
     * The PlaceGroup over which the DistArray is defined
     */
    public final def placeGroup():PlaceGroup = placeGroup;

    /**
     * Get an IterationSpace that represents all Points contained in
     * the global iteration space (valid indices) of the DistArray.
     * @return an IterationSpace for the DistArray
     */
    public abstract def globalIndices():IterationSpace{self.rank==this.rank(),self.rect,self!=null};

    /**
     * Define default iteration to be over globalIndices to support
     * idiomatic usage in <code>ateach</code>
     */
    public def iterator():Iterator[Point(this.rank())] = globalIndices().iterator();

    /**
     * Get an IterationSpace that represents all Points contained in
     * the local iteration space (valid indices) of the DistArray at the current Place.
     * @return an IterationSpace for the Array
     */
    public abstract def localIndices():IterationSpace{self.rank==this.rank(),self!=null};

    /**
     * Return the Place which contains the data for the argument
     * Point or Place.INVALID_PLACE if the Point is not in the globalIndices
     * of this DistArray
     *
     * @param p the Point to lookup
     * @return the Place where p is a valid index in the DistArray; 
     *          will return Place.INVALID_PLACE if p is not contained in globalIndices
     */
    public abstract def place(p:Point(this.rank())):Place;

    /**
     * Return the element of this array corresponding to the given point.
     * The rank of the given point has to be the same as the rank of this array.
     * If the distribution does not map the given Point to the current place,
     * then a BadPlaceException will be raised.
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
     * If the distribution does not map the given Point to the current place,
     * then a BadPlaceException will be raised.
     * 
     * @param v the given value
     * @param p the given point
     * @return the new value of the element of this array corresponding to the given point.
     * @see #operator(Point)
     */
    public abstract operator this(p:Point(this.rank()))=(v:T):T{self==v};

    /**
     * Returns the specified rectangular patch of this array as a Rail.
     * 
     * @param space the IterationSpace representing the portion of this array to copy
     * @throws ArrayIndexOutOfBoundsException if the specified region is not
     *        contained in this array
     */
    public abstract def getPatch(space:IterationSpace(this.rank){rect}):Rail[T];

    /**
     * Reduce this array using the given function and the given initial value.
     * Each element of the array will be given as an argument to the reduction
     * function exactly once, but in an arbitrary order.  The reduction function
     * may be applied concurrently to implement a parallel reduction.
     * 
     * @param op the reduction function
     * @param unit the given initial value
     * @return the final result of the reduction.
     * @see #reduce((U,T)=>U,(U,U)=>U,U)
     */
    public final def reduce(op:(T,T)=>T, unit:T):T  = reduce[T](op, op, unit);
    
    /**
     * Reduce this array using the given function and the given initial value.
     * Each element of the array will be given as an argument to the reduction
     * function exactly once, but in an arbitrary order.  The reduction function
     * may be applied concurrently to implement a parallel reduction.
     * 
     * @param lop the local reduction function
     * @param gop the global reduction function
     * @param unit the given initial value
     * @return the final result of the reduction.
     */
    public final def reduce[U](lop:(U,T)=>U, gop:(U,U)=>U, unit:U):U {
        val reducer = new Reducible[U]() {
        	public def zero():U = unit;
        	public operator this(a:U, b:U):U = gop(a,b);
        };

        val result = finish(reducer) {
            for (where in placeGroup) {
                at (where) async {
                    val localRes:U = RailUtils.reduce(raw(), lop, unit);
                    offer(localRes);
                }
            }
        };

        return result;
    }

    /**
     * Map the given function onto the elements of this array
     * storing the results in the dst array. For maximum flexibility
     * of use, map does not require that the src and destination array
     * have the same dimesionality, rank or distribution, only that they have the same
     * number of elements as every Place.  
     * When applied to arrays that use the same IterationSpace,
     * the result will be that for all <code>pt</code> in the IterationSpace
     * </code> dst(pt) == op(src(pt)) </code>.  When applied to arrays that use
     * a different iteration space, the mapping from src to dst is defined in
     * terms of the index of the backing rails, that is <code>dst.raw()(i) = op(src.raw()(i))</code>
     * for i in <code>0..(src.size()-1)</code>.
     * 
     * @param dst the destination array for the results of the map operation
     * @param op the function to apply to each element of the array
     * @return dst after updating its contents to contain the result of the map operation.
     */
    public @Inline final def map[U](dst:DistArray[U], op:(T)=>U):DistArray[U]{self==dst} {
        placeGroup.broadcastFlat(()=> {
            val s = this.raw();
            val d = dst.raw();
            RailUtils.map(s, d, op);
        });
        return dst;
    }

    /**
     * Map the given function onto the elements of this array
     * and the argument src array storing the results in the dst array. 
     * For maximum flexibility of use, map does not require that the three arrays
     * have the same dimesionality, rank or distribution, only that they have the same
     * number of elements at every Place.  
     * When applied to arrays that use the same IterationSpace,
     * the result will be that for all <code>pt</code> in the IterationSpace
     * </code> dst(pt) == op(this(pt), src(pt)) </code>.  When applied to arrays that use
     * a different iteration space, the mapping from src to dst is defined in
     * terms of the index of the backing rails, that is 
     * <code>dst.raw()(i) = op(this.raw()(i), src.raw()(i))</code>
     * for i in <code>0..(src.size()-1)</code>.
     * 
     * @param src2 the second source array to use as input to the map function
     * @param dst the destination array for the results of the map operation
     * @param op the function to apply to each element of the arrays
     * @return dst after updating its contents to contain the result of the map operation.
     */
    public @Inline final def map[S,U](src2:DistArray[S], dst:DistArray[U], op:(T,S)=>U):DistArray[U]{self==dst} {
        placeGroup.broadcastFlat(()=> {
            val s1 = this.raw();
            val s2 = src2.raw();
            val d = dst.raw();
            if (s1.size != s2.size) {
                throw new IllegalArgumentException("Source arrays have different sizes ("+s1.size+", "+s2.size+") at "+here);
            }
            RailUtils.map(s1, s2 as Rail[S]{self.size==s1.size}, d, op);
        });
        return dst;
    }

    protected static @NoInline @NoReturn def raiseBoundsError(i:Long) {
        throw new ArrayIndexOutOfBoundsException("(" + i + ") not contained in array");
    }    
    protected static @NoInline @NoReturn def raiseBoundsError(i:Long, j:Long) {
        throw new ArrayIndexOutOfBoundsException("(" + i + ", "+j+") not contained in array");
    }    
    protected static @NoInline @NoReturn def raiseBoundsError(i:Long, j:Long, k:Long) {
        throw new ArrayIndexOutOfBoundsException("(" + i + ", "+j+", "+k+") not contained in array");
    }    

    protected static @NoInline @NoReturn def raisePlaceError(i:Long) {
        throw new BadPlaceException("point (" + i + ") not defined at " + here);
    }    
    protected static @NoInline @NoReturn def raisePlaceError(i:Long, j:Long) {
        throw new BadPlaceException("point (" + i + ", "+ j +") not defined at " + here);
    }    
    protected static @NoInline @NoReturn def raisePlaceError(i:Long, j:Long, k:Long) {
        throw new BadPlaceException("point (" + i + ", "+ j +", "+ k +") not defined at " + here);
    }    

    protected static @NoInline @NoReturn def raiseNegativeArraySizeException() {
        throw new NegativeArraySizeException();
    }    
}

// TODO:  Would prefer this to be a protected static nested class, but 
//        when written that way we non-deterministically fail compilation.
class LocalState[S](pg:PlaceGroup{self!=null},rail:Rail[S]{self!=null},size:Long) {
    def this(pg:PlaceGroup{self!=null},rail:Rail[S]{self!=null}, size:Long) {
        property(pg, rail, size);
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
