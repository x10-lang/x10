/*
 * Created on Oct 23, 2004
 */
package x10.array.sharedmemory;

import x10.array.Distribution;
import x10.array.DoubleArray;
import x10.array.IntArray;
import x10.array.Place;
import x10.array.Range;
import x10.array.Region;
import x10.array.ArrayFactory;

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
    
    /**
     * @return  New array with Distribution d.
     */
    public IntArray makeIntArray(Distribution d) {
    	// if the distribution has dimension1, 2, 3, 
    	// we can be much smarter here and return an 
    	// instance of a specialized class. 
    	return new IntArray_c((Distribution_c)d);
    }
    
    public IntArray makeIntArray(Distribution d, int c) {
    	// if the distribution has dimension1, 2, 3, 
    	// we can be much smarter here and return an 
    	// instance of a specialized class. 
    	return new IntArray_c((Distribution_c)d, c);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public DoubleArray makeDoubleArray(Distribution d) {
    	throw new RuntimeException("not implemented");
    }
    
    public DoubleArray makeDoubleArray(Distribution d, double c) {
    	throw new RuntimeException("not implemented");
    }
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks over all available Places.
     * @param r
     * @return
     */
    public Distribution makeBlockDistribution(Region R, Place[] Q) {
    	return Distribution_c.makeBlock(R, Q);
    }
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks of size n over all available Places.
     * @param r
     * @return
     */
    public Distribution makeBlockDistribution(Region r, int n, Place[] p) {
    	return Distribution_c.makeBlock(r, n, p);
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
    	return Distribution_c.makeCyclic(r,  p);
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
    	return Distribution_c.makeBlockCyclic(r, n, p);
    }
    
    /**
     * Create a Distribution where the points of the Region are
     * distributed randomly over all available Places.
     * @param r
     * @return
     */
    public Distribution makeArbitraryDistribution(Region r, Place[] p) {
    	return Distribution_c.makeArbitrary(r, p);
    }
    
    /**
     * Create a Distribution where all points in the given
     * Region are mapped to the same Place.
     * @param r
     * @param p specifically use the given place for all points
     * @return
     */
    public Distribution makeConstantDistribution(Region r, Place p) {
    	return Distribution_c.makeConstant(r, p);
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
