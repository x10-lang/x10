package x10.lang;

import x10.compiler.ArithmeticOps;

// note: use dist.region.rank, not rank -- only the properties in the
// header are in scope

public abstract value class Array[T](dist: Dist) implements
    Indexable[Point(dist.region.rank),T],
    Settable[Point(dist.region.rank),T],
    ArithmeticOps[Array[T]]
{
    // region via dist
    property region: Region = dist.region;
    property rank: int = dist.rank;
    property rect: boolean = dist.rect;
    property zeroBased: boolean = dist.zeroBased;
    
    // dist
    property rail: boolean = dist.rail;
    property unique: boolean = dist.unique;
    property constant: boolean = dist.constant;
    property onePlace: Place = dist.onePlace;

    incomplete public static def make[T](dist: Dist, init: Indexable[nat,T]): Array[T];
    incomplete public static def make[T](region: Region, init: Indexable[nat,T]): Array[T];
    incomplete public static def make[T](region: Region, init: Indexable[nat,T], value: boolean): Array[T];
    incomplete public static def make[T](r: Rail[T]): Array[T]{rank==1};
    incomplete public static def make[T](r: ValRail[T]): Array[T]{rank==1};
    
    public abstract def restriction(r: Region): Array[T];
    
    public def apply(pt: Point(this.rank)): T = get(pt);
    
    public abstract def get(pt: Point(this.rank)): T;
    public abstract def get(i0: int){rank==1}: T;
    public abstract def get(i0: int, i1: int){rank==2}: T;
    public abstract def get(i0: int, i1: int, i2: int){rank==3}: T;
                     
    public abstract def set(pt: Point(this.rank), v: T): void;
    public abstract def set(i0: int, v: T){rank==1}: void;
    public abstract def set(i0: int, i1: int, v: T){rank==2}: void;
    public abstract def set(i0: int, i1: int, i2: int, v: T){rank==3}: void;
                     
    incomplete public def $plus(): Array[T];
    incomplete public def $minus(): Array[T];

    incomplete public def $plus(that: Array[T]): Array[T];
    incomplete public def $minus(that: Array[T]): Array[T];
    incomplete public def $times(that: Array[T]): Array[T];
    incomplete public def $over(that: Array[T]): Array[T];

    incomplete public def $bar(r: Region): Array[T];
    incomplete public def $bar(p: Place): Array[T];

    incomplete public static def $convert[T](r: Rail[T]): Array[T];    
    incomplete public static def $convert[T](r: ValRail[T]): Array[T];    

    //
    // private/protected
    //

    protected def this(dist: Dist) = {
        property(dist);
    }
}
