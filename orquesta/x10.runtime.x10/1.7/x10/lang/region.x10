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

public abstract value region(rank:int, rect:boolean, zeroBased:boolean, rail:boolean, 
			     colMajor:boolean) {

    public region(_rank: nat, _rect: boolean, _zeroBased: boolean, _rail: boolean, 
		  _colMajor: boolean): region{rank==_rank, zeroBased=_zeroBased, rail==_rail,
					      colMajor=_colMajor} = 
    {
	property(_rank, _rect, _zerobased, _rail,_colMajor);
    }				      

    /** Return the size of this region = number of points in the region.
     */
    public abstract def size():nat;

    /**
     * Return the bounding box for the given dimension. 

     Used to be called rank.

     TODO: Investigate projecting a region of rank k to its first p
     dimensions, p < k.

     @throws UnsupportedOperationException -- if the region is empty
    */
    public def proj(index :nat(1..rank)): region{rank==1} throws UnsupportedOperationException;

    /**
     * Returns true iff the region contains every point between two
     * points in the region.
     */
    public def isConvex():boolean;

    /**
     * Return the low bound for this region in the given dimension. Throws an exception if
     * size of the region is 0.
     * @throws UnsupportedOperationException -- if the region is empty or there is no least point.
     */
    public abstract def low(index : nat(1..rank)) throws UnsupportedOperationException;

    /**
     * Return the high bound for the index'th dimension, if it exists, else
     * throws UnsupportedOperationException. 
     * @throws UnsupportedOperationException -- if the region is empty or there is no highest point.
     */
    public abstract def high(index : nat(1..rank)) throws UnsupportedOperationException;

    /**
       Return the minimum point in this region, if there is one.
       If there isnt, throw an UnsupportedOperationException.
     */
    public abstract def min() throws UnsupportedOperationException;

    /**
       Return the minimum point in this region, if there is one.
       If there isnt, throw an UnsupportedOperationException.
     */
    public abstract def max() throws UnsupportedOperationException;

    public abstract def union(r: region{rank==this.rank}):region{rank==this.rank};
    public abstract def intersection(r: region{rank==this.rank}):region{rank==this.rank};
    public abstract def difference(r: region{rank==this.rank}):region{rank==this.rank};
    public abstract def convexHull(r: region{rank==this.rank}):region{rank==this.rank};
    public abstract def disjoint(r: region{rank==this.rank}):boolean;

    /**
       The product this * r of regions is a region of rank this.rank +
       r.rank, which has precisely the points p obtained by appending
       every point in this to ever
       r1*r2 = { p1.append(p2) | pi in r1, p2 in r2}
       
       Currently defined only for rectangular regions, hence returns a
       rectangular region.
     */
    public abstract def product(r: region{rect}){this.rect}:region{rect,rank=this.rank+r.rank};

    public def product(regions: ValRail[region{rect}]){this.rect}: region{rect}= {
	val n = regions.length;
	if (n == 0) return this;
	if (n == 1) return product(regions(0));
	var r = regions(n-1);
	for (int i = n-2; i >= 0; i--) { 
	    r = regions(i).product(r);
	}
	product(r)
    }

    /** 
	Does every point in r lie in this region? 
	Intended to be overridden by subclasses representing non-full regions. 
    */
    public abstract def contains(r :region{rank==this.rank}):boolean;

    /**
     * Returns true iff the set of points in r and this are equal.
     */
    public def equals(r :region{rank==this.rank}):boolean = contains(r)&& r.contains(this);

    /**
     * @param p a point in the coordinate space
     * @return the ordinal number of the point [0 ... size()]
     */
    public abstract def ordinal(p: point(this));

    public def coord(ord: nat):point{length==rank} throws ArrayIndexOutofBoundsException = {
	if (ord > size()) throw new ArrayIndexOutOfBoundsException();
	// incomplete... use colMajor variable to determine the order in which rows/columsn are
	// traversed
	
    }
    /**
     * @return Iterator that yields the individual points of a region in
     * lexicographical order.
     */
    public abstract def iterator(): Iterator;

    // define equals and hashcode.
    // look into tiled regions, breaking a region up into tiles.


    // TODO: define static methods to produce upperTriangular,
    // lowerTriangular, banded regions.

    /**
       Make an empty region of the given rank.
     */
    public static def makeEmptyRegion(rank: nat) = Runtime.makeEmptyRegion(rank);

    /**
       Make the unique unit region.
     */
    public static def makeUnitRegion(): region{rank==0, rect, zeroBased, !rail, !colMajor} = unitRegion;
    public static val unitRegion = Runtime.makeUnitRegion();
}

