// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.array.Array1;
import x10.array.ArrayN;
import x10.array.ArrayV;

public abstract value class BaseArray[T] extends Array[T] {

    // XTENLANG-49
    static type BaseRegion(rank:int) = BaseRegion{self.rank==rank};


    //
    // factories
    //

    public static def makeVal1[T](region: Region, init: (Point)=>T): Array[T] {
        var dist: Dist = Dist.makeConstant(region);
        return makeVal[T](dist, init);
    }

    public static def makeVar1[T](region: Region, init: (Point)=>T): Array[T](region) {
        var dist: Dist = Dist.makeConstant(region); // XXX _.x10 shd have Dist(Region) type?
        return makeVar[T](dist, init) as Array[T](region); // would eliminate cast here
    }

    public static def makeVal1[T](dist: Dist, init: (Point)=>T): Array[T] {
        return new ArrayV[T](dist, init);
    }

    public static def makeVar1[T](dist: Dist, init: (Point)=>T): Array[T](dist) {
        if (dist.constant)
            return new Array1[T](dist as Dist{constant}, init) as Array[T](dist); // XXXXX ???
        else
            return new ArrayN[T](dist, init);
    }

    public static def makeVar1[T](rail: Rail[T]): Array[T]{rank==1&&rect&&zeroBased} {
        val r = Region.makeRectangular(0, rail.length-1);
        return makeVar[T](r, (p:Point)=>rail(p(0)))
            as Array[T]{rank==1 && rect && zeroBased}; // XXXX
    }


    //
    // expose these if performance demands
    //

    protected abstract def raw(): Rail[T];
    protected abstract def layout(): RectLayout;


    //
    // low-perfomance methods here
    // high-performance methods are in subclass to facilitate inlining
    //     

    public final def apply(pt: Point(rank)): T {
        checkBounds(pt);
        return raw()(layout().offset(pt));
    }

    public final def get(pt: Point(rank)): T = apply(pt(rank));

    // XXXX settable order
    public final def set(v: T, pt: Point(rank)):void {
        checkBounds(pt);
        raw()(layout().offset(pt)) = v;
    }

    def checkBounds(pt: Point(rank)) {
        (region as BaseRegion(rank)).checkBounds(pt);
    }

    def checkBounds(i0: int) {
        (region as BaseRegion).checkBounds(i0);
    }

    def checkBounds(i0: int, i1: int) {
        (region as BaseRegion).checkBounds(i0, i1);
    }

    def checkBounds(i0: int, i1: int, i2: int) {
        (region as BaseRegion).checkBounds(i0, i1, i2);
    }

    def checkBounds(i0: int, i1: int, i2: int, i3: int) {
        (region as BaseRegion).checkBounds(i0, i1, i2, i3);
    }


    //
    // views
    //

    public def restriction(r: Region(rank)): Array[T] {
        return restriction(dist.restriction(r));
    }

    public def restriction(p: Place): Array[T] {
        return restriction(dist.restriction(p));
    }

    // must be internal only - assumes Dist places match
    protected abstract def restriction(d: Dist): Array[T];


    //
    // operations
    //

    public def $bar(r: Region(rank)): Array[T] = restriction(r);
    public def $bar(p: Place): Array[T] = restriction(p);

    public def lift(f:(T)=>T): Array[T]
        = Array.make[T](dist, (p:Point)=>f(this(p as Point(rank))));

    incomplete public def $plus(): Array[T];
    incomplete public def $minus(): Array[T];

    incomplete public def $plus(that: Array[T]): Array[T];
    incomplete public def $minus(that: Array[T]): Array[T];
    incomplete public def $times(that: Array[T]): Array[T];
    incomplete public def $over(that: Array[T]): Array[T];
    incomplete public def $percent(that: Array[T]): Array[T];

    incomplete public def $eq(x: Array[T]): boolean;
    incomplete public def $lt(x: Array[T]): boolean;
    incomplete public def $gt(x: Array[T]): boolean;
    incomplete public def $le(x: Array[T]): boolean;
    incomplete public def $ge(x: Array[T]): boolean;
    incomplete public def $ne(x: Array[T]): boolean;

    incomplete public def sum(): T; // XTENLANG-116



    /**
     * for now since we only have RectLayouts we hard-code that here
     * for efficiency, since RectLayout is a final value class.
     *
     * if/when we have other layouts, this might need to be a generic
     * type parameter, i.e. BaseArray[T,L] where L is a layout class
     */

    protected def layout(r: Region): RectLayout {
        if (r.isEmpty()) {
            // XXX EmptyLayout class?
            val min = Rail.makeVal[int](r.rank, (nat)=>0);
            val max = Rail.makeVal[int](r.rank, (nat)=>-1);
            return new RectLayout(min, max);
        } else
            return new RectLayout(r.min(), r.max());
    }

    protected def this(dist: Dist) {
        super(dist);
    }

    public def toString(): String {
        return "Array(" + dist + ")";
    }

}

