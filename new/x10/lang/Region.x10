// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.lang;

import x10.compiler.SetOps;

import x10.array.BaseRegion;

public abstract value class Region(
    rank: int,
    rect: boolean,
    zeroBased: boolean
) implements Iterable[Point] {

    property rail = rank==1 && rect && zeroBased;

    public abstract def size(): int;
    public abstract def isConvex(): boolean;
    abstract public def isEmpty(): boolean;
    public abstract def disjoint(that: Region(rank)): boolean;

    public static def makeEmpty(val rank: int): Region(rank) {
        return BaseRegion.makeEmpty1(rank);
    }
    
    public static def makeFull(val rank: int): Region(rank) {
        return BaseRegion.makeFull1(rank);
    }
    
    public static def makeUnit(): Region(0) {
        return BaseRegion.makeUnit1();
    }
    
    public static def makeRectangular(val min: Rail[int], val max: Rail[int]): RectRegion(min.length) {
        return BaseRegion.makeRectangular1(min, max);
    }        
    
    // XTENLANG-109 prevents zeroBased==(min==0)
    public static def makeRectangular(min: int, max: int): Region{rank==1 && /*zeroBased==(min==0) &&*/ rect} {
        return BaseRegion.makeRectangular1(min, max);
    }        
    
    public static def makeBanded(size: int, upper: int, lower: int): Region(2) {
        return BaseRegion.makeBanded1(size, upper, lower);
    }
    
    public static def makeBanded(size: int): Region(2) {
        return BaseRegion.makeBanded1(size);
    }
    
    public static def makeUpperTriangular(size: int): Region(2) {
        return BaseRegion.makeUpperTriangular1(size);
    }
    
    public static def makeLowerTriangular(size: int): Region(2) {
        return BaseRegion.makeLowerTriangular1(size);
    }
    
    public static def make(min: int, max: int): RectRegion(1) {
        return BaseRegion.makeRectangular1(min, max);
    }
    
    public static def make(val regions: ValRail[Region/*(1)*/]): RectRegion(regions.length) {
        return BaseRegion.make1(regions);
    }


    abstract public def complement(): Region(rank);
    abstract public def union(that: Region(rank)): Region(rank);
    abstract public def disjointUnion(that: Region(rank)): Region(rank);
    abstract public def intersection(that: Region(rank)): Region(rank);
    abstract public def difference(that: Region(rank)): Region(rank);
    abstract public def product(that: Region): Region;
    abstract public def projection(axis: int): Region(1);
    abstract public def boundingBox(): Region(rank);

    abstract public def min(): Rail[int];
    abstract public def max(): Rail[int];
    
    public def min(i:nat) = min()(i);
    public def max(i:nat) = max()(i);    

    public static def $convert(rs: ValRail[Region]): RectRegion(rs.length) = make(rs);


    //
    // set ops
    //

    public def $not(): Region(rank) {
        return complement();
    }

    public def $and(that: Region(rank)): Region(rank) {
        return intersection(that);
    }

    public def $or(that: Region(rank)): Region(rank) {
        return union(that);
    }

    public def $minus(that: Region(rank)): Region(rank) {
        return difference(that);
    }


    //
    // comparison operations
    //

    abstract public def contains(that: Region(rank)): boolean;
    abstract public def equals(that: Region/*(rank)*/): boolean; // XTENLANG-???
    abstract public def contains(p: Point): boolean;


    //
    // efficient scanning - rank is known at compile time
    //

    public abstract def scanners(): Iterator[Scanner];

    public static interface Scanner {
        def set(axis: int, position: int): void;
        def min(axis: int): int;
        def max(axis: int): int;
    }

    //
    // iteration - rank is not known at compile time
    //
    // Region.Iterator it = r.iterator();
    // while (it.hasNext()) {
    //     int [] x = it.next();
    //     ... body using x[0],x[1], etc. ...
    // }
    //

    public abstract def iterator(): Iterator[Point(rank)];


    protected def this(rank: int, rect: boolean, zeroBased: boolean) {
        property(rank, rect, zeroBased);
    }
}

