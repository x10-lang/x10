/*
 * Created on Oct 3, 2004
 */
package x10.array;

import java.util.Iterator;

import x10.lang.region;
import x10.lang.point;
import x10.lang.RankMismatchException;
import x10.runtime.distributed.DeserializerBuffer;
import x10.runtime.distributed.SerializerBuffer;

/**
 * Implementation of Region. Instance of this class are immutable!
 * This class only implements a cross-product of contiguous or strided regions,
 * [lo1:hi1,...,lok:hik].
 *  @seealso point
 * @author Christoph von Praun
 * @author Christian Grothoff
 * @author vj
 */
public class MultiDimRegion extends region {

	final region[] dims_;
	final int[] base_;
	final int card;

	public MultiDimRegion(final region[] d) {
		super(d.length);
		assert d != null;
		// assert that all dims are actually Ranges
		dims_ = new region[d.length];
		for (int i = 0; i < dims_.length; ++i)
			dims_[i] = d[i];

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
	public region[] partition(int n, int dim_to_split) {
		assert (n > 0);

		if (!(dims_[dim_to_split] instanceof ContiguousRange))
			throw new Error("MultiDimRegion::partition can only block those arrays that have a contiguous dimension to split.");
		ContiguousRange cr = (ContiguousRange) dims_[dim_to_split];
		// partition fails if the dim_to_split dimension has size less than n
		if (cr.size() < n)
			throw new Error("MultiDimRegion::partition can only block those arrays that have size of dimension of the dimension to split larger than or equal to number of partitions.");
		region[] ret = new region[n];
		if (n == 1) {
			ret[0] = this;
		} else {
			region[] split_dim = cr.partition(n, 0);
			for (int i = 0; i < n; ++i) {
				if (split_dim[i].size() == 0)
					ret[i] = new EmptyRegion(rank);
				else {
					region[] new_dims = new region[rank];
					new_dims[dim_to_split] = split_dim[i];
					for (int j = 0; j < rank; ++j) {
						if (j != dim_to_split)
							new_dims[j] = dims_[j];
					}
					ret[i] = new MultiDimRegion(new_dims);
				}
			}
		}
		return ret;
	}

	public region union(region r) {
		assert r != null;
		// ArbitraryRegion.union() will check the rank
		region ret = ArbitraryRegion.union(this, r);
		if (ret.size() == 0)
			new EmptyRegion(rank);
		return ret;
	}

	public region intersection(region r) {
		assert r != null;
		// ArbitraryRegion.intersection() will check the rank
		region ret = ArbitraryRegion.intersection(this, r);
		if (ret.size() == 0)
			ret = new EmptyRegion(rank);
		return ret;
	}

	// This won't return a contiguous region.
	public region difference(region d) {
		assert d != null;
		// ArbitraryRegion.difference() will check the rank
		return ArbitraryRegion.difference(this, d);
	}

	/**
	 * @return range in the i-th dimension.
	 */
	public region rank(/*nat*/int i) {
		assert i < dims_.length;
		assert i >= 0;

		return dims_[i];
	}

	public boolean contains(region r) {
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
	public boolean contains(point p) {
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
	 * @param p A point in the region; the dimension of p must be compatible
	 *          with this region.
	 * @return the ordinal of the point in this region (its position,
	 *         where the initial constant is assigned an ordinal of zero).
	 */
	public /*nat*/int ordinal(point/*(this)*/ p) {
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

	public region convexHull() {
		region ret;
		if (isConvex()) {
			ret = this;
		} else {
			if (rank == 1) {
				ret = new ContiguousRange(low(), high());
			} else {
				region[] dims = new region[rank];
				for (int i = 0; i < rank; ++i) {
					if (dims_[i].isConvex())
						dims[i] = dims_[i];
					else
						dims[i] = dims_[i].convexHull();
				}
				ret = new MultiDimRegion(dims);
			}
		}
		return ret;
	}

	public boolean disjoint(region r) {
		boolean ret = true;
		if (r.rank == rank) {
			for (Iterator it = r.iterator(); it.hasNext(); ) {
				point p = (point) it.next();
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
		for (int i = 0; ret & i < rank && ret; ++i) {
			ret &= dims_[i].isConvex();
		}
		return ret;
	}

	public point coord(/*nat*/ int ordinal) throws ArrayIndexOutOfBoundsException {
		assert ordinal < size();

		int[] ret = new int[rank];
		int rest = ordinal;
		int base = 0;
		for (int i = 0; i < rank ; ++i) {
			region r = dims_[i];
			int tmp = rest / base_[i];
			rest = rest % base_[i];
			ret[i] = r.coord(tmp).get(0);
		}
		return point.factory.point(ret);
	}

	// [<unique id> <type>,<dim size>,[<region>,...]
	public void serialize(SerializerBuffer outputBuffer) {
		Integer originalIndex = outputBuffer.findOriginalRef(this);

		if (originalIndex == null) {
			originalIndex = new Integer(outputBuffer.getOffset());
			outputBuffer.recordRef(this,originalIndex);
		}
		else {
			outputBuffer.writeLong(originalIndex.intValue());
			return;
		}

		outputBuffer.writeLong(originalIndex.intValue());

		outputBuffer.writeLong(MULTIDIM);
		outputBuffer.writeLong(dims_.length);
		for (int i=0; i < dims_.length; ++i)
			dims_[i].serialize(outputBuffer);
	}

	/* to be called from region */
	// assume the ref(<unique id>) slot and type slot have already been read
	public static region deserializeMultiDim(DeserializerBuffer inputBuffer) {
		int numRegions = (int)inputBuffer.readLong();
		region theRegions[] = new region[numRegions];

		for (int i=0; i < numRegions; ++i)
			theRegions[i] = x10.lang.region.deserialize(inputBuffer);
		region result = factory.region(theRegions);
		return result;
	}
}

