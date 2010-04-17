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
import x10.compiler.ClockedVar;

/***
 * This class represents an array with single clockedRaw chunk in one place.
 *
 * @author nalini
 */

// TODO: Need to come through and clean up place types and global methods.
//       This is a completely local array....
public class ClockedLocalArray[T] extends BaseArray[T] {

	private global val raw: Rail[T]{self.at(dist.onePlace)};
 
    private global val clockedRaw:Rail[ClockedVar[T]]{self.at(dist.onePlace)};
    private global val layout:RectLayout;

    final protected global def layout() = layout;
    final protected global def raw() = null as Rail[T]!;
    final protected global def clockedRaw() = clockedRaw as Rail[ClockedVar[T]]!;

    //
    // high-performance methods here to facilitate inlining
    //
    // NB: local array, so don't do place checking
    //
    final public safe global def apply(i0: int):T {
        val r = clockedRaw() as Rail[ClockedVar[T]]!;        
	val l = layout() as RectLayout!;
        if (checkBounds) checkBounds(i0);
        val cv = r(layout.offset(i0)) as ClockedVar[T]!;
        return cv.get();
    }

    final public safe global def apply(i0: int, i1: int): T {
        val r = clockedRaw() as Rail[ClockedVar[T]]!;        
        if (checkBounds) checkBounds(i0, i1);
        val cv = r(layout.offset(i0,i1)) as ClockedVar[T]!;
         return cv.get();
    }

    final public safe global def apply(i0: int, i1: int, i2: int): T {
        val r = clockedRaw() as Rail[ClockedVar[T]]!;        
        if (checkBounds) checkBounds(i0, i1, i2);
       val cv = r(layout.offset(i0,i1,i2)) as  ClockedVar[T]!;
        return cv.get();
    }

    final public safe global def apply(i0: int, i1: int, i2: int, i3: int): T {
        val r = clockedRaw() as Rail[ClockedVar[T]]!;        
        if (checkBounds) checkBounds(i0, i1, i2, i3);
       val cv =  r(layout.offset(i0,i1,i2,i3)) as ClockedVar[T]!;
       return cv.get();
    }
    
  	public safe global def apply(pt: Point(rank)): T {
        if (checkPlace) checkPlace(pt);
        if (checkBounds) checkBounds(pt);
         val cv =  clockedRaw()(layout().offset(pt)) as ClockedVar[T]!;
       	return cv.get();
    }


    //
    // high-performance methods here to facilitate inlining
    //
    // NB: local array, so don't do place checking
    //

    final public safe global def set(v: T, i0: int): T {
        val r = clockedRaw() as Rail[ClockedVar[T]]!;        
        if (checkBounds) checkBounds(i0);
        val cv = r(layout.offset(i0)) as ClockedVar[T]!;
        cv.set(v);
        return v;
    }

    final public safe global def set(v: T, i0: int, i1: int): T {
        val r = clockedRaw() as Rail[ClockedVar[T]]!;        
        if (checkBounds) checkBounds(i0, i1);
        val cv = r(layout.offset(i0,i1)) as ClockedVar[T]!;
        cv.set(v);
        return v;
    }

    final public safe global def set(v: T, i0: int, i1: int, i2: int): T {
        val r = clockedRaw() as Rail[ClockedVar[T]]!;        
        if (checkBounds) checkBounds(i0, i1, i2);
        val cv = r(layout.offset(i0,i1,i2)) as ClockedVar[T]!;
         cv.set(v);
        return v;
    }

    final public safe global def set(v: T, i0: int, i1: int, i2: int, i3: int): T {
        val r = clockedRaw() as Rail[ClockedVar[T]]!;        
        if (checkBounds) checkBounds(i0, i1, i2, i3);
        val cv = r(layout.offset(i0,i1,i2,i3)) as ClockedVar[T]!;
         cv.set(v);
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

    

    
     def this(dist: Dist{constant}, init: (Point(dist.rank))=>T, c: Clock, op: (T,T)=>T, opInit: T){here == dist.onePlace}:ClockedLocalArray[T]{self.dist==dist} {
        super(dist);

        layout = layout(region);
        val n = layout.size();
        val r = Rail.makeClocked[T](n, c, op, opInit);
        

        val f = init as (Point) => T;
        for (p:Point in region) {
        	val cv = r(layout.offset(p)) as ClockedVar[T]!;
        	cv.setR(f(p));
        
        }
        raw = null;
        clockedRaw = r as Rail[ClockedVar[T]]{self.at(this.dist.onePlace)};
        
    }


     public safe global def restriction(d: Dist(rank)): Array[T](rank) {
        val dd = d as Dist{self==d, constant,here==self.onePlace};
        return new ClockedLocalArray[T](this, dd);
    }

    def this(a: ClockedLocalArray[T], d: Dist{constant}){here == d.onePlace}:ClockedLocalArray{self.dist==d} {
        super(d);
	
	if (d.region.isEmpty()) {
            this.layout = layout(d.region);
        this.clockedRaw = null;
	    this.raw = Rail.make[T](0) as Rail[T]{self.at(this.dist.onePlace)};
	   
        } else {
            this.layout = a.layout();
            this.raw = null;
            this.clockedRaw =  a.clockedRaw() as Rail[ClockedVar[T]]{self.at(this.dist.onePlace)};
     
        }
    }

    

}
