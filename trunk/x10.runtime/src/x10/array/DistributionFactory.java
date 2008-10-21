/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.array;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Arrays;
import java.lang.Comparable;

import x10.lang.dist;
import x10.lang.place;
import x10.lang.region;
import x10.lang.point;
import x10.array.ContiguousRange;
import x10.array.MultiDimRegion;
import x10.lang.Runtime;

public class DistributionFactory extends dist.factory {
	
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
	public dist cyclic(region r, Set/*<place>*/ qs) {
		assert r.rank > 0;
		final int dim_to_split = r.rank - 1;
		int sz = r.rank(dim_to_split).size();
		//if (sz % qs.size() != 0)
		//  throw new Error("DistributionFactory::cyclic - only supported if least significant dimension has a size that is a multiple of the number of places to cycle over");
		//else
		return blockCyclic(r, 1, qs);
	}
	
	public dist blockCyclic(region r, /*nat*/ int p) {
		return blockCyclic(r, p, x10.lang.place.places);
	}
	
	/**
	 * Create a Distribution where the elements in the Region_c are
	 * distributed over all Places in p in a cyclic manner,
	 * that is the next point in the Region_c is at the next place
	 * for a cyclic ordering of the given places.
	 * @param r
	 * @return
	 */
	public dist blockCyclic(region r, /*nat*/int n, Set/*<place>*/ qs) {
		dist ret;
		assert n > 0;
		assert r.rank > 0;
		
		final boolean dim_splittable = r instanceof ContiguousRange || r instanceof MultiDimRegion;
		final int dim_to_split = r.rank - 1;
		int sz = r.rank(dim_to_split).size();
		int qsize = qs.size();
		assert (qsize > 0);
		
		//if (sz % (n * qsize) != 0)
		//  throw new Error("DistributionFactory::blockCyclic - only supported if least significant dimension has a size that is a multiple of the number of places to cycle over");
		Object[] q = qs.toArray();
		// actually qs should be a sorted set of places - here we sort the in global order 
		assert (q[0] instanceof Comparable);
		
		Arrays.sort(q);
		if (qsize == 1) {
			place p = (place) q[0];
			ret = new Distribution_c.Constant(r, p);
		} else if (sz % (n * qsize) == 0 && dim_splittable) {
			// partitioning done along dimension dim_to_split
			int chunks = (sz % n == 0 || sz < n) ? (sz / n) : ((sz / n) + 1);
			assert chunks > 0;
			Distribution_c[] dists = new Distribution_c[chunks];       
			region[] sub = r.partition(chunks, dim_to_split); 
			for (int i=0; i < chunks; i++) {
				dists[i] = new Distribution_c.Constant(sub[i], (place) q[i % q.length]);
			}
			ret = new Distribution_c.Combined(r, dists, q.length==1 ? (place) q[0] : null);
			
		} else if (sz == n && dim_to_split > 0 && qsize == r.rank(dim_to_split-1).size()){
			// blocking entire rows
			// currently only support it if virtual mapping function is a simple offset ie
			// number of rows must equal number of places
			
			int adjustment=0;
			// FIXME n should be == num of places, but if x10c config differs from x10
			// then it won't be
			int adjustmentOffset[] = new int[Runtime.places().length];
			int chunks = r.rank(dim_to_split-1).size();;
			Distribution_c[] dists = new Distribution_c[chunks];
			region[] sub = r.partition(chunks,dim_to_split - 1);//split along rows
			for(int i=0;i < chunks;++i){
				int placeId = ((place)q[i%chunks]).id;
				adjustmentOffset[placeId] = adjustment;
				
				//System.err.println("set adjustment to:"+adjustment);
				adjustment+= sub[i].size();
				dists[i] = new Distribution_c.Constant(sub[i],(place)q[i%chunks]);
			}
			ret = new Distribution_c.Combined(r,dists, q.length==1?(place) q[0] : null);
			
			
			
		} else {
			ret = blockCyclicHelper_(r, n, q);
		}
		
		return ret;
	}
	
	private Distribution_c.Arbitrary blockCyclicHelper_(region r, int bf, Object[] places) {
		assert bf > 0;
		HashMap hm = new HashMap();
		int offset = 0;
		for (Iterator it = r.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			hm.put(p, places[(offset / bf) % places.length]);
			offset++;
		}
		Distribution_c.Arbitrary ret = new Distribution_c.Arbitrary(r, hm, 
				places.length==1? (place) places[0] : null); 
		
		return ret;
	}
	
	
	public dist random(region r) {
		return  cyclic(r);
		
	}
	
	/**
	 * Create a Distribution where the given Region is distributed
	 * into blocks the specified places
	 * @param r Region
	 * @param q The set of Places
	 * @return
	 */
	public dist block(region r, Set/*<place>*/ q) {
		return block(r, q.size(), q);
	}
	/**
	 * Create a Distribution where the given tiled region r is distributed
	 * into blocks over the given q places. 
	 * For now we require the programmer to provide the base region, base,
	 * which is intended to be split into r as well. It is the 
	 * programmer's responsibility to ensure that r partitions base.
	 * @param r
	 * @return
	 */
	public dist block(region base, region[] r, Set/*<place>*/ q) {
		return block(base, r, q.size(), q);
	}
	
	
	public dist block(region base, region[] r, int n, Set/*<place>*/ qs) {
		assert (n > 0);
		if ( r.length != n)
		throw new Error("Not implemented yet.");
		Distribution_c[] dists = new Distribution_c[n];
		Object[] q = qs.toArray();
		
		for (int i=0; i < n; i++) {
			dists[i]= new Distribution_c.Constant(r[i],(place)q[i]);
		}
		return new Distribution_c.Combined(base,dists, null);
	}
	/**
	 * Create a Distribution where the given Region is distributed
	 * into blocks over the first n Places.
	 * @param r
	 * @return
	 */
	public dist block(region r, int n, Set/*<place>*/ qs) {
		assert n <= qs.size();
		assert n > 0;
		
		final boolean dim_splittable = r instanceof ContiguousRange || r instanceof MultiDimRegion;
		final int dim_to_split = 0; //r.rank - 1;
		dist ret = null;
		int sz = r.rank(dim_to_split).size();
		
		//if (sz < n)
		// throw new Error("DistributionFactory::block - blocking only supported along the most significant dimension and blocking factor must be lower than or equal to the size of that dimension.");
		
		Object[] q = qs.toArray();
		//      actually qs should be a sorted set of places - here we sort the in global order 
		assert (q[0] instanceof Comparable);
		
		Arrays.sort(q);
		if (n == 1) { 
			place p = (place) q[0];
			ret = new Distribution_c.Constant(r, p);
		} else if (sz >= n && sz % n == 0 && dim_splittable) {
			// partition along dimension dim_to_split           
			region sub[] = r.partition(n, dim_to_split);
			
			Distribution_c[] dists = new Distribution_c[n];
			int adjustment=0;
			// FIXME n should be == num of places, but if x10c config differs from x10
			// then it won't be
			int adjustmentOffset[] = new int[Runtime.places().length];
			for (int i=0; i < n; i++) {
				int placeId = ((place)q[i]).id;
				adjustmentOffset[placeId] = adjustment;
				
				//System.err.println("set adjustment to:"+adjustment);
				adjustment+= sub[i].size();
				dists[i] = new Distribution_c.Constant(sub[i], (place) q[i]);
			}
			ret =  new Distribution_c.Combined(r, dists, n==1? (place) q[0] : null);
			
		} else {
			
			ret = blockHelper_(r, n, q);
		}
		
		return ret;
	}
	
	private Distribution_c.Arbitrary blockHelper_(region r, int nb, Object[] places) {
		assert nb > 0 && nb <= places.length;
		int total_points = r.size();
		int p = total_points / nb;
		int q = total_points % nb;
		
		int adjustment=0;
		int adjustmentOffset[] = new int[Runtime.places().length];
		
		HashMap hm = new HashMap();
		int offsWithinPlace = 0;
		int blockNum = 0;
		for (Iterator it = r.iterator(); it.hasNext(); ) {
			point pt = (point) it.next();
			hm.put(pt, places[blockNum]);
			offsWithinPlace++;
			++adjustment;
			if (offsWithinPlace == (p + ((blockNum < q) ? 1 : 0))) {
				offsWithinPlace=0; // start next block
				blockNum++;
				if(blockNum < places.length)
					adjustmentOffset[blockNum]=adjustment;
			}           
		}
		Distribution_c.Arbitrary ret = new Distribution_c.Arbitrary(r, hm, 
				places.length==1? (place) places[0] : null); 
		
		return ret;
	}
	
	/**
	 * Create a Distribution where the points of the Region are
	 * distributed randomly over all available Places.
	 * @param r
	 * @return
	 */
	public dist arbitrary(region r) {
		dist ret;
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
	public dist constant(region r, place p) {
		// initialized to zero
		int adjustmentOffset[] = new int[Runtime.places().length];
		
		dist newDist = new Distribution_c.Constant(r, p);
		return newDist;
	}
	
	/**
	 * Create a Distribution where the points in the
	 * Region_c 1...p.length are mapped to the respective
	 * places.
	 * @param p the list of places (implicitly defines the Region_c)
	 * @return
	 */
	public dist unique(Set/*<place>*/ p) {
		Object[] places = p.toArray();
		place[] ps = new place[places.length];
		for (int i=0;i<places.length;i++)
			ps[i] = (place) places[i];
		
		dist newDist = new Distribution_c.Unique(ps);
		return newDist;
	}
	public dist unique(place[] P) {
		return new Distribution_c.Unique(P);
	}
	public dist unique(region R) {
		if (R instanceof MultiDimRegion) {
			return new MultiDimRegionUnique((MultiDimRegion) R);
		}
		dist newDist = new KDimUnique(R);
		return newDist;
	}
	
}
