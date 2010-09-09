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

public abstract value class Array[T](dist:Dist)
    implements (Point{self.rank==dist.region.rank})=>T,
               Iterable[Point{self.rank==dist.region.rank}]
{

    //
    // properties
    //

    // region via dist
    public property region: Region(rank) = dist.region;
    public property rank: int = dist.rank;
    public property rect: boolean = dist.rect;
    public property zeroBased: boolean = dist.zeroBased;
    
    // dist
    public property rail: boolean = dist.rail;
    public property unique: boolean = dist.unique;
    public property constant: boolean = dist.constant;
    public property onePlace: Place = dist.onePlace;


    //
    // factories
    //

    public static def make[T](region:Region): Array[T](region)
        = make[T](region, null as Box[(Point)=>T]) as Array[T](region);

    public static def make[T](dist: Dist): Array[T](dist)
        = make[T](dist, null as Box[(Point)=>T]) as Array[T](dist);

    public static def make[T](region:Region, init: Box[(Point)=>T]): Array[T](region)
        = makeVar[T](region, init) as Array[T](region);

    public static def make[T](dist: Dist, init: Box[(Point)=>T]): Array[T](dist)
        = makeVar[T](dist, init) as Array[T](dist);

    public static def makeVar[T](region: Region): Array[T](region)
        = makeVar[T](region, null as Box[(Point)=>T]) as Array[T](region);

    public static def makeVar[T](dist: Dist): Array[T](dist)
        = makeVar[T](dist, null as Box[(Point)=>T]) as Array[T](dist);

    public static def makeVar[T](region: Region, init: Box[(Point)=>T]): Array[T](region)
        = BaseArray.makeVar1[T](region, init) as Array[T](region);

    public static def makeVar[T](dist: Dist, init: Box[(Point)=>T]): Array[T](dist)
        = BaseArray.makeVar1[T](dist, init);

    public static def makeVal[T](region: Region): Array[T](region)
        = makeVal[T](region, null as Box[(Point)=>T]) as Array[T](region);

    public static def makeVal[T](dist: Dist): Array[T](dist)
        = makeVal[T](dist, null as Box[(Point)=>T]) as Array[T](dist);

    public static def makeVal[T](region: Region, init: Box[(Point)=>T]): Array[T](region)
        = BaseArray.makeVal1[T](region, init) as Array[T](region);

    public static def makeVal[T](dist: Dist, init: Box[(Point)=>T]): Array[T](dist)
        = BaseArray.makeVal1[T](dist, init) as Array[T](dist);

    public static def make[T](rail: Rail[T]): Array[T]{rank==1&&rect&&zeroBased}
        = BaseArray.makeVar1[T](rail);
    public static def make[T](rail: ValRail[T]): Array[T]{rank==1&&rect&&zeroBased}
        = BaseArray.makeVar1[T](rail);

    public static def make[T](size: nat, init: Box[(Point)=>T]): Array[T](1)
        = makeVar[T](0..size-1, init) as Array[T](1);

    public static def makeFast[T](region: Region)
        = makeVar[T](region, null as Box[(Point)=>T])
            as FastArray[T]{region==region};

    public static def makeFast[T](dist: Dist)
        = makeVar[T](dist, null as Box[(Point)=>T])
            as FastArray[T]{dist==dist};

    public static def makeFast[T](region: Region, init: (Point)=>T)
        = BaseArray.makeVar1[T](region, (init as (Point)=>T) as Box[(Point)=>T])
            as FastArray[T]{region==region};

    public static def makeFast[T](dist: Dist, init: (Point)=>T)
        = BaseArray.makeVar1[T](dist, (init as (Point)=>T) as Box[(Point)=>T])
            as FastArray[T]{dist==dist};


    //
    // operations
    //

    public abstract safe def apply(pt: Point(rank)): T;
    public abstract safe def apply(i0: int) {rank==1}: T;
    public abstract safe def apply(i0: int, i1: int) {rank==2}: T;
    public abstract safe def apply(i0: int, i1: int, i2: int) {rank==3}: T;
    public abstract safe def apply(i0: int, i1: int, i2: int, i3:int) {rank==4}: T;
    
    public abstract safe def set(v:T, pt: Point(rank)): T;
    public abstract safe def set(v:T, i0: int) {rank==1}: T;
    public abstract safe def set(v:T, i0: int, i1: int) {rank==2}: T;
    public abstract safe def set(v:T, i0: int, i1: int, i2: int) {rank==3}: T;
    public abstract safe def set(v:T, i0: int, i1: int, i2: int, i3:int) {rank==4}: T;

    public abstract safe def restriction(r: Region(rank)): Array[T];
    public abstract safe def restriction(p: Place): Array[T];

    public abstract safe operator + this: Array[T];
    public abstract safe operator - this: Array[T];

    public abstract safe operator this + (that: Array[T]): Array[T];
    public abstract safe operator this - (that: Array[T]): Array[T];
    public abstract safe operator this * (that: Array[T]): Array[T];
    public abstract safe operator this / (that: Array[T]): Array[T];

    public abstract safe operator this | (r: Region(rank)): Array[T];
    public abstract safe operator this | (p: Place): Array[T];


    //
    // array operations
    //

    public abstract def lift(op:(T)=>T): Array[T](dist);
    public abstract def reduce(op:(T,T)=>T, unit:T): T;
    public abstract def scan(op:(T,T)=>T, unit:T): Array[T](dist);

    //
    // further generalizations TBD:
    // - extra array arg to contain result
    // - op takes current Point
    //
    // also TBD:
    //   public abstract def lift[U](op:(T)=>U): Array[U](dist);
    //   public abstract def lift[U,V](op:(T,U)=>V, that:Array[U](dist)): Array[V](dist);
    //   public abstract def overlay(that:Array[T](rank)): Array[T](rank);
    //   public abstract def update(that:Array[T](rank)): Array[T](rank);
    //   public abstract def sum(): T;
    //

    //
    //
    //

    public static operator [T](r: Rail[T]): Array[T] = make(r);
    public static operator [T](r: ValRail[T]): Array[T] = make(r);


    public def iterator(): Iterator[Point(rank)] = region.iterator() as Iterator[Point(rank)];


    //
    //
    //

    protected def this(dist: Dist) = property(dist);

}
