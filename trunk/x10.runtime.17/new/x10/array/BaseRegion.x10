// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.io.PrintStream;

abstract public value class BaseRegion extends Region {

    // XTENLANG-49
    static type PolyRegion(rank:nat) = PolyRegion{self.rank==rank};
    static type PolyRegionList(rank:nat) = PolyRegionList{self.rank==rank};
    static type Halfspace(rank:nat) = Halfspace{self.rank==rank};
    static type HalfspaceList(rank:nat) = HalfspaceList{self.rank==rank};
    static type BaseRegion(rank:int) = BaseRegion{self.rank==rank};


    //
    // factories
    //

    public static def makeEmpty1(val rank: int): Region(rank) { // XTENLANG-4
        return new EmptyRegion(rank);
    }

    public static def makeFull1(val rank: int): Region(rank) { // XTENLANG-4
        return new FullRegion(rank);
    }

    public static def makeUnit1(): Region(0) { // XTENLANG-4
        return new FullRegion(0);
    }

    public static def makeHalfspace1(rank:int, normal:Point(rank), k:int): Region(rank) {
        val hl = new HalfspaceList(rank);
        val as = Rail.makeVar[int](rank+1);
        for (var i:int=0; i<rank; i++)
            as(i) = normal(i);
        as(rank) = k;
        val h = new Halfspace(as);
        hl.add(h);
        return PolyRegion.make(hl);
    }

    public static def makeRectangular1(val min: Rail[int], val max: Rail[int]): RectRegion(min.length) { // XTENLANG-4
        return RectRegion.make1(min, max);
    }        

    // XTENLANG-109 prevents zeroBased==(min==0)
    public static def makeRectangular1(min: int, max: int): Region{rank==1 && rect /*&& zeroBased==(min==0)*/} { // XTENLANG-4
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
    
    public static def make1(val regions: Rail[Region]): RectRegion(regions.length) { // XTENLANG-4
        var r: Region = regions(0);
        for (var i: int = 1; i<regions.length; i++)
            r = r.product(regions(i));
        return r as RectRegion(regions.length);
    }

    //
    // basic information
    //

    abstract public def isConvex(): boolean;
    abstract public def isEmpty(): boolean;
    abstract public def size(): int;


    //
    // region composition
    //

    public def union(that: Region(rank)): Region(rank) {
        var rs: PolyRegionList(rank) = new PolyRegionList(rank);
        rs.add(this);
        rs.add(that.difference(this));
        return UnionRegion.make(rs);
    }

    public def disjointUnion(that: Region(rank)): Region(rank) {
        if (!this.intersection(that).isEmpty())
            throw U.illegal("regions are not disjoint");
        var rs: PolyRegionList(rank) = new PolyRegionList(rank);
        rs.add(this);
        rs.add(that);
        return UnionRegion.make(rs);
    }

    public def difference(that: Region(rank)): Region(rank) {
        // complement might be expensive so we do some special casing
        if (this.isEmpty() || that.isEmpty())
            return this;
        else
            return this.intersection(that.complement());
    }

    public def disjoint(that: Region(rank)): boolean {
        return this.intersection(that).isEmpty();
    }

    abstract public def complement(): Region(rank);
    abstract public def intersection(that: Region(rank)): Region(rank);
    abstract public def product(that: Region): Region;
    abstract public def projection(axis: int): Region(1);
    abstract public def boundingBox(): Region(rank);


    //
    // cached boundingbox, maintained by subclasses
    // B should be Box[Region] - workaround for XTENLANG-117
    //

    protected class B(b:Region) {
        def this(b:Region) = property(b);
    }

    protected class BB {
        var b:B = null;
    }        

    protected val boundingBox = new BB();


    //
    // low-performance bounds checking support
    //
    // XXX do we care about high-performance bounds checking for
    // rectangular regions?
    //
    // XXX in any case need mechanism to disable bounds checking for
    // performance
    //

    def checkBounds(pt: Point(rank)) {
        if (!contains(pt))
            throw new ArrayIndexOutOfBoundsException("point " + pt + " not contained in array");
    }

    def checkBounds(i0: int) {rank==1} {
        (this as BaseRegion(1)).checkBounds([i0] to Point(1)); // XXXX cast?
    }

    def checkBounds(i0: int, i1: int) {rank==2} {
        (this as BaseRegion(2)).checkBounds([i0,i1] to Point(2)); // XXXX cast?
    }

    def checkBounds(i0: int, i1: int, i2: int) {rank==3} {
        (this as BaseRegion(3)).checkBounds([i0,i1,i2] to Point(3)); // XXXX cast?
    }

    def checkBounds(i0: int, i1: int, i2: int, i3: int) {rank==4} {
        (this as BaseRegion(4)).checkBounds([i0,i1,i2,i3] to Point(4)); // XXXX cast?
    }


    //
    // region comparison operations
    //

    public def contains(that: Region(rank)): boolean {
        return that.difference(this).isEmpty();
    }

    public def equals(that: Region/*(rank)*/): boolean { // XTENLANG-???
        val t = that as Region(rank);
        return this.contains(t/*that*/) && t/*that*/.contains(this);
    }


    //
    // pointwise
    //

    public def contains(p: Point): boolean {
        throw U.unsupported(this, "contains(Point)");
    }



    //
    // scanning, iterating
    //
    // XXX - slight generalization, or wrappering, of
    // PolyRegion.Iterator gives us a BaseRegion.Iterator
    //

    public def scanners(): Iterator[Scanner] {
        throw U.unsupported(this, "scanners()");
    }

    public def iterator(): Iterator[Point(rank)] {
        throw U.unsupported(this, "iterator()");
    }


    //
    // debugging
    //

    public def printInfo(out: PrintStream): void {
        out.println("Region " + this/*.getClass().getName()*/);
    }


    //
    //
    //

    protected def this(rank: int, rect: boolean, zeroBased: boolean): BaseRegion {
        super(rank, rect, zeroBased);
    }

    public def min(): Rail[int] {
        throw U.unsupported(this, "min()");
    }

    public def max(): Rail[int] {
        throw U.unsupported(this, "max()");
    }
}
