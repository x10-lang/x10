/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.array.sharedmemory;

import x10.array.ContiguousRange;
import x10.array.TriangularRegion;
import x10.array.BandedRegion;
import x10.array.EmptyRegion;
import x10.array.MultiDimRegion;
import x10.array.StridedRange;
import x10.lang.region;

public  class RegionFactory extends region.factory {
	/** Create a Region_c of zero ranks. This is an empty
	 * Region_c of size 0.
	 */
	public region /*(k)*/ emptyRegion(/*nat*/ int k) {
		return new EmptyRegion(k);
	}
	
	/** Construct a 1-dimensional Region_c, low..high with the given stride.
	 * Return an empty region if low > high
	 */
	public region /*(1)*/ region(int low, int high, int stride) {
		if (low > high) 
			return emptyRegion(1);
		
		return (stride == 1) 
		? (region) new ContiguousRange(low, high)
				: (region) new StridedRange(low, high, stride);
	}
	
	
	/** Construct a Region_c, using the list of Region_c(1)'s passed as
	 * arguments to the constructor.
	 */
	public region/*(regions.length)*/ region(region/*(1)*/[] regions ) {
		boolean zeroBased = true;
		for (int i=0; i<regions.length; i++) zeroBased &= regions[i].zeroBased;
		return new MultiDimRegion( regions, zeroBased );
	}
	public region/*(2)*/ region(region a, region b) {
		return new MultiDimRegion( new region[] {a, b}, a.zeroBased&&b.zeroBased);
	}
	
	/** Return an \code{upperTriangular} Region_c for a dim-rankal
	 * space of size \code{size} in each dimension.
	 */
	public region/*(rank)*/ upperTriangular( /*nat*/ int rank, /*nat*/ int size ) {
        region ret;
        if (rank != 2)
            throw new Error("Triangular region of dimension != 2 not supported.");
        else {
            ContiguousRange cr =  new ContiguousRange(0, size-1);
            region[] r = new region[] {cr, cr};
            ret = new TriangularRegion(r, false);
        }
        return ret;
	}
	
	/** Return a lowerTriangular Region_c for a rank-dimensional space of
	 * size \code{size} in each dimension.
	 */
	public region/*(rank)*/ lowerTriangular( /*nat*/ int rank, /*nat*/ int size ) {
		region ret;
        if (rank != 2)
            throw new Error("Triangular region of dimension != 2 not supported.");
        else {
            ContiguousRange cr =  new ContiguousRange(0, size-1);
            region[] r = new region[] {cr, cr};
            ret = new TriangularRegion(r, true);
        }
        return ret;
	}
	
	/** Return a banded Region_c of width {\code width} for a
	 * rank-dimensional space of size {\code size} in each dimension.
	 */
	public region/*(rank)*/ banded( /*nat*/ int rank, 
			/*nat*/ int size, 
			/*nat*/ int width) {
        region ret;
        if (rank != 2)
            throw new Error("Banded region of dimension != 2 not supported.");
        else {
            ContiguousRange cr =  new ContiguousRange(0, size-1);
            region[] r = new region[] {cr, cr};
            ret = new BandedRegion(r, width, true);
        }
        return ret;
	}
}
