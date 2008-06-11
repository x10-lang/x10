package x10.lang;
/*
*
* (C) Copyright IBM Corporation 2006, 2007, 2008
*
*/

/**
* A region represents a (sparse or dense) k-dimensional space of
* points. A region is a dependent class, with the value parameter
* specifying the dimension of the region.  A convex k-dimensional
* region is easy to represent, e.g. as a list of k (min, max)
* pairs. In general, regions may not be convex. (For instance
* difference(region(dimension) r) produces non-convex regions.)
* Non-convex regions are very important for many physical
* problems. For instance the region of the halo around a 2-d array is
* non-convex.
*
* A region is rectangular if it was constructed as a cross product of
rectangular regions, or as a region with a zero and a diagonal
element, and all points in between. Rectangular regions are always
convex. Such a region is always representable with a zero and a
diagonal vector (of the same length), and contains exactly the points
geq zero and leq diagonal (if any). 

* @author vj
*/

public value region(rank:int, rect:boolean, zeroBased:boolean, rail:boolean, colMajor:boolean) {

    /** Every region has a zero and a diagonal. If a region is full,
     * then every point between the zero and diagonal is in the
     * region. Constructors on this class can only create full
     * regions. Constructors on subclasses create partially full
     * regions.
     */
    val zero, diagonal: point{length==rank};

    /**
       Creates an empty region of the given rank.
    */
    public this(r: nat): region{:rank==r,rect,rail==(rank==1), zeroBased,! colMajor}= {
	this(ValRail[int].allK(rank, 0), ValRail[int].allK(rank, -1), 
	     true, true, true, false, true);
    }
    /** Construct the convex rowMajor 1-d region from [low] to [high]
     */
    public this(low: nat, high: nat): region{rank==1,rect,zeroBased==(low==0),rail==zeroBased} ={
	this(new point(low), new point(high), false, true, true);
    }
    /**
       Construct the convex rowMajor region from z to d.
    */
    public this(_zero:point, _diagonal: point{rank==zero.rank}): 
	region{rank==_zero.rank,rail==(rank==1),!colMajor) = {
	this(_zero, _diagonal, false, true, true);
    }
    /**
       Construct the convex rowMajor region from zero to d.
    */
    public this(d: point): region{rank==d.rank,rail==(rank==1),!colMajor) = {
	this(point.allZero(d.length) _diagonal, false, true, true);
    }
	
    protected this(d: point): region{rank==d.rank,rail==(rank==1),!colMajor) = {
    protected region(:rank==_diagonal.rank&&rect&&zeroBased&&rail==(rank==1)&&colMajor==_colMajor)
	(final point _diagonal, final boolean _colMajor) {
		property(_diagonal.rank, true, true, _diagonal.rank==1, _colMajor);
		zero = point.allZero(_diagonal.rank);
		diagonal =_diagonal;
		
	}
    protected region(_zero : point, _diag: point{length==zero.length}, _colMajor:boolean): 
    region{rank==_zero.length,rail==(rank==1),colMajor==_colMajor) {
	 val rank = _zero.length;
	 val isZero = _zero.isZero();
	 property(rank, true, isZero, (rank==1&& isZero), _colMajor);
	 zero = _zero;
	 diagonal = _diag;
	 full = true;
     }
    
	/** Create the rectangular region which is the cross product of the first and second.
	TODO vj: Figure out how colMajor plays into this.
	*/
    public static 
    def makeProduct(first: region{rect}, second: region{rect}): 
             region{zeroBased == first.zeroBased&&second.zeroBased, rect} = {
		 point z = first.zero.append(second.zero);
		 point d = first.diagonal.append(second.diagonal);
		 new region(z,d);
	     }
    public static 
    def makeProduct(l:nat, regions: ValRail[region{rect,rank==l}]): region{rect,rank==l}= {
	val n = regions.length;
	if (n == 0) return new region(0);
	if (n == 1) return regions(1);
	var r = regions(n-1);
	for (int i = n-2; i >= 0; i--) r = makeProduct(regions(i), r);
	r
    }
    /**
       Return the number of points in this region. This should be overridden by 
       subclasses which implement non-rect regions.
    */
    var size = -1;
    public  size():nat {
	if (size >= 0) return size;
	size = 1;
	for (int i = 0; i < rank; i++) {
	    val u = diagonal(i), l = zero(i);
	    if (u < l) return size=0; // multiplying by empty region.
	    size *= u-l +1;
	}
	size
    }
    /**
     * Return the bounding box for the given dimension. Use modular arithmetic on the index to 
     determine the dimension to return.
    */
    public def rank(index :nat(1,rank)): region{rank==1} = {
	new region(zero(index), diagonal(index))
    }

    /**
     * Returns true iff the region contains every point between two
     * points in the region.
     */
    public def isConvex():boolean= true; 

    /**
     * Return the low bound for this region in the given dimension. Throws an exception if
     * size of the region is 0.
     */
    public def low(index : nat(1, rank)) = {
	if (size()<= 0) throw new UnsupportedOperationException();
	zero(index)
    }

    /**
     * Return the high bound for a 1-dimensional region. Can only be
     * invoked on 1-dimensional objects. Thrwos an exception if
     * size of the region is 0.
     */
    public def high(index : nat(1, rank)) = {
	if (size()<= 0) throw new UnsupportedOperationException();
	diagonal(index)
    }

    public def zero() = {
	if (size()<= 0) throw new UnsupportedOperationException();	
	zero
    }

    public def diagonal() = {
	if (size()<= 0) throw new UnsupportedOperationException();	
	diagonal
    }

    public def contains(p :point{length==rank}):boolean = 
        size() > 0 && p.andReduce(i:nat(0,rank-1) => zero(i)<=p(i)&&p(i)<=diagonal(i));

    public native def union(r: region{rank==this.rank}):region{rank==this.rank};
    public native def intersection(r: region{rank==this.rank}):region{rank==this.rank};
    public native def difference(r: region{rank==this.rank}):region{rank==this.rank};
    public native def convexHull(r: region{rank==this.rank}):region{rank==this.rank};

    /** 
	Does every point in r lie in this region? 
	Intended to be overridden by subclasses representing non-full regions. 
    */
    public def contains(r :region{rank==this.rank}):boolean =
        size()==0 ? r.size()==0 
                  : size() > 0&& zero <= r.zero() && r.diagonal() <= diagonal;


    /**
     * Returns true iff this is disjoint from r.
     */
     public native def disjoint(region/*(rank)*/ r):boolean;

    /**
     * Returns true iff the set of points in r and this are equal.
     */
    public def equals(r :region{rank==this.rank}):boolean = contains(r)&& r.contains(this);

    /**
     * @param p a point in the coordinate space
     * @return the ordinal number of the point [0 ... size()]
     */
    public def ordinal(p: point{length==rank)) = {
	// incomplete... use colMajor variable to determine the order in which rows/columsn are
	// traversed

    }

    public def coord(ord: nat):point{length==rank} = {
	if (ord > size()) throw new ArrayIndexOutOfBoundsException();
	// incomplete... use colMajor variable to determine the order in which rows/columsn are
	// traversed
	
    }
	/**
	 * @return Iterator that yields the individual points of a region in
	 * lexicographical order.
	 */
    public def iterator(): Iterator {
	// incomplete
    }

    // define equals and hashcode.
    // look into tiled regions, breaking a region up into tiles.


    // TODO: define static methods to produce upperTriangular,
    // lowerTriangular, banded regions.
	
}

