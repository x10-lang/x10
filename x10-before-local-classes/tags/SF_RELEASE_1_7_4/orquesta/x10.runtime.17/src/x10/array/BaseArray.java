/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.array;

import java.util.Iterator;

import x10.core.Value;
import x10.runtime.Place;

/* root for all array classes */
abstract public class BaseArray extends Value {

    protected final boolean mutable_;
    
    /* parameter */public final Dist dist;
    /* parameter */public final/* nat */int rank /* = distribution.rank */;
    /* parameter */public final Region/* (rank) */region /* = distribution.region */;
    /* parameter */public final boolean rect;
    /* parameter */public final boolean rail;
    /* parameter */public final Place onePlace;
    /* parameter */public final boolean zeroBased;

    protected BaseArray(Dist d, boolean mutable) {
        mutable_ = mutable;
        dist = d;
        rank = d.rank;
        region = d.region;
        rect = region instanceof Rectangular;
        onePlace = d.onePlace;
        zeroBased = d.zeroBased;
        rail = (rank == 1) && onePlace != null && region.zeroBased;
    }

    /**
     * @return low bound for a 1D array else throw and exception
     */
    public final int low0() {
        if (rank != 1)
            throw new Error("low0 can only be called for 1D arrays:" + region);
        int lowBound = region.rank(0).low();
        // System.err.println("low bound is:"+lowBound);
        return lowBound;
    }

    /**
     * Return an iterator over the array's region.
     */
    public final Iterator<Point> iterator() {
        return region.iterator();
    }

    /**
     * Return the array's distribution.
     */
    public final Dist toDistribution() {
        return dist;
    }

    /**
     * Create a multi-dimensional Java array.
     */
    public abstract java.lang.Object toJava();

    public boolean isValue() {
        return !this.mutable_;
    }

    public int rank() {
        return rank;
    }

    public boolean rect() {
        return rect;
    }

    public boolean zeroBased() {
        return zeroBased;
    }

    public boolean rail() {
        return rail;
    }

    public Dist distribution() {
        return dist;
    }

    public Region region() {
        return region;
    }

    public Place onePlace() {
        return onePlace;
    }
}
