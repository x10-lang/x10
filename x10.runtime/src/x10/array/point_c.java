package x10.array;

import x10.lang.point;
import x10.lang.region;

public class point_c extends point {
	
	public static class factory extends point.factory {
		public point/*(region)*/ point(region region, int[/*rank*/] val) {
			return new point_c(region,  val);
		}
	}
	final int[] val;
	private point_c(region region , int[] val) {
		super(region);
		assert region.rank == val.length;
		// vj: need to make sure that this point is in the region...
		this.val = val;
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
	
}
