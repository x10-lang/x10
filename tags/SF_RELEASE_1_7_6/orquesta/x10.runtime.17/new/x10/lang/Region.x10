package x10.lang;

import x10.compiler.SetOps;
import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "x10.array.Region")
public abstract value Region(
    rank: int,
    rect: boolean,
    zeroBased: boolean
) implements SetOps[Region] {

    property rail = rank==1 && rect && zeroBased;

@Native("java", "(#0).size()")
    public abstract def size(): int;
@Native("java", "(#0).isConvex()")
    public abstract def isConvex(): boolean;
@Native("java", "(#0).disjoint(#1)")
    public abstract def disjoint(that: Region): boolean;

@Native("java", "x10.array.RegionFactory.makeEmpty(#1)")
    incomplete public static def makeEmpty(rank: int): Region;
@Native("java", "x10.array.RegionFactory.makeFull(#1)")
    incomplete public static def makeFull(rank: int): Region;
@Native("java", "x10.array.RegionFactory.makeUnit()")
    incomplete public static def makeUnit(): Region;
@Native("java", "x10.array.RegionFactory.makeRect(#1, #2)")
    incomplete public static def makeRectangular(min: Rail[int], max: Rail[int]): Region;
@Native("java", "x10.array.RegionFactory.makeRect(#1, #2)")
    incomplete public static def makeRectangular(min: int, max: int): Region;
@Native("java", "x10.array.RegionFactory.makeBanded(#1, #2, #3)")
    incomplete public static def makeBanded(size: int, upper: int, lower: int): Region;
@Native("java", "x10.array.RegionFactory.makeBanded(#1)")
    incomplete public static def makeBanded(size: int): Region;
@Native("java", "x10.array.RegionFactory.makeUpperTriangular(#1)")
    incomplete public static def makeUpperTriangular(size: int): Region;
@Native("java", "x10.array.RegionFactory.makeLowerTriangular(#1)")
    incomplete public static def makeLowerTriangular(size: int): Region;
    
@Native("java", "x10.array.RegionFactory.makeFromRail(#1)")
    incomplete public static def make(regions: Rail[Region]): Region;
@Native("java", "x10.array.RegionFactory.makeFromValRail(#1)")
    incomplete public static def make(regions: ValRail[Region]): Region;

@Native("java", "(#0).union(#1)")
    public abstract def union(that: Region): Region;
@Native("java", "(#0).intersection(#1)")
    public abstract def intersection(that: Region): Region;
@Native("java", "(#0).difference(#1)")
    public abstract def difference(that: Region{rank==this.rank}): Region{rank==this.rank};
@Native("java", "(#0).product(#1)")
    public abstract def product(that: Region): Region;
@Native("java", "(#0).projection(#1)")
    public abstract def projection(axis: int): Region;
@Native("java", "(#0).boundingBox()")
    public abstract def boundingBox(): Region;

@Native("java", "(#0).contains(#1)")
    public abstract def contains(that: Region): boolean;
@Native("java", "(#0).equals(#1)")
    public abstract def equals(that: Region): boolean;

@Native("java", "(#0).contains(#1)")
    public abstract def contains(p: Point): boolean;

@Native("java", "(#0).complement()")
    incomplete public def $not(): Region;
@Native("java", "(#0).intersection(#1)")
    incomplete public def $and(that: Region): Region;
@Native("java", "(#0).union(#1)")
    incomplete public def $or(that: Region): Region;

/*
    public abstract def scanners(): Iterator[Scanner];

    public static interface Scanner {
        def set(axis: int, position: int): void;
        def min(axis: int): int;
        def max(axis: int): int;
    }
*/

    public abstract def iterator(): Iterator[Rail[int]];

    public abstract def printInfo(label: String): void;

    protected def this(rank: int, rect: boolean, zeroBased: boolean) = {
        property(rank, rect, zeroBased);
    }
}

