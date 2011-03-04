/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.array;


/**
 * Represents [0:I,0:J,0:K].
 * @author vj
 */
public class Region3D0Base extends MultiDimRegion implements Region0Base {

	public final int I, J, K;

	public Region3D0Base(Region[] d) {
		super(d, true);
		assert (d.length == 3);
		assert (d[0].low() == 0 && d[1].low() == 0 && d[2].low() == 0);
		I = d[0].high(); J = d[1].high(); K = d[2].high();
	}

	/**
	 * @return range in the i-th dimension.
	 */
	public Region rank(/*nat*/int i) {
		assert i < 3;
		assert i >= 0;
		return dims_[i];
	}

	public boolean contains(Region r) {
		assert r != null;
		if (r.rank != rank)
			throw new RankMismatchException(r, rank);
		if (r instanceof Region3D0Base) {
			Region3D0Base other = (Region3D0Base) r;
			return dims_[0].high() >= other.dims_[0].high()
				&& dims_[1].high() >= other.dims_[1].high()
				&& dims_[2].high() >= other.dims_[2].high();
		}
		return super.contains(r);
	}

	// [IP] FIXME: Should we throw a RankMismatchException here?
	public boolean contains(Point p) { return contains(p.val()); }

	public boolean contains(int[] v) {
		return v.length == 3 && v[0] <= I 	&& v[1] <= J && v[2] <= K;
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
		assert (v.length == 3);
		return v[0] + (J+1)*(v[1]+(K+1)*v[2]);
	}

	public Region convexHull() { return this; }
	public int high() { throw new UnsupportedOperationException(); }
	public int low() { throw new UnsupportedOperationException(); }
	public boolean isConvex() { return true; }

	public Point coord(/*nat*/ int ordinal) throws ArrayIndexOutOfBoundsException {
		assert ordinal < size();

		int[] ret = new int[rank];
		int rest = ordinal;
		int base = 0;
		for (int i = 0; i < rank; ++i) {
			Region r = dims_[i];
			int tmp = rest / base_[i];
			rest = rest % base_[i];
			ret[i] = r.coord(tmp).get(0);
		}
		return Point.makeFromJavaArray(ret);
	}
}

