package x10.array.sharedmemory;

import x10.array.ContiguousRange;
import x10.array.EmptyRegion;
import x10.array.StridedRange;
import x10.lang.region;

public  class RegionFactory extends region.factory {
	/** Create a Region_c of zero ranks. This is an empty
	 * Region_c of size 0.
	 */
	public region /*(k)*/ emptyRegion(/*nat*/ long k) {
		return new EmptyRegion(k);
	}
	
	/** Construct a 1-dimensional Region_c low..high with stride 1. 
	 */
	public region/*(1)*/ region( int low, int high ) {
		final region/*(1)*/ result = region(low, high, 1);
		assert result.rank == 1;
		return result;
	}
	
	/** Construct a 1-dimensional Region_c, low..high with the given stride.
	 */
	public region /*(1)*/ region(int low, int high, int stride) {
		return (stride == 1) 
		? (region) new ContiguousRange(low, high) 
				: (region) new StridedRange(low, high, stride);
	}
	
	
	/** Construct a Region_c, using the list of Region_c(1)'s passed as
	 * arguments to the constructor.
	 */
	public region/*(regions.length)*/ region(region/*(1)*/[] regions ) {
		return new Region_c( regions );
	}
	
	/** Return an \code{upperTriangular} Region_c for a dim-rankal
	 * space of size \code{size} in each dimension.
	 */
	public region/*(rank)*/ upperTriangular( /*nat*/ long rank, 
			/*nat*/ long size ) {
		throw new Error("TODO");
	}
	
	/** Return a lowerTriangular Region_c for a rank-dimensional space of
	 * size \code{size} in each rankension.
	 */
	public region/*(rank)*/ lowerTriangular( /*nat*/ long rank, 
			/*nat*/ long size ) {
		throw new Error("TODO");
	}
	
	/** Return a banded Region_c of width {\code width} for a
	 * rank-dimensional space of size {\code size} in each dimension.
	 */
	public region/*(rank)*/ banded( /*nat*/ long rank, 
			/*nat*/ long size, 
			/*nat*/ long width) {
		throw new Error("TODO");	    
	}
}
