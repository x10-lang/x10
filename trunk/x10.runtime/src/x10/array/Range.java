/*
 * Created on Oct 3, 2004
 */
package x10.array;

import x10.base.TypeArgument;
import x10.lang.region;

/**
 * Ranges are a collection of points in the int space.
 * Currently, only contiguous sets of points are supported.
 * Range objects are immutable.
 * 
 * @author Christoph von Praun
 * @author Christian Grothoff
 * @author vj
 */
public abstract class Range 
extends region/*(1)*/
implements TypeArgument {
	
	/**
	 * Cardinality of the range, i.e., the number of element in the integer
	 * space it covers.
	 */
	public final int size;
	public final int lo;
	public final int hi;
	
	public Range (int l, int h, int c) {
		super(1);
		lo = l;
		hi = h;
		size = c;
		assert hi >= lo;
	}
	public int ordinal(int j) {
		return j-lo;
	}
	public boolean contains(int i) {
		return lo <= i && i < hi;
	}
	public int high() {
		return hi;
	}
	public int low() {
		return lo;
	}
	/**
	 * Returns itself -- this is the 1-d region at index (i % 1) = 0.
	 */
	public region rank(int i) {
		return this;
	}
	
}
