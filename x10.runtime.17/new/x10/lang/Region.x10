package x10.lang;

import x10.compiler.SetOps;
import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "x10.array.Region")
public abstract value Region(
    rank: int,
    rect: boolean,
    zeroBased: boolean
) implements SetOps[Region], Iterable[Rail[Int]] {

    property rail = rank==1 && rect && zeroBased;

@Native("java", "(#0).size()")
    native public  def size(): int;
    
@Native("java", "(#0).low()")
    native public def low(){rank==1}:Int;
    
@Native("java", "(#0).high()")
    native public def high(){rank==1}:Int;
    
    
@Native("java", "(#0).rank(#1)")
    native public  def rank(n:Int):Region(1);
    
@Native("java", "(#0).isConvex()")
    native public  def isConvex(): boolean;
@Native("java", "(#0).disjoint(#1)")
    native public def disjoint(that: Region): boolean;

@Native("java", "x10.array.RegionFactory.makeEmpty(#1)")
    native public static def makeEmpty(rank: int): Region(rank);

@Native("java", "x10.array.RegionFactory.makeFull(#1)")
    native public static def makeFull(rank: int): Region(rank);

@Native("java", "x10.array.RegionFactory.makeUnit()")
    native public static def makeUnit(): Region;

@Native("java", "x10.array.RegionFactory.makeRect(#1, #2)")
    native public static def makeRectangular(min: ValRail[Int], 
                       max: ValRail[Int](min.length))
    : Region{rank==min.length,rect};

@Native("java", "x10.array.RegionFactory.makeRect(#1, #2)")
    native public static def makeRectangular(min: int, max: int)
    : Region{rect,rank==1,zeroBased==(min==0)};

@Native("java", "x10.array.RegionFactory.makeBanded(#1, #2, #3)")
    native public static def makeBanded(size: int, upper: int, lower: int): Region(1);

@Native("java", "x10.array.RegionFactory.makeBanded(#1)")
    native public static def makeBanded(size: int): Region(1);

@Native("java", "x10.array.RegionFactory.makeUpperTriangular(#1)")
    native public static def makeUpperTriangular(size: int): Region;

@Native("java", "x10.array.RegionFactory.makeLowerTriangular(#1)")
    native public static def makeLowerTriangular(size: int): Region;
    
@Native("java", "x10.array.RegionFactory.makeFromRail(#1)")
    native public static def make(regions: Rail[Region]): Region(regions.length);

@Native("java", "x10.array.RegionFactory.makeFromRail(#1)")
    native public static def $convert(regions: Rail[Region]): Region(regions.length);

@Native("java", "x10.array.RegionFactory.make(#1,#2)")
    public static native def make(a: Region, b:Region): Region;

@Native("java", "x10.array.RegionFactory.make(#1)")
    public native static def make(regions: ValRail[Region]): Region(regions.length);
    
@Native("java", "x10.array.RegionFactory.make(#1)")
    public native static def $convert(regions: ValRail[Region]): Region(regions.length);


@Native("java", "(#0).union(#1)")
    public abstract def union(that: Region(rank)): Region(rank);

@Native("java", "(#0).intersection(#1)")
    public abstract def intersection(that: Region(rank)): Region(rank);

@Native("java", "(#0).difference(#1)")
    public abstract def difference(that: Region{rank==this.rank}): Region{rank==this.rank};

@Native("java", "(#0).product(#1)")
    public abstract def product(that: Region): Region;

@Native("java", "(#0).projection(#1)")
    public abstract def projection(axis: int): Region;

@Native("java", "(#0).boundingBox()")
    public abstract def boundingBox(): Region;

@Native("java", "(#0).contains(#1)")
    public abstract def contains(that: Region{rank==this.rank}): boolean;
@Native("java", "(#0).equals(#1)")
    public abstract def equals(that: Region): boolean;

@Native("java", "(#0).contains(#1)")
    public abstract def contains(p: Point): boolean;

@Native("java", "(#0).complement()")
    native public def $not(): Region;
    
@Native("java", "(#0).intersection(#1)")
    native public def $and(that: Region{rank==this.rank}): Region{rank==this.rank};
    
@Native("java", "(#0).union(#1)")
    native public def $or(that: Region{rank==this.rank}): Region{rank==this.rank};

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

