/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 3, 2004
 */
package x10.array;

import x10.lang.point;
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
extends region/*(1)*/ {
	
	/**
	 * Cardinality of the range, i.e., the number of element in the integer
	 * space it covers.
	 */
	public final int size;
	public final int lo;
	public final int hi;
	public Range(int h) {
		this(0, h, h);
	}
	public Range(int l, int h) {
		this(l, h, h-l+1);
	}
	public Range (int l, int h, int c) {
		super(1, true, l==0);
		lo = l;
		hi = h;
		size = c;
		assert size == hi-lo+1;
		assert hi >= lo;
	}
	
	public /*nat*/int size() {
		return size;
	}
	
	public int ordinal(int j) {
		int ret = j-lo;
        if (ret < 0 || ret >= size)
            throw new ArrayIndexOutOfBoundsException(j);
        return ret;
	}
	
	public boolean contains(int i) {
		return lo <= i && i <= hi;
	}
	
	public boolean contains( int[] p) {
	    return contains(point.factory.point(p));
	}
		
	public int high() {
		return hi;
	}
	
	public int low() {
		return lo;
	}
	
	public region union(region r ) {
		assert r != null;
		return ArbitraryRegion.union(this, r);
	}
	
	public region intersection( region r ) {
		assert r != null;
		return ArbitraryRegion.intersection(this, r);
	}
	
	public region difference(region r ) {
		assert r != null;
		return ArbitraryRegion.difference(this, r);
	}
	
	public boolean disjoint(region r ) {
		assert r != null;
		return intersection(r).size() == 0;
	}
	
	/**
	 * Returns itself -- this is the 1-d region at index (i % 1) = 0.
	 */
	public region rank(int i) {
	    assert i == 0;
		return this;
	}


}
