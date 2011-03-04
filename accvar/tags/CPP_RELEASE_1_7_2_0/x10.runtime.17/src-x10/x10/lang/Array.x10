// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.lang;

import x10.compiler.ArithmeticOps;
import x10.array.BaseArray;

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
    implements (Point{self.rank==dist.region.rank})=>T
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
        = make[T](region, null as (Point)=>T) as Array[T](region);

    public static def make[T](dist: Dist): Array[T](dist)
        = make[T](dist, null as (Point)=>T) as Array[T](dist);

    public static def make[T](region:Region, init: (Point)=>T): Array[T](region)
        = makeVar[T](region, init) as Array[T](region);

    public static def make[T](dist: Dist, init: (Point)=>T): Array[T](dist)
        = makeVar[T](dist, init) as Array[T](dist);

    public static def makeVar[T](region: Region): Array[T](region)
        = makeVar[T](region, null as (Point)=>T) as Array[T](region);

    public static def makeVar[T](dist: Dist): Array[T](dist)
        = makeVar[T](dist, null as (Point)=>T) as Array[T](dist);

    public static def makeVar[T](region: Region, init: (Point)=>T): Array[T](region)
        = BaseArray.makeVar1[T](region, init) as Array[T](region);

    public static def makeVar[T](dist: Dist, init: (Point)=>T): Array[T](dist)
        = BaseArray.makeVar1[T](dist, init);

    public static def makeVal[T](region: Region): Array[T](region)
        = makeVal(region, null as (Point)=>T) as Array[T](region);

    public static def makeVal[T](dist: Dist): Array[T](dist)
        = makeVal(dist, null as (Point)=>T) as Array[T](dist);

    public static def makeVal[T](region: Region, init: (Point)=>T): Array[T](region)
        = BaseArray.makeVal1[T](region, init) as Array[T](region);

    public static def makeVal[T](dist: Dist, init: (Point)=>T): Array[T](dist)
        = BaseArray.makeVal1(dist, init) as Array[T](dist);

    public static def make[T](rail: Rail[T]): Array[T]{rank==1&&rect&&zeroBased}
        = BaseArray.makeVar1[T](rail);

    public static def make[T](size: nat, init: (Point)=>T): Array[T](1)
        = makeVar[T](0..size-1, init) as Array[T](1);


    //
    // operations
    //

    public abstract def apply(pt: Point(rank)): T;
    public abstract def apply(i0: int) {rank==1}: T;
    public abstract def apply(i0: int, i1: int) {rank==2}: T;
    public abstract def apply(i0: int, i1: int, i2: int) {rank==3}: T;
    public abstract def apply(i0: int, i1: int, i2: int, i3:int) {rank==4}: T;
    
    public abstract def set(v:T, pt: Point(rank)): T;
    public abstract def set(v:T, i0: int) {rank==1}: T;
    public abstract def set(v:T, i0: int, i1: int) {rank==2}: T;
    public abstract def set(v:T, i0: int, i1: int, i2: int) {rank==3}: T;
    public abstract def set(v:T, i0: int, i1: int, i2: int, i3:int) {rank==4}: T;

    public abstract def restriction(r: Region(rank)): Array[T];
    public abstract def restriction(p: Place): Array[T];

    public abstract def $plus(): Array[T];
    public abstract def $minus(): Array[T];

    public abstract def $plus(that: Array[T]): Array[T];
    public abstract def $minus(that: Array[T]): Array[T];
    public abstract def $times(that: Array[T]): Array[T];
    public abstract def $over(that: Array[T]): Array[T];

    public abstract def $bar(r: Region(rank)): Array[T];
    public abstract def $bar(p: Place): Array[T];


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

    incomplete public static def $convert[T](r: Rail[T]): Array[T];
    incomplete public static def $convert[T](r: ValRail[T]): Array[T];

    public def iterator(): Iterator[Point(rank)] = region.iterator();


    //
    //
    //

    protected def this(dist: Dist) = property(dist);

}
