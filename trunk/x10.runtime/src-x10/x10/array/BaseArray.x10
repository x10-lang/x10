/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

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

public abstract class BaseArray[T] extends Array[T] {

    // XTENLANG-49
    static type BaseRegion(rank:int) = BaseRegion{self.rank==rank};

    //
    // factories
    //

   

    public static def makeVar1[T](region: Region, init:(Point(region.rank))=>T): Array[T](region) {
        val dist = Dist.makeConstant(region); // XXX _.x10 shd have Dist(Region) type?
        return makeVar1[T](dist, init) as Array[T](region); // would eliminate cast here
    }
    public static def makeVar1[T](region: Region): Array[T](region) {
        val dist = Dist.makeConstant(region); // XXX _.x10 shd have Dist(Region) type?
        return makeVar1[T](dist) as Array[T](region); // would eliminate cast here
    }

    public static def makeVar1[T](dist: Dist, init: (Point(dist.rank))=>T): Array[T](dist) {
        if (dist.constant) {
           if (checkBounds || checkPlace)
               return at (dist.onePlace) { new LocalArray[T](dist as Dist{constant,onePlace==here,self==dist}, init) }; 
           else
               return at (dist.onePlace) { new FastArray[T](dist as Dist{constant,onePlace==here,self==dist}, init) }; 
        }
        else {
            return new DistArray[T](dist, init);
        }
    }
    public static def makeVar1[T](dist: Dist): Array[T](dist) {
        if (dist.constant) {
           if (checkBounds || checkPlace)
               return at (dist.onePlace) { new LocalArray[T](dist as Dist{constant,onePlace==here}) as Array[T](dist) }; // XXXXX ???
           else
               return at (dist.onePlace) { new FastArray[T](dist as Dist{constant,onePlace==here}) as Array[T](dist) }; // XXXXX ???
        }
        else {
            return new DistArray[T](dist);
        }
    }

    public static def makeVar1[T](rail: Rail[T]!): Array[T]{rank==1&&rect&&zeroBased} {
        val r = Region.makeRectangular(0, rail.length-1);
        return makeVar1[T](r, (p:Point)=>rail(p(0)))
            as Array[T]{rank==1 && rect && zeroBased}; // XXXX
    }

    public static def makeVar1[T](rail: ValRail[T]): Array[T]{rank==1&&rect&&zeroBased} {
        val r = Region.makeRectangular(0, rail.length-1);
        return makeVar1[T](r, (p:Point)=>rail(p(0)))
            as Array[T]{rank==1 && rect && zeroBased}; // XXXX
    }


    //
    // expose these if performance demands
    //

    protected abstract global def raw(): Rail[T]!;
    protected abstract global def layout(): RectLayout;


    //
    // low-perfomance methods here
    // high-performance methods are in subclass to facilitate inlining
    //     

    public final safe global def apply(pt: Point(rank)): T {
        if (checkPlace) checkPlace(pt);
        if (checkBounds) checkBounds(pt);
        return raw()(layout().offset(pt));
    }

    public final safe global def get(pt: Point(rank)): T = apply(pt);

    // XXXX settable order
    public final safe global def set(v: T, pt: Point(rank)): T {
        if (checkPlace) checkPlace(pt);
        if (checkBounds) checkBounds(pt);
        val r = raw();
        r(layout().offset(pt)) = v;
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

    // TODO: XTENLANG-1188;  This field should be a const, but C++ backend generates bad code if it is
    global val bounds = (pt:Point):RuntimeException =>
        new ArrayIndexOutOfBoundsException("point " + pt + " not contained in array");

    // TODO: XTENLANG-1188;  This field should be a const, but C++ backend generates bad code if it is
    global val place = (pt:Point):RuntimeException =>
        new BadPlaceException("point " + pt + " not defined at " + here);

    safe global def checkBounds(pt: Point(rank)) {
        (region as BaseRegion(rank)).check(bounds, pt);
    }

    safe global def checkBounds(i0: int) {
        (region as BaseRegion(1)).check(bounds, i0);
    }

    safe global def checkBounds(i0: int, i1: int) {
        (region as BaseRegion(2)).check(bounds, i0, i1);
    }

    safe global def checkBounds(i0: int, i1: int, i2: int) {
        (region as BaseRegion(3)).check(bounds, i0, i1, i2);
    }

    safe global def checkBounds(i0: int, i1: int, i2: int, i3: int) {
        (region as BaseRegion(4)).check(bounds, i0, i1, i2, i3);
    }

    safe global def checkPlace(pt: Point(rank)) {
        (dist.get(here) as BaseRegion(rank)).check(place, pt);
    }

    safe global def checkPlace(i0: int) {
        (dist.get(here) as BaseRegion(1)).check(place, i0);
    }

    safe global def checkPlace(i0: int, i1: int) {
        (dist.get(here) as BaseRegion(2)).check(place, i0, i1);
    }

    safe global def checkPlace(i0: int, i1: int, i2: int) {
        (dist.get(here) as BaseRegion(3)).check(place, i0, i1, i2);
    }

    safe global def checkPlace(i0: int, i1: int, i2: int, i3: int) {
        (dist.get(here) as BaseRegion(4)).check(place, i0, i1, i2, i3);
    }
    //
    // views
    //

    public safe global def restriction(r: Region(rank)): Array[T](rank) {
        return restriction(dist.restriction(r));
    }

    public safe global def restriction(p: Place): Array[T](rank) {
        return restriction(dist.restriction(p));
    }

    // must be internal only - assumes Dist places match
    protected abstract safe global def restriction(d: Dist(rank)): Array[T](rank);


    //
    // operations
    //

    public global def lift(op:(T)=>T): Array[T](dist)
        = Array.make[T](dist, ((p:Point)=>op(this(p as Point(rank)))));

    //    incomplete public global def reduce(op:(T,T)=>T, unit:T):T;

//
// seems to be causing non-deterministic typechecking failures in
// a(pt).  perhaps related to XTENLANG-128 and/or XTENLANG-135
//
    public global def reduce(op:(T,T)=>T, unit:T):T {

        // scatter
        val ps:ValRail[Place] = dist.places();
        val results = Rail.make[T](ps.length, (p:Int) => unit);
        val r = 0..(ps.length-1);
        
        
	finish foreach (p:Point(1)  in r) {
        	results(p(0)) = at (ps(p(0))) {
        	    var result: T = unit;
                val a = (this | here) as Array[T](rank);
                for (pt:Point(dist.region.rank)  in a.region)
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
    public global def reduce(op:(T,T)=>T, unit:T):T {

        // scatter
        val ps = dist.places();
        val results = ValRail.make[Future[T]](ps.length, (p:Int) => {
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
    incomplete public global def scan(op:(T,T)=>T, unit:T): Array[T](dist);


    //
    // ops
    //

    public safe global operator this | (r: Region(rank)) = restriction(r);
    public safe global operator this | (p: Place) = restriction(p);

    incomplete public safe global operator + this: Array[T](dist);
    incomplete public safe global operator - this: Array[T](dist);

    incomplete public safe global operator this + (that: Array[T](dist)): Array[T](dist);
    incomplete public safe global operator this - (that: Array[T](dist)): Array[T](dist);
    incomplete public safe global operator this * (that: Array[T](dist)): Array[T](dist);
    incomplete public safe global operator this / (that: Array[T](dist)): Array[T](dist);
    incomplete public safe global operator this % (that: Array[T](dist)): Array[T](dist);

    incomplete public safe global operator this == (x: Array[T](dist)): boolean;
    incomplete public safe global operator this <  (x: Array[T](dist)): boolean;
    incomplete public safe global operator this >  (x: Array[T](dist)): boolean;
    incomplete public safe global operator this <= (x: Array[T](dist)): boolean;
    incomplete public safe global operator this >= (x: Array[T](dist)): boolean;
    incomplete public safe global operator this != (x: Array[T](dist)): boolean;

    // incomplete public global def sum(): T; // XTENLANG-116



    /**
     * for now since we only have RectLayouts we hard-code that here
     * for efficiency, since RectLayout is a final class.
     *
     * if/when we have other layouts, this might need to be a generic
     * type parameter, i.e. BaseArray[T,L] where L is a layout class
     */

    // safe to call from witin a constructor, does not read fields.
    protected proto global def layout(r: Region): RectLayout {
        if (r.isEmpty()) {
            // XXX EmptyLayout class?
            val min = ValRail.make[int](r.rank, (Int)=>0);
            val max = ValRail.make[int](r.rank, (Int)=>-1);
            return new RectLayout(min, max);
        } else {
            return new RectLayout(r.min(), r.max());
        }
    }

    protected def this(dist: Dist) {
        super(dist);
    }

    public global safe def toString(): String {
        return "Array(" + dist + ")";
    }

}

