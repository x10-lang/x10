// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.io.Printer;

/**
 * A PolyRegion represents a polyhedral region represented as the
 * intersection of a list of PolyRows. The halfspaces are stored in
 * a PolyMat object, which is essentially a constraint matrix
 * that defines the region. The PolyRegion object wraps a
 * HalfpaceList, adding some static factory methods for PolyRegions
 * and some methods such as region algebra that operate on
 * PolyMat objects.
 *
 * @author bdlucas
 */

value class PolyRegion extends BaseRegion {

    // XTENLANG-49
    static type PolyRegion(rank:nat) = PolyRegion{self.rank==rank};
    static type PolyRegionListBuilder(rank:nat) = PolyRegionListBuilder{self.rank==rank};
    static type PolyRow(rank:nat) = PolyRow{self.rank==rank};
    static type PolyMat(rank:nat) = PolyMat{self.rank==rank};
    static type UnionRegion(rank:nat) = UnionRegion{self.rank==rank};

    //
    // value
    //

    protected val halfspaces: PolyMat;


    //
    // basic info
    //

    public def isConvex(): boolean {
        return true;
    }

    incomplete public def size():int;


    //
    // scanners
    //

    private class Scanners implements Iterator[Scanner] {

        var hasNext: boolean = true;

        public def hasNext(): boolean {
            return hasNext;
        }

        public def next(): Region.Scanner {
            if (hasNext) {
                hasNext = false;
                return scanner();
            } else
                throw new NoSuchElementException("in scanner");
        }

        public def remove(): void {
            throw U.unsupported(this, "remove");
        }
    };

    public def scanners(): Iterator[Scanner] {
        return new Scanners();
    }

    protected def scanner(): Region.Scanner {
        return new PolyScanner(halfspaces);
    }


    public def iterator(): Iterator[Point(rank)] {
        //return new PointIt();
        //return scanner().iterator();
        return new PolyScanner(halfspaces).iterator() to Iterator[Point(rank)]; // XXXX cast
    }


    //
    // Region methods
    //

    public def intersection(t: Region(rank)): Region(rank) {

        if (t instanceof PolyRegion) {

            // start
            val that = t as PolyRegion; // XXX
            val hlb = new PolyMatBuilder(rank);

            // these halfspaces
            for (h:PolyRow in this.halfspaces)
                hlb.add(h);

            // those halfspaces
            for (h:PolyRow in that.halfspaces)
                hlb.add(h);

            // done
            val hl = hlb.toPolyMat();
            return PolyRegion.make(hl) as Region(rank); // XXXX why?

        } else if (t instanceof UnionRegion) {

            return (t as Region(rank)).intersection(this);

        } else {
            throw U.unsupported(this, "intersection(" + t/*.getClass().getName()*/ + ")");
        }
    }
                          
                          
    /**
     * Projection is computed by using FME to eliminate variables on
     * all but the axis of interest.
     */

    public def projection(axis: int): Region(1) {
        var hl: PolyMat = halfspaces;
        for (var k: int = 0; k<rank; k++)
            if (k!=axis)
                hl = hl.eliminate(k, true);
        return Region.makeRectangular(hl.rectMin(axis), hl.rectMax(axis)) as Region(1);
    }

    /**
     * Eliminate the ith axis.
     */

    // XXX add a test case for this; also for projection!
    public def eliminate(axis: int): Region(rank-1) {
        val hl = halfspaces.eliminate(axis, true); 
        val result = PolyRegion.make(hl);
        return result as Region(rank-1);
    }

    /**
     * Cartesian product requires copying the halfspace matrices into
     * the result blockwise
     */

    public def product(r: Region): Region {
        if (!(r instanceof PolyRegion))
            throw U.unsupported(this, "product(" + r/*.getClass().getName()*/ + ")");
        val that = r as PolyRegion;
        val hlb = new PolyMatBuilder(this.rank + that.rank);
        copy(hlb, this.halfspaces, 0);         // padded w/ 0s on the right
        copy(hlb, that.halfspaces, this.rank); // padded w/ 0s on the left
        val hl = hlb.toPolyMat();
        return PolyRegion.make(hl);
    }

    private static def copy(tt: PolyMatBuilder, ff: PolyMat, offset: int): void {
        for (h:PolyRow in ff) {
            val f = h;
            val t = Rail.makeVar[int](tt.rank+1);
            for (var i: int = 0; i<ff.rank; i++)
                t(offset+i) = f(i);
            t(tt.rank) = f(ff.rank);
            tt.add(new PolyRow(t));
        }
    }


    /**
     * -H0 || -H1 && H0 || -H2 && H1 && H0 || ...
     */

    public def complement(): Region(rank) {
        
        val rl = new PolyRegionListBuilder(rank);

        for (h:PolyRow in halfspaces) {
            val hi = h as PolyRow(rank); // XXXX
            val hlb = new PolyMatBuilder(rank);
            hlb.add(hi.complement());
            for (hj:PolyRow in halfspaces) {
                if (hj==hi)
                    break;
                hlb.add(hj);
            }
            val hl = hlb.toPolyMat();
            val r = PolyRegion.make(hl);
            rl.add(r as Region(rank)); // XXXX
        }


        return new UnionRegion(rl);
    }

    public def isEmpty(): boolean {
        return halfspaces.isEmpty();
    }


    /**
     * Bounding box is computed by taking the projection on each
     * axis. This implementation is more efficient than computing
     * projection on each axis because it re-uses the FME results.
     */

    val cache:Cache;

    public def boundingBox(): Region(rank) {
        return cache.boundingBox() as Region(rank); // XXXX
    }

    protected def computeBoundingBox(): Region(rank) {
        val min = Rail.makeVar[int](rank);
        val max = Rail.makeVar[int](rank);
        var hl: PolyMat = halfspaces;
        for (var axis: int = 0; axis<rank; axis++) {
            var x: PolyMat = hl;
            for (var k: int = axis+1; k<rank; k++)
                x = x.eliminate(k, true);
            min(axis) = x.rectMin(axis);
            max(axis) = x.rectMax(axis);
            hl = hl.eliminate(axis, true);
        }
        return Region.makeRectangular(min, max);
    }


    /**
     * point
     */

    public def contains(p: Point): boolean {

        for (h:PolyRow in halfspaces) {
            if (!h.contains(p))
                return false;
        }

        return true;
    }



    /**
     * lower==1 and lower==1 include the diagonal
     * lower==size and upper==size includes entire size x size square
     *
     * col-colMin >= row-rowMin - (lower-1)
     * col-colMin <= row-rowMin + (upper-1)
     *
     * col-row >= colMin-rowMin - (lower-1)
     * col-row <= colMin-rowMin + (upper-1)
     */

    private const ROW: int = PolyMatBuilder.X(0);
    private const COL: int = PolyMatBuilder.X(1);

    public static def makeBanded(rowMin: int, colMin: int, rowMax: int, colMax: int, upper: int, lower: int): Region(2) {
        val hlb = new PolyMatBuilder(2);
        hlb.add(ROW, hlb.GE, rowMin);
        hlb.add(ROW, hlb.LE, rowMax);
        hlb.add(COL, hlb.GE, colMin);
        hlb.add(COL, hlb.LE, colMax);
        hlb.add(COL-ROW, hlb.GE, colMin-rowMin-(lower-1));
        hlb.add(COL-ROW, hlb.LE, colMin-rowMin+(upper-1));
        val hl = hlb.toPolyMat();
        return PolyRegion.make(hl);
    }

    public static def makeBanded(size: int, upper: int, lower: int): Region(2) {
        return makeBanded(0, 0, size-1, size-1, upper, lower);
    }

    public static def makeUpperTriangular2(rowMin: int, colMin: int, size: int): Region(2) {
        var hlb: PolyMatBuilder{rank==2} = new PolyMatBuilder(2);
        hlb.add(ROW, hlb.GE, rowMin);
        hlb.add(COL, hlb.LE, colMin+size-1);
        hlb.add(COL-ROW, hlb.GE, colMin-rowMin);
        val hl = hlb.toPolyMat(true);
        return PolyRegion.make(hl);
    }

    public static def makeLowerTriangular2(rowMin: int, colMin: int, size: int): Region(2) {
        val hlb = new PolyMatBuilder(2);
        hlb.add(COL, hlb.GE, colMin);
        hlb.add(ROW, hlb.LE, rowMin+size-1);
        hlb.add(ROW-COL, hlb.GE, rowMin-colMin);
        val hl = hlb.toPolyMat(true);
        return PolyRegion.make(hl);
    }



    /**
     * here's where we examine the halfspaces and generate
     * special-case subclasses, such as RectRegion, for efficiency
     */

    public static def make(hl: PolyMat): Region(hl.rank) {
        if (hl.isEmpty()) {
            return new EmptyRegion(hl.rank);
        } else  if (hl.isRect() && hl.isBounded())
            return new RectRegion(hl);
        else
            return new PolyRegion(hl, false);
    }

    protected def this(val hl: PolyMat, hack198:boolean): PolyRegion{rank==hl.rank} {

        super(hl.rank, hl.isRect(), hl.isZeroBased());

        // simplifyAll catches more (all) stuff, but may be expensive.
        //this.halfspaces = hl.simplifyParallel();
        this.halfspaces = hl.simplifyAll();

        // cache stuff up front
        cache = new Cache(this, hack198);
    }

    public def min(): ValRail[int] {
        return boundingBox().min();
    }

    public def max(): ValRail[int] {
        return boundingBox().max();
    }


    //
    // debugging
    //

    public def printInfo(out: Printer): void {
        halfspaces.printInfo(out, /*this.getClass().getName()*/this.toString());
    }

    public def toString(): String {
        return halfspaces.toString();
    }

}
