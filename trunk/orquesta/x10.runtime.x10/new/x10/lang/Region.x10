package x10.lang;

import x10.compiler.SetOps;


public abstract value Region(
    rank: int,
    rect: boolean,
    zeroBased: boolean
) implements SetOps[Region] {

    property rail = rank==1 && rect && zeroBased;

    public abstract def size(): int;
    public abstract def isConvex(): boolean;
    public abstract def disjoint(that: Region): boolean;

    incomplete public static def makeEmpty(rank: int): Region;
    incomplete public static def makeFull(rank: int): Region;
    incomplete public static def makeUnit(): Region;
    incomplete public static def makeRectangular(min: Rail[int], max: Rail[int]): Region;
    incomplete public static def makeRectangular(min: int, max: int): Region;
    incomplete public static def makeBanded(size: int, upper: int, lower: int): Region;
    incomplete public static def makeBanded(size: int): Region;
    incomplete public static def makeUpperTriangular(size: int): Region;
    incomplete public static def makeLowerTriangular(size: int): Region;
    incomplete public static def make(regions: Rail[Region]): Region;

    public abstract def union(that: Region): Region;
    public abstract def intersection(that: Region): Region;
    public abstract def difference(that: Region): Region;
    public abstract def product(that: Region): Region;
    public abstract def projection(axis: int): Region;
    public abstract def boundingBox(): Region;

    public abstract def contains(that: Region): boolean;
    public abstract def equals(that: Region): boolean;

    public abstract def contains(p: Point): boolean;

    incomplete public def $not(): Region;
    incomplete public def $and(that: Region): Region;
    incomplete public def $or(that: Region): Region;

    public abstract def scanners(): Iterator[Scanner];

    public static interface Scanner {
        def set(axis: int, position: int): void;
        def min(axis: int): int;
        def max(axis: int): int;
    }

    public abstract def iterator(): Iterator[Rail[int]];

    public abstract def printInfo(label: String): void;

    protected def this(rank: int, rect: boolean, zeroBased: boolean) = {
        property(rank, rect, zeroBased);
    }
}

