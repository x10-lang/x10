// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.io.Printer;

/**
 * The BaseRegion class is the base of the hierarchy of classes that
 * implement Region. BaseRegion provides a set of factory methods, and
 * also provides some function common to all Region implementations,
 * such as default implementations of some Region methods. Specific
 * Region implementation classes may override these methods with more
 * efficient implementations.
 *
 * @author bdlucas
 */

abstract public class BaseRegion extends Region {

    // XTENLANG-49
    static type PolyRegion(rank:nat) = PolyRegion{self.rank==rank};
    static type PolyRegionListBuilder(rank:nat) = PolyRegionListBuilder{self.rank==rank};
    static type PolyRow(rank:nat) = PolyRow{self.rank==rank};
    static type PolyMat(rank:nat) = PolyMat{self.rank==rank};
    static type BaseRegion(rank:int) = BaseRegion{self.rank==rank};


    //
    // factories
    //

    public static def makeEmpty1(rank: int): Region(rank) { // XTENLANG-4
        return new EmptyRegion(rank);
    }

    public static def makeFull1(rank: int): Region(rank) { // XTENLANG-4
        return new FullRegion(rank);
    }

    public static def makeUnit1(): Region(0) { // XTENLANG-4
        return new FullRegion(0);
    }

    public static def makeHalfspace1(normal:Point, k:int): Region(normal.rank) {
        val rank = normal.rank;
        val pmb = new PolyMatBuilder(rank);
        val r = new PolyRow(normal, k);
        pmb.add(r);
        val pm = pmb.toSortedPolyMat(false);
        return PolyRegion.make(pm) as Region(normal.rank); // XXXX
    }

    public static def makeRectangular1(min: Rail[int], max: Rail[int]): RectRegion(min.length) { // XTENLANG-4
        return RectRegion.make1(min, max);
    }        

    // XTENLANG-109 prevents zeroBased==(min==0)
    public static def makeRectangular1(min: int, max: int): Region{self.rank==1 && self.rect /*&& self.zeroBased==(min==0)*/} { // XTENLANG-4
        return RectRegion.make1(min, max);
    }        

    public static def makeBanded1(size: int, upper: int, lower: int): Region(2) { // XTENLANG-4
        return PolyRegion.makeBanded(size, upper, lower);
    }

    public static def makeBanded1(size: int): Region(2) { // XTENLANG-4
        return PolyRegion.makeBanded(size, 1, 1);
    }

    public static def makeUpperTriangular1(rowMin: int, colMin: int, size: int): Region(2)
        = PolyRegion.makeUpperTriangular2(rowMin, colMin, size);

    public static def makeLowerTriangular1(rowMin: int, colMin: int, size: int): Region(2)
        = PolyRegion.makeLowerTriangular2(rowMin, colMin, size);
    
    public static def make1(regions: Rail[Region]!): RectRegion(regions.length) { // XTENLANG-4
        var r: Region = regions(0);
        for (var i: int = 1; i<regions.length; i++)
            r = r.product(regions(i));
        return r as RectRegion(regions.length);
    }

    //
    // basic information
    //

    abstract public global def isConvex(): boolean;
    abstract public global def isEmpty(): boolean;
    abstract public global def size(): int;


    //
    // region composition
    //

    public global def union(that: Region(rank)): Region(rank) {
        val rs = new PolyRegionListBuilder(rank);
        rs.add(this);
        rs.add(that.difference(this));
        return UnionRegion.make(rs);
    }

    public global def disjointUnion(that: Region(rank)): Region(rank) {
        if (!this.intersection(that).isEmpty())
            throw U.illegal("regions are not disjoint");
        val rs = new PolyRegionListBuilder(rank);
        rs.add(this);
        rs.add(that);
        return UnionRegion.make(rs);
    }

    public global def difference(that: Region(rank)): Region(rank) {
        // complement might be expensive so we do some special casing
        if (this.isEmpty() || that.isEmpty())
            return this;
        else
            return this.intersection(that.complement());
    }

    public global def disjoint(that: Region(rank)): boolean {
        return this.intersection(that).isEmpty();
    }

    abstract public global def complement(): Region(rank);
    abstract public global def intersection(that: Region(rank)): Region(rank);
    abstract public global def product(that: Region): Region;
    abstract public global def projection(axis: int): Region(1);

    // XTENLANG-571
    /**
     * Bounding box is computed by taking the projection on each
     * axis. This implementation is more efficient than computing
     * projection on each axis because it re-uses the FME results.
     */

    global val boundingBoxException: RuntimeException;
    global val boundingBox:Region(rank);

    public global def boundingBox(): Region(rank) {
        if (boundingBoxException != null)
	    throw boundingBoxException;
        return boundingBox;
    }
    abstract protected global def computeBoundingBox(): Region(rank);


    //
    // low-performance bounds checking support
    //
    // XXX do we care about high-performance bounds checking for
    // rectangular regions?
    //
    // XXX in any case need mechanism to disable bounds checking for
    // performance
    //

    global def check(err:(Point)=>RuntimeException, pt: Point(rank)) {
        if (!contains(pt))
            throw err(pt);
    }

    global def check(err:(Point)=>RuntimeException, i0: int) {rank==1} {
        (this as BaseRegion(1)).check(err, [i0] as Point(1)); // XXXX cast?
    }

    global def check(err:(Point)=>RuntimeException, i0: int, i1: int) {rank==2} {
        (this as BaseRegion(2)).check(err, [i0,i1] as Point(2)); // XXXX cast?
    }

    global def check(err:(Point)=>RuntimeException, i0: int, i1: int, i2: int) {rank==3} {
        (this as BaseRegion(3)).check(err, [i0,i1,i2] as Point(3)); // XXXX cast?
    }

    global def check(err:(Point)=>RuntimeException, i0: int, i1: int, i2: int, i3: int) {rank==4} {
        (this as BaseRegion(4)).check(err, [i0,i1,i2,i3] as Point(4)); // XXXX cast?
    }


    //
    // region comparison operations
    //

    public global def contains(that: Region(rank)): boolean {
        return that.difference(this).isEmpty();
    }

    public global def equals(that:Object):boolean {
	if (!(that instanceof Region)) return false;
        val t = that as Region(rank);
        return this.contains(t) && t.contains(this);
    }


    //
    // pointwise
    //

    public global def contains(p: Point): boolean {
        throw U.unsupported(this, "contains(Point)");
    }



    //
    // scanning, iterating
    //
    // XXX - slight generalization, or wrappering, of
    // PolyRegion.Iterator gives us a BaseRegion.Iterator
    //

    public global def scanners(): Iterator[Scanner] {
        throw U.unsupported(this, "scanners()");
    }

    public global def iterator(): Iterator[Point(rank)] {
        throw U.unsupported(this, "iterator()");
    }


    //
    // debugging
    //

    public global def printInfo(out: Printer): void {
        out.println("Region " + this/*.getClass().getName()*/);
    }

    protected def this(rank: int, rect: boolean, zeroBased: boolean): BaseRegion {
        super(rank, rect, zeroBased);
	// XTENLANG-571
	try {
	    boundingBox = computeBoundingBox();
	    boundingBoxException = null;
	} catch (z:RuntimeException) {
	    boundingBox = null;
	    boundingBoxException = z;
	}
    }

    public global def min(): ValRail[int] {
        throw U.unsupported(this, "min()");
    }

    public global def max(): ValRail[int] {
        throw U.unsupported(this, "max()");

    }
}

