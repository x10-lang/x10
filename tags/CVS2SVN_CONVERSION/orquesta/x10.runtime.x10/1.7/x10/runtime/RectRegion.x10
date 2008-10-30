package x10.runtime;

public value class RectRegion extends region {
    /** Every region has a zero and a diagonal. If a region is full,
     * then every point between the zero and diagonal is in the
     * region. Constructors on this class can only create full
     * regions. Constructors on subclasses create partially full
     * regions.
     */
    val zero, diagonal: point{length==rank};

    /**
       Construct the 1-d rowmajor region low..high.
     */
    public def this(low: nat(0..high), high: nat):
	region{rank==1,rect,zeroBased==(low=0),rail=zeroBased} = {
	    this(new point(low), new point(hight), false);
	}

    /** Construct the row major rectangular region from 0 to d.
     */
    public def this(d: point) = {
	this(d, false);
    }
    public def this(d: point, colMajor: boolean) = {
	this(ValRail[int].allZero(d.rank), d, boolean);
    }
    public def this(z: point, d: point{length=z.length}, colMajor: boolean) = {
	val rank = z.length;
	val zeroBased = z.isZero();
	val rail = zeroBased && rank==1;
	super(rank, true, zeroBased, rail, colMajor);
	zero = z;
	diagonal = d;
    }
    /**
       Return the number of points in this region. This should be overridden by 
       subclasses which implement non-rect regions.
    */
    var size = -1;
    @Overrides public  
	def size():nat = {
	    if (size >= 0) return size;
	    size = 1;
	    for (int i = 0; i < rank; i++) {
		val u = diagonal(i), l = zero(i);
		if (u < l) return size=0; // multiplying by empty region.
		size *= u-l +1;
	    }
	    size
	}
    @Overrides public 
	def projection(index :nat(1..rank)): region{rank==1}  = 
	new region(zero(index), diagonal(index));

    @Overrides public def isConvex()= true; 
    @Overrides public def low(index : nat(1, rank)) = zero(index);
    @Overrides public def high(index : nat(1, rank)) = diagonal(index);
    @Overrides public def min() = zero;
    @Overrides public def max() = diagonal;
    @Override public def contains(p :point{length==rank}):boolean = zero.le(p) && p.le(diagonal);
    @Overrides public def contains(r :region{rank==this.rank}):boolean =
        size()==0 ? r.size()==0 
                  : size() > 0&& zero <= r.zero() && r.diagonal() <= diagonal;

    @Overrides public 
	def union(r: region{rank==this.rank}):region{rank==this.rank} = {
	    if (! r.rect) throw UnsupportedOperationException();
	    val rr = r as RectRegion;
	    if (contains(rr.zero)) 
	    return new RectRegion(zero, diagonal.le(rr.diagonal)? rr.diagonal: diagonal);
	    if (rr.contains(zero)) 
	    return new RectRegion(rr.zero, diagonal.le(rr.diagonal)? rr.diagonal: diagonal);
	    throw new UnsupportedOperationException();
    }
    @Overrides public 
	def intersection(r: region{rank==this.rank}):region{rank==this.rank} = {
	    if (! r.rect) throw UnsupportedOperationException();
	    val rr = r as RectRegion;
	    if (contains(r)) return r;
	    if (r.contains(this)) return this;
	    throw new UnsupportedOperationException();
	}
    @Overrides unimplemented public 
	def difference(r: region{rank==this.rank}):region{rank==this.rank};
    @Overrides public 
	def disjoint(r: region{rank==this.rank}):boolean = {
	    if (! r.rect) throw UnsupportedOperationException();
	    val rr = r as RectRegion;
	    return r.zero.gt(diagonal) || zero.gt(r.diagonal);
	}

    @Overrides public def convexHull(r: region{rank==this.rank})=this;

    // 	TODO vj: Figure out how colMajor plays into this.
    @Overrides public def product(r: region) {
	if (! r.rect) throw new UndefinedExeption();
	val z = first.zero.append(second.zero);
	val d: point{rank==z.rank} = first.diagonal.append(second.diagonal);
	new region(z,d);
    }
    // just do it! This is where colMajor is used.
    @Override public unimplemented
	def ordinal(p: point(this)):nat;

    // just do it! This is where colMajor is used.
    @Override public unimplemented
	def coord(ord: nat):point{length==rank} throws ArrayIndexOutOfBoundsException;

}
