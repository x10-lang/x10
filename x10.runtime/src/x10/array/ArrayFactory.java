/*
 * Created on Oct 27, 2004
 */
package x10.array;

import x10.array.sharedmemory.SharedMemoryArrayFactory;

/**
 * @author Christoph von Praun
 */
public abstract class ArrayFactory {
	
	/** default array factory */
	static final ArrayFactory _;
	
	static {
		// depending on the system environment, this might change
		_ = new SharedMemoryArrayFactory();
	}
	
	/**
     * @return New Range.
     */
    public static Range newRange(int lo, int hi) {
    	// no abstract constructor necessary for Ranges - they are all
    	// regardless the underlying machine architecture
    	return new Range(lo, hi);
    }
    
    /**
     * @return New Region.
     */
    public static Region newRegion(Range[] dims) {
    	return _.makeRegion(dims);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public static IntArray newIntArray(Distribution d) {
    	return _.makeIntArray(d);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public static IntArray newIntArray(Distribution d, int c) {
    	return _.makeIntArray(d, c);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public static DoubleArray newDoubleArray(Distribution d) {
    	throw new RuntimeException ("not implemented");
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public static DoubleArray newDoubleArray(Distribution d, double c) {
    	return _.makeDoubleArray(d, c);
    }
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks over all available Places.
     * @param r
     * @return
     */
    public static Distribution newBlockDistribution(Region r, Place[] q) {
    	return _.makeBlockDistribution(r, q);
    }
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks of size n over all available Places.
     * @param r
     * @return
     */
    public static Distribution newBlockDistribution(Region r, int n, Place[] p) {
    	return _.makeBlockDistribution(r, n, p);
    }
    
    /**
     * Create a Distribution where the elements in the region are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the region is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    public static Distribution newCyclicDistribution(Region r, Place[] p) {
    	return _.makeCyclicDistribution(r,  p);
    }
    
    /**
     * Create a Distribution where the elements in the region are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the region is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    public static Distribution newBlockCyclicDistribution(Region r, int n, Place[] p) {
    	return _.makeBlockCyclicDistribution(r, n, p);
    }
    
    /**
     * Create a Distribution where the points of the Region are
     * distributed randomly over all available Places.
     * @param r
     * @return
     */
    public static Distribution newArbitraryDistribution(Region r, Place[] p) {
    	return _.makeArbitraryDistribution(r, p);
    }
    
    /**
     * Create a Distribution where all points in the given
     * Region are mapped to the same Place.
     * @param r
     * @param p specifically use the given place for all points
     * @return
     */
    public static Distribution newConstantDistribution(Region r, Place p) {
    	return _.makeConstantDistribution(r, p);
    }
    
    /**
     * Create a Distribution where the points in the
     * region 1...p.length are mapped to the respective
     * places.
     * @param p the list of places (implicitly defines the region)
     * @return
     */
    public static Distribution newUniqueDistribution(Place[] p) {
    	return _.makeUniqueDistribution(p);
    }
    
    /**
     * @return New Region.
     */
    public abstract Region makeRegion(Range[] dims);
    
    /**
     * @return  New array with Distribution d.
     */
    public abstract IntArray makeIntArray(Distribution d);
    
    /**
     * @return  New array with Distribution d and initialize every element
     * with constant c.
     */
    public abstract IntArray makeIntArray(Distribution d, int c);
    
    /**
     * @return  New array with Distribution d.
     */
    public abstract DoubleArray makeDoubleArray(Distribution d); 
    
    /**
     * @return  New array with Distribution d and initialize every element
     * with constant c. 
     */
    public abstract DoubleArray makeDoubleArray(Distribution d, double c); 
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks over all available Places.
     * @param r
     * @return
     */
    public abstract Distribution makeBlockDistribution(Region R, Place[] Q); 
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks of size n over all available Places.
     * @param r
     * @return
     */
    public abstract Distribution makeBlockDistribution(Region r, int n, Place[] p);
    
    /**
     * Create a Distribution where the elements in the region are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the region is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    public abstract Distribution makeCyclicDistribution(Region r, Place[] p);
    
    /**
     * Create a Distribution where the elements in the region are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the region is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    public abstract Distribution makeBlockCyclicDistribution(Region r, int n, Place[] p);
    /**
     * Create a Distribution where the points of the Region are
     * distributed randomly over all available Places.
     * @param r
     * @return
     */
    public abstract Distribution makeArbitraryDistribution(Region r, Place[] p);
    
    /**
     * Create a Distribution where all points in the given
     * Region are mapped to the same Place.
     * @param r
     * @param p specifically use the given place for all points
     * @return
     */
    public abstract Distribution makeConstantDistribution(Region r, Place p);
    
    /**
     * Create a Distribution where the points in the
     * region 1...p.length are mapped to the respective
     * places.
     * @param p the list of places (implicitly defines the region)
     * @return
     */
    public abstract Distribution makeUniqueDistribution(Place[] p);
}