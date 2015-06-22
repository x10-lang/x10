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

package x10.regionarray;

import x10.compiler.Inline;

/**
 * A Region(rank) represents a set of points of class Point(rank). The
 * Region class defines a set of static factory methods for
 * constructing regions. There are properties and methods for
 * accessing basic information about of a region, such as its bounding
 * box, its size, whether or not it is convex, whether or not it is
 * empty. There are a set of methods for supporting algebraic
 * operations on regions, such as intersection, union, difference, and
 * so on. The set of points in a region may be iterated over.
 */
public abstract class Region(
    /**
     * The rank of this region.
     */
    rank:Long,
    /**
     * Is the region rectangular?
     * A rectangular region is defined to be region such that every point contained
     * in the bounding box of the region is a point in the region.
     */
    rect:Boolean,
    /**
     * Is the region zero-based?
     * A region is zero based if for each dimension the min value is 0.
     */
    zeroBased:Boolean,
    /**
     * Is the region rank 1, rectangular, and zero-based?
     */
    rail:Boolean
) implements Iterable[Point(rank)] {
    public property rank():Long = rank;
    public property rect():Boolean = rect;
    public property zeroBased():Boolean = zeroBased;
    public property rail():Boolean = rail;


    //
    // factories
    //

    /**
     * Construct an empty region of the specified rank.
     */
    public static def makeEmpty(rank:Long):Region(rank){self!=null,rect} = new EmptyRegion(rank);
     
    /**
     * Construct an unbounded region of a given rank that contains all
     * points of that rank.
     */
    public static def makeFull(rank:Long): Region(rank){self !=null,rect} = new FullRegion(rank);
    
    /**
     * Construct a region of rank 0 that contains the single point of
     * rank 0. Useful as the identity region under Cartesian product.
     */
    public static def makeUnit():Region(0){self != null,rect} = new FullRegion(0);


    /**
     * Construct an unbounded halfspace region of rank normal.rank
     * that consists of all points p satisfying dot(p,normal) + k <= 0.
     */
    public static def makeHalfspace(normal:Point, k:Long):Region(normal.rank) {
        val rank = normal.rank;
        val pmb = new PolyMatBuilder(rank);
        val r = new PolyRow(normal, k as Int);
        pmb.add(r);
        val pm = pmb.toSortedPolyMat(false);
        return PolyRegion.make(pm);
    }

    //
    // rectangular factories
    //

    /**
     * Returns a PolyRegion that represents the rectangular region with smallest point minArg and largest point
     * maxArg. 
     * <p> Most users of the Region API should call makeRectangular which will return a 
     * RectRegion. Methods on RectRegion automatically construct a PolyRegion (by calling makeRectangularPoly) 
     * if they need to implement operations (such as intersection, product etc) that are difficult to define
     * on a RectRegion's representation.
     * @param minArg:Rail[Long] -- specifies the smallest point in the region
     * @param maxArg:Rail[Long] -- specifies the largest point in the region (must have the same rank as minArg
     * @return A Region of rank minarg.length 
     */
    public static def makeRectangularPoly(minArg:Rail[Long], maxArg:Rail[Long]):Region(minArg.size){
       if (minArg.size != maxArg.size) throw new IllegalArgumentException("min and max not equal size ("+minArg.size+" != "+maxArg.size+")");
           val rank = minArg.size;
           val pmb = new PolyMatBuilder(rank); 
           for (i in 0..(rank-1)) {
               if (minArg(i) > Long.MIN_VALUE) {
                   // add -1*x(i) + minArg(i) <= 0, i.e. x(i) >= minArg(i)
                   val r = new PolyRow(Point.make(rank, (j:Long) => i==j ? -1 : 0), minArg(i) as Int);
                   pmb.add(r);
                }
                if (maxArg(i) < Long.MAX_VALUE) {
                   // add 1*x(i) - maxArg(i) <= 0, i.e. x(i) <= maxArg(i)
                   val s = new PolyRow(Point.make(rank, (j:Long) => i==j ? 1 : 0), -maxArg(i) as Int);
                   pmb.add(s);
               }
           }
           val pm = pmb.toSortedPolyMat(false);
           return PolyRegion.make(pm);// as Region(minArg.size); 
    }
     
    /**
     * Construct a rectangular region whose bounds are specified as
     * rails of longs.
     */
    public static def makeRectangular(minArg:Rail[Long], maxArg:Rail[Long]):Region(minArg.size){self.rect} {
        if (minArg.size == 1) {
            // To remove the cast, the constraint solver should be able to handle arithmetic.
            return new RectRegion1D(minArg(0), maxArg(0)) as Region(minArg.size){rect}; 
        } else {
            return new RectRegion(new Rail[Long](minArg.size, minArg), new Rail[Long](maxArg.size, maxArg));
        }
    }

    /**
     * Construct a rectangular region from a Rail of IntRange that represent the min/max of each dimension.
     */
    public static def makeRectangular(ranges:Rail[IntRange]):Region(ranges.size){self.rect} {
        if (ranges.size == 1) {
            // To remove the cast, the constraint solver should be able to handle arithmetic.
            return new RectRegion1D(ranges(0).min,ranges(0).max) as Region(ranges.size){rect}; 
        } else {
            val mins = new Rail[Long](ranges.size, (i:Long)=>(ranges(i).min as Long));
            val maxs = new Rail[Long](ranges.size, (i:Long)=>(ranges(i).max as Long));
            return new RectRegion(mins, maxs);
        }
    }
    /**
     * Construct a rectangular region from a Rail of IntRange that represent the min/max of each dimension.
     */
    public static def make(ranges:Rail[IntRange]) = makeRectangular(ranges);

    /**
     * Construct a rectangular region from a Rail of LongRange that represent the min/max of each dimension.
     */
    public static def makeRectangular(ranges:Rail[LongRange]):Region(ranges.size){self.rect} {
        if (ranges.size == 1) {
            // To remove the cast, the constraint solver should be able to handle arithmetic.
            return new RectRegion1D(ranges(0).min,ranges(0).max) as Region(ranges.size){rect}; 
        } else {
            val mins = new Rail[Long](ranges.size, (i:Long)=>ranges(i).min);
            val maxs = new Rail[Long](ranges.size, (i:Long)=>ranges(i).max);
            return new RectRegion(mins, maxs);
        }
    }
    /**
     * Construct a rectangular region from a Rail of LongRange that represent the min/max of each dimension.
     */
    public static def make(ranges:Rail[LongRange]) = makeRectangular(ranges);

    /**
     * Construct a rectangular rank 1 region from an IntRange
     */
    public static def make(r:IntRange):Region(1){self.rect} = new RectRegion1D(r.min, r.max);
    /**
     * Construct a rectangular rank 1 region from an IntRange
     */
    public static def makeRectangular(r:IntRange):Region(1){self.rect} = new RectRegion1D(r.min, r.max);

    /**
     * Construct a rectangular rank 1 region from a LongRange
     */
    public static def make(r:LongRange):Region(1){self.rect} = new RectRegion1D(r.min, r.max);
    /**
     * Construct a rectangular rank 1 region from a LongRange
     */
    public static def makeRectangular(r:LongRange):Region(1){self.rect} = new RectRegion1D(r.min, r.max);

    /**
     * Construct a rectangular rank 2 region from a pair of IntRanges
     */
    public static def makeRectangular(r1:IntRange, r2:IntRange):Region(2){self.rect} {
        return new RectRegion([r1.min as Long, r2.min as Long], 
                              [r1.max as Long, r2.max as Long]) as Region(2){self.rect};
    }
    /**
     * Construct a rectangular rank 2 region from a pair of IntRanges
     */
    public static def make(r1:IntRange, r2:IntRange):Region(2){self.rect} = makeRectangular(r1, r2);

    /**
     * Construct a rectangular rank 2 region from a pair of LongRanges
     */
    public static def makeRectangular(r1:LongRange, r2:LongRange):Region(2){self.rect} {
        return new RectRegion([r1.min, r2.min], [r1.max, r2.max]) as Region(2){self.rect};
    }
    /**
     * Construct a rectangular rank 2 region from a pair of LongRanges
     */
    public static def make(r1:LongRange, r2:LongRange):Region(2){self.rect} = makeRectangular(r1, r2);

    /**
     * Construct a rectangular rank 3 region from a triple of IntRanges
     */
    public static def makeRectangular(r1:IntRange, r2:IntRange, r3:IntRange):Region(3){self.rect} {
        return new RectRegion([r1.min as Long, r2.min as Long, r3.min as Long], 
                              [r1.max as Long, r2.max as Long, r3.max as Long]) as Region(3){self.rect};
    }
    /**
     * Construct a rectangular rank 3 region from a triple of IntRanges
     */
    public static def make(r1:IntRange, r2:IntRange, r3:IntRange):Region(3){self.rect} {
        return makeRectangular(r1, r2, r3);
    }

    /**
     * Construct a rectangular rank 3 region from a triple of LongRanges
     */
    public static def makeRectangular(r1:LongRange, r2:LongRange, r3:LongRange):Region(3){self.rect} {
        return new RectRegion([r1.min, r2.min, r3.min], [r1.max, r2.max, r3.max]) as Region(3){self.rect};
    }
    /**
     * Construct a rectangular rank 3 region from a triple of LongRanges
     */
    public static def make(r1:LongRange, r2:LongRange, r3:LongRange):Region(3){self.rect} {
        return makeRectangular(r1, r2, r3);
    }

    /**
     * Construct a rectangular rank 4 region from four IntRanges
     */
    public static def makeRectangular(r1:IntRange, r2:IntRange, r3:IntRange, r4:IntRange):Region(4){self.rect}{
        return new RectRegion([r1.min as Long, r2.min as Long, r3.min as Long, r4.min as Long], 
                              [r1.max as Long, r2.max as Long, r3.max as Long, r4.max as Long]) as Region(4){self.rect};
    }
    /**
     * Construct a rectangular rank 4 region from four IntRanges
     */
    public static def make(r1:IntRange, r2:IntRange, r3:IntRange, r4:IntRange):Region(4){self.rect} {
        return makeRectangular(r1, r2, r3, r4);
    }

    /**
     * Construct a rectangular rank 4 region from four LongRanges
     */
    public static def makeRectangular(r1:LongRange, r2:LongRange, r3:LongRange, r4:LongRange):Region(4){self.rect}{
        return new RectRegion([r1.min, r2.min, r3.min, r4.min], [r1.max, r2.max, r3.max, r4.max]) as Region(4){self.rect};
    }
    /**
     * Construct a rectangular rank 4 region from four LongRanges
     */
    public static def make(r1:LongRange, r2:LongRange, r3:LongRange, r4:LongRange):Region(4){self.rect} {
        return makeRectangular(r1, r2, r3, r4);
    }


    /**
     * Construct a rank-1 rectangular region with the specified bounds.
     */
    // XTENLANG-109 prevents zeroBased==(min==0)
    // Changed RegionMaker_c to add clause explicitly.
    public static def makeRectangular(min:Long, max:Long):Region(1){self.rect}
        = new RectRegion1D(min, max);

    /**
     * Construct a rank-1 rectangular region with the specified bounds.
     */
    public static def make(min:Long, max:Long): Region(1){self.rect} = new RectRegion1D(min, max);

    //
    // non-rectangular factories
    //

    /**
     * Construct a banded region of the given size, with the specified
     * number of diagonals above and below the main diagonal
     * (inclusive of the main diagonal).
     * @param size -- number of elements in the banded region
     * @param upper -- the number of diagonals in the band, above the main diagonal
     * @param lower -- the number of diagonals in the band, below the main diagonal
     */
    public static def makeBanded(size:Long, upper:Long, lower:Long):Region(2)
        = PolyRegion.makeBanded(size as Int, upper as Int, lower as Int);

    /**
     * Construct a banded region of the given size that includes only
     * the main diagonal.
     */
    public static def makeBanded(size:Long):Region(2) = PolyRegion.makeBanded(size as Int, 1, 1);
    
    /**
     * Construct an upper triangular region of the given size.
     */

    public static def makeUpperTriangular(size:Long):Region(2) = makeUpperTriangular(0, 0, size);

    /**
     * Construct an upper triangular region of the given size with the
     * given lower bounds.
     */
    public static def makeUpperTriangular(rowMin:Long, colMin:Long, size:Long): Region(2)
        = PolyRegion.makeUpperTriangular2(rowMin as Int, colMin as Int, size as Int);
    
    /**
     * Construct a lower triangular region of the given size.
     */
    public static def makeLowerTriangular(size:Long): Region(2) = makeLowerTriangular(0, 0, size);

    /**
     * Construct an lower triangular region of the given size with the
     * given lower bounds.
     */
    public static def makeLowerTriangular(rowMin:Long, colMin:Long, size:Long):Region(2)
        = PolyRegion.makeLowerTriangular2(rowMin as Int, colMin as Int, size as Int);


    //
    // Basic non-property information.
    //

    /**
     * Returns the number of points in this region.
     */
    public abstract def size():Long;

    /**
     * Returns true iff this region is convex.
     */
    public abstract def isConvex():Boolean;

    /**
     * Returns true iff this region is empty.
     */
    public abstract def isEmpty():Boolean;


    /**
     * Returns the index of the argument point in the lexograpically ordered
     * enumeration of all Points in this region.  Will return -1 to indicate 
     * that the argument point is not included in this region.  If the argument
     * point is contained in this region, then a value between 0 and size-1
     * will be returned.  The primary usage of indexOf is in the context of 
     * Arrays, where it enables the conversion from "logical" indicies 
     * specified in Points into lower level indices specified by Ints that
     * can be used in primitive operations such as copyTo and in interfacing
     * to native code.  Often indexOf will be used in conjuntion with the 
     * raw() method of Array or DistArray.
     */
    public abstract def indexOf(p:Point):Long;
    
    public def indexOf(i0:Long) = indexOf(Point.make(i0));
    public def indexOf(i0:Long, i1:Long) = indexOf(Point.make(i0, i1));
    public def indexOf(i0:Long, i1:Long, i2:Long) = indexOf(Point.make(i0,i1,i2));
    public def indexOf(i0:Long, i1:Long, i2:Long, i3:Long) = indexOf(Point.make(i0,i1,i2,i3));


    //
    // bounding box
    //

    /**
     * The bounding box of a region r is the smallest rectangular region
     * that contains all the points of r.
     */
    public def boundingBox(): Region(rank) = computeBoundingBox();


    abstract protected  def computeBoundingBox(): Region(rank);

    /**
     * Returns a function that can be used to access the lower bounds 
     * of the bounding box of the region. 
     */
    abstract public def min():(Long)=>Long;

    /**
     * Returns a function that can be used to access the upper bounds 
     * of the bounding box of the region. 
     */
    abstract public def max():(Long)=>Long;
    
    /**
     * Returns the lower bound of the bounding box of the region along
     * the ith axis.
     */
    public def min(i:Long):Long = min()(i);

    /**
     * Returns the upper bound of the bounding box of the region along
     * the ith axis.
     */
    public def max(i:Long):Long = max()(i);    

    /**
     * Returns the smallest point in the bounding box of the region
     */
    public def minPoint():Point(this.rank) = Point.make(rank, min());

    /**
     * Returns the largest point in the bounding box of the region
     */
    public def maxPoint():Point(this.rank) = Point.make(rank, max());


    //
    // geometric ops
    //

    /**
     * Returns the complement of a region. The complement of a bounded
     * region will be unbounded.
   

    abstract public def complement(): Region(rank);
  */
    
    /**
     * Returns the union of two regions: a region that contains all
     * points that are in either this region or that region.
     

    abstract public def union(that: Region(rank)): Region(rank);

*/
    
    /**
     * Returns the union of two regions if they are disjoint,
     * otherwise throws an exception.
     *   abstract public def disjointUnion(that: Region(rank)): Region(rank);
     */

  

    /**
     * Returns the intersection of two regions: a region that contains all
     * points that are in both this region and that region.
     * 
     */
    abstract public def intersection(that: Region(rank)): Region(rank);
    

    /**
     * Returns the difference between two regions: a region that
     * contains all points that are in this region but are not in that
     * region.
     *  abstract public def difference(that: Region(rank)): Region(rank);
     */

    /**
     * Returns true iff this region has no points in common with that
     * region.
     */
     public def disjoint(that:Region(rank)) = intersection(that).isEmpty();
   

    /**
     * Returns the Cartesian product of two regions. The Cartesian
     * product has rank <code>this.rank+that.rank</code>. For every point <code>p</code> in the
     * Cartesian product, the first <code>this.rank</code> coordinates of <code>p</code> are a
     * point in this region, while the last <code>that.rank</code> coordinates of p
     * are a point in that region.
     */

    abstract public def product(that: Region): Region{self!=null};

    /**
     * Returns the region shifted by a Point (vector). The Point has
     * to have the same rank as the region. A point p+v is in 
     * <code>translate(v)</code> iff <code>p</code> is in <code>this</code>. 
     */

    abstract public def translate(v: Point(rank)): Region(rank);

    /**
     * Returns the projection of a region onto the specified axis. The
     * projection is a rank-1 region such that for every point <code>[i]</code> in
     * the projection, there is some point <code>p</code> in this region such that
     * <code>p(axis)==i</code>.
     */

    abstract public def projection(axis:Long): Region(1);

    /**
     * Returns the projection of a region onto all axes but the
     * specified axis.
     */

   
    abstract public def eliminate(axis:Long): Region /*(rank-1)*/;


    /**
     * Return an iterator for this region. Normally accessed using the
     * syntax
     *
     *    for (p:Point in r)
     *        ... p ...
     */

    public abstract def iterator(): Iterator[Point(rank)];


    //
    // ops
    //

   // public operator ! this: Region(rank) = complement();
    public operator this && (that: Region(rank)): Region(rank) = intersection(that);
    //public operator this || (that: Region(rank)): Region(rank) = union(that);
    //public operator this - (that: Region(rank)): Region(rank) = difference(that);

    public operator this * (that: Region) = product(that);
    
    public operator this + (v: Point(rank)) = translate(v);
    public operator (v: Point(rank)) + this = translate(v);
    public operator this - (v: Point(rank)) = translate(-v);


    //
    // comparison
    //

    public def equals(that:Any):Boolean {
       if (this == that) return true; // short-circuit
       if (!(that instanceof Region)) return false;
       val t1 = that as Region;
       if (rank != t1.rank) return false;
       val t2 = t1 as Region(rank);
       return this.contains(t2) && t2.contains(this);
    }

    abstract public def contains(that: Region(rank)):Boolean;


    abstract public def contains(p:Point):Boolean;
    
    public def contains(i:Long){rank==1} = contains(Point.make(i));

    public def contains(i0:Long, i1:Long){rank==2} = contains(Point.make(i0,i1));

    public def contains(i0:Long, i1:Long, i2:Long){rank==3} = contains(Point.make(i0,i1,i2));

    public def contains(i0:Long, i1:Long, i2:Long, i3:Long){rank==4} = contains(Point.make(i0,i1,i2,i3));

    protected @Inline def this(r:Long, t:Boolean, z:Boolean):Region{self.rank==r,self.rect==t,self.zeroBased==z} {
        val isRail = (r == 1) && t && z;
        property(r, t, z, isRail);
    }

    // The purpose of this constructor is to simplify the analysis of 
    // the common case of the zero-based RectRegion1D constructor such that
    // the constraint solver can properly statically determine that the 
    // constructed RectRegion1D has all the desirable properties
    protected @Inline def this(r:Long{self==1}):Region{self.rank==r,self.zeroBased,self.rect,self.rail} {
        property(r, true, true, true);
    }

    /**
     * Constructs a distribution over this region that maps
     * every point in the region to the specified place.
     * @param p the given place
     * @return a "constant" distribution over this region that maps to p.
     */
    public operator this -> (p:Place) = Dist.makeConstant(this, p);
}
public type Region(r:Long) = Region{self.rank==r};
public type Region(r:Region) = Region{self==r};
