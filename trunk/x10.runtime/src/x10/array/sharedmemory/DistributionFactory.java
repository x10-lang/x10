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
    public distribution cyclic(region r, Set/*<place>*/ qs) {
        assert r.rank > 0;
        final int dim_to_split = r.rank - 1;
        int sz = r.rank(dim_to_split).size();
        if (sz % qs.size() != 0)
            throw new Error("DistributionFactory::cyclic - only supported if least significant dimension has a size that is a multiple of the number of places to cycle over");
        else
            return blockCyclic(r, 1, qs);
    }
    
    /**
     * Create a Distribution where the elements in the Region_c are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the Region_c is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    public distribution blockCyclic(region r, /*nat*/int n, Set/*<place>*/ qs) {
        distribution ret;
        assert n > 0;
        assert r.rank > 0;
        
        final int dim_to_split = r.rank - 1;
        int sz = r.rank(dim_to_split).size();
        int qsize = qs.size();
        
        if (sz % (n * qsize) != 0)
            throw new Error("DistributionFactory::blockCyclic - only supported if least significant dimension has a size that is a multiple of the number of places to cycle over");
        
        Object[] q = qs.toArray();    
        if (qsize == 1) {
            place p = (place) q[0];
            ret = new Distribution_c.Constant(r, p);
        } else {
            // partitioning done along dimension dim_to_split
            int chunks = (sz % n == 0 || sz < n) ? (sz / n) : ((sz / n) + 1);
            assert chunks > 0;
            
            Distribution_c[] dists = new Distribution_c[chunks];       
            region[] sub = r.partition(chunks, dim_to_split); 
            for (int i=0; i < chunks; i++) {
                dists[i] = new Distribution_c.Constant(sub[i], (place) q[i % q.length]);
            }
            ret = new Distribution_c.Combined(r, dists);
        }
        return ret;
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
        assert n > 0;
        
        final int dim_to_split = 0; //r.rank - 1;
        distribution ret = null;
        int sz = r.rank(dim_to_split).size();
        
        if (sz < n)
           throw new Error("DistributionFactory::block - blocking only supported along the most significant dimension and blocking factor must be lower than or equal to the size of that dimension.");
        
        Object[] q = qs.toArray();         
        if (n == 1) { 
            place p = (place) q[0];
            ret = new Distribution_c.Constant(r, p);
        } else {
            // partition along dimension dim_to_split           
            region sub[] = r.partition(n, dim_to_split);
            Distribution_c[] dists = new Distribution_c[n];
            for (int i=0; i < n; i++) 
                dists[i] = new Distribution_c.Constant(sub[i], (place) q[i]);
            ret =  new Distribution_c.Combined(r, dists);
        } 
        return ret;
	}
    
    
    /**
     * Create a Distribution where the points of the Region are
     * distributed randomly over all available Places.
     * @param r
     * @return
     */
    public distribution arbitrary(region r) {
        distribution ret;
        int blocksize = r.size() / x10.lang.place.places.size();
        if (blocksize == 0) 
            ret = constant(r, x10.lang.place.FIRST_PLACE);
        else
            ret = blockCyclic(r, blocksize, x10.lang.place.places);
        return ret;
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