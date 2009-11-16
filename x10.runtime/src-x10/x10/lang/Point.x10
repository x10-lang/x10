// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.lang;

import x10.compiler.ArithmeticOps;
import x10.compiler.ComparisonOps;

/**
 * Point(rank) represents a point in a rank-dimensional space. The
 * coordinates of a point p may be accessed individually using p(i)
 * because Point implements (nat)=>int. The coordinates may also be
 * accessed as a Rail[int]. Point arithmetic is supported.
 *
 * @author bdlucas
 */

final public class Point(rank: nat) implements (nat) => int {

    /**
     * Returns the value of the ith coordinate.
     */

    public global def apply(i: nat): int = coords(i);

    /**
     * Returns the coordinates as a ValRail[int].
     */

    public global def coords(): ValRail[int] = coords;


    //
    // factories
    //

    /**
     * Constructs a Point from a ValRail[int].
     */

    public static def make(cs: ValRail[int]): Point(cs.length) = new Point(cs);

    /**
     * Constructs a Point from a Rail[int]
     */

    public static def make(cs: Rail[int]!): Point(cs.length) {
        // (i:nat)=>cs(i) is workaround for XTENLANG-32
	//        val a: ValRail[int](cs.length) = Rail.makeVal[int](cs.length, (i:nat)=>cs(i));
	val a = Rail.makeVal[int](cs.length, (i:nat)=>cs(i));
        return make(a);
    }

    /**
     * Constructs a Point from a closure that computes the points
     * coordinates.
     */

    public static def make(rank:nat, init:(i:nat)=>int) {
        val a = Rail.makeVal[int](rank, init);
        return make(a);
    }


    public static def make(i0:int) = make([i0]);
    public static def make(i0:int, i1:int) = make([i0,i1]);
    public static def make(i0:int, i1:int, i2:int) = make([i0,i1,i2]);
    public static def make(i0:int, i1:int, i2:int, i3:int) = make([i0,i1,i2,i3]);


    //
    // conversion ops
    //

    public static operator (r: Rail[int]): Point(r.length) = make(r);
    public static operator (r: ValRail[int]): Point(r.length) = make(r);



    //
    // arithmetic ops
    //

    public global operator + this: Point(rank) {
        return this;
    }

    public global operator - this: Point(rank) {
        val cs = Rail.makeVar[int](rank, (i:nat)=>-this.coords(i));
        return Point.make(cs);
    }

    public global operator this + (that: Point(rank)): Point(rank) {
        val init = (i:nat) => this.coords(i) + that.coords(i);
        val cs = Rail.makeVar[int](rank, init);
        return Point.make(cs);
    }

    public global operator this - (that: Point(rank)): Point(rank) {
        val init = (i:nat) => this.coords(i) - that.coords(i);
        val cs = Rail.makeVar[int](rank, init);
        return Point.make(cs);
    }

    public global operator this * (that: Point(rank)): Point(rank) {
        val init = (i:nat) => this.coords(i) * that.coords(i);
        val cs = Rail.makeVar[int](rank, init);
        return Point.make(cs);
    }

    public global operator this / (that: Point(rank)): Point(rank) {
        val init = (i:nat) => this.coords(i) / that.coords(i);
        val cs = Rail.makeVar[int](rank, init);
        return Point.make(cs);
    }

    public global operator this + (c: int): Point(rank) {
        val init = (i:nat) => this.coords(i) + c;
        val cs = Rail.makeVar[int](rank, init);
        return Point.make(cs);
    }

    public global operator this - (c: int): Point(rank) {
        val init = (i:nat) => this.coords(i) - c;
        val cs = Rail.makeVar[int](rank, init);
        return Point.make(cs);
    }

    public global operator this * (c: int): Point(rank) {
        val init = (i:nat) => this.coords(i) * c;
        val cs = Rail.makeVar[int](rank, init);
        return Point.make(cs);
    }

    public global operator this / (c: int): Point(rank) {
        val init = (i:nat) => this.coords(i) / c;
        val cs = Rail.makeVar[int](rank, init);
        return Point.make(cs);
    }

    public global operator (c: int) + this: Point(rank) {
        val init = (i:nat) => c + this.coords(i);
        val cs = Rail.makeVar[int](rank, init);
        return Point.make(cs);
    }

    public global operator (c: int) - this: Point(rank) {
        val init = (i:nat) => c - this.coords(i);
        val cs = Rail.makeVar[int](rank, init);
        return Point.make(cs);
    }

    public global operator (c: int) * this: Point(rank) {
        val init = (i:nat) => c * this.coords(i);
        val cs = Rail.makeVar[int](rank, init);
        return Point.make(cs);
    }

    public global operator (c: int) / this: Point(rank) {
        val init = (i:nat) => c / this.coords(i);
        val cs = Rail.makeVar[int](rank, init);
        return Point.make(cs);
    }


    //
    // comparison ops
    //

    public global operator this == (that: Point(rank)): boolean {
        for (var i: int = 0; i<rank; i++)
            if (!(this.coords(i)==that.coords(i)))
                return false;
        return true;
    }

    public global operator this < (that: Point(rank)): boolean {
        for (var i: int = 0; i<rank; i++)
            if (!(this.coords(i)<that.coords(i)))
                return false;
        return true;
    }

    public global operator this > (that: Point(rank)): boolean {
        for (var i: int = 0; i<rank; i++)
            if (!(this.coords(i)>that.coords(i)))
                return false;
        return true;
    }

    public global operator this <= (that: Point(rank)): boolean {
        for (var i: int = 0; i<rank; i++)
            if (!(this.coords(i)<=that.coords(i)))
                return false;
        return true;
    }

    public global operator this >= (that: Point(rank)): boolean {
        for (var i: int = 0; i<rank; i++)
            if (!(this.coords(i)>=that.coords(i)))
                return false;
        return true;
    }

    public global operator this != (that: Point(rank)): boolean {
        for (var i: int = 0; i<rank; i++)
            if (!(this.coords(i)!=that.coords(i)))
                return false;
        return true;
    }

    //
    //
    //

    public global def toString() {
        var s:String = "(";
        if (coords.length>0)
            s = s + coords(0); // XTENLANG-45
        for (var i:int=1; i<coords.length; i++)
            s = s + "," + coords(i); // XTENLANG-45
        s += ")";
        return s;
    }

    //
    //
    //

    private global val coords: ValRail[int];

    private def this(cs: ValRail[int]): Point(cs.length) {
        property(cs.length);
        this.coords = cs;
    }

}
