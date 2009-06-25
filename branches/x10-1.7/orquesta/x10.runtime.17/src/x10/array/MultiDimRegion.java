/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 3, 2004
 */
package x10.array;

import java.util.Iterator;


/**
 * Implementation of Region. Instance of this class are immutable!
 * This class only implements a cross-product of contiguous or strided regions,
 * [lo1:hi1,...,lok:hik].
 *  @seealso Point
 * @author Christoph von Praun
 * @author Christian Grothoff
 * @author vj
 */
public class MultiDimRegion extends Region implements Rectangular {

	final Region[] dims_;
	final int[] base_;
	final int card;

	public MultiDimRegion(final Region[] d, boolean zeroBased) {
		super(d.length, true,zeroBased);
		assert d != null;
		// assert that all dims are actually Ranges
		dims_ = new Region[d.length];
		for (int i = 0; i < dims_.length; ++i) {
			// this assertion is too sharp -- e.g. Constructs/DepTypes/StaticReturn
			// assert d[i] instanceof Range;
			dims_[i] = d[i];
			if (zeroBased) assert d[i].zeroBased;
		}

		
		int tmp_card = 1;
		base_ = new int[dims_.length];
		// row major ordering (C conventions)
		for (int i = rank-1; i >= 0; --i) {
			base_[i] = tmp_card;
			tmp_card *= dims_[i].size();
		}
		card = tmp_card;
	}
	/** Creates zero based region, zero to d[i], inclusive. */
	public MultiDimRegion(final int[] d) {
		super(d.length, true, true);
		assert d != null;
		// assert that all dims are actually Ranges
		dims_ = new Region[d.length];
		for (int i = 0; i < dims_.length; ++i)
			dims_[i] = new ContiguousRange(d[i]);

		int tmp_card = 1;
		base_ = new int[dims_.length];
		// row major ordering (C conventions)
		for (int i = rank-1; i >= 0; --i) {
			base_[i] = tmp_card;
			tmp_card *= dims_[i].size();
		}
		card = tmp_card;
	}

	/**
	 * Creates a subregion from this region
	 * @param partitions
	 * @param part
	 * @return The new sub-region.
	 * TODO: vj -- check the intended logic survived the rework.
	 */
	public Region[] partition(int n, int dim_to_split) {
		assert (n > 0);

		if (!(dims_[dim_to_split] instanceof ContiguousRange))
			throw new Error("MultiDimRegion::partition can only block those arrays that have a contiguous dimension to split.");
		ContiguousRange cr = (ContiguousRange) dims_[dim_to_split];
		// partition fails if the dim_to_split dimension has size less than n
		if (cr.size() < n)
			throw new Error("MultiDimRegion::partition can only block those arrays that have size of dimension of the dimension to split larger than or equal to number of partitions.");
		Region[] ret = new Region[n];
		if (n == 1) {
			ret[0] = this;
		} else {
			Region[] split_dim = cr.partition(n, 0);
			for (int i = 0; i < n; ++i) {
				if (split_dim[i].size() == 0)
					ret[i] = new EmptyRegion(rank);
				else {
					boolean zeroBased = true;
					Region[] new_dims = new Region[rank];
					new_dims[dim_to_split] = split_dim[i];
					zeroBased &= new_dims[dim_to_split].zeroBased;
					for (int j = 0; j < rank; ++j) {
						if (j != dim_to_split) {
							new_dims[j] = dims_[j];
							zeroBased &= new_dims[j].zeroBased;
						}
					}
					ret[i] = new MultiDimRegion(new_dims, zeroBased);
				}
			}
		}
		return ret;
	}

	public Region union(Region r) {
		assert r != null;
		// ArbitraryRegion.union() will check the rank
		Region ret = ArbitraryRegion.union(this, r);
		if (ret.size() == 0)
			new EmptyRegion(rank);
		return ret;
	}

	public Region intersection(Region r) {
		assert r != null;
		// ArbitraryRegion.intersection() will check the rank
		Region ret = ArbitraryRegion.intersection(this, r);
		if (ret.size() == 0)
			ret = new EmptyRegion(rank);
		return ret;
	}

	// This won't return a contiguous region.
	public Region difference(Region d) {
		assert d != null;
		Region ret;
		
		if (d instanceof MultiDimRegion && rank == 2 && d.rank == 2) {
			// specialization for 2D 
			MultiDimRegion d2 = (MultiDimRegion) d;			
			Region[] tmp = null;
			if (dims_[0].equals(d2.dims_[0])) { 
				tmp = new Region[2];
				tmp[0] = dims_[0];
				tmp[1] = dims_[1].difference(d2.dims_[1]);
			} else if (dims_[1].equals(d2.dims_[1])) {
				tmp = new Region[2];
				tmp[1] = dims_[1];
				tmp[0] = dims_[0].difference(d2.dims_[0]);
			} 
			if (tmp != null 
					&& tmp[0] instanceof ContiguousRange 
					&& tmp[1] instanceof ContiguousRange) 
				return new MultiDimRegion(tmp, false);
		}
		// ArbitraryRegion.difference() will check the rank
		return ArbitraryRegion.difference(this, d);
	}

	/**
	 * @return range in the i-th dimension.
	 */
	public Region rank(/*nat*/int i) {
		assert i < dims_.length;
		assert i >= 0;

		return dims_[i];
	}

	public boolean contains(Region r) {
		assert r != null;
		if (r.rank != rank)
			throw new RankMismatchException(r, rank);

		boolean ret = true;
		if (r instanceof MultiDimRegion) {
			MultiDimRegion r_c = (MultiDimRegion) r;
			for (int i = 0; i < r_c.rank && ret; ++i)
				ret = dims_[i].contains(r_c.dims_[i]);
		} else
			ret = super.contains(r);
		return ret;
	}

	// [IP] FIXME: should we throw a RankMismatchException here?
	public boolean contains(Point p) {
		boolean ret;
		if (p.rank == rank) {
			ret = true;
			for (int i = 0; ret && i < rank; ++i) {
				int[] coord = {p.get(i)};
				ret = dims_[i].contains(coord);
			}
		} else
			ret = false;
		return ret;
	}

	public boolean contains(int[] val) {
		boolean ret;
		if (val.length == rank) {
			ret = true;
			for (int i = 0; ret && i < rank; ++i) {
				int[] coord = {val[i]};
				ret = dims_[i].contains(coord);
			}
		} else
			ret = false;
		return ret;
	}

	public /*nat*/int size() {
		return card;
	}

	/**
	 * @param p A Point in the region; the dimension of p must be compatible
	 *          with this region.
	 * @return the ordinal of the Point in this region (its position,
	 *         where the initial constant is assigned an ordinal of zero).
	 */
	public /*nat*/int ordinal(Point/*(this)*/ p) {
		if (p.rank != rank)
			throw new RankMismatchException(p, rank);

		/*nat*/int ret = 0;
		int base = 1;
		for (int i = p.rank -1 ; i >= 0; --i) {
			Range r = (Range) dims_[i];
			ret += r.ordinal(p.get(i)) * base;
			base *= r.size;
		}
		return ret;
	}

	/**
	 * @return Iterator that yields the individual points of a region
	 * in lexicographical * order. Points are specified as arrays of
	 * int.
	 */
	public Iterator iterator() {
		return new MultiDimRegionIterator_();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		for (int i = 0; i < rank; ++i) {
			sb.append(dims_[i].toString());
			if (i < rank - 1)
				sb.append(",");
		}
		sb.append("}");
		return sb.toString();
	}

	private class MultiDimRegionIterator_ implements Iterator {
		private int nextOrd_ = 0;

		public boolean hasNext() {
			return nextOrd_ < card;
		}

		public void remove() {
			throw new Error("not implemented");
		}

		public java.lang.Object next() {
			assert hasNext();
			return coord(nextOrd_++);
		}
	}

	public Region convexHull() {
		Region ret;
		if (isConvex()) {
			ret = this;
		} else {
			if (rank == 1) {
				ret = new ContiguousRange(low(), high());
			} else {
			
				Region[] dims = new Region[rank];
				for (int i = 0; i < rank; ++i) {
					if (dims_[i].isConvex())
						dims[i] = dims_[i];
					else
						dims[i] = dims_[i].convexHull();
					
					
				}
				ret = new MultiDimRegion(dims, zeroBased);
			}
		}
		return ret;
	}

	public boolean disjoint(Region r) {
		boolean ret = true;
		if (r.rank == rank) {
			for (Iterator it = r.iterator(); it.hasNext(); ) {
				Point p = (Point) it.next();
				if (contains(p)) {
					ret = false;
					break;
				}
			}
		}
		return ret;
	}

	public int high() {
		if (rank != 1)
			throw new UnsupportedOperationException();
		else
			return dims_[0].high();
	}

	public int low() {
		if (rank != 1)
			throw new UnsupportedOperationException();
		else
			return dims_[0].low();
	}

	public boolean isConvex() {
		boolean ret = true;
		for (int i = 0; i < rank && ret; ++i) {
			ret &= dims_[i].isConvex();
		}
		return ret;
	}

	public Point coord(/*nat*/ int ordinal) throws ArrayIndexOutOfBoundsException {
		assert ordinal < size();

		int[] ret = new int[rank];
		int rest = ordinal;
		int base = 0;
		for (int i = 0; i < rank ; ++i) {
			Region r = dims_[i];
			int tmp = rest / base_[i];
			rest = rest % base_[i];
			ret[i] = r.coord(tmp).get(0);
		}
		return Point.makeFromJavaArray(ret);
	}

	
	
}

