/*
 * Created on Oct 3, 2004
 */
package x10.array.sharedmemory;

import java.util.HashSet;

import x10.array.Distribution;
import x10.array.Place;
import x10.array.Range;
import x10.array.ContiguousRange;
import x10.array.Region;
import x10.array.InvalidIndexException;


/**
 * Implementation of Distributions.
 * 
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
abstract class Distribution_c extends Region_c implements Distribution {

    // First: static factory methods for the use by the Runtime implemenation
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks the specified places
     * @param r Region
     * @param q The set of Places
     * @return
     */
	static Distribution_c makeBlock(Region_c r, Place[] q) {
	    return makeBlock(r, q.length, q);
    }
    
    /**
     * Create a Distribution where the given Region is distributed
     * into blocks over the first n Places.
     * @param r
     * @return
     */
	static Distribution_c makeBlock(Region_c r, int n, Place[] q) {
        assert n <= q.length;
        
        Distribution_c[] dists = new Distribution_c[n];
        for (int i=0; i < n; i++) 
            dists[i] = new Distribution_c.Constant(r.sub(n, i), q[i]);
        return new Distribution_c.Combined(r, dists);
    }
    
    /**
     * Create a Distribution where the elements in the region are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the region is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    static Distribution_c makeCyclic(Region_c r, Place[] q) {
        return makeBlockCyclic(r, q.length, q);
    }
    
    /**
     * Create a Distribution where the elements in the region are
     * distributed over all Places in p in a cyclic manner,
     * that is the next point in the region is at the next place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    static Distribution_c makeBlockCyclic(Region_c r, int n, Place[] q) {
        throw new Error("TODO");
    }
    
    /**
     * Create a Distribution where the points of the Region are
     * distributed randomly over all available Places.
     * @param r
     * @return
     */
    static Distribution_c makeArbitrary(Region_c r, Place[] p) {
        return makeBlockCyclic(r, 32, p);
    }
    
    /**
     * Create a Distribution where all points in the given
     * Region are mapped to the same Place.
     * @param r
     * @param p specifically use the given place for all points
     * @return
     */
    static Distribution_c makeConstant(Region_c r, Place p) {
        return new Distribution_c.Constant(r, p);
    }
    
    /**
     * Create a Distribution where the points in the
     * region 1...p.length are mapped to the respective
     * places.
     * @param p the list of places (implicitly defines the region)
     * @return
     */
    static Distribution_c makeUnique(Place[] p) {
        return new Distribution_c.Unique(p);
    }

    private Distribution_c(Region_c r) {
        super(r);
    }
    
    /* 
     * Subclasses must override this method! We cannot make it abstract though
     * because this stub only serves to hand over thin invocation to the subclass.
     */
    public boolean equals(Object o) {
        return super.equals(o);
    }
    
    public Distribution asymetricUnion(Distribution d) { 
        throw new Error("TODO");
    }
    
    // called range restriction in the language report
    public abstract Distribution placeRestriction(Place r);
    
    public Distribution domainRestriction(Region r) { 
        throw new Error("TODO");
    }

    static final class Empty extends Distribution_c {
        
        Empty() {
            super(new Region_c.Empty());
        }
        
        public Place placeOf(int[] point) { 
            throw new InvalidIndexException();
        }
        
        public Distribution placeRestriction(Place r) {
            return this;
        }
        
        public boolean equals(Object o) {
            return super.equals(o);
        }
    } // end of Distribution_c.Empty
    
    static final class Constant extends Distribution_c {
        
        final Place place_;

        Constant(Region_c r, Place p) {
            super(r);
            this.place_ = p;
        }
        
        public Place placeOf(int[] point) { 
            return place_;
        }
        
        public Distribution placeRestriction(Place r) {
            Distribution ret;
            if (r == place_)
                ret = this;
            else
                ret = new Empty();
            return ret;
        }
        
        public boolean equals(Object o) {
            boolean ret = super.equals(o);
            if (ret) {
                Constant c = (Constant) o;
                ret &= place_ == c.place_;
            }
            return ret;
        }	
        
    } // end of Distribution_c.Constant
    
    static class Unique extends Distribution_c {
        
        private final Place[] places_;
        
        Unique(Place[] p) {
            super(new Region_c(new Range[] { new ContiguousRange(1, p.length) }));
            // defensive copy
            this.places_ = (Place[]) p.clone();
        }
        
        public Place placeOf(int[] point) { 
            assert point.length == 1;
            assert contains(point);
            return places_[point[0]-1];
        }
        
        public Distribution placeRestriction(Place r) {
            Distribution ret;
            boolean found = false;
            for (int i = 0; i < places_.length; ++ i) {
                if (places_[i] == r) {
                    found = true;
                    break;
                }
            }
            if (found) {
                Place[] parr = {r};
                ret = new Unique(parr);
            } else {
                ret = new Empty();
            }
            return ret;
        }
        
        public boolean equals(Object o) {
            boolean ret = super.equals(o);
            if (ret) {
                Unique u = (Unique) o;
                if (places_.length == u.places_.length) {
                    for (int i = 0; ret && i < places_.length; ++ i) {
                    	ret &= places_[i] == u.places_[i];
                    }
                } else 
                    ret = false;
            }
            return ret;
        }
        
    } // end of Distribution_c.Unique
    
    static class Combined extends Distribution_c {
        
        private final Distribution_c[] members_;
        
        Combined(Region_c r, Distribution_c[] members_) {
            super(r);
            assert members_ != null;
            // defensive copy
            this.members_ = (Distribution_c[]) members_.clone();
        }
        
        public Place placeOf(int[] point) {
            // 
            Place ret = null;
            for (int i=0; ret == null && i < members_.length; ++i) {
                if (members_[i].contains(point)) 
                    ret = members_[i].placeOf(point);
            }
            assert ret != null;
            return ret;
        }
        
        /** 
         * Currently only implemented for combined distributions where each part
         * is a Distribution.Constant
         */ 
        public Distribution placeRestriction(Place r) {
            // make sure that conditions are met under which this 
            // implementation works properly
            for (int i = 0; i < members_.length; ++i) {
                assert(members_[i] instanceof Constant);
            }
            
            
            Distribution ret;
            HashSet dists = new HashSet();
            for (int i = 0; i < members_.length; ++ i) {
                Constant c = (Constant) members_[i];
                if (c.place_ == r)
                    dists.add(c);
            }
            int size = dists.size();
            
            if (size == 0)
                ret = new Empty();
            else if (size == 1) 
                ret = (Distribution) dists.iterator().next();
            else {
                // create array of distributions
                Distribution_c[] arr = new  Distribution_c[size];
                dists.toArray(arr);
                // create union of all regions
                Region u_region = arr[0];
                for (int i = 1; i < size; ++ i) 
                    u_region = u_region.union(arr[i]);
                ret = new Combined((Region_c) u_region, arr);
            }
            return ret;
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
        
    } // end of Distribution_c.Combined
    
} // end of Distribution_c