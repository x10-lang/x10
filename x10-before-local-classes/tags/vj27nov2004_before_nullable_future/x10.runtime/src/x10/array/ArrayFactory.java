/*
 * Created on Oct 27, 2004
 */
package x10.array;

import x10.array.sharedmemory.SharedMemoryArrayFactory;
import x10.base.Runtime;

/**
 * @author Christoph von Praun
 */
public abstract class ArrayFactory {
	
	/** default array factory */
	private static final ArrayFactory af_;
	private static final Runtime ar_;
	
	static {
		// depending on the system environment, this might change
		af_ = new SharedMemoryArrayFactory();
		ar_ = ArrayRuntime.getRuntime();
	}
	
	/**
     * @return New Range.
     */
    public static Range newRange(int lo, int hi) {
    	// no abstract constructor necessary for Ranges - they are all
    	// regardless the underlying machine architecture
    	return new ContiguousRange(lo, hi);
    }
    
    public static Range newRange(int lo, int hi, int stride) {
    	return new StridedRange(lo, hi, stride);
    }
    
    /**
     * @return New Region.
     */
    public static Region newRegion(Range[] dims) {
    	return af_.makeRegion(dims);
    }
    
    public static Region newUpperTriangularRegion(int n) {
        return af_.makeUpperTriangularRegion(n);
	}
	
    public static Region newLowerTriangularRegion(int n) {
        return af_.makeLowerTriangularRegion(n);
	}
 
    public static Region newBandedRegion(int n, int k) {
        return af_.makeBandedRegion(n, k);
	}
    
    private static Distribution regionToDistribution_(Region r) {
        Distribution d;
        if (r instanceof Distribution) 
            d = (Distribution) r;
        else {
            Place[] lp = { ar_.currentPlace() };
            d = newBlockDistribution(r, lp);
        }
        return d;
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public static IntArray newIntArray(Region r) {
        return af_.makeIntArray(regionToDistribution_(r), true);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public static IntArray newIntArray(Region r, int c) {
    	return af_.makeIntArray(regionToDistribution_(r), c, true);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public static IntArray newIntArray(Region r, boolean safe) {
    	return af_.makeIntArray(regionToDistribution_(r), safe);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public static IntArray newIntArray(Region r, int c, boolean safe) {
    	return af_.makeIntArray(regionToDistribution_(r), c, safe);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public static DoubleArray newDoubleArray(Region r) {
        return af_.makeDoubleArray(regionToDistribution_(r), true);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public static DoubleArray newDoubleArray(Region r, double c) {
    	return af_.makeDoubleArray(regionToDistribution_(r), c, true);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public static DoubleArray newDoubleArray(Region r, boolean safe) {
        return af_.makeDoubleArray(regionToDistribution_(r), safe);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public static DoubleArray newDoubleArray(Region r, double c, boolean safe) {
    	return af_.makeDoubleArray(regionToDistribution_(r), c, safe);
    }
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks over all available Places.
     * @param r
     * @return
     */
    public static Distribution newBlockDistribution(Region r, Place[] q) {
    	return af_.makeBlockDistribution(r, q);
    }
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks of size n over all available Places.
     * @param r
     * @return
     */
    public static Distribution newBlockDistribution(Region r, int n, Place[] p) {
    	return af_.makeBlockDistribution(r, n, p);
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
    	return af_.makeCyclicDistribution(r,  p);
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
    	return af_.makeBlockCyclicDistribution(r, n, p);
    }
    
    /**
     * Create a Distribution where the points of the Region are
     * distributed randomly over all available Places.
     * @param r
     * @return
     */
    public static Distribution newArbitraryDistribution(Region r, Place[] p) {
    	return af_.makeArbitraryDistribution(r, p);
    }
    
    /**
     * Create a Distribution where all points in the given
     * Region are mapped to the same Place.
     * @param r
     * @param p specifically use the given place for all points
     * @return
     */
    public static Distribution newConstantDistribution(Region r, Place p) {
    	return af_.makeConstantDistribution(r, p);
    }
    
    /**
     * Create a Distribution where the points in the
     * region 1...p.length are mapped to the respective
     * places.
     * @param p the list of places (implicitly defines the region)
     * @return
     */
    public static Distribution newUniqueDistribution(Place[] p) {
    	return af_.makeUniqueDistribution(p);
    }
    
    /**
     * @return New Region.
     */
    public abstract Region makeRegion(Range[] dims);
    
    public abstract Region makeUpperTriangularRegion(int n);
	
    public abstract Region makeLowerTriangularRegion(int n);
 
    public abstract Region makeBandedRegion(int n, int k);
    
    /**
     * @return  New array with Distribution d.
     */
    public abstract IntArray makeIntArray(Distribution d, boolean safe);
    
    /**
     * @return  New array with Distribution d and initialize every element
     * with constant c.
     */
    public abstract IntArray makeIntArray(Distribution d, int c, boolean safe);
    
    /**
     * @return  New array with Distribution d.
     */
    public abstract DoubleArray makeDoubleArray(Distribution d, boolean safe); 
    
    /**
     * @return  New array with Distribution d and initialize every element
     * with constant c. 
     */
    public abstract DoubleArray makeDoubleArray(Distribution d, double c, boolean safe); 
    
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