/*
 * Created on Oct 3, 2004
 */
package x10.array.sharedmemory;

import java.util.HashSet;
import java.util.Set;

import x10.array.Range;
import x10.array.ContiguousRange;
import x10.lang.region;
import x10.lang.distribution;
import x10.lang.place;
import x10.lang.point;
import x10.lang.Runtime;


/**
 * Implementation of Distributions.
 * vj: Changed class to extend x10.lang.distribution, not Region_c.
 * vj: Added factory class to separate out factory methods.
 * @author Christoph von Praun
 * @author Christian Grothoff
 * @author vj
 */
public abstract class Distribution_c extends /*Region_c*/distribution /*implements Distribution*/ {
	protected Set/*<place>*/ places;
	public Set/*<place>*/ places() {
		return places;
	}
	protected Distribution_c( region r) {
		this(r, new HashSet());
	}
	protected Distribution_c(region r, Set/*<place>*/ s) {
		super( r);
		this.places = s;
	}
	
  
	public distribution asymetricUnion( distribution d ) { 
        throw new Error("TODO");
    }
	
    static final class Empty extends Distribution_c {
        
    	Empty() {
    		this(1);
    	}
    	/** The empty region of rank k
    	 * @param k
    	 */
        Empty(/*nat*/int k) {
        	// get an empty region of rank 0
            super( Runtime.factory.getRegionFactory().emptyRegion(k));
        }
        
        /** Returns the place to which the point p in region is mapped.
         */
        public place valueAt(point/*(region)*/ p) throws MalformedError {
        	throw new MalformedError();
        }
        public place valueAt(int[] p) throws MalformedError {
        	throw new MalformedError();
        }
    	
        /** Returns the region mapped by this distribution to the place P.
    	The value returned is a subset of this.region.
        */
        public region/*(rank)*/ restriction( place P ) {
        	return this.region;
        }
    	
        /** Returns the distribution obtained by range-restricting this to Ps.
            The region of the distribution returned is contained in this.region.
        */
        public distribution/*(:this.region.contains(region))*/
            restriction( Set/*<place>*/Ps ) {
        	return this;
        }

        /** Returns a new distribution obtained by restricting this to the
         * domain region.intersection(R), where parameter R is a region
         * with the same dimension.
         */
        public 
    	/*(region(rank) R)*/ distribution/*(region.intersection(R))*/
    	restriction( region/*(rank)*/ R) {
        	assert this.region.rank == R.rank;
        	return this;
        }

        /** Returns the restriction of this to the domain region.difference(R),
            where parameter R is a region with the same dimension.
         */
        public 
    	/*(region(rank) R)*/ distribution/*(region.difference(R))*/
            difference( region/*(rank)*/ R) {
        	assert this.region.rank == R.rank;
        	return this;
        }

        /** Takes as parameter a distribution D defined over a region
            disjoint from this. Returns a distribution defined over a
            region which is the union of this.region and D.region.
            This distribution must assume the value of D over D.region
            and this over this.region.

            @seealso distribution.asymmetricUnion.
         */
        public /*(distribution(:region.disjoint(this.region) &&
    		      rank=this.rank) D)*/ 
            distribution/*(region.union(D.region))*/
    	union(distribution D) {
        	assert this.region.rank == D.region.rank;
        	return D;
        }

        /** Returns a distribution defined on region.union(R): it takes on 
            this.valueAt(p) for all points p in region, and D.valueAt(p) for all
            points in R.difference(region).
         */
        public /*(region(rank) R)*/ distribution/*(region.union(R))*/ 
            overlay( region/*(rank)*/ R, distribution/*(R)*/ D) {
        	assert D.region.rank == this.region.rank;
        	return D;
        }

        /** Return true iff the given distribution D, which must be over a
         * region of the same rank as this, is defined over a subset
         * of this.region and agrees with it at each point.
         */
        public /*(region(rank) r)*/ 
            boolean subDistribution( region/*(rank)*/ R, distribution/*(R)*/ D) {
        	assert D.region.rank == this.region.rank;
        	return (D instanceof Empty);
        }   
        public String toString() {
        	StringBuffer s = new StringBuffer("Distribution_c.Empty<");
        	s.append(region.toString());
        	s.append("|>");
        	return s.toString();
        }
        
    } // end of Distribution_c.Empty
    
    static final class Constant extends Distribution_c {
    	place place_;

        Constant(region r, place p) {
        	super(r);
        	this.places.add(p);
        	place_ = p;
           
        }
        /** Returns the place to which the point p in region is mapped.
         */
        public place valueAt(point/*(region)*/ p) {
        	return place_;
        }
        public place valueAt(int[] p) {
        	return place_;
        }
        /** Returns the region mapped by this distribution to the place P.
    	The value returned is a subset of this.region.
        */
        public region/*(rank)*/ restriction( place P ) {
        	if (P.equals(place_)) 
        		return this.region;
        	return Runtime.factory.getRegionFactory().emptyRegion(this.rank);
        }
    	
        /** Returns the distribution obtained by range-restricting this to Ps.
            The region of the distribution returned is contained in this.region.
        */
        public distribution/*(:this.region.contains(region))*/
            restriction( Set/*<place>*/Ps ) {
        	if (Ps.contains(place_))
        		return this;
        	return new Empty(this.rank);
        }

        /** Returns a new distribution obtained by restricting this to the
         * domain region.intersection(R), where parameter R is a region
         * with the same dimension.
         */
        public 
    	/*(region(rank) R)*/ distribution/*(region.intersection(R))*/
    	restriction( region/*(rank)*/ R) {
        	assert R.rank == this.rank; //assume
        	region r = this.region.intersection( R );
        	final distribution result = new Constant( r, place_);
        	// assert result.region == R.intersection( this.region);
        	return result;
        }

        /** Returns the restriction of this to the domain region.difference(R),
            where parameter R is a region with the same dimension.
         */
        public 
    	/*(region(rank) R)*/ distribution/*(region.difference(R))*/
            difference( region/*(rank)*/ R) {
          	assert R.rank == this.rank; //assume
          	region r = this.region.difference( R );
        	final distribution result = new Constant( r, place_);
        	// assert result.region == R.intersection( this.region);
        	return result;
          	
        }

        /** Takes as parameter a distribution D defined over a region
            disjoint from this. Returns a distribution defined over a
            region which is the union of this.region and D.region.
            This distribution must assume the value of D over D.region
            and this over this.region.

            @seealso distribution.asymmetricUnion.
         */
        public /*(distribution(:region.disjoint(this.region) &&
    		      rank=this.rank) D)*/ 
            distribution/*(region.union(D.region))*/
    	union(distribution/*(:region.disjoint(this.region) &&
    			    rank=this.rank)*/ D) {
        	assert D.rank == this.rank && D.region.disjoint(this.region); // assume
        	throw new Error("TODO");
        	
        }

        /** Returns a distribution defined on region.union(R): it takes on 
            this.valueAt(p) for all points p in region, and D.valueAt(p) for all
            points in R.difference(region).
         */
        public /*(region(rank) R)*/ distribution/*(region.union(R))*/ 
            overlay( region/*(rank)*/ R, distribution/*(R)*/ D) {
        	assert D.region.equals(R) && this.region.equals(R); // assume
        	throw new Error("TODO");
        }


        /** Return true iff the given distribution D, which must be over a
         * region of the same rank as this, is defined over a subset
         * of this.region and agrees with it at each point.
         */
        public /*(region(rank) r)*/ 
            boolean subDistribution( region/*(rank)*/ R, distribution/*(R)*/ D) {
           	assert D.region.equals(R) && this.region.equals(R); // assume
           	return (this.region.contains(D.region) && (D instanceof Constant) && ((Constant) D).place_.equals(place_));
        }
        public String toString() {
        	StringBuffer s = new StringBuffer("Distribution_c.Constant<region=|");
        	s.append(region.toString());
        	s.append("|, place=|");
        	s.append(place_);
        	s.append("|>");
        	return s.toString();
        }
        
    } // end of Distribution_c.Constant
    
    static class Unique extends Distribution_c {
    	place[] placeseq;
        Unique(place[] ps) {
            super(new Region_c(new Range[] { new ContiguousRange(1, ps.length) }));
            this.placeseq = ps;
            Set s = new HashSet();
            for (int i=0;i<placeseq.length;i++) 
            	s.add(ps[i]);
            this.places=s;
        }
        

        /** Returns the place to which the point p in region is mapped.
         */
        public place valueAt(point/*(region)*/ p) {
        	assert this.region.contains(p.region);
        	return placeseq[(p.valueAt(0)) % placeseq.length];
        }
        public place valueAt(int[] val) {
        	assert val.length == 1;
        	return placeseq[val[0] % placeseq.length];
        }
    	
        /** Returns the region mapped by this distribution to the place P.
    	The value returned is a subset of this.region.
        */
        public region/*(rank)*/ restriction( place P ) {
        	 int index = -1;
             for (int i = 0; i < placeseq.length; ++ i) {
             	if (placeseq[i] == P) {
             		index = i;
             		break;
             	}
             }
             region.factory RF = Runtime.factory.getRegionFactory();
             if (index < 0)
             	return RF.emptyRegion(1);
             return RF.region(index, index);
        }
    	
        /** Returns the distribution obtained by range-restricting this to Ps.
            The region of the distribution returned is contained in this.region.
        */
        public distribution/*(:this.region.contains(region))*/
            restriction( Set/*<place>*/Ps ) {
        	throw new Error("TODO");
        }

        /** Returns a new distribution obtained by restricting this to the
         * domain region.intersection(R), where parameter R is a region
         * with the same dimension.
         * vj: Note that the resulting distribution may not have a contiguous region.
         */
        public 
    	/*(region(rank) R)*/ distribution/*(region.intersection(R))*/
    	restriction( region/*(rank)*/ R) {
        	throw new Error("TODO");
        }

        /** Returns the restriction of this to the domain region.difference(R),
            where parameter R is a region with the same dimension.
            vj: Note that the resulting distribution may not have a contiguous region.
         */
        public 
    	/*(region(rank) R)*/ distribution/*(region.difference(R))*/
            difference( region/*(rank)*/ R) {
        	throw new Error("TODO");
        }

        /** Takes as parameter a distribution D defined over a region
            disjoint from this. Returns a distribution defined over a
            region which is the union of this.region and D.region.
            This distribution must assume the value of D over D.region
            and this over this.region.
            vj: Note that the resulting distribution may not be unique.
            @seealso distribution.asymmetricUnion.
         */
        public /*(distribution(:region.disjoint(this.region) &&
    		      rank=this.rank) D)*/ 
            distribution/*(region.union(D.region))*/
    	union(distribution/*(:region.disjoint(this.region) &&
    			    rank=this.rank)*/ D) {
        	throw new Error("TODO");
        }

        /** Returns a distribution defined on region.union(R): it takes on 
            this.valueAt(p) for all points p in region, and D.valueAt(p) for all
            points in R.difference(region).
         */
        public /*(region(rank) R)*/ distribution/*(region.union(R))*/ 
            overlay( region/*(rank)*/ R, distribution/*(R)*/ D) {
        	throw new Error("TODO");
        }


        /** Return true iff the given distribution D, which must be over a
         * region of the same rank as this, is defined over a subset
         * of this.region and agrees with it at each point.
         */
        public /*(region(rank) r)*/ 
            boolean subDistribution( region/*(rank)*/ R, distribution/*(R)*/ D) {
        	throw new Error("TODO");
        }
    	
        public boolean equals(Object o) {
        	boolean ret = super.equals(o);
        	if (ret) {
        		Unique u = (Unique) o;
        		ret = placeseq.length == u.placeseq.length;
        		if (ret) 
        			for (int i = 0; ret && i < placeseq.length; ++ i) 
        				ret = (placeseq[i] == u.placeseq[i]);
        	}
        	return ret;
        }
        public String toString() {
        	StringBuffer s = new StringBuffer("Distribution_c.Unique<");
        	for (int i=1; i < placeseq.length;i++)
        		s.append(placeseq[i].toString());
        	return s.append(">").toString();
        }
        
    } // end of Distribution_c.Unique

  static class Combined extends Distribution_c {
        private final Distribution_c[] members_;

	/**
	 * @param r
	 */
        Combined(Region_c r, Distribution_c[] members_) {
        	super(r);
        	assert members_ != null;
        	// defensive copy
        	this.members_ = (Distribution_c[]) members_.clone();
        	Set myplaces = new HashSet();
        	for (int i=0; i < members_.length; i++) {
        		myplaces.addAll( members_[i].places());
        	}
        	this.places = myplaces;
        }


	/* (non-Javadoc)
	 * @see x10.lang.distribution#valueAt(x10.lang.point)
	 */
	public place valueAt(point p) throws MalformedError {
            place ret = null;
            for (int i=0; ret == null && i < members_.length; ++i) {
                if (members_[i].region.contains(p)) 
                    ret = members_[i].valueAt(p);
            }
            assert ret != null;
            return ret;
	}
	public place valueAt(int[] p) throws MalformedError {
        place ret = null;
        for (int i=0; ret == null && i < members_.length; ++i) {
        	
            if (members_[i].region.contains(p)) 
                ret = members_[i].valueAt(p);
 
        }
        assert ret != null;
        return ret;
}
	/* Currently only implemented for combined distributions where
         * each part is a Distribution.Constant
	 * @see x10.lang.distribution#restriction(x10.lang.place)
	 */
	public region restriction(place P) {
            // make sure that conditions are met under which this 
            // implementation works properly
            for (int i = 0; i < members_.length; ++i) {
                assert(members_[i] instanceof Constant);
            }
            distribution ret;
            HashSet dists = new HashSet();
            for (int i = 0; i < members_.length; ++ i) {
                Distribution_c.Constant c = (Distribution_c.Constant) members_[i];
                if (c.place_ == P)
                    dists.add(c);
            }
            int size = dists.size();
            
            if (size == 0)
                ret = new Distribution_c.Empty();
            else if (size == 1) 
                ret = (distribution) dists.iterator().next();
            else {
                // create array of distributions
                Distribution_c[] arr = new  Distribution_c[size];
                dists.toArray(arr);
                // create union of all regions
                region u_region = arr[0].region;
                for (int i = 1; i < size; ++ i) 
                    u_region = u_region.union(arr[i].region);
                ret = new Combined((Region_c) u_region, arr);
            }
            return ret.region;

	}

	/* (non-Javadoc)
	 * @see x10.lang.distribution#restriction(java.util.Set)
	 */
	public distribution restriction(Set Ps) {
	    throw new Error("TODO");
	}

	/* (non-Javadoc)
	 * @see x10.lang.distribution#restriction(x10.lang.region)
	 */
	public distribution restriction(region R) {
	    assert (this.region.contains(R));
	    throw new Error("TODO");
	}

	/* (non-Javadoc)
	 * @see x10.lang.distribution#difference(x10.lang.region)
	 */
	public distribution difference(region R) {
		return null;
	}

	/* (non-Javadoc)
	 * @see x10.lang.distribution#union(x10.lang.distribution)
	 */
	public distribution union(distribution D) {
	    throw new Error("TODO");
	}

	/* (non-Javadoc)
	 * @see x10.lang.distribution#overlay(x10.lang.region, x10.lang.distribution)
	 */
	public distribution overlay(region R, distribution D) {
	    throw new Error("TODO");
	}

	/* (non-Javadoc)
	 * @see x10.lang.distribution#subDistribution(x10.lang.region, x10.lang.distribution)
	 */
	public boolean subDistribution(region R, distribution D) {
	    throw new Error("TODO");
	}
	public boolean equals(Object o) {
		boolean ret = super.equals(o);
		if (ret) {
			Combined u = (Combined) o;
			if (members_.length == u.members_.length) {
				for (int i = 0; ret && i < members_.length; ++ i) {
					ret &= members_[i].equals(u.members_[i]);
				}
			} else 
				ret = false;
		}
		return ret;
	}	
	public String toString() {
		StringBuffer s = new StringBuffer("CombinedDistribution_c<");
		for (int i=0; i < members_.length;i++) 
			s.append(members_[i]);
		return s.append(">").toString();
		
	}
  }// end of Distribution_c
    
    
} 
