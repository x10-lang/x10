/*
 * Created on Oct 23, 2004
 */
package x10.array.sharedmemory;

import x10.array.Distribution;
import x10.array.DoubleArray;
import x10.array.IntArray;
import x10.array.Range;
import x10.array.Region;
import x10.array.ArrayFactory;
import x10.base.Place;

/**
 * Abstract Factory for Array related classes.
 * 
 * @author Christoph von Praun
 */
public class SharedMemoryArrayFactory extends ArrayFactory {
	
    /**
     * @return New Region.
     */
    public Region makeRegion(Range[] dims) {
    	return new Region_c(dims);
    }
    
    public Region makeUpperTriangularRegion(int n) {
        return Region_c.makeUpperTriangular(n);
    }
	
    public Region makeLowerTriangularRegion(int n) {
        return Region_c.makeLowerTriangular(n);
    }
 
    public Region makeBandedRegion(int n, int k) {
        return Region_c.makeBanded(n, k);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public IntArray makeIntArray(Distribution d, boolean safe) {
    	// if the distribution has dimension1, 2, 3, 
    	// we can be much smarter here and return an 
    	// instance of a specialized class. 
    	return new IntArray_c((Distribution_c)d, safe);
    }
    
    public IntArray makeIntArray(Distribution d, int c, boolean safe) {
    	// if the distribution has dimension1, 2, 3, 
    	// we can be much smarter here and return an 
    	// instance of a specialized class. 
    	return new IntArray_c((Distribution_c)d, c, safe);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public DoubleArray makeDoubleArray(Distribution d, boolean safe) {
        return new DoubleArray_c((Distribution_c)d, safe);
    }
    
    public DoubleArray makeDoubleArray(Distribution d, double c, boolean safe) {
        return new DoubleArray_c((Distribution_c)d, c, safe);
    }
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks over all available Places.
     * @param r
     * @return
     */
    public Distribution makeBlockDistribution(Region r, Place[] q) {
        assert r instanceof Region_c;
        return Distribution_c.makeBlock((Region_c) r, q);
    }
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks of size n over all available Places.
     * @param r
     * @return
     */
    public Distribution makeBlockDistribution(Region r, int n, Place[] p) {
        assert r instanceof Region_c;
        return Distribution_c.makeBlock((Region_c) r, n, p);
    }
    
    /**
     * Create a Distribution where the elements in the region are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the region is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    public Distribution makeCyclicDistribution(Region r, Place[] p) {
    	assert r instanceof Region_c;
        return Distribution_c.makeCyclic((Region_c) r,  p);
    }
    
    /**
     * Create a Distribution where the elements in the region are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the region is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    public Distribution makeBlockCyclicDistribution(Region r, int n, Place[] p) {
        assert r instanceof Region_c;
        return Distribution_c.makeBlockCyclic((Region_c)r, n, p);
    }
    
    /**
     * Create a Distribution where the points of the Region are
     * distributed randomly over all available Places.
     * @param r
     * @return
     */
    public Distribution makeArbitraryDistribution(Region r, Place[] p) {
        assert r instanceof Region_c;
        return Distribution_c.makeArbitrary((Region_c)r, p);
    }
    
    /**
     * Create a Distribution where all points in the given
     * Region are mapped to the same Place.
     * @param r
     * @param p specifically use the given place for all points
     * @return
     */
    public Distribution makeConstantDistribution(Region r, Place p) {
        assert r instanceof Region_c;
        return Distribution_c.makeConstant((Region_c)r, p);
    }
    
    /**
     * Create a Distribution where the points in the
     * region 1...p.length are mapped to the respective
     * places.
     * @param p the list of places (implicitly defines the region)
     * @return
     */
    public Distribution makeUniqueDistribution(Place[] p) {
    	return Distribution_c.makeUnique(p);
    }
}
