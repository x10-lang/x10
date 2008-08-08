package x10.lang;

import x10.compiler.ArithmeticOps;

public abstract value class Array[T] (

    region: Region,
    rank: int,
    rect: boolean,
    zeroBased: boolean,
    rail: boolean,

    dist: Dist,
    unique: boolean,
    constant: boolean,
    onePlace: Place

) implements Indexable[Point,T], Settable[Point,T], ArithmeticOps[Array[T]] {

    incomplete public static def make(dist: Dist, init: Indexable[nat,T]): Array[T];
    incomplete public static def make(region: Region, init: Indexable[nat,T]): Array[T];
    incomplete public static def make(region: Region, init: Indexable[nat,T], value: boolean): Array[T];
    incomplete public static def make(r: Rail[T]): Array[T];

    public abstract def restriction(r: Region): Array[T];

    public abstract def get(pt: Point): T;
    public abstract def get(i0: int): T;
    public abstract def get(i0: int, i1: int): T;
    public abstract def get(i0: int, i1: int, i2: int): T;

    public abstract def set(pt: Point, v: T): void;
    public abstract def set(i0: int, v: T): void;
    public abstract def set(i0: int, i1: int, v: T): void;
    public abstract def set(i0: int, i1: int, i2: int, v: T): void;

    incomplete public def $plus(): Array[T];
    incomplete public def $minus(): Array[T];

    incomplete public def $plus(that: Array[T]): Array[T];
    incomplete public def $minus(that: Array[T]): Array[T];
    incomplete public def $times(that: Array[T]): Array[T];
    incomplete public def $over(that: Array[T]): Array[T];

    incomplete public def $bar(r: Region): Array[T];
    incomplete public def $bar(p: Place): Array[T];

    incomplete public static def $convert(r: Rail[T]): Array[T];    


    protected def this[T](dist: Dist) = {

        property(
            dist,
            dist.unique,
            dist.constant,
            dist.onePlace,

            dist.region,
            region.rank,
            region.rect,
            region.zeroBased,
            region.rail
        );
    }

}
