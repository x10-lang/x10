package x10.array;

import x10.lang.point;
import x10.lang.region;

public class point_c extends point {
	
	public static class factory extends point.factory {
		public point/*(region)*/ point(region region, int[/*rank*/] val) {
			return new point_c(region,  val);
		}
		
		public point point(int[/*rank*/] val) {
			region[] dims = new region[val.length];
			for (int i=0; i<val.length;i++) 
				dims[i] = x10.lang.region.factory.region(val[i], val[i]);	
			region R = x10.lang.region.factory.region(dims);
			return point(R, val);
		}
	}
	private final int[] val;
	private final int hash_;
	
	private point_c(region region , int[] val) {
		super(region);
		assert region.rank == val.length;
		assert region.contains(val);
		this.val = val;
		
		// compute hash
		int b = 378551;
	    int a = 63689;
	    int hash_tmp = 0;
	    for (int i = 0; i < val.length; i++) {
	         hash_tmp = hash_tmp * a + val[i];
	         a = a*b;
	    }
	    hash_ = hash_tmp;
	}
	
	/** Return the value of this point on the i'th dimension.
	 */    
	public int valueAt( /*nat*/int i ) {
		return val[((int) i) % val.length];
	}
	
	/** Return true iff the point is on the upper boundary of the i'th
	 * dimension of its region.
	 */
	public boolean onUpperBoundary( /*nat*/int i ) {
		return region.rank(i).high() == valueAt(i);
	}
	
	
	/** Return true iff the point is on the lower boundary of the i'th
	 * dimension.
	 */
	public boolean onLowerBoundary( /*nat*/int i ) {
		return region.rank(i).low() == valueAt(i);
	}
	
	/* 
	 * This method overrideds superclass implementation - the argument 
	 * must be of type java.lang.Object. 
	 */
	public boolean equals(java.lang.Object o) {
	    assert o != null;
		boolean ret = false;
	    
		if (!(o instanceof point_c)) {
			point_c tmp = (point_c) o;
			if (tmp.rank == rank) {
			    ret = true;
			    for (int i = 0; i < val.length; ++i) {
			        if (val[i] != tmp.val[i]) {
			            ret = false;
			            break;
			        }
			    }
			}
		}
		return ret;
	}
	
	public int hashCode() {
	    return hash_;
	}
}
