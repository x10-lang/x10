package x10.lang;

abstract public /*value*/ class point/*( region region )*/ extends Object {
	public final region region;
	/*parameter*/ public final /*nat*/int rank;
	
	
	abstract public static /*value*/ class factory {
		/** Create a point with the given coordinates in the given region.
		 * Throws an error if the point is not in the region.
		 * @param r
		 * @param val
		 * @return
		 */
		abstract public point/*(region)*/ point(region r, int[/*rank*/] val);
		
		/** Return a point with the given coordinates, created in the smallest region
		 * that can enclose it.
		 * 
		 */
		abstract public point point(int[/*rank*/] val);
	}
	public static final factory factory = Runtime.factory.getPointFactory();
	
	protected point( region region) {
		this.region = region;
		this.rank = region.rank;
	}
	/** Return the value of this point on the i'th dimension.
	 */    
	abstract public int get( /*nat*/ int i );
	
	/** Return true iff the point is on the upper boundary of the i'th
	 * dimension.
	 */
	abstract public boolean onUpperBoundary( /*nat*/ int i );
	
	/** Return true iff the point is on the lower boundary of the i'th
	 * dimension.
	 */
	abstract public boolean onLowerBoundary( /*nat*/ int i );
	
}
