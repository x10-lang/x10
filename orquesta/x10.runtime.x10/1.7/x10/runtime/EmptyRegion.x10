package x10.runtime;

/**

   Instances of this class represent the unique empty region of the
   given rank. 

   All such regions z are zeros for products: z * r = r * z = z

   TODO: How do we ensure that only one empty region is actually ever
   created in a computation (modulo colMajor)? i.e. we want automatic
   interning here (easy enough to program explicitly ... but do we have to?!)

   TODO: see note on EmptyRegion about colMajor.

   @author vj 06/12/08
 */
public value class EmptyRegion extends region {

    /**
       Construct the empty region with given rank.
     */
    public def this(rank :nat) {
	super(rank, true, false, false, false);
    }
    @Overrides public def size()=0;
    @Overrides public def isConvex()=true;
    @Overrides public def low(index : nat(1, rank)) throws UnsupportedOperationException = { 
	throw new UnsupportedOperationException();
    }
    @Overrides public def high(index : nat(1, rank)) throws UnsupportedOperationException = { 
	throw new UnsupportedOperationException();
    }
    @Overrides public def min() throws UnsupportedOperationException = { 
	throw new UnsupportedOperationException();
    }
    @Overrides public def max() throws UnsupportedOperationException = { 
	throw new UnsupportedOperationException();
    }
    @Overrides public def contains(p: point{length==rank}) = false;

    @Overrides public def union(r: region{rank==this.rank}) = r;
    @Overrides public def intersection(r: region{rank==this.rank}) = this;
    @Overrides public def difference(r: region{rank==this.rank}) = this;
    @Overrides public def convexHull(r: region{rank==this.rank}) = this;
    @Overrides public def contains(r :region{rank==this.rank}):boolean = r.size()==0;
    @Overrides public def product(r :region) = new EmptyRegion(rank+r.rank);

    // This method can never since it can never be established that a point
    // lies in this region. Nevertheless we must define it to make sure that
    // all methods are defined.
    @Overrides public def ordinal(p: point(this)):nat = { assert false;}
    @Overrides public def coord(n: nat): point(length==rank} 
	throws ArrayIndexOutOfBoundsException = {
	    throw new ArrayIndexOutOfBoundsException();
	}
    
}
