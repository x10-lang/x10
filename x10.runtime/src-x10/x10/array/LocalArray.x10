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

/**
 * This class represents an array with single raw chunk in one place.
 *
 * @author bdlucas
 */

// TODO: Need to come through and clean up place types and global methods.
//       This is a completely local array....
public class LocalArray[T] extends BaseArray[T] {

    private global val raw:Rail[T]{self.at(dist.onePlace)};
    private global val layout:RectLayout;

    final protected global def layout() = layout;
    final protected global def raw() = raw as Rail[T]!;

    //
    // high-performance methods here to facilitate inlining
    //
    // NB: local array, so don't do place checking
    //
    final public safe global def apply(i0: int):T {
        val r = raw() as Rail[T]!;        
	val l = layout() as RectLayout!;
        if (checkBounds) checkBounds(i0);
        return r(layout.offset(i0));
    }

    final public safe global def apply(i0: int, i1: int): T {
        val r = raw() as Rail[T]!;        
        if (checkBounds) checkBounds(i0, i1);
        return r(layout.offset(i0,i1));
    }

    final public safe global def apply(i0: int, i1: int, i2: int): T {
        val r = raw() as Rail[T]!;        
        if (checkBounds) checkBounds(i0, i1, i2);
        return r(layout.offset(i0,i1,i2));
    }

    final public safe global def apply(i0: int, i1: int, i2: int, i3: int): T {
        val r = raw() as Rail[T]!;        
        if (checkBounds) checkBounds(i0, i1, i2, i3);
        return r(layout.offset(i0,i1,i2,i3));
    }


    //
    // high-performance methods here to facilitate inlining
    //
    // NB: local array, so don't do place checking
    //

    final public safe global def set(v: T, i0: int): T {
        val r = raw() as Rail[T]!;        
        if (checkBounds) checkBounds(i0);
        r(layout.offset(i0)) = v;
        return v;
    }

    final public safe global def set(v: T, i0: int, i1: int): T {
        val r = raw() as Rail[T]!;        
        if (checkBounds) checkBounds(i0, i1);
        r(layout.offset(i0,i1)) = v;
        return v;
    }

    final public safe global def set(v: T, i0: int, i1: int, i2: int): T {
        val r = raw() as Rail[T]!;        
        if (checkBounds) checkBounds(i0, i1, i2);
        r(layout.offset(i0,i1,i2)) = v;
        return v;
    }

    final public safe global def set(v: T, i0: int, i1: int, i2: int, i3: int): T {
        val r = raw() as Rail[T]!;        
        if (checkBounds) checkBounds(i0, i1, i2, i3);
        r(layout.offset(i0,i1,i2,i3)) = v;
        return v;
    }


    //
    // is this really a good idea?
    //

    static class Accumulator[S] {
        var result:S;
        def this(r:S) {
            result = r;
        }
    }

    public global global def scan(op:(T,T)=>T, unit:T): Array[T](dist) {
        val a = new Accumulator[T](unit);
        return Array.make[T](dist, (p:Point):T => {
            a.result = op(a.result, apply(p as Point(rank)));
            return a.result;
        });

    }

    

    //
    //
    //

    def this(dist: Dist{constant}){here == dist.onePlace}:LocalArray[T]{self.dist==dist} {
        super(dist);

        layout = layout(region);
        val n = layout.size();
        val r = Rail.make[T](n);
        raw = r as Rail[T]{this.dist.onePlace==self.home};
    }

    def this(dist: Dist{constant}, init: (Point(dist.rank))=>T){here == dist.onePlace}:LocalArray[T]{self.dist==dist} {
        super(dist);

        layout = layout(region);
        val n = layout.size();
        val r = Rail.make[T](n);

        val f = init as (Point) => T;
        for (p:Point in region)
        	r(layout.offset(p)) = f(p);

        raw = r as Rail[T]{this.dist.onePlace==self.home};
    }


    /*
     * restriction view
     */

    public safe global def restriction(d: Dist(rank)): Array[T](rank) {
        val dd = d as Dist{self==d, constant,here==self.onePlace};
        return new LocalArray[T](this, dd);
    }

    def this(a: BaseArray[T], d: Dist{constant}){here == d.onePlace}:LocalArray{self.dist==d} {
        super(d);
	
	if (d.region.isEmpty()) {
            this.layout = layout(d.region);
	    this.raw = Rail.make[T](0) as Rail[T]{this.dist.onePlace==self.home};
        } else {
            this.layout = a.layout();
            this.raw =  a.raw() as Rail[T]{this.dist.onePlace==self.home};
        }
    }

}
