/*
 * Created on Oct 27, 2004
 */
package x10.array;

import x10.array.sharedmemory.SharedMemoryArrayFactory;
import x10.base.Place;
import x10.base.Runtime;

/**
 * @author Christoph von Praun
 */
public abstract class ArrayFactory {
	
	/** default array factory */
	private static ArrayFactory af_;
	
	private static Runtime ar_;
	
	/* this must be called exactly once when the runtime system is bootstrapped */
	public static void init(Runtime r) {
	    assert (af_ == null);
	    
	    af_ = new SharedMemoryArrayFactory();
		ar_ = r;
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
        assert (af_ == null); 
        return af_.makeRegion(dims);
    }
    
    public static Region newUpperTriangularRegion(int n) {
        assert (af_ == null);
        return af_.makeUpperTriangularRegion(n);
	}
	
    public static Region newLowerTriangularRegion(int n) {
        assert (af_ == null);
        return af_.makeLowerTriangularRegion(n);
	}
 
    public static Region newBandedRegion(int n, int k) {
        assert (af_ == null);
        return af_.makeBandedRegion(n, k);
	}
       
    /**
     * @return  New array with Distribution d.
     */
    public static IntArray newIntArray(Distribution r) {
        assert (af_ == null);    
        return af_.makeIntArray(r, true);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public static IntArray newIntArray(Distribution r, int c) {
        assert (af_ == null);    
        return af_.makeIntArray(r, c, true);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public static IntArray newIntArray(Distribution r, boolean safe) {
        assert (af_ == null);    
        return af_.makeIntArray(r, safe);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public static IntArray newIntArray(Distribution r, int c, boolean safe) {
        assert (af_ == null);    
        return af_.makeIntArray(r, c, safe);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public static DoubleArray newDoubleArray(Distribution r) {
        assert (af_ == null);    
        return af_.makeDoubleArray(r, true);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public static DoubleArray newDoubleArray(Distribution r, double c) {
        assert (af_ == null);    
        return af_.makeDoubleArray(r, c, true);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public static DoubleArray newDoubleArray(Distribution r, boolean safe) {
        assert (af_ == null);    
        return af_.makeDoubleArray(r, safe);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public static DoubleArray newDoubleArray(Distribution r, double c, boolean safe) {
        assert (af_ == null);    
        return af_.makeDoubleArray(r, c, safe);
    }
    
    public static Distribution newHereDistribution(Region r) {
        Place p = ar_.currentPlace();
        return af_.makeConstantDistribution(r, p);
    }
        
        /**
    }
     * Create a Distribution where the given Region is distributed
     * into blocks over all available Places.
     * @param r
     * @return
     */
    public static Distribution newBlockDistribution(Region r, Place[] q) {
        assert (af_ == null);    
        return af_.makeBlockDistribution(r, q);
    }
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks of size n over all available Places.
     * @param r
     * @return
     */
    public static Distribution newBlockDistribution(Region r, int n, Place[] p) {
        assert (af_ == null);    
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
        assert (af_ == null);    
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
        assert (af_ == null);    
        return af_.makeBlockCyclicDistribution(r, n, p);
    }
    
    /**
     * Create a Distribution where the points of the Region are
     * distributed randomly over all available Places.
     * @param r
     * @return
     */
    public static Distribution newArbitraryDistribution(Region r, Place[] p) {
        assert (af_ == null);    
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
        assert (af_ == null);    
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
        assert (af_ == null);    
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