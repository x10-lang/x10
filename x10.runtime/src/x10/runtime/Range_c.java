/*
 * Created on Oct 16, 2004
 */
package x10.runtime;

import x10.lang.Range;

/**
 * @author Christoph von Praun
 * @author Christian Grothoff (bugfixes)
 */
class Range_c extends Range {

	Range_c(int lo, int hi) {
		super(lo, hi);
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
