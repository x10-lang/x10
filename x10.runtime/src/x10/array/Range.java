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
public class Range implements TypeArgument {
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
    
	public int ordinal(int p) {
		assert contains(p);
		
		return p - lo;
	}
	
	public boolean contains(int p) {
		return lo <= p && p <= hi; // X10 spec says range is inclusive!
	}
	
	public boolean contains(Range r) {
		return r.lo >= lo && r.hi <= hi;
	}
	
	public String toString() {
		return "[" + lo + ".." + hi + "]";
	}
	
	public boolean equals(Object o) {
		assert o instanceof Range;
		Range rhs = (Range) o;
		return rhs.lo == lo && rhs.hi == hi;
	}
	
	public int hashCode() {
		return card;
	}
}
