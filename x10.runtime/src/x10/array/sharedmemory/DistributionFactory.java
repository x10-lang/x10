package x10.array.sharedmemory;

import java.util.Set;

import x10.array.ContiguousRange;
import x10.array.MultiDimRegion;
import x10.lang.distribution;
import x10.lang.place;
import x10.lang.region;

public class DistributionFactory extends distribution.factory {
	
	public DistributionFactory() {
	 super();
	}
	/**
     * Create a Distribution where the elements in the Region_c are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the Region_c is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    public distribution cyclic(region r, Set/*<place>*/ q) {
        return blockCyclic(r, q.size(), q);
    }
    
    /**
     * Create a Distribution where the elements in the Region_c are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the Region_c is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    public distribution blockCyclic(region r, /*nat*/int n, Set/*<place>*/ q) {
        throw new Error("TODO");
    }
    public distribution blockCyclic(region r, /*nat*/ int p) {
        return blockCyclic(r, p, x10.lang.place.places);
    }

    public distribution random(region r) {
    		return  cyclic(r);
    	
    }
     
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks the specified places
     * @param r Region
     * @param q The set of Places
     * @return
     */
	public distribution block(region r, Set/*<place>*/ q) {
	    return block(r, q.size(), q);
    }
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks over the first n Places.
     * @param r
     * @return
     */
	public distribution block(region r, int n, Set/*<place>*/ qs) {
        assert n <= qs.size();
        
        // avoid all the hardwork if it is an empty region.
        if (r.size() == 0)
        	return new Distribution_c.Empty( r.rank );
        
        Object[] q = qs.toArray();

        if (r instanceof MultiDimRegion) {
            MultiDimRegion reg = (MultiDimRegion) r;
            region first_d = reg.rank(0);
            if (! (first_d instanceof ContiguousRange)) 
                throw new Error("DistributionFactory::block can only block those arrays that have contiguos dimension 0.");
            if (first_d.size() % n != 0) 
                throw new Error("DistributionFactory::block can't block array with " + first_d.size() + " rows in " + n + " parts.");
                
        	Distribution_c[] dists = new Distribution_c[n];
        	for (int i=0; i < n; i++) 
        		dists[i] = new Distribution_c.Constant(((MultiDimRegion) r).sub(n, i), (place) q[i]);
        	return new Distribution_c.Combined((MultiDimRegion) r, dists);
        } else
            throw new Error("DistributionFactory::block not supported for the given shape of distribution.");
	}
    
    
    /**
     * Create a Distribution where the points of the Region are
     * distributed randomly over all available Places.
     * @param r
     * @return
     */
    public distribution arbitrary(region r) {
        return blockCyclic(r, 32, x10.lang.place.places);
    }
    
    /**
     * Create a Distribution where all points in the given
     * Region are mapped to the same Place.
     * @param r
     * @param p specifically use the given place for all points
     * @return
     */
    public distribution constant(region r, place p) {
        return new Distribution_c.Constant(r, p);
    }
    
    /**
     * Create a Distribution where the points in the
     * Region_c 1...p.length are mapped to the respective
     * places.
     * @param p the list of places (implicitly defines the Region_c)
     * @return
     */
    public distribution unique(Set/*<place>*/ p) {
    	Object[] places = p.toArray();
    	place[] ps = new place[places.length];
    	for (int i=0;i<places.length;i++)
    		ps[i] = (place) places[i];
    	return new Distribution_c.Unique(ps);
    }

}