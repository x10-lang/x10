package x10.lang;

abstract public /*value*/ class point/*(region region)*/ extends Object
	implements Indexable, ValueType
{
	public final region region;
	/*parameter*/ public final /*nat*/int rank;

	abstract public static /*value*/ class factory implements ValueType {
		/**
		 * Create a point with the given coordinates in the given region.
		 * Throws an error if the point is not in the region.
		 * @param r
		 * @param val
		 * @return
		 */
		abstract public point/*(region)*/ point(region r, int[/*rank*/] val);

		/**
		 * Return a point with the given coordinates, created in the smallest region
		 * that can enclose it.
		 */
		abstract public point point(int[/*rank*/] val);
		/**
		 * Return a point of rank 1 with the given coordinates, created in the smallest region
		 * that can enclose it.
		 */
		abstract public point point(int v1);
		/**
		 * Return a point of rank 2 with the given coordinates, created in the smallest region
		 * that can enclose it.
		 */
		abstract public point point(int v1, int v2);
		/**
		 * Return a point of rank 3 with the given coordinates, created in the smallest region
		 * that can enclose it.
		 */
		abstract public point point(int v1, int v2, int v3);
		/**
		 * Return a point of rank 4 with the given coordinates, created in the smallest region
		 * that can enclose it.
		 */
		abstract public point point(int v1, int v2, int v3, int v4);
		/**
		 * Return a point of rank 5 with the given coordinates, created in the smallest region
		 * that can enclose it.
		 */
		abstract public point point(int v1, int v2, int v3, int v4, int v5);
	}
	public static final factory factory = Runtime.factory.getPointFactory();

	protected point(region region) {
		this.region = region;
		this.rank = region.rank;
	}
	/**
	 * Return the value of this point on the i'th dimension.
	 */
	abstract public int get(/*nat*/ int i);

	/**
	 * Return true iff the point is on the upper boundary of the i'th
	 * dimension.
	 */
	abstract public boolean onUpperBoundary(/*nat*/ int i);

	/**
	 * Return true iff the point is on the lower boundary of the i'th
	 * dimension.
	 */
	abstract public boolean onLowerBoundary(/*nat*/ int i);

	abstract public boolean gt(point/*(region)*/ p);
	abstract public boolean lt(point/*(region)*/ p);
	abstract public boolean ge(point/*(region)*/ p);
	abstract public boolean le(point/*(region)*/ p);

	abstract public point/*(region)*/ neg();
	abstract public point/*(region)*/ add(point/*(region)*/ p);
	public point/*(region)*/ sub(point/*(region)*/ p) { return add(p.neg()); }
	abstract public point/*(region)*/ mul(point/*(region)*/ p);
	abstract public point/*(region)*/ div(point/*(region)*/ p);
	private static point/*(region)*/ toPoint(int rank, int c) {
		int[] a = new int[rank];
		for (int i = 0; i < rank; i++) a[i] = c;
		return factory.point(a);
	}
	public point/*(region)*/ add(int c) { return add(toPoint(rank, c)); }
	public point/*(region)*/ sub(int c) { return add(-c); }
	public point/*(region)*/ mul(int c) { return mul(toPoint(rank, c)); }
	public point/*(region)*/ div(int c) { return div(toPoint(rank, c)); }
	public point/*(region)*/ invadd(int c) { return add(c); }
	public point/*(region)*/ invsub(int c) { return add(-c).neg(); }
	public point/*(region)*/ invmul(int c) { return mul(c); }
	public point/*(region)*/ invdiv(int c) { return toPoint(rank, c).div(this); }
	abstract public int[] val();
}

