// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

/**
 * A UnionRegion is a region represented as the union of a disjoint
 * set of polyhedral regions, represented as a list of
 * PolyRegions. Since each PolyRegion is a conjunction of a list of
 * halfspaces, this means a UnionRegion is essentially a boolean
 * combination of linear inequalities represented in disjunctive
 * normal form.
 *
 * @author bdlucas
 */

public value class UnionRegion extends BaseRegion {

    // XTENLANG-49
    static type PolyRegion(rank:nat) = PolyRegion{self.rank==rank};
    static type PolyRegionListBuilder(rank:nat) = PolyRegionListBuilder{self.rank==rank};
    static type PolyRow(rank:nat) = PolyRow{self.rank==rank};
    static type PolyMat(rank:nat) = PolyMat{self.rank==rank};
    static type UnionRegion(rank:nat) = UnionRegion{self.rank==rank};


    //
    // value
    //

    val regions: ValRail[PolyRegion(rank)]; // XTENLANG-118


    //
    // constructors
    //

    protected def this(rs: PolyRegionListBuilder!): UnionRegion(rs.rank) {
        super(rs.rank, false, false);
        this.regions = rs.toValRail() as ValRail[PolyRegion(rank)]; 
	//(rs as PolyRegionListBuilder(rank)).toValRail();
        cache = new Cache(this, false);
    }

    public static def make(rs: PolyRegionListBuilder!): Region(rs.rank) {
        if (rs.size()==0)
            return Region.makeEmpty(rs.rank);
        else if (rs.size()==1)
            return rs.get(0) as Region(rs.rank); // XXXX why cast?
        else
            return new UnionRegion(rs);
    }


    //
    // algebra
    //

    public def intersection(that: Region(rank)): Region(rank) {
        val rs = new PolyRegionListBuilder(rank);
        for (r:Region(rank) in regions)
            rs.add(r.intersection(that));
        return make(rs);
    }

    public def complement(): Region(rank) {
        var r: Region(rank) = Region.makeFull(rank);
        for (r1:Region(rank) in regions)
            r = r.intersection(r1.complement());
        return r;
    }

    public def isEmpty(): boolean {
        return regions.length==0;
    }

    public def isConvex(): boolean {
        return false;
    }

    public def size(): int {
        var size: int = 0;
        for (r:Region in regions)
            size += r.size();
        return size;
    }

    incomplete public def product(Region): Region(rank);
    incomplete public def projection(int): Region(1);
    incomplete public def eliminate(int): Region(rank-1);

    public def contains(p: Point): boolean {
        for (r:PolyRegion(rank) in regions)
            if (r.contains(p))
                return true;
        return false;
    }

    //
    // scanner
    //

    class Scanners implements Iterator[Scanner] {
        
        private var i: int = 0;
        
        public def hasNext(): boolean {
            return i<regions.length;
        }
        
        public def next(): Region.Scanner {
            return regions(i++).scanner();
        }

        // from java.util.Iterator
        incomplete public def remove(): void;
    }


    public def scanners(): Iterator[Scanner] {
        return new Scanners();
    }


    //
    // iterator
    //

    class It implements Iterator[Point(UnionRegion.this.rank)] {

        private var i: int = 0;
        var it: Iterator[Point/*(rank)*/] = null; // XTENLANG-118
        
        public def hasNext(): boolean {
            for (;;) {
                if (it!=null && it.hasNext())
                    return true;
                if (i >= regions.length)
                    return false;
                it = regions(i++).iterator();
            }
        }
        
        final public def next(): Point(rank) = it.next() as Point(rank);  // XTENLANG-118

        incomplete public def remove(): void;
    }

    public def iterator(): Iterator[Point(rank)] {
        return new It();
    }


    //
    //
    //

    val cache:Cache;

    public def boundingBox(): Region(rank) {
        return cache.boundingBox() as Region(rank); // XXXX
    }

    protected def computeBoundingBox(): Region(rank) {
        val min = Rail.makeVar[int](rank);
        val max = Rail.makeVar[int](rank);
        for (var axis: int = 0; axis<rank; axis++)
            min(axis) = Int.MAX_VALUE;
        for (var axis: int = 0; axis<rank; axis++)
            max(axis) = Int.MIN_VALUE;
        for (r:Region in regions) {
            val rmin = r.min();
            val rmax = r.max();
            for (var axis: int = 0; axis<rank; axis++) {
                if (rmin(axis)<min(axis)) min(axis) = rmin(axis);
                if (rmax(axis)>max(axis)) max(axis) = rmax(axis);
            }
        }
        return Region.makeRectangular(min, max);
    }

    public def min(): ValRail[int] {
        return boundingBox().min();
    }

    public def max(): ValRail[int] {
        return boundingBox().max();
    }


    //
    //
    //

    public def toString(): String {
        var s: String = "(";
        for (var i:int=0; i<regions.length; i++) {
            if (i>0) s += " || ";
            s = s + regions(i); // XTENLANG-45
        }
        s += ")";
        return s;
    }
}
