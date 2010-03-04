/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

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

public class UnionRegion extends BaseRegion {

    // XTENLANG-49
    static type PolyRegion(rank:Int) = PolyRegion{self.rank==rank};
    static type PolyRegionListBuilder(rank:Int) = PolyRegionListBuilder{self.rank==rank};
    static type PolyRow(rank:Int) = PolyRow{self.rank==rank};
    static type PolyMat(rank:Int) = PolyMat{self.rank==rank};
    static type UnionRegion(rank:Int) = UnionRegion{self.rank==rank};


    //
    // value
    //

    global val regions: ValRail[PolyRegion(rank)]; // XTENLANG-118


    //
    // constructors
    //

    protected def this(rs: PolyRegionListBuilder!): UnionRegion(rs.rank) {
        super(rs.rank, false, false);
        this.regions = rs.toValRail() as ValRail[PolyRegion(rank)]; 
	//(rs as PolyRegionListBuilder(rank)).toValRail();
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

    public global def intersection(that: Region(rank)): Region(rank) {
        val rs = new PolyRegionListBuilder(rank);
        for (r:Region(rank) in regions)
            rs.add(r.intersection(that));
        return make(rs);
    }

    public global def complement(): Region(rank) {
        var r: Region(rank) = Region.makeFull(rank);
        for (r1:Region(rank) in regions)
            r = r.intersection(r1.complement());
        return r;
    }

    public global def isEmpty(): boolean {
        return regions.length==0;
    }

    public global def isConvex(): boolean {
        return false;
    }

    public global def size(): int {
        var size: int = 0;
        for (r:Region in regions)
            size += r.size();
        return size;
    }

    incomplete public global def product(Region): Region(rank);
    incomplete public global def projection(int): Region(1);
    incomplete public global def eliminate(int): Region/*(rank-1)*/;

    public global def translate(v: Point(rank)): Region(rank) {
        val rs = new PolyRegionListBuilder(rank);
        for (r:Region(rank) in regions)
            rs.add(r.translate(v));
        return make(rs);
    }

    public global def contains(p: Point): boolean {
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


    public global def scanners()=new Scanners();


    //
    // iterator
    //

    class It implements Iterator[Point(UnionRegion.this.rank)] {

        private var i: int = 0;
        var it: Iterator[Point(UnionRegion.this.rank)]! = null; // XTENLANG-118
        
        public def hasNext(): boolean {
            for (;;) {
                if (it!=null && it.hasNext())
                    return true;
                if (i >= regions.length)
                    return false;
                it = regions(i++).iterator();
            }
        }
        
        final public def next(): Point(UnionRegion.this.rank) = it.next();  // XTENLANG-118

        incomplete public def remove(): void;
    }

    public global def iterator(): Iterator[Point(rank)] {
        return new It();
    }


    //
    //
    //


    protected global def computeBoundingBox(): Region(rank) {
        val myMin = Rail.make[int](rank);
        val myMax = Rail.make[int](rank);
        for (var axis: int = 0; axis<rank; axis++)
            myMin(axis) = Int.MAX_VALUE;
        for (var axis: int = 0; axis<rank; axis++)
            myMax(axis) = Int.MIN_VALUE;
        for (r:Region in regions) {
            val rmin = r.min();
            val rmax = r.max();
            for (var axis: int = 0; axis<rank; axis++) {
                if (rmin(axis)<myMin(axis)) 
		    myMin(axis) = rmin(axis);
                if (rmax(axis)>myMax(axis)) 
		    myMax(axis) = rmax(axis);
            }
        }
        return Region.makeRectangular(myMin, myMax);
    }

    public global def min(): ValRail[int] {
        return boundingBox().min();
    }

    public global def max(): ValRail[int] {
        return boundingBox().max();
    }


    //
    //
    //

    public global safe def toString(): String {
        var s: String = "(";
        for (var i:int=0; i<regions.length; i++) {
            if (i>0) s += " || ";
            s = s + regions(i); // XTENLANG-45
        }
        s += ")";
        return s;
    }
}
