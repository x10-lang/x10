package x10.lang;

import java.util.Iterator;

import x10.compilergenerated.Parameter1;

/**
 * 
 * @author xinb
 *
 */
abstract public class x10Array implements Indexable, Unsafe, Parameter1 {
	/**
	 * Taking into consideration the cluster case, 'distribution' and 'region' 
	 * are meant to be global.  However, there is a window, during which they
	 * are set to values of a local view, that is during array constructor call.
	 * XXX It might be best to get rid of this confusion.
	 * 
	 * @author xinb 
	 */
	public /*final*/ dist distribution;
	public final int rank;
	public /*final*/ region region;
	
	/**
	 * In a cluster environment, we need to distinguish a "local" and a "global"
	 * view of the arrays.  "local" view mostly are used inside the runtime for
	 * memory management and element access; "global" view are what the programmer
	 * sees the array.
	 * 
	 * @author xinb
	 */
	protected final region localRegion;
	protected final dist localDist;
	
	
	protected x10Array(dist D) {
		this.distribution = this.localDist = D;
		this.region = this.localRegion = D.region;
		this.rank = D.rank;
	}
	
	/**
	 * @return  global 'region' of this x10 array.
	 */
	public region getRegion() { return region; }
	
	/**
	 * @return global 'distribution' of this x10 array.
	 */
	public dist getDistribution() { return distribution;}

	/**
	 * @return  an iterator for the global view of this x10 array, i.e., region.iterator.
	 */
	public Iterator iterator() {
		return region.iterator();
	}	
}
