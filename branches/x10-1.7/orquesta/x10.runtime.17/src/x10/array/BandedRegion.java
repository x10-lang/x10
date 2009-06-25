/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.array;

import java.util.Iterator;

/**
 * @author Christoph von Praun
 */
public class BandedRegion extends Region {

	private final int bands_;
	private final Region[] dims_;
	private final int size_;

	/* this is the number of vars that are not in the matrix (shadow cells) */
	private final int offset_;

	/**
	 * @param dims
	 * @param bands
	 */
	public BandedRegion(Region[] dims, int bands, boolean zeroBased) {
		super(2, false,zeroBased); // rank must be == 2
		// all regions must be one dimensional and have the same size > 0
		assert (dims != null && dims.length == 2);
		int size, tmp_offset, tmp_size = dims[0].size();
		assert (tmp_size > 0);

		for (int i = 0; i < dims.length; ++i) {
			assert (dims[i] instanceof ContiguousRange);
			assert (dims[i].size() == tmp_size);
			if (zeroBased) assert(dims[i].low()==0);
		}

		// number of bands must be odd
		assert (bands >= 1 && bands % 2 == 1);
		assert (bands <= tmp_size);
		bands_ = bands;
		dims_ = dims;
		// calculate the size
		size = tmp_size;
		for (int i = bands_ - 1; i > 0; i-=2) {
			tmp_size += 2 * (size - 1);
			size --;
		}
		size_ = tmp_size;
		int tmp = (bands_ - 1) / 2;
		offset_ = (tmp * (tmp + 1)) / 2;
		//System.err.println(this);
	}

	public int size() {
		return size_;
	}

	public Region rank(int index) {
		assert index >= 0;
		assert index < 2;
		return dims_[index];
	}

	public boolean isConvex() {
		return true;
	}

	public int low() {
		throw new UnsupportedOperationException("BandedRegion::low");
	}

	public int high() {
		throw new UnsupportedOperationException("BandedRegion::high");
	}

	public Region union(Region r) {
		assert r != null;
		// ArbitraryRegion.union() will check the rank
		Region ret = ArbitraryRegion.union(this, r);
		if (ret.size() == 0)
			new EmptyRegion(2);
		return ret;
	}

	public Region intersection(Region r) {
		assert r != null;
		// ArbitraryRegion.intersection() will check the rank
		Region ret = ArbitraryRegion.intersection(this, r);
		if (ret.size() == 0)
			new EmptyRegion(2);
		return ret;
	}

	public Region difference(Region r) {
		assert r != null;
		// ArbitraryRegion.difference() will check the rank
		Region ret = ArbitraryRegion.difference(this, r);
		if (ret.size() == 0)
			new EmptyRegion(2);
		return ret;
	}

	public Region convexHull() {
		return this;
	}

	public boolean contains(Point p) {
		if (p.rank != 2)
			throw new RankMismatchException(p, 2);
		return contains_(p.get(0), p.get(1));
	}

	public boolean contains(int[] p) {
		assert (p != null);
		return (p.length == 2) && contains_(p[0], p[1]);
	}

	private boolean contains_(int a, int b) {
		int a_ordinal = a - dims_[0].low();
		int b_ordinal = b - dims_[1].low();
		boolean size_criterion =
			dims_[0].contains(new int[] {a}) &&
			dims_[1].contains(new int[] {b});
		boolean band_criterion = Math.abs(a_ordinal - b_ordinal) <= (bands_ - 1) / 2;
		return band_criterion && size_criterion;
	}

	public boolean disjoint(Region r) {
		assert r != null;
		// intersection() will check the rank
		return intersection(r).size() == 0;
	}

	public int ordinal(Point p) throws ArrayIndexOutOfBoundsException {
		assert p != null;
		if (p.rank != 2)
			throw new RankMismatchException(p, 2);
		int ret;
		if (size() == 0)
			throw new ArrayIndexOutOfBoundsException();
		if (!contains(p))
			throw new ArrayIndexOutOfBoundsException();
		else
			ret = ordinal_(p.get(0), p.get(1));
		return ret;
	}

	private int ordinal_(int x, int y) {
		int x_ordinal = x - dims_[0].low();
		int y_ordinal = y - dims_[1].low();
		int ret = x_ordinal * bands_;
		ret += (y_ordinal - x_ordinal) + ((bands_ - 1) / 2);
		ret -= offset_;
		return ret;
	}

	public Point coord(int ord) throws ArrayIndexOutOfBoundsException {
		assert ord >= 0;
		Point ret;
		int x_ordinal, y_ordinal;
		int[] tmp;
		if (ord >= size())
			throw new ArrayIndexOutOfBoundsException();
		else {
			ord = ord + offset_;
			x_ordinal = ord / bands_;
			y_ordinal = (ord % bands_) + x_ordinal - ((bands_ - 1) / 2);
			ret = Point.makeFromVarArgs(x_ordinal + dims_[0].low(), y_ordinal + dims_[1].low());
		}
		//System.err.println("coord (" + ord + ") = " + ret);
		return ret;
	}

	private class BandedRegionIterator_ implements Iterator {
		private int nextOrd_ = 0;

		public boolean hasNext() {
			return nextOrd_ < size_;
		}

		public void remove() {
			throw new UnsupportedOperationException("BandedRegionIterator_::remove - not implemented");
		}

		public java.lang.Object next() {
			assert hasNext();
			return coord(nextOrd_++);
		}
	}

	public Iterator iterator() {
		return new BandedRegionIterator_();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("banded-region(bands=" + bands_ + ", size=" + size_ + ", offset=" + offset_ + ", ");
		sb.append(dims_[0].toString());
		sb.append(", ");
		sb.append(dims_[1].toString());
		sb.append(")");
		return sb.toString();
	}

	
}

