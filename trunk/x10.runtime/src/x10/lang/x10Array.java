package x10.lang;
import java.util.Iterator;

import x10.lang.place;

import x10.compilergenerated.Parameter1;

/* root for all array classes */
abstract public class x10Array extends x10.lang.Object implements Indexable, Unsafe, Parameter1, ValueType  {
	public final dist distribution;
	/*parameter*/ public final /*nat*/int rank /*= distribution.rank*/;
	/*parameter*/ public final region/*(rank)*/ region /*= distribution.region*/;
	protected x10Array(dist d) {
		distribution = d;
		rank = d.rank;
		region = d.region;
	}
	
	// TODO: Remove me
	public final dist getDistribution() { return distribution; }

	/**
	 * Return an iterator over the array's region.
	 */
	public final Iterator iterator() { return region.iterator(); }

	/**
	 * Return the array's distribution.
	 */
	public final dist toDistribution() { return distribution; }

	/**
	 * Create a multi-dimensional Java array.
	 */
	public abstract java.lang.Object toJava();
}
