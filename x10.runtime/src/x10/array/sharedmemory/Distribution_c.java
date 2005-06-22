/*
 * Created on Oct 3, 2004
 */
package x10.array.sharedmemory;

import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import x10.array.Range;
import x10.array.ContiguousRange;
import x10.array.ArbitraryRegion;
import x10.array.MultiDimRegion;
import x10.array.point_c;
import x10.lang.Indexable;
import x10.lang.region;
import x10.lang.dist;
import x10.lang.place;
import x10.lang.point;
import x10.lang.Runtime;



/**
 * Implementation of Distributions.
 * vj: Changed class to extend x10.lang.dist, not Region_c.
 * vj: Added factory class to separate out factory methods.
 * @author Christoph von Praun
 * @author Christian Grothoff
 * @author vj
 */
public abstract class Distribution_c extends /*Region_c*/dist /*implements Distribution*/ {
    
    public boolean isValue() {
        return true;
    }

    /**
     * Is this indexable value-equals to the other indexable?
     * @param other
     * @return true if these objects are value-equals
     */
    public boolean valueEquals(Indexable other) {
        return equals(other);
    }
    
    /* this field should actually be final - ?? */
    protected final Set/*<place>*/ places;
    
    public Set/*<place>*/ places() {
        return places;
    }
    
    protected Distribution_c(region r) {
        super( r);
        this.places = new HashSet();
    }
    
    public region restrictToRegion(place pl) {
        return restriction(this, pl); 
    }
    
    /** Returns the region mapped by this distribution to the place P.
    The value returned is a subset of this.region.
    */
    protected static region/*(rank)*/ restriction(dist th, place pl) {
       Set points = new HashSet();
       for (Iterator it = th.region.iterator(); it.hasNext(); ) {
           point p = (point) it.next();
           if (th.get(p) == pl) 
               points.add(p);
       }
       region ret = new ArbitraryRegion(th.region.rank, points);
       return ret;
   }
   
    public dist restriction(Set Ps) {
        return restriction(this, Ps); 
    }
    
    /** Returns the distribution obtained by range-restricting this to Ps.
    The region of the distribution returned is contained in this.region.
    */
   protected static dist/*(:this.region.contains(region))*/
   restriction(dist th, Set/*<place>*/Ps ) {
       HashMap hm = new HashMap();
       Set points = new HashSet();
       for (Iterator it = th.region.iterator(); it.hasNext(); ) {
           point p = (point) it.next();
           place pl = th.get(p);
           if (Ps.contains(pl)) {
               points.add(p);
               hm.put(p, pl);
           }
       }
       region reg = new ArbitraryRegion(th.rank, points);
       dist ret = new Arbitrary(reg, hm); 
       return ret;
   }
   
   
   public dist restriction(region r) {
       return restriction(this, r);
   }
   /** Returns a new distribution obtained by restricting this to the
    * domain region.intersection(R), where parameter R is a region
    * with the same dimension.
    */
   protected static
   /*(region(rank) R)*/ dist/*(region.intersection(R))*/
   restriction(dist th, region/*(rank)*/ r) {
       assert r.rank == th.rank;
       
       HashMap hm = new HashMap();
       Set points = new HashSet();
       for (Iterator it = th.region.iterator(); it.hasNext(); ) {
           point p = (point) it.next();
           if (r.contains(p)) {
               points.add(p);
               hm.put(p, th.get(p));
           }
       }
       region reg = new ArbitraryRegion(th.rank, points);
       dist ret = new Arbitrary(reg, hm); 
       return ret;
   }
   
   public dist difference(region r) {
       return difference(this, r);
   }
   
   /** Returns the restriction of this to the domain region.difference(R),
    * where parameter R is a region with the same dimension.
    */
   protected static
   /*(region(rank) R)*/ dist/*(region.difference(R))*/
   difference(dist th, region/*(rank)*/ r) {
       assert r.rank == th.rank;
       region reg = th.region.difference(r);
       HashMap hm = new HashMap();
       for (Iterator it = reg.iterator(); it.hasNext(); ) {
           point p = (point) it.next();
           hm.put(p, th.get(p));
       }
       dist ret = new Arbitrary(reg, hm); 
       return ret;
   }
   
   public dist union(dist d) {
       return union(this, d);
   }
   
   /** Takes as parameter a distribution D defined over a region
    disjoint from this. Returns a distribution defined over a
    region which is the union of this.region and D.region.
    This distribution must assume the value of D over D.region
    and this over this.region.
    
    @seealso distribution.asymmetricUnion.
    */
   protected static /*(distribution(:region.disjoint(this.region) &&
   rank=this.rank) D)*/ 
   dist/*(region.union(D.region))*/
   union(dist th, dist/*(:region.disjoint(this.region) &&
   rank=this.rank)*/ d) {
       assert d.rank == th.rank && d.region.disjoint(th.region); // assume
       
       region reg = d.region.union(th.region);
       HashMap hm = new HashMap();
       for (Iterator it = th.region.iterator(); it.hasNext(); ) {
           point p = (point) it.next();
           hm.put(p, th.get(p));
       }
       for (Iterator it = d.region.iterator(); it.hasNext(); ) {
           point p = (point) it.next();
           hm.put(p, d.get(p));
       }
       dist ret = new Arbitrary(reg, hm); 
       return ret;
   }
   
    public dist intersection(dist D) {
        return intersection(this, D);
    }
    
    public dist overlay(region r, dist d) {
       return overlay(this, r, d);
   }
   
   public dist overlay(dist d) {
       return overlay(this, d.region, d);
   }
   
   /** Returns a distribution defined on region.union(R): it takes on 
    this.get(p) for all points p in region, and D.get(p) for all
    points in R.difference(region).
    */
   protected static /*(region(rank) R)*/ dist/*(region.union(R))*/ 
   overlay(dist th, region/*(rank)*/ r, dist/*(R)*/ d) {
       assert (d.region.rank == r.rank) &&  (r.rank == th.rank);
       
       region reg = r.union(th.region);
       HashMap hm = new HashMap();
       for (Iterator it = reg.iterator(); it.hasNext(); ) {
           point p = (point) it.next();
           place pl;
           if (d.region.contains(p)) {
               pl = d.get(p);
               assert (pl != null);
           } else {
               pl = th.get(p);
               assert (pl != null);
           }
           hm.put(p, pl);
       }
       dist ret = new Arbitrary(reg, hm); 
       return ret;
   }
   
   /** Returns a the intersection of distributions th and d. The result contains  
    *  all those points that are contained in the intersection of th.region and 
    *  d.region that are mapped to the *same place* in both distributions.
    */
  protected static /*(region(rank) R)*/ dist/*(region.union(R))*/ 
  intersection(dist th, dist/*(R)*/ d) {
      assert (d.rank == th.rank);
      
      region reg = th.region.intersection(d.region);
      HashMap hm = new HashMap();
      Set points = new HashSet();
      for (Iterator it = reg.iterator(); it.hasNext(); ) {
          point p = (point) it.next();
          place pl;
          if ((pl = th.get(p)).equals(d.get(p))) {
              hm.put(p, pl);
              points.add(p);
          }
      }
      region reg_new = new ArbitraryRegion(th.rank, points);
      dist ret = new Arbitrary(reg_new, hm); 
      return ret;
  }
   
   public boolean subDistribution(region r, dist d) {
       return subDistribution(this, r, d);
   }
   
   /** Return true iff the given distribution D, which must be over a
    * region of the same rank as this, is defined over a subset
    * of this.region and agrees with it at each point.
    */
   protected /*(region(rank) r)*/ 
   static boolean subDistribution(dist th, region/*(rank)*/ r, dist/*(R)*/ d) {
       assert d.region.rank == th.rank;
       boolean ret = false;
       if (th.region.contains(d.region)) {
           ret = true;
           for (Iterator it = d.region.iterator(); it.hasNext(); ) {
               point p = (point) it.next();
               place p1 = th.get(p);
               place p2 = d.get(p);
               if (!p1.equals(p2)) {
                   ret = false;
                   break;
               }
           }
       }
       return ret;
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
        public place get(point/*(region)*/ p) {
            throw new ArrayIndexOutOfBoundsException();
        }

        public place get(int[] p)  {
            throw new ArrayIndexOutOfBoundsException();
        }
        
        /** Returns the region mapped by this distribution to the place P.
         The value returned is a subset of this.region.
         */
        public region/*(rank)*/ restrictToRegion(place P ) {
            return this.region;
        }
        
        /** Returns the distribution obtained by range-restricting this to Ps.
         The region of the distribution returned is contained in this.region.
         */
        public dist/*(:this.region.contains(region))*/
        restriction( Set/*<place>*/Ps ) {
            return this;
        }
        
        /** Returns a new distribution obtained by restricting this to the
         * domain region.intersection(R), where parameter R is a region
         * with the same dimension.
         */
        public 
        /*(region(rank) R)*/ dist/*(region.intersection(R))*/
        restriction( region/*(rank)*/ R) {
            assert this.region.rank == R.rank;
            return this;
        }
        
        /** Returns the restriction of this to the domain region.difference(R),
         where parameter R is a region with the same dimension.
         */
        public 
        /*(region(rank) R)*/ dist/*(region.difference(R))*/
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
        dist/*(region.union(D.region))*/
        union(dist D) {
            assert this.region.rank == D.region.rank;
            return D;
        }
        

        
        /** Return true iff the given distribution D, which must be over a
         * region of the same rank as this, is defined over a subset
         * of this.region and agrees with it at each point.
         */
        public /*(region(rank) r)*/ 
        boolean subDistribution( region/*(rank)*/ R, dist/*(R)*/ D) {
            assert D.region.rank == this.region.rank;
            return (D instanceof Empty || D.region.size() == 0);
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
        public place get(point/*(region)*/ p) {
            if (!region.contains(p))
                throw new ArrayIndexOutOfBoundsException();
            else
                return place_;
        }

        /** Returns the region mapped by this distribution to the place P.
         The value returned is a subset of this.region.
         */
        public region/*(rank)*/ restrictToRegion( place P ) {
            if (P.equals(place_)) 
                return this.region;
            return Runtime.factory.getRegionFactory().emptyRegion(this.rank);
        }
        
        /** Returns the distribution obtained by range-restricting this to Ps.
         The region of the distribution returned is contained in this.region.
         */
        public dist/*(:this.region.contains(region))*/
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
        /*(region(rank) R)*/ dist/*(region.intersection(R))*/
        restriction( region/*(rank)*/ R) {
            assert R.rank == this.rank; //assume
            region r = this.region.intersection( R );
            dist ret;
            if (r.size() == 0) 
                ret = new Empty(rank);
            else 
                ret =  new Constant( r, place_);
            // assert result.region == R.intersection( this.region);
            return ret;
        }
        
        /** Returns the restriction of this to the domain region.difference(R),
         where parameter R is a region with the same dimension.
         */
        public 
        /*(region(rank) R)*/ dist/*(region.difference(R))*/
        difference( region/*(rank)*/ R) {
            assert R.rank == this.rank; //assume
            region r = this.region.difference( R );
            dist ret;
            if (r.size() == 0) 
                ret = new Empty(rank);
            else 
                ret  = new Constant( r, place_);
            return ret;            
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
        dist/*(region.union(D.region))*/
        union(dist/*(:region.disjoint(this.region) &&
        rank=this.rank)*/ D) {
            assert D.rank == rank && D.region.disjoint(region); // assume
            assert D instanceof Distribution_c;
            dist ret;
            if (D.region.size() == 0)
                ret = this;
            else {
                Distribution_c arr[] = new Distribution_c[2];
                arr[0] = this;
                arr[1] = (Distribution_c) D;
                region r = region.union(D.region);
                ret = new Distribution_c.Combined(r, arr);
            }
            return ret;
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
            super(new ContiguousRange(0, ps.length - 1));
            this.placeseq = ps;
            for (int i=0;i<placeseq.length;i++) 
                places.add(ps[i]);
        }
        
        
        /** Returns the place to which the point p in region is mapped.
         */
        public place get(point/*(region)*/ p) {
            if (!region.contains(p)) {
                throw new ArrayIndexOutOfBoundsException();
            } else
                return placeseq[(p.get(0)) % placeseq.length];
        }

        public place get(int[] val) {
            assert val.length == 1;
            if (val[0] >= placeseq.length || val[0] < 0)
                throw new ArrayIndexOutOfBoundsException();
            return placeseq[val[0]];
        }

        
        /** Returns the region mapped by this distribution to the place P.
         The value returned is a subset of this.region.
         */
        public region/*(rank)*/ restrictToRegion( place P ) {
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



        public String toString() {
            StringBuffer s = new StringBuffer("Distribution_c.Unique<");
            for (int i=0; i < placeseq.length; i++)
                s.append(placeseq[i].toString());
            return s.append(">").toString();
        }
        
    } // end of Distribution_c.Unique
    
    static class Combined extends Distribution_c {
        private final Distribution_c[] members_;
        
        /**
         * @param r
         */
        Combined(region r, Distribution_c[] members_) {
            super(r);
            assert members_ != null;
            // defensive copy
            this.members_ = (Distribution_c[]) members_.clone();
            for (int i=0; i < members_.length; i++) {
                places.addAll( members_[i].places());
            }
        }
        
        
        /* (non-Javadoc)
         * @see x10.lang.distribution#get(x10.lang.point)
         */
        public place get(point p) {
            place ret = null;
            for (int i=0; ret == null && i < members_.length; ++i) {
                if (members_[i].region.contains(p)) {
                    ret = members_[i].get(p);
                    assert ret != null;
                }
            }
            if (ret == null)
                throw new ArrayIndexOutOfBoundsException();
            return ret;
        }
        
        public place get(int[] p)  {
            place ret = null;
            for (int i=0; ret == null && i < members_.length; ++i) {   
                if (members_[i].region.contains(p)) 
                    ret = members_[i].get(p);
            }
            if (ret == null)
                throw new ArrayIndexOutOfBoundsException();
            return ret;
        }
        


        public String toString() {
            StringBuffer s = new StringBuffer("CombinedDistribution_c<");
            for (int i=0; i < members_.length;i++) 
                s.append(members_[i]);
        
            if(x10.runtime.Configuration.isMultiNodeVM()){
            	s.append(" virtural index map::[");
            	for(int j=0;j<_indexAdjustment.length;++j){
            		s.append(_indexAdjustment[j]);
            		s.append(" ");
            	}
            	s.append("]");
            }
            
            return s.append(">").toString();
        }
    } // end of Distribution_c.Combined
    
    static final class Arbitrary extends Distribution_c {
        
        private final Map map_;
        
        Arbitrary(region r, Map m) {
            super(r);
            map_ = m;
            places.addAll(m.values());
        }
        
        /** Returns the place to which the point p in region is mapped.
         */
        public place get(point/*(region)*/ p) {
            assert p != null;
            place ret = (place) map_.get(p);
            if (ret == null)
                throw new ArrayIndexOutOfBoundsException();
            return ret;
        }

        
        public String toString() {
            StringBuffer s = new StringBuffer("Distribution_c.Arbitrary<\n");
            for (Iterator it = map_.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry me = (Map.Entry) it.next();
                point p = (point) me.getKey();
                place pl = (place) me.getValue();
                s.append("[" + p + ", " + pl + "]");
                if (it.hasNext()) 
                    s.append(",\n");
                
            }
            s.append(">");
            return s.toString();
        }
        
    } // end of Distribution_c.Arbitrary
} 
