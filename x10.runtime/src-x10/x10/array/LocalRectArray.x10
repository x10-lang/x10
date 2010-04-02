/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.array;

import x10.compiler.Inline;
import x10.compiler.Native;

/**
 * This class represents a k-dimensional dense array whose 
 * data is in a single place, which is the same place 
 * as the LocalRectArray's home.</p>
 *
 * Warning: This class is part of an initial prototyping
 *          of the array library redesign targeted for 
 *          completion in X10 2.1.  It's API is not 
 *          likely to be stable from one release to another
 *          until after X10 2.1.</p>
 * 
 * Warning:  Eventually the operations implemented by
 *          this class will match those promised by 
 *          x10.lang.Array, but for now some of the API
 *          is stubbed out here (and may eventually 
 *          by removed from x10.lang.Array).  This 
 *          will be resolved by X10 2.1<p>
 */
public final class LocalRectArray[T] extends Array[T] {

    private global val raw:Rail[T]!; // TODO: Should not be global
    private global val layout:RectLayout!;  // TODO: Should not be global

    @Native("java", "true") // TODO: optimize this for Java as well.
    @Native("c++", "BOUNDS_CHECK_BOOL")
    private native global def checkBounds():boolean;

    // TODO: very short term hack until I re-do x10.lang.Array
    //       so that set/apply are not global.
    // TODO: made public for experimentation in ANU code until proper copyTo is implemented.
    public global @Inline def raw():Rail[T]! = raw as Rail[T]!;
   

    // TODO: This is a hack around the way regions are currently defined.
    //       Even when we compile with NO_CHECKS, we still have to have
    //       the checking code inlined. or the presence of the call in a loop
    //       blows register allocation significantly impacts performance.
    // TODO: Should not be global
    private global val baseRegion:BaseRegion{self.rank==this.rank};

    // TODO: XTENLANG-1188 this should be a const (static) field, but working around C++ backend bug
    // TODO: Should not be global
    private global val bounds = (pt:Point):RuntimeException => new ArrayIndexOutOfBoundsException("point " + pt + " not contained in array");


    /**
     * Construct an uninitialized LocalRectArray over the region reg.
     *
     * @param reg The region over which to construct the array.
     */
    public def this(reg:Region):LocalRectArray[T]{self.dist.region==reg} {
        super(Dist.makeConstant(reg));

        layout = new RectLayout(reg.min(), reg.max());
        val n = layout.size();
        raw = Rail.make[T](n);
        baseRegion = reg as BaseRegion{self.rank==this.rank};
    }


    /**
     * Construct LocalRectArray over the region reg whose
     * values are initialized as specified by the init function.
     *
     * @param reg The region over which to construct the array.
     * @param init The function to use to initialize the array.
     */    
    public def this(reg:Region, init:(Point(reg.rank))=>T):LocalRectArray[T]{self.dist.region==reg} {
        super(Dist.makeConstant(reg));

        layout = new RectLayout(reg.min(), reg.max());
        val n = layout.size();
        val r  = Rail.make[T](n);
	for (p:Point(reg.rank) in reg) {
            r(layout.offset(p))= init(p);
        }
        raw = r;
        baseRegion = reg as BaseRegion{self.rank==this.rank};
    }

    /**
     * Construct LocalRectArray over the region 0..rail.length-1 whose
     * values are initialized to the corresponding values in the 
     * argument Rail.
     *
     */    
    public def this(rail:Rail[T]!):LocalRectArray[T]{self.rank==1,rect,zeroBased} {
        // TODO: could make this more efficient by optimizing rail copy.
	this(Region.makeRectangular(0, rail.length-1), ((i):Point(1)) => rail(i));
    }


    /**
     * Construct LocalRectArray over the region 0..rail.length-1 whose
     * values are initialized to the corresponding values in the 
     * argument ValRail.
     *
     * TODO: rail is declared to be a ValRail[T]! as a hack around
     *       a compiler bug.  Without the !, the front-end complains that you
     *       can't refer to "T" in a static context, which is complete nonsense
     *       since this is a constructor.
     */    
    public def this(rail:ValRail[T]!):LocalRectArray[T]{self.rank==1,rect,zeroBased} {
        // TODO: could make this more efficient by optimizing rail copy.
	this(Region.makeRectangular(0, rail.length-1), ((i):Point(1)) => rail(i));
    }


    /**
     * Construct LocalRectArray over the region 0..rail.length-1 whose
     * values are uninitialized
     */    
    public def this(size:int):LocalRectArray[T]{self.rank==1,rect,zeroBased} {
	this(Region.makeRectangular(0, size-1));
    }


    /**
     * Construct LocalRectArray over the region reg whose
     * values are initialized as specified by the init function.
     *
     * @param reg The region over which to construct the array.
     * @param init The function to use to initialize the array.
     */    
    public def this(size:int, init:(Point(1))=>T):LocalRectArray[T]{self.rank==1,rect,zeroBased} {
	this(Region.makeRectangular(0, size-1), init);
    }







    /**
     * Return an iterator over the points in the region of this array.
     *
     * @return an iterator over the points in the region of this array.
     * @see x10.lang.Iterable[T]#iterator()
     */
    public global def iterator():Iterator[Point(rank)] = region.iterator() as Iterator[Point(rank)];


    /**
     * Return the element of this array corresponding to the given index.
     * Only applies to one-dimensional arrays.
     * Functionally equivalent to indexing the array via a one-dimensional point.
     * 
     * TODO: This method should not be global.
     *
     * @param i0 the given index in the first dimension
     * @return the element of this array corresponding to the given index.
     * @see #apply(Point)
     * @see #set(T, Int)
     */
    public safe global @Inline def apply(i0:int){rank==1}:T {
        if (checkBounds()) baseRegion.check(bounds, i0);
        return raw()(layout.offset(i0));
    }

    /**
     * Return the element of this array corresponding to the given pair of indices.
     * Only applies to two-dimensional arrays.
     * Functionally equivalent to indexing the array via a two-dimensional point.
     * 
     * TODO: This method should not be global.
     *
     * @param i0 the given index in the first dimension
     * @param i1 the given index in the second dimension
     * @return the element of this array corresponding to the given pair of indices.
     * @see #apply(Point)
     * @see #set(T, Int, Int)
     */
    public safe global @Inline def apply(i0:int, i1:int){rank==2}:T {
        if (checkBounds()) baseRegion.check(bounds, i0, i1);
        return raw()(layout.offset(i0,i1));
    }

    /**
     * Return the element of this array corresponding to the given triple of indices.
     * Only applies to three-dimensional arrays.
     * Functionally equivalent to indexing the array via a three-dimensional point.
     * 
     * TODO: This method should not be global.
     *
     * @param i0 the given index in the first dimension
     * @param i1 the given index in the second dimension
     * @param i2 the given index in the third dimension
     * @return the element of this array corresponding to the given triple of indices.
     * @see #apply(Point)
     * @see #set(T, Int, Int, Int)
     */
    public safe global @Inline def apply(i0:int, i1:int, i2:int){rank==3}:T {
        if (checkBounds()) baseRegion.check(bounds, i0, i1, i2);
        return raw()(layout.offset(i0, i1, i2));
    }

    /**
     * Return the element of this array corresponding to the given quartet of indices.
     * Only applies to four-dimensional arrays.
     * Functionally equivalent to indexing the array via a four-dimensional point.
     * 
     * TODO: This method should not be global.
     *
     * @param i0 the given index in the first dimension
     * @param i1 the given index in the second dimension
     * @param i2 the given index in the third dimension
     * @param i3 the given index in the fourth dimension
     * @return the element of this array corresponding to the given quartet of indices.
     * @see #apply(Point)
     * @see #set(T, Int, Int, Int, Int)
     */
    public safe global @Inline def apply(i0:int, i1:int, i2:int, i3:int){rank==4}:T {
        if (checkBounds()) baseRegion.check(bounds, i0, i1, i2, i3);
        return raw()(layout.offset(i0, i1, i2, i3));
    }

    /**
     * Return the element of this array corresponding to the given point.
     * The rank of the given point has to be the same as the rank of this array.
     * 
     * TODO: This method should not be global.
     *
     * @param pt the given point
     * @return the element of this array corresponding to the given point.
     * @see #apply(Int)
     * @see #set(T, Point)
     */
    public safe global @Inline def apply(pt:Point{self.rank==this.rank}):T {
        if (checkBounds()) {
            throw new UnsupportedOperationException("Haven't implemented bounds checking for general Points on LocalRectArray");
            // TODO: SHOULD BE: region.check(pt);
        }
        return raw()(layout.offset(pt));
    }

 
    /**
     * Set the element of this array corresponding to the given index to the given value.
     * Return the new value of the element.
     * Only applies to one-dimensional arrays.
     * Functionally equivalent to setting the array via a one-dimensional point.
     * 
     * TODO: This method should not be global.
     *
     * @param v the given value
     * @param i0 the given index in the first dimension
     * @return the new value of the element of this array corresponding to the given index.
     * @see #apply(Int)
     * @see #set(T, Point)
     */
    public safe global @Inline def set(v:T, i0:int){rank==1}:T {
        if (checkBounds()) baseRegion.check(bounds, i0);
        raw()(layout.offset(i0)) = v;
        return v;
    }

    /**
     * Set the element of this array corresponding to the given pair of indices to the given value.
     * Return the new value of the element.
     * Only applies to two-dimensional arrays.
     * Functionally equivalent to setting the array via a two-dimensional point.
     * 
     * TODO: This method should not be global.
     *
     * @param v the given value
     * @param i0 the given index in the first dimension
     * @param i1 the given index in the second dimension
     * @return the new value of the element of this array corresponding to the given pair of indices.
     * @see #apply(Int, Int)
     * @see #set(T, Point)
     */
    public safe global @Inline def set(v:T, i0:int, i1:int){rank==2}:T {
        if (checkBounds()) baseRegion.check(bounds, i0, i1);
        raw()(layout.offset(i0,i1)) = v;
        return v;
    }

    /**
     * Set the element of this array corresponding to the given triple of indices to the given value.
     * Return the new value of the element.
     * Only applies to three-dimensional arrays.
     * Functionally equivalent to setting the array via a three-dimensional point.
     * 
     * TODO: This method should not be global.
     *
     * @param v the given value
     * @param i0 the given index in the first dimension
     * @param i1 the given index in the second dimension
     * @param i2 the given index in the third dimension
     * @return the new value of the element of this array corresponding to the given triple of indices.
     * @see #apply(Int, Int, Int)
     * @see #set(T, Point)
     */
    public safe global @Inline def set(v:T, i0:int, i1:int, i2:int){rank==3}:T {
        if (checkBounds()) baseRegion.check(bounds, i0, i1, i2);
        raw()(layout.offset(i0, i1, i2)) = v;
        return v;
    }

    /**
     * Set the element of this array corresponding to the given quartet of indices to the given value.
     * Return the new value of the element.
     * Only applies to four-dimensional arrays.
     * Functionally equivalent to setting the array via a four-dimensional point.
     * 
     * TODO: This method should not be global.
     *
     * @param v the given value
     * @param i0 the given index in the first dimension
     * @param i1 the given index in the second dimension
     * @param i2 the given index in the third dimension
     * @param i3 the given index in the fourth dimension
     * @return the new value of the element of this array corresponding to the given quartet of indices.
     * @see #apply(Int, Int, Int, Int)
     * @see #set(T, Point)
     */
    public safe global @Inline def set(v:T, i0:int, i1:int, i2:int, i3:int){rank==4}:T {
        if (checkBounds()) baseRegion.check(bounds, i0, i1, i2, i3);
        raw()(layout.offset(i0, i1, i2, i3)) = v;
        return v;
    }

    /**
     * Set the element of this array corresponding to the given point to the given value.
     * Return the new value of the element.
     * The rank of the given point has to be the same as the rank of this array.
     * 
     * TODO: This method should not be global.
     *
     * @param v the given value
     * @param pt the given point
     * @return the new value of the element of this array corresponding to the given point.
     * @see #apply(Point)
     * @see #set(T, Int)
     */
    public safe global @Inline def set(v:T, p:Point{self.rank==this.rank}):T {
        if (checkBounds()) {
            throw new UnsupportedOperationException("Haven't implemented bounds checking for general Points on LocalRectArray");
            // TODO: SHOULD BE: region.check(p);
        }
        raw()(layout.offset(p)) = v;
        return v;
    }

    /*
     * TODO: Cruft inherited from Array but not yet implemented.
     *       Some of this will get implemented, other parts will
     *       get removed from Array.
     */

    public incomplete safe global def restriction(r: Region(rank)): Array[T](rank);    

    public incomplete safe global def restriction(p: Place): Array[T](rank);

    public incomplete safe global operator this | (r: Region(rank)): Array[T](rank);

    public incomplete safe global operator this | (p: Place): Array[T](rank);

    public incomplete global def lift(op:(T)=>T): Array[T](dist);

    public incomplete global def reduce(op:(T,T)=>T, unit:T): T;

    public incomplete global def scan(op:(T,T)=>T, unit:T): Array[T](dist);
}
