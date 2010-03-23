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

package x10.lang;

import x10.util.Ordered;

/**
 * The type <code>Point(rank)</code> represents a point in a
 * rank-dimensional space. The coordiIntes of a point <code>p</code>
 * may be accessed individually (with zero-based indexing) using
 * <code>p(i)</code> because <code>Point</code> implements
 * <code>(Int)=>int</code>. The coordiIntes may also be accessed as a
 * Rail[int]. Point arithmetic is supported.
 *
 * @author bdlucas
 * @author vj
 */
final public class Point(rank: Int) implements (Int) => Int, Ordered[Point(rank)] {

    /**
     * Returns the value of the ith coordiInte.
     */
    public global safe def apply(i: Int): Int = coords(i);

    /**
     * Returns the coordiIntes as a <code>ValRail[Int]</code>.
     */

    public global safe def coords(): ValRail[int] = coords;

    /**
     * Constructs a Point from a ValRail[int].
     */
    public static global safe def make(cs: ValRail[int]): Point(cs.length)! = new Point(cs);

    /**
     * Constructs a Point from a Rail[int]
     */
    public static global safe def make(cs: Rail[int]!): Point(cs.length)! {
	val a = ValRail.make[int](cs.length, (i:Int)=>cs(i));
        return make(a);
    }

    /**
     * Returns a <code>Point p</code> of rank <code>rank</code> with <code>p(i)=init(i)</code>.
     */
    public static global safe def make(rank:Int, init:(i:Int)=>int):Point(rank)! {
        val a = ValRail.make[int](rank, init);
        return make(a);
    }


    public static global safe def make(i0:int) = make([i0]);
    public static global safe def make(i0:int, i1:int) = make([i0,i1]);
    public static global safe def make(i0:int, i1:int, i2:int) = make([i0,i1,i2]);
    public static global safe def make(i0:int, i1:int, i2:int, i3:int) = make([i0,i1,i2,i3]);


    /** A <code>Rail</code> <code>r</code> of size <code>k</code> can be converted to a point <code>p</code>
	of the same rank with <code>p(i)=r(i)</code>.
     */
    public static global safe operator (r: Rail[int]!): Point(r.length)! = make(r);

    /** A <code>ValRail</code> <code>r</code> of size <code>k</code> can be converted to a point <code>p</code>
	of the same rank with <code>p(i)=r(i)</code>.
     */
    public static global safe operator (r: ValRail[int]): Point(r.length)! = make(r);


    /**  The point <code>+p</code> is the same as <code>p</code>.
     */
    public global safe operator + this: Point(rank) = this;

    /**  The point <code>-p</code> is the same as <code>p</code> with each index negated.
     */
    public global safe operator - this: Point(rank) 
       = Point.make(rank, (i:Int)=>-this.coords(i));

    /**  The ith coordiInte of point <code>p+q</code> is <code>p(i)+q(i)</code>.
     */
    public global safe operator this + (that: Point(rank)): Point(rank) 
       = Point.make(rank, (i:Int)=> this.coords(i) + that.coords(i));


    /**  The ith coordiInte of point <code>p-q</code> is <code>p(i)-q(i)</code>.
     */
    public global safe operator this - (that: Point(rank)): Point(rank) 
       = Point.make(rank, (i:Int)=> this.coords(i) - that.coords(i));

    /**  The ith coordiInte of point <code>p*q</code> is <code>p(i)*q(i)</code>.
     */
    public global safe operator this * (that: Point(rank)): Point(rank) 
       = Point.make(rank, (i:Int)=> this.coords(i) * that.coords(i));

    /**  The ith coordiInte of point <code>p/q</code> is <code>p(i)/q(i)</code>.
     */
    public global safe operator this / (that: Point(rank)): Point(rank) 
       = Point.make(rank, (i:Int)=> this.coords(i) / that.coords(i));

    /**  The ith coordiInte of point <code>p+c</code> is <code>p(i)+c</code>.
     */
    public global safe operator this + (c: int): Point(rank) 
       = Point.make(rank, (i:Int) => this.coords(i) + c);

    /**  The ith coordiInte of point <code>p-c</code> is <code>p(i)-c</code>.
     */
    public global safe operator this - (c: int): Point(rank) 
       = Point.make(rank, (i:Int) => this.coords(i) - c);

    /**  The ith coordiInte of point <code>p*c</code> is <code>p(i)*c</code>.
     */
    public global safe operator this * (c: int): Point(rank) 
       = Point.make(rank, (i:Int) => this.coords(i) * c);

    /**  The ith coordiInte of point <code>p/c</code> is <code>p(i)/c</code>.
     */
    public global safe operator this / (c: int): Point(rank) 
       = Point.make(rank, (i:Int) => this.coords(i) / c);

    /**  The ith coordiInte of point <code>c+p</code> is <code>c+p(i)</code>.
     */
    public global safe operator (c: int) + this: Point(rank) 
       = Point.make(rank, (i:Int) => c + this.coords(i));

    /**  The ith coordiInte of point <code>c-p</code> is <code>c-p(i)</code>.
     */
    public global safe operator (c: int) - this: Point(rank) 
       = Point.make(rank, (i:Int) => c - this.coords(i));

    /**  The ith coordiInte of point <code>c*p</code> is <code>c*p(i)</code>.
     */
    public global safe operator (c: int) * this: Point(rank) 
       = Point.make(rank, (i:Int) => c * this.coords(i));

    /**  The ith coordiInte of point <code>c/p</code> is <code>c/p(i)</code>.
     */
    public global safe operator (c: int) / this: Point(rank) 
       = Point.make(rank, (i:Int) => c / this.coords(i));

    /** Two points of the same rank are equal if and only if their
     * corresponding indices are equal.
     */
    public global safe def equals(other:Any):boolean {
        if (!(other instanceof Point)) return false;
	val otherPoint = other as Point;
        if (rank != otherPoint.rank) return false;
        for (var i: int = 0; i<rank; i++)
            if (!(this.coords(i)==otherPoint.coords(i)))
                return false;
        return true;
    }

    /** Two points of the same rank are equal if and only if their
     * corresponding indices are equal.
     */
    public global safe operator this == (that: Point(rank)): boolean {
        for (var i: int = 0; i<rank; i++)
            if (!(this.coords(i)==that.coords(i)))
                return false;
        return true;
    }

    /** For points a, b, <code> a &lt; b</code> if <code>a</code> is lexicographically smaller than <code>b</code>.
     */
    public global safe operator this < (that: Point(rank)): boolean {
        for (var i: int = 0; i<rank-1; i++) {
	    val a = this.coords(i);
	    val b = that.coords(i);
            if (a > b) return false;
	    if (a < b) return true;
	}
        return this.coords(rank-1) < that.coords(rank-1);
    }
    /** For points <code>a, b</code>, <code> a &gt; b</code> if <code>a</code> is lexicographically bigger than
	<code> b</code>.
     */
    public global safe operator this > (that: Point(rank)): boolean {
        for (var i: int = 0; i<rank-1; i++) {
	    val a = this.coords(i);
	    val b = that.coords(i);
            if (a < b) return false;
	    if (a > b) return true;
	}
        return this.coords(rank-1) > that.coords(rank-1);
    }
    /** For points <code>a, b</code>, <code> a &le; b</code> if <code>a</code> is lexicographically less than
	<code> b</code> or the same as <code>b</code>.
     */
    public global safe operator this <= (that: Point(rank)): boolean {
        for (var i: int = 0; i<rank-1; i++) {
	    val a = this.coords(i);
	    val b = that.coords(i);
            if (a > b) return false;
	    if (a < b) return true;
	}
        return this.coords(rank-1) <= that.coords(rank-1);
    }
    /** For points <code>a, b</code>, <code> a &ge; b</code> if <code>a</code> is lexicographically greater than
	<code> b</code> or the same as <code>b</code>.
     */
    public global safe operator this >= (that: Point(rank)): boolean {
        for (var i: int = 0; i<rank-1; i++) {
	    val a = this.coords(i);
	    val b = that.coords(i);
            if (a < b) return false;
	    if (a > b) return true;
	}
        return this.coords(rank-1) >= that.coords(rank-1);
    }

    /** For points <code>a, b</code>, <code> a &ne; b</code> if <code>a</code> is for some index 
	<code> a</code> is distinct from <code>b</code>.
     */
    public global safe operator this != (that: Point(rank)): boolean {
        for (var i: int = 0; i<rank; i++) {
	    val a = this.coords(i);
	    val b = that.coords(i);
	    if (a !=b) return true;
	}
        return false;
    }

    /** A point with coordiIntes <code>i1,..., ik</code> is printed as <code>(i1,.., ik)</code>.
     */
    public global safe def toString() {
        var s:String = "(";
        if (coords.length>0) s += coords(0); 
        for (var i:int=1; i<coords.length; i++)
            s += "," + coords(i); 
        s += ")";
        return s;
    }

    private global val coords: ValRail[int];
    private def this(cs: ValRail[int]): Point(cs.length) {
        property(cs.length);
        this.coords = cs;
    }

    

}
