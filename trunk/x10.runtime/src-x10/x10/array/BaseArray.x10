// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.compiler.Native;

/**
 * The BaseArray class is the base of the hierarchy of classes that
 * implement Array. BaseArray provides a set of factory methods, and
 * also provides some function common to all Array implementations,
 * such as default implementations of some Array methods. Specific
 * Array implementation classes may override these methods with more
 * efficient implementations.
 *
 * @author bdlucas
 */

public abstract value class BaseArray[T] extends Array[T] {

    // XTENLANG-49
    static type BaseRegion(rank:int) = BaseRegion{self.rank==rank};

    //
    // factories
    //

    public static def makeVal1[T](region: Region, init: Box[(Point)=>T]): Array[T] {
        val dist = Dist.makeConstant(region);
        return makeVal[T](dist, init);
    }

    public static def makeVar1[T](region: Region, init: Box[(Point)=>T]): Array[T](region) {
        val dist = Dist.makeConstant(region); // XXX _.x10 shd have Dist(Region) type?
        return makeVar[T](dist, init) as Array[T](region); // would eliminate cast here
    }

    public static def makeVal1[T](dist: Dist, init: Box[(Point)=>T]): Array[T] {
        return makeVar1[T](dist, init); // XXX for now
    }

    public static def makeVar1[T](dist: Dist, init: Box[(Point)=>T]): Array[T](dist) {
        if (dist.constant) {
           if (checkBounds || checkPlace)
               return new LocalArray[T](dist as Dist{constant}, init) as Array[T](dist); // XXXXX ???
           else
               return new FastArray[T](dist as Dist{constant}, init) as Array[T](dist); // XXXXX ???
        }
        else {
            return new DistArray[T](dist, init);
        }
    }

    public static def makeVar1[T](rail: Rail[T]): Array[T]{rank==1&&rect&&zeroBased} {
        val r = Region.makeRectangular(0, rail.length-1);
        return makeVar[T](r, ((p:Point)=>rail(p(0))) as Box[(Point)=>T])
            as Array[T]{rank==1 && rect && zeroBased}; // XXXX
    }

    public static def makeVar1[T](rail: ValRail[T]): Array[T]{rank==1&&rect&&zeroBased} {
        val r = Region.makeRectangular(0, rail.length-1);
        return makeVar[T](r, ((p:Point)=>rail(p(0))) as Box[(Point)=>T])
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

    public final safe def apply(pt: Point(rank)): T {
        if (checkPlace) checkPlace(pt);
        if (checkBounds) checkBounds(pt);
        return raw()(layout().offset(pt));
    }

    public final safe def get(pt: Point(rank)): T = apply(pt);

    // XXXX settable order
    public final safe def set(v: T, pt: Point(rank)): T {
        if (checkPlace) checkPlace(pt);
        if (checkBounds) checkBounds(pt);
        val raw:Rail[T]{self.at(here)} = raw();
        raw(layout().offset(pt)) = v;
        return v;
    }


    //
    // bounds and place checking
    // set env variable X10_NO_CHECKS to disable checks
    //

    @Native("java", "System.getenv(#1)")
    @Native("c++", "x10::lang::String::Lit(getenv((#1)->c_str()))")
    public native static def getenv(String): String;

    @Native("java", "(System.getenv(#1)!=null)")
    @Native("c++", "((x10_boolean)(getenv((#1)->c_str())!=NULL))")
    public native static def hasenv(String): boolean;

    const x10NoChecks = hasenv("X10_NO_CHECKS");
    const checkBounds = !x10NoChecks;
    const checkPlace = !x10NoChecks;

    val bounds = (pt:Point):RuntimeException =>
        new ArrayIndexOutOfBoundsException("point " + pt + " not contained in array");

    val place = (pt:Point):RuntimeException =>
        new BadPlaceException("point " + pt + " not defined at " + here);

    safe def checkBounds(pt: Point(rank)) {
        (region as BaseRegion(rank)).check(bounds, pt);
    }

    safe def checkBounds(i0: int) {
        (region as BaseRegion(1)).check(bounds, i0);
    }

    safe def checkBounds(i0: int, i1: int) {
        (region as BaseRegion(2)).check(bounds, i0, i1);
    }

    safe def checkBounds(i0: int, i1: int, i2: int) {
        (region as BaseRegion(3)).check(bounds, i0, i1, i2);
    }

    safe def checkBounds(i0: int, i1: int, i2: int, i3: int) {
        (region as BaseRegion(4)).check(bounds, i0, i1, i2, i3);
    }

    safe def checkPlace(pt: Point(rank)) {
        (dist.get(here) as BaseRegion(rank)).check(place, pt);
    }

    safe def checkPlace(i0: int) {
        (dist.get(here) as BaseRegion(1)).check(place, i0);
    }

    safe def checkPlace(i0: int, i1: int) {
        (dist.get(here) as BaseRegion(2)).check(place, i0, i1);
    }

    safe def checkPlace(i0: int, i1: int, i2: int) {
        (dist.get(here) as BaseRegion(3)).check(place, i0, i1, i2);
    }

    safe def checkPlace(i0: int, i1: int, i2: int, i3: int) {
        (dist.get(here) as BaseRegion(4)).check(place, i0, i1, i2, i3);
    }



    //
    // views
    //

    public safe def restriction(r: Region(rank)): Array[T] {
        return restriction(dist.restriction(r));
    }

    public safe def restriction(p: Place): Array[T] {
        return restriction(dist.restriction(p));
    }

    // must be internal only - assumes Dist places match
    protected abstract safe def restriction(d: Dist): Array[T];


    //
    // operations
    //

    public def lift(op:(T)=>T): Array[T](dist)
        = Array.make[T](dist, (p:Point)=>op(this(p as Point(rank))));

    //    incomplete public def reduce(op:(T,T)=>T, unit:T):T;

//
// seems to be causing non-deterministic typechecking failures in
// a(pt).  perhaps related to XTENLANG-128 and/or XTENLANG-135
//
    public def reduce(op:(T,T)=>T, unit:T):T {

        // scatter
        val ps = dist.places();
        val results = Rail.makeVar[T](ps.length, (p:nat) => unit);
        val r = 0..(ps.length-1);
	finish foreach (val (p) in r) {
        	results(p) = at (ps(p)) {
        	    var result: T = unit;
                val a = (this | here) as Array[T](rank);
                for (pt:Point(rank)  in a.region)
                    result = op(result, a(pt));
                return result;
            };
        }

        // gather
        var result: T = unit;
        for (var i:int = 0; i < results.length; i++) 
            result = op(result, results(i));

        return result;
    }            

/*
    public def reduce(op:(T,T)=>T, unit:T):T {

        // scatter
        val ps = dist.places();
        val results = Rail.makeVal[Future[T]](ps.length, (p:nat) => {
            future(ps(p)) {
                var result: T = unit;
                val a = (this | here) as Array[T](rank);
                for (pt:Point(rank) in a)
                    result = op(result, a(pt));
                return result;
            }
        });

        // gather
        var result: T = unit;
        for (var i:int = 0; i < results.length; i++) 
            result = op(result, results(i).force());

        return result;
    }            
*/

    // LocalArray only for now!
    incomplete public def scan(op:(T,T)=>T, unit:T): Array[T](dist);


    //
    // ops
    //

    public safe operator this | (r: Region(rank)): Array[T] = restriction(r);
    public safe operator this | (p: Place): Array[T] = restriction(p);

    incomplete public safe operator + this: Array[T];
    incomplete public safe operator - this: Array[T];

    incomplete public safe operator this + (that: Array[T]): Array[T];
    incomplete public safe operator this - (that: Array[T]): Array[T];
    incomplete public safe operator this * (that: Array[T]): Array[T];
    incomplete public safe operator this / (that: Array[T]): Array[T];
    incomplete public safe operator this % (that: Array[T]): Array[T];

    incomplete public safe operator this == (x: Array[T]): boolean;
    incomplete public safe operator this <  (x: Array[T]): boolean;
    incomplete public safe operator this >  (x: Array[T]): boolean;
    incomplete public safe operator this <= (x: Array[T]): boolean;
    incomplete public safe operator this >= (x: Array[T]): boolean;
    incomplete public safe operator this != (x: Array[T]): boolean;

    // incomplete public def sum(): T; // XTENLANG-116



    /**
     * for now since we only have RectLayouts we hard-code that here
     * for efficiency, since RectLayout is a final value class.
     *
     * if/when we have other layouts, this might need to be a generic
     * type parameter, i.e. BaseArray[T,L] where L is a layout class
     */

    // safe to call from witin a constructor, does not read fields.
    protected proto def layout(r: Region): RectLayout {
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

