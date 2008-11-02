// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.util.ArrayList;

/**
 * A union of poly regions. Since a poly region is a conjunction of
 * halfspaces, this means we are essentially representing systems of
 * inequalities in DNF.
 */

value class UnionRegion extends BaseRegion {

    // XTENLANG-49
    static type PolyRegion(rank:nat) = PolyRegion{self.rank==rank};
    static type PolyRegionList(rank:nat) = PolyRegionList{self.rank==rank};
    static type Halfspace(rank:nat) = Halfspace{self.rank==rank};
    static type HalfspaceList(rank:nat) = HalfspaceList{self.rank==rank};
    static type UnionRegion(rank:nat) = UnionRegion{self.rank==rank};


    //
    // value
    //

    val regions: Rail[PolyRegion/*(rank)*/]; // XTENLANG-118


    //
    // constructors
    //

    protected def this(rs: PolyRegionList): UnionRegion(rs.rank) {
        super(rs.rank, false, false);
        this.regions = (rs as PolyRegionList(rank)).toArray();
    }

    static def make(rs: PolyRegionList): Region(rs.rank) {
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
        var rs: PolyRegionList(rank) = new PolyRegionList(rank);
        for (var i: int = 0; i<regions.length; i++) {
            val r = regions(i) as Region(rank); // XXXX
            rs.add(r.intersection(that));
        }
        return make(rs);
    }

    public def complement(): Region(rank) {
        var r: Region(rank) = Region.makeFull(rank);
        for (var i: int = 0; i<regions.length; i++) {
            val r1 = regions(i) as Region(rank); // XXXX
            r = r.intersection(r1.complement());
        }
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
        for (var i: int = 0; i<regions.length; i++)
            size += regions(i).size();
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

    // XXX should get these from Integer but they are missing from
    // x10.lang.Integer in the Java runtime so just put them here
    const MAX_VALUE: int = 2147483647;
    const MIN_VALUE: int = -2147483648;

    public def boundingBox(): Region(rank) {
        if (boundingBox.b==null) {
            val min = Rail.makeVar[int](rank);
            val max = Rail.makeVar[int](rank);
            for (var axis: int = 0; axis<rank; axis++)
                min(axis) = MAX_VALUE;
            for (var axis: int = 0; axis<rank; axis++)
                max(axis) = MIN_VALUE;
            for (var i: int = 0; i<regions.length; i++) {
                var rmin: Rail[int] = regions(i).min();
                var rmax: Rail[int] = regions(i).max();
                for (var axis: int = 0; axis<rank; axis++) {
                    if (rmin(axis)<min(axis)) min(axis) = rmin(axis);
                    if (rmax(axis)>max(axis)) max(axis) = rmax(axis);
                }
            }
            boundingBox.b = new B(Region.makeRectangular(min, max));
        }
        return boundingBox.b.b as Region(rank);
    }

    public def min(): Rail[int] {
        return boundingBox().min();
    }

    public def max(): Rail[int] {
        return boundingBox().max();
    }


    //
    //
    //

    public def toString(): String {
        var s: String = "(";
        for (var i: int = 0; i<regions.length; i++) {
            if (i>0) s += " || ";
            s = s + regions(i); // XTENLANG-45
        }
        s += ")";
        return s;
    }
}
