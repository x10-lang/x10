/*
 * Created on Nov 1, 2004
 *
 *
 */
package x10.array;
import x10.lang.Object;
import x10.lang.point;
import x10.lang.region;
import java.util.Iterator;

/**
 * @author praun
 * @author vj
 * 
 */
public class StridedRange extends Range {
    
    public final int stride; 
	
	public StridedRange(int lo, int hi, int stride) {
	    super(lo, hi, ((hi - lo) / stride) + 1);
	    
		assert ((hi - lo) % stride) == 0;
		this.stride = stride;
	}
	
	public point coord(/*nat*/int ord) {
	    assert ord < size;
	    throw new Error("TODO");
	}
	
	public /*nat*/int ordinal(point p) {
        throw new Error("TODO");
    }
	
	public region union( region r) {
	    assert r instanceof StridedRange;
	    
	    throw new Error("TODO");
	}
	
	public region intersection( region r) {
	    assert r instanceof StridedRange;
	    
	    throw new Error("TODO");
	}

	public region difference( region r) {
	    assert r instanceof StridedRange;
	    
	    throw new Error("TODO");
	}

	public boolean contains(int p) {
	    throw new Error("TODO");
	}

	public boolean contains(Range r) {
	    assert r instanceof StridedRange;
	    
	    throw new Error("TODO");
	}

	public String toString() {
	    return "[" + lo + ", " + hi + ": " + stride + "]";
	}
	
	public boolean equals(Object o) {
	    assert o != null;
		assert o instanceof Range;
		if (!(o instanceof StridedRange))
			return false;
		StridedRange rhs = (StridedRange) o;
		return rhs.lo == lo && rhs.hi == hi;
		
	}
	
	public int hashCode() {
	    return stride;
	}
	public boolean contains(point p) {
	 throw new Error("TODO");
	}
	public boolean isConvex() {
		return stride != 1;
	}
	public region convexHull() {
		if (stride == 1) 
			return this;
		return new ContiguousRange(lo, hi);
	}
	public boolean disjoint( region r) {
		throw new Error("TODO");
	}
	public /*nat*/int size() {
		throw new Error("TODO");
	}
	public boolean contains( region r) {
		throw new Error("TODO");
	}
	public Iterator iterator() {
		throw new Error("TODO");
	}
}
