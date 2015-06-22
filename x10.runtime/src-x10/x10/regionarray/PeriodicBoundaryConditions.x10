/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2010-2011.
 *  (C) IBM Corporation 2014.
 */

package x10.regionarray;

/**
 * Implements periodic boundary conditions, in which elements at each edge
 * of a region are considered to be neighbours, and indices that fall
 * outside the locally-held region in any dimension are wrapped around modulo 
 * the size of the full region in that dimension.
 */
public final class PeriodicBoundaryConditions {
    /**
     * Wrap the given index if it falls outside of [min,max], by adding
     * or subtracting multiples of <code>wrap</code>.
     * @param index the given index into a single dimension of a region
     * @param min the minimum index of the region in the given dimension
     * @param max the maximum index of the region in the given dimension
     * @param wrap the offset to add or subtract from the index when wrapping.
     *   For a region that is entirely held at a single place, wrap==(max-min+1).
     *   For the case of a block distribution over the given dimension,
     *   wrap==(fullRegion.max(dim)-fullRegion.min(dim)+1).
     */
    public static def getPeriodicIndex(index:Long, min:Long, max:Long, wrap:Long):Long {
        var actualIndex:Long = index;
        while (actualIndex < min) actualIndex += wrap;
        while (actualIndex > max) actualIndex -= wrap;
        return actualIndex;
    }

    /**
     * If <code>pt</code> falls outside of <code>localRegion</code>, wrap
     * the indices by adding or subtracting multiples of the size of
     * <code>fullRegion</code> in each dimension.
     * For example,
     * getPeriodic(Point([-2,2]),
     *             Region.makeRectangular(1..4, 0..7),
     *             Region.makeRectangular(1..8, 0..7))
     * returns: Point(6,2).
     */              
    public static def wrapPeriodic(pt:Point,
                                   localRegion:Region(pt.rank),
                                   fullRegion:Region(pt.rank)):Point{self.rank==pt.rank} {
        return Point.make(pt.rank, (i:Long)=> 
            getPeriodicIndex(pt(i), localRegion.min(i), localRegion.max(i), fullRegion.max(i)-fullRegion.min(i)+1));
    }

    /**
     * If <code>pt</code> falls outside of <code>region</code>, wrap
     * the indices by adding or subtracting multiples of the size of
     * <code>region</code> in each dimension.
     * For example,
     * getPeriodic(Point([-2,2]),
     *             Region.makeRectangular(1..4, 0..7))
     * returns: Point(2,2).
     */              
    public static def wrapPeriodic(pt:Point, region:Region(pt.rank)):Point{self.rank==pt.rank} {
        return Point.make(pt.rank, (i:Long)=> 
            getPeriodicIndex(pt(i), region.min(i), region.max(i), region.max(i)-region.min(i)+1));
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
