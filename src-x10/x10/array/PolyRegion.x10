// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.io.PrintStream;

/**
 * A PolyRegion represents a polyhedral region represented as the
 * intersection of a list of Halfspaces. The halfspaces are stored in
 * a HalfspaceList object, which is essentially a constraint matrix
 * that defines the region. The PolyRegion object wraps a
 * HalfpaceList, adding some static factory methods for PolyRegions
 * and some methods such as region algebra that operate on
 * HalfspaceList objects.
 *
 * @author bdlucas
 */

value class PolyRegion extends BaseRegion {

    // XTENLANG-49
    static type PolyRegion(rank:nat) = PolyRegion{self.rank==rank};
    static type PolyRegionListBuilder(rank:nat) = PolyRegionListBuilder{self.rank==rank};
    static type Halfspace(rank:nat) = Halfspace{self.rank==rank};
    static type HalfspaceList(rank:nat) = HalfspaceList{self.rank==rank};
    static type UnionRegion(rank:nat) = UnionRegion{self.rank==rank};

    //
    // value
    //

    protected val halfspaces: HalfspaceList;


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


    /**
     * odometer-style iterator
     *
     * hasNext() computes the k that is the axis to be bumped:
     *
     * axis    0    1         k        k+1
     * now   x[0] x[1] ...  x[k]   max[k+1] ...  max[rank-1]
     * next  x[0] x[1] ...  x[k]+1 min[k+1] ...  min[rank-1]
     *
     * i.e. bump k, reset k+1 through rank-1
     * finished if k<0
     *
     * next() does the bumping and resetting
     */

    final private class RailIt implements Iterator[Rail[int]] {
        
        private val rank: int = PolyRegion.this.rank;
        private val s: Region.Scanner = scanner();

        private val x = Rail.makeVar[int](rank);
        private val min = Rail.makeVar[int](rank);
        private val max = Rail.makeVar[int](rank);

        private var k: int;

        def this() {
            min(0) = s.min(0);
            max(0) = s.max(0);
            x(0) = min(0);
            for (k=1; k<rank; k++) {
                s.set(k-1, x(k-1));
                val m = s.min(k);
                x(k) = m;
                min(k) = m;
                max(k) = s.max(k);
            }
            x(rank-1)--;
        }

        final public def hasNext(): boolean {
            k = rank-1;
            while (x(k)>=max(k))
                if (--k<0)
                    return false;
            return true;
        }

        final public def next() {
            x(k)++;
            for (k=k+1; k<rank; k++) {
                s.set(k-1, x(k-1));
                val m = s.min(k);
                x(k) = m;
                min(k) = m;
                max(k) = s.max(k);
            }
            return x;
        }

        public def remove() {}
    }

    /**
     * required by API, but less efficient b/c of allocation
     * XXX figure out how to expose
     *   1. Any/Var/ValPoint?
     *   2. hide inside iterator(body:(Point)=>void)?
     */

    public class PointIt implements Iterator[Point(PolyRegion.this.rank)] {

        val it: RailIt;

        def this() {
            it = new RailIt();
        }

        public final def hasNext() = it.hasNext();
        public final def next(): Point(rank) = it.next() to Point(rank);
        public final def remove() = it.remove();
    }


    public def iterator(): Iterator[Point(rank)] {
        return new PointIt();
    }


    //
    // Region methods
    //

    public def intersection(t: Region(rank)): Region(rank) {

        if (t instanceof PolyRegion) {

            // start
            val that = t as PolyRegion; // XXX
            val hlb = new HalfspaceListBuilder(rank);

            // these halfspaces
            for (h:Halfspace in this.halfspaces)
                hlb.add(h);

            // those halfspaces
            for (h:Halfspace in that.halfspaces)
                hlb.add(h);

            // done
            val hl = hlb.toHalfspaceList();
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
        var hl: HalfspaceList = halfspaces;
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
        val hlb = new HalfspaceListBuilder(this.rank + that.rank);
        copy(hlb, this.halfspaces, 0);         // padded w/ 0s on the right
        copy(hlb, that.halfspaces, this.rank); // padded w/ 0s on the left
        val hl = hlb.toHalfspaceList();
        return PolyRegion.make(hl);
    }

    private static def copy(tt: HalfspaceListBuilder, ff: HalfspaceList, offset: int): void {
        for (h:Halfspace in ff) {
            val f = h.as;
            val t = Rail.makeVar[int](tt.rank+1);
            for (var i: int = 0; i<ff.rank; i++)
                t(offset+i) = f(i);
            t(tt.rank) = f(ff.rank);
            tt.add(new Halfspace(t));
        }
    }


    /**
     * -H0 || -H1 && H0 || -H2 && H1 && H0 || ...
     */

    public def complement(): Region(rank) {
        
        val rl = new PolyRegionListBuilder(rank);

        for (h:Halfspace in halfspaces) {
            val hi = h as Halfspace(rank); // XXXX
            val hlb = new HalfspaceListBuilder(rank);
            hlb.add(hi.complement());
            for (hj:Halfspace in halfspaces) {
                if (hj==hi)
                    break;
                hlb.add(hj);
            }
            val hl = hlb.toHalfspaceList();
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

    public def boundingBox(): Region(rank) {
        if (boundingBox.b==null) {
            val min = Rail.makeVar[int](rank);
            val max = Rail.makeVar[int](rank);
            var hl: HalfspaceList = halfspaces;
            for (var axis: int = 0; axis<rank; axis++) {
                var x: HalfspaceList = hl;
                for (var k: int = axis+1; k<rank; k++)
                    x = x.eliminate(k, true);
                min(axis) = x.rectMin(axis);
                max(axis) = x.rectMax(axis);
                hl = hl.eliminate(axis, true);
            }
            boundingBox.b = new B(Region.makeRectangular(min, max));
        }
        return boundingBox.b.b as Region(rank);
    }


    /**
     * point
     */

    public def contains(p: Point): boolean {

        for (h:Halfspace in halfspaces) {
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

    private const ROW: int = HalfspaceListBuilder.X(0);
    private const COL: int = HalfspaceListBuilder.X(1);

    public static def makeBanded(rowMin: int, colMin: int, rowMax: int, colMax: int, upper: int, lower: int): Region(2) {
        val hlb = new HalfspaceListBuilder(2);
        hlb.add(ROW, hlb.GE, rowMin);
        hlb.add(ROW, hlb.LE, rowMax);
        hlb.add(COL, hlb.GE, colMin);
        hlb.add(COL, hlb.LE, colMax);
        hlb.add(COL-ROW, hlb.GE, colMin-rowMin-(lower-1));
        hlb.add(COL-ROW, hlb.LE, colMin-rowMin+(upper-1));
        val hl = hlb.toHalfspaceList();
        return PolyRegion.make(hl);
    }

    public static def makeBanded(size: int, upper: int, lower: int): Region(2) {
        return makeBanded(0, 0, size-1, size-1, upper, lower);
    }

    public static def makeUpperTriangular2(rowMin: int, colMin: int, size: int): Region(2) {
        var hlb: HalfspaceListBuilder{rank==2} = new HalfspaceListBuilder(2);
        hlb.add(ROW, hlb.GE, rowMin);
        hlb.add(COL, hlb.LE, colMin+size-1);
        hlb.add(COL-ROW, hlb.GE, colMin-rowMin);
        val hl = hlb.toHalfspaceList(true);
        return PolyRegion.make(hl);
    }

    public static def makeLowerTriangular2(rowMin: int, colMin: int, size: int): Region(2) {
        val hlb = new HalfspaceListBuilder(2);
        hlb.add(COL, hlb.GE, colMin);
        hlb.add(ROW, hlb.LE, rowMin+size-1);
        hlb.add(ROW-COL, hlb.GE, rowMin-colMin);
        val hl = hlb.toHalfspaceList(true);
        return PolyRegion.make(hl);
    }



    /**
     * here's where we examine the halfspaces and generate
     * special-case subclasses, such as RectRegion, for efficiency
     */

    public static def make(hl: HalfspaceList): Region(hl.rank) {
        if (hl.isEmpty()) {
            return new EmptyRegion(hl.rank);
        } else  if (hl.isRect() && hl.isBounded())
            return new RectRegion(hl);
        else
            return new PolyRegion(hl);
    }

    protected def this(val hl: HalfspaceList): PolyRegion{rank==hl.rank} {

        super(hl.rank, hl.isRect(), hl.isZeroBased());

        // simplifyAll catches more (all) stuff, but may be expensive.
        //this.halfspaces = hl.simplifyParallel();
        this.halfspaces = hl.simplifyAll();
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

    public def printInfo(out: PrintStream): void {
        halfspaces.printInfo(out, /*this.getClass().getName()*/this.toString());
    }

    public def toString(): String {
        return halfspaces.toString();
    }

}
