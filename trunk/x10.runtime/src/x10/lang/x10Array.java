package x10.lang;
import java.util.Iterator;

import x10.lang.place;

import x10.array.ArrayFactory;
import x10.array.ContiguousRange;
import x10.array.MultiDimRegion;
import x10.compilergenerated.Parameter1;

/* root for all array classes */
abstract public class x10Array extends x10.lang.Object implements Array, Unsafe, Parameter1, ValueType {
	
	public static final ArrayFactory factory = Runtime.factory.getArrayFactory();
	/*parameter*/ public final dist distribution;
	/*parameter*/ public final /*nat*/int rank /*= distribution.rank*/;
	/*parameter*/ public final region/*(rank)*/ region /*= distribution.region*/;
	/*parameter*/ public final boolean rect;
	/*parameter*/ public final boolean rail;
	/*parameter*/ public final place onePlace;
	/*parameter*/ public final boolean zeroBased;
	public static final String propertyNames$ = "distribution rank region rect rail onePlace zeroBased ";
    protected x10Array(dist d) {
        distribution = d;
        rank = d.rank;
        region = d.region;
        rect = region instanceof MultiDimRegion ;
        onePlace = d.onePlace;
        zeroBased = d.zeroBased;
        rail = (rank==1) && onePlace!=null && region.zeroBased;
        
    }
         /**
         * @return low bound for a 1D array else throw and exception
         */
        public final int low0(){
           if(rank != 1) throw new Error("low0 can only be called for 1D arrays:"+region);
           int lowBound = region.rank(0).low();
           //System.out.println("low bound is:"+lowBound);
           return lowBound;
        }

	/**
	 * Return an iterator over the array's region.
	 */
	public final Iterator iterator() { return region.iterator(); }

	/**
	 * Return the array's distribution.
	 */
	public final dist toDistribution() { return distribution; }

	/**
	 * Create a multi-dimensional Java array.
	 */
	public abstract java.lang.Object toJava();   

}
