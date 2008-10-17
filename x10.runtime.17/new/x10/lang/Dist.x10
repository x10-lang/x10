// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.lang;

import x10.array.BaseDist;

public abstract value class Dist(
    region: Region,
    unique: boolean,
    constant: boolean,
    onePlace: Place
) implements
    (Point/*(region.rank)*/)=>Place
    // (Place)=>Region XTENLANG-60
{

    property rank: int = region.rank;
    property rect: boolean = region.rect;
    property zeroBased: boolean = region.zeroBased;
    property rail: boolean = region.rail;

    // XTENLANG-50: workaround requires explicit return type decls here
    // XTENLANG-4: workaround requires BaseDist methods to be named differently from these methods

    //
    // factories - place is all applicable places
    //

    public static def makeUnique(): Dist(1) = BaseDist.makeUnique1();

    public static def makeUnique(ps:Rail[Place]): Dist(1) = BaseDist.makeUnique1(ps);

    public static def makeConstant(r: Region): Dist(r.rank) = BaseDist.makeConstant1(r);

    public static def makeCyclic(r: Region, axis: int): Dist(r.rank) = BaseDist.makeBlockCyclic1(r, axis, 1);

    public static def makeBlock(r: Region, axis: int): Dist(r.rank) {
        val n = r.max()(axis) - r.min()(axis) + 1;
        val bs = (n + Place.MAX_PLACES - 1) / Place.MAX_PLACES;
        return BaseDist.makeBlockCyclic1(r, axis, bs);
    }

    public static def makeBlockCyclic(r: Region, axis: int, blockSize: int): Dist(r.rank) = BaseDist.makeBlockCyclic1(r, axis, blockSize);

    public static def make(r: Region): Dist(r.rank) = makeConstant(r);


    //
    // factories - place is a parameter
    //

    public static def makeUnique(ps: Set[Place]): Dist(1) = BaseDist.makeUnique1(ps);

    public static def makeConstant(r: Region, p: Place): Dist(r.rank) = BaseDist.makeConstant1(r, p);

    public static def makeCyclic(r: Region, axis: int, ps: Set[Place]): Dist(r.rank) = BaseDist.makeCyclic1(r, axis, ps);

    public static def makeBlock(r: Region, axis: int, ps: Set[Place]): Dist(r.rank) = BaseDist.makeBlock1(r, axis, ps);

    public static def makeBlockCyclic(r: Region, axis: int, blockSize: int, ps: Set[Place]) = BaseDist.makeBlockCyclic1(r, axis, blockSize, ps);


    //
    // mapping places to regions
    //

    abstract public def places(): Rail[Place];   // essentially regionMap().keys()
    abstract public def regions(): Rail[Region]; // essentially regionMap().values()
    abstract public def get(p: Place): Region(rank);

    //
    // mapping points to places
    //

    abstract public def apply(pt: Point/*(rank)*/): Place;
    abstract public def apply(i0: int): Place;
    abstract public def apply(i0: int, i1: int): Place;
    abstract public def apply(i0: int, i1: int, i2: int): Place;

    //
    //
    //

    public def iterator(): Iterator[Point(rank)] = region.iterator();

    //
    // region operations
    //

    abstract public def isSubdistribution(d: Dist(rank)): boolean;
    abstract public def intersection(r: Region(rank)): Dist(rank);
    abstract public def difference(r: Region(rank)): Dist(rank);
    abstract public def intersection(d: Dist(rank)): Dist(rank);
    abstract public def difference(d: Dist(rank)): Dist(rank);
    abstract public def union(d: Dist(rank)): Dist(rank);
    abstract public def overlay(d: Dist(rank)): Dist(rank);
    abstract public def restriction(r: Region(rank)): Dist(rank);
    abstract public def restriction(p: Place): Dist(rank);
    abstract public def equals(that: Dist/*(rank)*/): boolean;
    abstract public def contains(p: Point): boolean;

    public def $bar(r: Region(this.rank)): Dist(this.rank) = {
        return restriction(r);
    }

    public def $bar(p: Place): Dist(rank) = {
        return restriction(p);
    }

    public def $and(d: Dist(rank)): Dist(rank) = {
        return intersection(d);
    }

    public def $or(d: Dist(rank)): Dist(rank) = {
        return union(d);
    }

    public def $minus(d: Dist(rank)): Dist(rank) = {
        return difference(d);
    }


    //
    //
    //

    protected def this(region: Region, unique: boolean, constant: boolean, onePlace: Place) = {
        property(region, unique, constant, onePlace);
    }
}
