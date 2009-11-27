// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.lang;

import x10.compiler.ArithmeticOps;
import x10.array.BaseArray;
import x10.array.FastArray;


/**
 * An array defines a mapping from points to data of some type T. An
 * array is defined over a particular region. Distributed arrays are
 * supported by defining an array over a distribution; the
 * distribution determines at what place each array data item is
 * stored. Arrays may be accessed using a(p) because arrays implement
 * (Point)=>T. Array views are supported via the restriction function.
 *
 * @author bdlucas
 */

public abstract class Array[T](dist:Dist)
    implements (Point{self.rank==dist.region.rank})=>T,
               Iterable[Point{self.rank==dist.region.rank}]
{

    //
    // properties
    //

    // region via dist
    public global property region: Region(rank) = dist.region;
    public global property rank: int = dist.rank;
    public global property rect: boolean = dist.rect;
    public global property zeroBased: boolean = dist.zeroBased;
    
    // dist
    public global property rail: boolean = dist.rail;
    public global property unique: boolean = dist.unique;
    public global property constant: boolean = dist.constant;
    public global property onePlace: Place = dist.onePlace;


    //
    // factories
    //

    public static def make[T](region:Region)=makeVar[T](region);
    public static def make[T](dist: Dist)= makeVar[T](dist);
    public static def make[T](region:Region, init: (Point(region.rank))=>T)= makeVar[T](region, init);
    public static def make[T](dist: Dist, init: (Point(dist.rank))=>T)= makeVar[T](dist, init);

    public static def makeVar[T](region: Region)= BaseArray.makeVar1[T](region);
    public static def makeVar[T](region: Region, init: (Point(region.rank))=>T): Array[T](region)
        = BaseArray.makeVar1[T](region, init) as Array[T](region);
    

    public static def makeVar[T](dist: Dist, init:(Point(dist.rank))=>T): Array[T](dist)
        = BaseArray.makeVar1[T](dist, init);
    public static def makeVar[T](dist: Dist): Array[T](dist)
    = BaseArray.makeVar1[T](dist);

    public static def makeVal[T](region: Region): Array[T](region)
        = BaseArray.makeVal1[T](region) as Array[T](region);

    public static def makeVal[T](dist: Dist): Array[T](dist)
        = BaseArray.makeVal1[T](dist) as Array[T](dist);

    public static def makeVal[T](region: Region, init: (Point(region.rank))=>T): Array[T](region)
        = BaseArray.makeVal1[T](region, init) as Array[T](region);

    public static def makeVal[T](dist: Dist, init: (Point(dist.rank))=>T): Array[T](dist)
        = BaseArray.makeVal1[T](dist, init) as Array[T](dist);
 
    public static def make[T](rail: Rail[T]): Array[T]{rank==1&&rect&&zeroBased}
        = BaseArray.makeVar1[T](rail);
    public static def make[T](rail: ValRail[T]): Array[T]{rank==1&&rect&&zeroBased}
        = BaseArray.makeVar1[T](rail);

    public static def make[T](size: nat, init: (Point(1))=>T): Array[T](1)
        = makeVar[T](0..size-1, init) as Array[T](1);

    public static def makeFast[T](region: Region)
        = makeVar[T](region)
            as FastArray[T]{region==region};

    public static def makeFast[T](dist: Dist)
        = makeVar[T](dist)
            as FastArray[T]{dist==dist};

    public static def makeFast[T](region: Region, init: (Point(region.rank))=>T)
        = BaseArray.makeVar1[T](region, init)
            as FastArray[T]{region==region};

    public static def makeFast[T](dist: Dist, init: (Point(dist.rank))=>T)
        = BaseArray.makeVar1[T](dist, init)
            as FastArray[T]{dist==dist};


    //
    // operations
    //

    public abstract safe global def apply(pt: Point(rank)): T;
    public abstract safe global def apply(i0: int) {rank==1}: T;
    public abstract safe global def apply(i0: int, i1: int) {rank==2}: T;
    public abstract safe global def apply(i0: int, i1: int, i2: int) {rank==3}: T;
    public abstract safe global def apply(i0: int, i1: int, i2: int, i3:int) {rank==4}: T;
    
    public abstract safe global def set(v:T, pt: Point(rank)): T;
    public abstract safe global def set(v:T, i0: int) {rank==1}: T;
    public abstract safe global def set(v:T, i0: int, i1: int) {rank==2}: T;
    public abstract safe global def set(v:T, i0: int, i1: int, i2: int) {rank==3}: T;
    public abstract safe global def set(v:T, i0: int, i1: int, i2: int, i3:int) {rank==4}: T;

    public abstract safe global def restriction(r: Region(rank)): Array[T](rank);
    public abstract safe global def restriction(p: Place): Array[T](rank);

    public abstract safe global operator + this: Array[T](rank);
    public abstract safe global operator - this: Array[T](rank);

    public abstract safe global operator this + (that: Array[T](dist)): Array[T](dist);
    public abstract safe global operator this - (that: Array[T](dist)): Array[T](dist);
    public abstract safe global operator this * (that: Array[T](dist)): Array[T](dist);
    public abstract safe global operator this / (that: Array[T](dist)): Array[T](dist);

    public abstract safe global operator this | (r: Region(rank)): Array[T](dist);
    public abstract safe global operator this | (p: Place): Array[T](dist);


    //
    // array operations
    //

    public abstract global def lift(op:(T)=>T): Array[T](dist);
    public abstract global def reduce(op:(T,T)=>T, unit:T): T;
    public abstract global def scan(op:(T,T)=>T, unit:T): Array[T](dist);

    //
    // further generalizations TBD:
    // - extra array arg to contain result
    // - op takes current Point
    //
    // also TBD:
    //   public abstract global def lift[U](op:(T)=>U): Array[U](dist);
    //   public abstract global def lift[U,V](op:(T,U)=>V, that:Array[U](dist)): Array[V](dist);
    //   public abstract global def overlay(that:Array[T](rank)): Array[T](rank);
    //   public abstract global def update(that:Array[T](rank)): Array[T](rank);
    //   public abstract global def sum(): T;
    //

    //
    //
    //

    public static operator [T](r: Rail[T]): Array[T](1) = make(r);
    public static operator [T](r: ValRail[T]): Array[T](1) = make(r);


    public global def iterator(): Iterator[Point(rank)] = region.iterator() as Iterator[Point(rank)];


    //
    //
    //

    protected def this(dist: Dist) = property(dist);

}
