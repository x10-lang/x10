package x10.lang;

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

    incomplete public static def makeUnique(): Dist;
    incomplete public static def makeConstant(r: Region): Dist;
    incomplete public static def makeCyclic(r: Region, axis: int): Dist;
    incomplete public static def makeBlock(r: Region, axis: int): Dist;
    incomplete public static def makeBlockCyclic(r: Region, axis: int, blockSize: int): Dist;
    incomplete public static def make(r: Region): Dist;

    incomplete public static def makeUnique(ps: Set[Place]): Dist;
    incomplete public static def makeConstant(r: Region, p: Place): Dist;
    incomplete public static def makeCyclic(r: Region, axis: int, ps: Set[Place]): Dist;
    incomplete public static def makeBlock(r: Region, axis: int, ps: Set[Place]): Dist;
    incomplete public static def makeBlockCyclic(r: Region, axis: int, blockSize: int, ps: Set[Place]): Dist;

    public abstract def regionMap(): Map[Place,Region];
    public abstract def places(): Rail[Place];
    public abstract def regions(): Rail[Region];
    public abstract def get(p: Place): Region;

    public abstract def difference(r: Region): Dist;
    public abstract def difference(d: Dist): Dist;
    public abstract def union(d: Dist): Dist;
    public abstract def intersection(r: Region): Dist;
    public abstract def intersection(d: Dist): Dist;
    public abstract def overlay(d: Dist): Dist;
    public abstract def isSubDistribution(d: Dist): boolean;
    public abstract def restriction(r: Region): Dist;
    public abstract def restriction(p: Place): Dist;

    incomplete public def $bar(r: Region): Dist;
    incomplete public def $bar(p: Place): Dist;

    protected def this(region: Region, unique: boolean, constant: boolean, onePlace: Place) = {
        property(region, unique, constant, onePlace);
    }
}
