/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2010-2011.
 */

package x10.regionarray;

/**
 * Represents periodic boundary conditions, in which elements at each edge
 * of a region are considered to be neighbours, and indices that fall
 * outside the "home" region in any dimension are wrapped around modulo 
 * the size of the region in that dimension.
 */
public final class PeriodicBoundaryConditions {
    public static def getPeriodicIndex(index:Long, min:Long, max:Long):Long {
        val delta = (max-min+1);
        var actualIndex:Long = index;
        while (actualIndex < min) actualIndex += delta;
        while (actualIndex > max) actualIndex -= delta;
        return actualIndex;
    }

    public static def wrapPeriodic(pt:Point, r:Region):Point{self.rank==pt.rank} {
        return Point.make(pt.rank, (i:Long)=> getPeriodicIndex(pt(i), r.min(i), r.max(i)));
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
