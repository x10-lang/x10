/*
 * Created on Nov 1, 2004
 *
 *
 */
package x10.array;
import x10.lang.point;
import x10.lang.region;
import java.util.Iterator;
import java.util.HashSet;

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
	    int val = lo + ord * stride;
	    return point.factory.point(StridedRange.this, new int[] {val});
	}
	
	public /*nat*/int ordinal(point p) {
	    assert contains(p);
	    int val = p.get(0);
	    return (val - lo) % stride;
    }
	
	public region union(region r) {
	    assert r.rank == 1;
        HashSet set = new HashSet();
        for (Iterator it = iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            set.add(p);
        }
        for (Iterator it = r.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            set.add(p);
        }
	    return new ArbitraryRegion(1, set);
	}
	
	public region intersection( region r) {
	    assert r.rank == 1;
	    HashSet set = new HashSet();
        for (Iterator it = iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            if (contains(p))
                set.add(p);
        }
	    return new ArbitraryRegion(1, set);
	}

	/** returns this range less the points that are in r. If r is not 
	 * one dimentional, then this is returned. 
	 */
	public region difference(region r) {
	    assert r != null;
        assert r.rank == rank;
        
        return super.difference(r);
	}

	public boolean contains(point p) {
		boolean ret = p.rank == 1;
	    if (ret) {
	        int val = p.get(0);
	        ret = (val >= lo) && (val <= hi) && ((val - lo) % stride) == 0;
	    }
	    return ret;
	}

	public String toString() {
	    return "[" + lo + ", " + hi + ": " + stride + "]";
	}
	
	public boolean isConvex() {
		return stride == 1;
	}
	public region convexHull() {
		if (stride == 1) 
			return this;
		return new ContiguousRange(lo, hi);
	}
    
	public boolean disjoint(region r) {
        boolean ret = false;
        if (r.rank == 1) {
            for (Iterator it = r.iterator(); ret && it.hasNext(); ) {
                point p = (point) it.next();
                if (contains(p))
                    ret = false;
            }
                        
        }
        return ret;
	}
	
	public boolean contains(region r) {
	    boolean ret = false;
	    if (r.rank == 1) {
	        ret = true;
	        for (Iterator it = r.iterator(); ret && it.hasNext(); ) {
	            if (!contains((point) it.next()))
	                ret = false;
	        }
	    } 
	    return ret;
	}
	
	public Iterator iterator() {
		return new StridedRangeIterator();
	}
	
	private class StridedRangeIterator implements Iterator {
		private int cur_ = lo;
		
		public boolean hasNext() {
			return cur_ < hi;
		}
		
		public void remove() {
			throw new Error("not implemented");
		}
		
		public java.lang.Object next() {
			assert hasNext();
			point ret = point.factory.point(StridedRange.this, new int[] {cur_});
			cur_ += stride;
			return ret;
		}
	}
}
