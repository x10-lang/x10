/*
 * Created on Nov 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package x10.array;

/**
 * @author praun
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StridedRange implements Range {
    
    /**
	 * Cardinality of the range, i.e., the number of element 
	 * in the integer space it covers.
	 */	
    public final int card;
    public final int lo;
    public final int hi;
    public final int stride; 
	
	public int count() {
	    return card;
	}
	
	public StridedRange(int lo, int hi, int stride) {
		assert hi >= lo;
		assert ((hi - lo) % stride) == 0;
		
		this.lo = lo;
		this.hi = hi;
		this.stride = stride;
		card = ((hi - lo) / stride) + 1;
	}
	
	public int coord(int ord) {
	    assert ord < card;
	    throw new Error("TODO");
	}
	
	public int ordinal(int p) {
        throw new Error("TODO");
    }
	
	public Range union(Range r) {
	    assert r instanceof StridedRange;
	    
	    throw new Error("TODO");
	}
	
	public Range intersect(Range r) {
	    assert r instanceof StridedRange;
	    
	    throw new Error("TODO");
	}

	public Range difference(Range r) {
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
		else {
		    StridedRange rhs = (StridedRange) o;
		    return rhs.lo == lo && rhs.hi == hi;
		}
	}
	
	public int hashCode() {
	    return stride;
	}

}
