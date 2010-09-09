package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "x10.array.Dist")
public abstract value Dist(
    region: Region,
    unique: boolean,
    constant: boolean,
    onePlace: Place
) {

    property rank: int = region.rank;
    property rect: boolean = region.rect;
    property zeroBased: boolean = region.zeroBased;
    property rail: boolean = region.rail;

    //public static final UNIQUE: Dist = makeUnique();

@Native("java", "x10.array.DistFactory.makeUnique()")
    incomplete public static def makeUnique(): Dist;
@Native("java", "x10.array.DistFactory.makeLocal(#1)")
    incomplete public static def makeConstant(r: Region): Dist;
@Native("java", "x10.array.DistFactory.makeCyclic(#1, #2)")
    incomplete public static def makeCyclic(r: Region, axis: int): Dist;
@Native("java", "x10.array.DistFactory.makeBlock(#1, #2)")
    incomplete public static def makeBlock(r: Region, axis: int): Dist;
@Native("java", "x10.array.DistFactory.makeBlockCyclic(#1, #2, #3)")
    incomplete public static def makeBlockCyclic(r: Region, axis: int, blockSize: int): Dist;
@Native("java", "x10.array.DistFactory.makeLocal(#1)")
    incomplete public static def make(r: Region): Dist;

    incomplete public static def makeUnique(ps: Set[Place]): Dist;
@Native("java", "x10.array.DistFactory.makeConstant(#1, #2)")
    incomplete public static def makeConstant(r: Region, p: Place): Dist;
    incomplete public static def makeCyclic(r: Region, axis: int, ps: Set[Place]): Dist;
    incomplete public static def makeBlock(r: Region, axis: int, ps: Set[Place]): Dist;
    incomplete public static def makeBlockCyclic(r: Region, axis: int, blockSize: int, ps: Set[Place]): Dist;

    public abstract def regionMap(): Map[Place,Region];
@Native("java", "x10.core.RailFactory.makeFromJavaArray((#0).placesArray())")
    public abstract def places(): Rail[Place];
    public abstract def regions(): Rail[Region];
    public abstract def get(p: Place): Region;
    
@Native("java", "(#0).get(#1)")
    public abstract def get(p: Point): Place;
@Native("java", "(#0).get(#1)")
    public abstract def apply(p: Point): Place;

@Native("java", "(#0).difference(#1)")
    public abstract def difference(r: Region): Dist;
@Native("java", "(#0).difference(#1)")
    public abstract def difference(d: Dist): Dist;
@Native("java", "(#0).union(#1)")
    public abstract def union(d: Dist): Dist;
@Native("java", "(#0).intersection(#1)")
    public abstract def intersection(r: Region): Dist;
@Native("java", "(#0).intersection(#1)")
    public abstract def intersection(d: Dist): Dist;
@Native("java", "(#0).overlay(#1)")
    public abstract def overlay(d: Dist): Dist;
@Native("java", "(#0).isSubDistribution(#1)")
    public abstract def isSubDistribution(d: Dist): boolean;
@Native("java", "(#0).restriction(#1)")
    public abstract def restriction(r: Region): Dist;
@Native("java", "(#0).restriction(#1)")
    public abstract def restriction(p: Place): Dist;

@Native("java", "(#0).restriction(#1)")
    incomplete public def $bar(r: Region): Dist;
@Native("java", "(#0).restriction(#1)")
    incomplete public def $bar(p: Place): Dist;

    protected def this(region: Region, unique: boolean, constant: boolean, onePlace: Place) = {
        property(region, unique, constant, onePlace);
    }
}
