/*
 * Created on Oct 23, 2004
 */
package x10.array;

import x10.array.Place;
import x10.compilergenerated.DoubleArray_c;
import x10.compilergenerated.IntArray_c;

/**
 * Abstract Factory for Array related classes.
 * 
 * @author Christoph von Praun
 */
public class ArrayFactory {
	
    /**
     * @return New Range.
     */
    public static Range newRange(int lo, int hi) {
    	return new Range_c(lo, hi);
    }
    
    /**
     * @return New Region.
     */
    public static Region newRegion(Range[] dims) {
    	return new Region_c(dims);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public static IntArray newIntArray(Distribution d) {
    	return new IntArray_c((Distribution_c)d);
    }
    
    /**
     * @return  New array with Distribution d.
     */
    public static DoubleArray newDoubleArray(Distribution d) {
    	return new DoubleArray_c((Distribution_c)d);
    }
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks over all available Places.
     * @param r
     * @return
     */
    public static Distribution newBlockDistribution(Region R, Place[] Q) {
    	return Distribution_c.makeBlock(R, Q);
    }
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks of size n over all available Places.
     * @param r
     * @return
     */
    public static Distribution newBlockDistribution(Region r, int n, Place[] p) {
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
    public static Distribution newCyclicDistribution(Region r, Place[] p) {
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
    public static Distribution newBlockCyclicDistribution(Region r, int n, Place[] p) {
    	return Distribution_c.makeBlockCyclic(r, n, p);
    }
    
    /**
     * Create a Distribution where the points of the Region are
     * distributed randomly over all available Places.
     * @param r
     * @return
     */
    public static Distribution newArbitraryDistribution(Region r, Place[] p) {
    	return Distribution_c.makeArbitrary(r, p);
    }
    
    /**
     * Create a Distribution where all points in the given
     * Region are mapped to the same Place.
     * @param r
     * @param p specifically use the given place for all points
     * @return
     */
    public static Distribution newConstantDistribution(Region r, Place p) {
    	return Distribution_c.makeConstant(r, p);
    }
    
    /**
     * Create a Distribution where the points in the
     * region 1...p.length are mapped to the respective
     * places.
     * @param p the list of places (implicitly defines the region)
     * @return
     */
    public static Distribution newUniqueDistribution(Place[] p) {
    	return Distribution_c.makeUnique(p);
    }
}
