/*
 * Created on Oct 3, 2004
 */
package x10.array;

import x10.lang.TypeArgument;

/**
 * Ranges are a collection of points in the int space.
 * Currently, only contiguous sets of points are supported.
 * Range objects are immutable.
 * 
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
public abstract class Range implements TypeArgument {
	/**
	 * Cardinality of the range, i.e., the number of element 
	 * in the integer space it covers.
	 */	
	public final int card;
	public final int lo;
	public final int hi;
	
	/** 
	 * Range that starts at lo (including)
	 * to hi (including!).
	 */
	public Range(int lo, int hi) {
		assert hi >= lo && lo >= 0;
		
		this.lo = lo;
		this.hi = hi;
		card = hi - lo + 1; // inclusive!
	}
    
	public abstract boolean contains(int p);
	
	public abstract boolean contains(Range r);
	
	public abstract int ordinal(int p);
	
	public abstract String toString();
	
	public abstract boolean equals(Object o);
	
	public abstract int hashCode();
}
