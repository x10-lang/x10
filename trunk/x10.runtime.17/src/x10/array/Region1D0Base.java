package x10.array;

/*
*
* (C) Copyright IBM Corporation 2006
*
*  This file is part of X10 Language.
*
*/


/**
* Represents [0:I].
* @author vj
*/
public class Region1D0Base extends MultiDimRegion implements Region0Base {

	public final int I;

	public Region1D0Base(Region[] d) {
		super(d, true);
		assert (d.length == 1);
		assert (d[0].low() == 0);
		I = d[0].high(); 
	}

	/**
	 * @return range in the i-th dimension.
	 */
	public Region rank(/*nat*/int i) {
		assert i == 0;
		return dims_[i];
	}

	public boolean contains(Region r) {
		assert r != null;
		if (r.rank != rank)
			throw new RankMismatchException(r, rank);
		if (r instanceof Region1D0Base) {
			Region1D0Base other = (Region1D0Base) r;
			return dims_[0].high() >= other.dims_[0].high()
				;
		}
		return super.contains(r);
	}

	// [IP] FIXME: Should we throw a RankMismatchException here?
	public boolean contains(Point p) { return contains(p.val()); }

	public boolean contains(int[] v) {
		return v.length == 1 && v[0] <= I;
	}

	/**
	 * @param p A point in the region; the dimension of p must be compatible
	 *          with this region.
	 * @return Returns the ordinal of the point in this region (its position,
	 *         where the initial constant is assigned an ordinal of zero).
	 */
	// [IP] FIXME: Should we throw a RankMismatchException here?
	public /*nat*/int ordinal(Point/*(this)*/ p) { return ordinal(p.val()); }

	public int ordinal(int[] v) {
		assert (v.length == 1);
		return v[0];
	}

	public Region convexHull() { return this; }
	public int high() { throw new UnsupportedOperationException(); }
	public int low() { throw new UnsupportedOperationException(); }
	public boolean isConvex() { return true; }

	public Point coord(/*nat*/ int ordinal) throws ArrayIndexOutOfBoundsException {
		assert ordinal < size();

		int[] ret = new int[]{ ordinal};
		return Point.makeFromJavaArray(ret);
	}
}

