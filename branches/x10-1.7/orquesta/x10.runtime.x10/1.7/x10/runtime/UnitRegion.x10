package x10.runtime;

/**

   A UnitRegion is the unique region of rank 0 which contains a single
   element, the point of length 0. By convention, this point is
   considered zerobased (all of its elements -- there are none -- are
   zero).

   The unit region u is the unit for product: u * r = r * u = r

   TODO: Actually the region isnt unique. We need to figure out how to
   deal with colMajor. We should really have one point whose colMajor
   attribute can be either true or false. But in X10 (and Java) you
   are forced to have two objects representing this point.

   @author vj 06/12/08 (thanks to Olivier Tardieu for telling me that a UnitRegion exists)


 */
public value class UnitRegion extends region{rank==0,rect,zeroBased,!rail, !colMajor} {
    public static val unit = new UnitRegion();
    private def this() {
	super(0, true, true, false, false);
    }

    public static val thePoint = new ValRail[int](0,0);
    public static val emptyRank0Region = new EmptyRegion(0);
    @Overrides public def size()=1;
    @Overrides public def isConvex()=true;

    // Can never be invoked, rank=0.
    @Overrides public def low(index : nat(1..rank)) = { assert false;}

    // Can never be invoked, rank=0.
    @Overrides public def high(index : nat(1..rank)) = { assert false;}

    @Overrides public def min() = thePoint;
    @Overrides public def max() = thePoint;
    @Overrides public def contains(p: point{length==rank}) = thePoint==p;

    // There are only two regions of rank 0 ... one is empty and the other unit.
    // hence the union is always this.
    @Overrides public def union(r: region{rank==this.rank}) = this;

    // There are only two regions of rank 0 ... one is empty and the other unit.
    // if r is empty, then the intersection is empty; if r is unit, then interesection is unit.
    @Overrides public def intersection(r: region{rank==this.rank}) = r;
    @Overrides public def difference(r: region{rank==this.rank}) = r==this? emptyRank0Region : this;
    @Overrides public def convexHull(r: region{rank==this.rank}) = this;

    // The typechecker should succeed since this.rank==0, per the extends clause.
    @Overrides public def product(r: region{rect}):region{rect,rank==r.rank}=r;

    // the unit region contains the empty region and the unit region.
    @Overrides public def contains(r :region{rank==this.rank}):boolean = true;


    @Overrides public def ordinal(p: point(this)):nat = 0;
    @Overrides public def coord(p: nat):point{length==rank} 
    throws ArrayIndexOutOfBoundsException = {
	if (p > 0) throw new ArrayIndexOutOfBoundsException();
	return thePoint;
    }
    

}
