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

import x10.util.Ordered;

/**
 * The type <code>Point(rank)</code> represents a point in a
 * rank-dimensional space. The coordinates of a point <code>p</code>
 * may be accessed individually (with zero-based indexing) using
 * <code>p(i)</code> because <code>Point</code> implements
 * <code>(Int)=>int</code>. The coordinates may also be accessed as a
 * Array[int]. Point arithmetic is supported.
 */

public final class Point(rank:Int) implements (Int) => Int, 
                                              Ordered[Point(rank)], 
                                              Comparable[Point(rank)] {
    private val c0:int;
    private val c1:int;
    private val c2:int;
    private val c3:int;
    private val cs:Rail[int];  // Will be null if rank<5

    def this(coords:Rail[int]):Point(coords.size) {
        property(coords.size);

	c0 = coords(0);
	c1 = rank>1 ? coords(1) : 0;
        c2 = rank>2 ? coords(2) : 0;
        c3 = rank>3 ? coords(3) : 0;
        cs = rank>4 ? coords : null;
    }

    def this(i0:int):Point(1) {
        property(1);
        c0 = i0;
        c1 = c2 = c3 = 0;
        cs = null;
    }

    def this(i0:int, i1:int):Point(2) {
        property(2);
        c0 = i0;
        c1 = i1;
        c2 = c3 = 0;
        cs = null;
    }

    def this(i0:int, i1:int, i2:int):Point(3) {
        property(3);
        c0 = i0;
        c1 = i1;
        c2 = i2;
        c3 = 0;
        cs = null;
    }

    def this(i0:int, i1:int, i2:int, i3:int):Point(4) {
        property(4);
        c0 = i0;
        c1 = i1;
        c2 = i2;
        c3 = i3;
        cs = null;
    }


    /**
     * Returns the value of the ith coordinate.
     */
    public operator this(i:Int):Int {
        if (i<0 || i>= rank) throw new ArrayIndexOutOfBoundsException("index "+i+" not contained in "+this);
        switch (i) {
            case 0: return c0;
            case 1: return c1;
            case 2: return c2;
            case 3: return c3;
            default: return cs(i);
        }
    }

    /**
     * Returns the coordinates as a <code>(int)=>int</code>.
     */
    public def coords():(int)=>int = (i:int)=> this(i);

    /**
     * Constructs a Point from a Array[int](1)
     */
    public static def make[T](r:Array[T](1)){T<: Int}:Point(r.size) {
        switch(r.size) {
            case 1: return new Point(r(0)) as Point(r.size);//{self.rank==r.size};
            case 2: return new Point(r(0), r(1)) as Point(r.size);
            case 3: return new Point(r(0), r(1), r(2)) as Point(r.size);
            case 4: return new Point(r(0), r(1), r(2), r(3)) as Point(r.size);
            default: return new Point(new Rail[int](r.size, (i:int)=>r(i)));
        }
    }

    /**
     * Returns a <code>Point p</code> of rank <code>rank</code> with <code>p(i)=init(i)</code>.
     */
    public static def make(rank:Int, init:(i:Int)=>int):Point(rank) {
        switch(rank) {
            case 1: return new Point(init(0)) as Point(rank);
	        case 2: return new Point(init(0), init(1)) as Point(rank);
            case 3: return new Point(init(0), init(1), init(2)) as Point(rank);
            case 4: return new Point(init(0), init(1), init(2), init(3)) as Point(rank);
            default: return new Point(new Rail[int](rank, init));
        }
    }


    public static def make(i0:int) = new Point(i0);
    public static def make(i0:int, i1:int) = new Point(i0, i1);
    public static def make(i0:int, i1:int, i2:int) = new Point(i0, i1, i2);
    public static def make(i0:int, i1:int, i2:int, i3:int) = new Point(i0, i1, i2, i3);

    /** A <code>Array</code> <code>r</code> of length <code>k</code> can be converted to a point <code>p</code>
	of the same rank with <code>p(i)=r(i)</code>.
     */
    public static operator[T] (a:Array[T](1)){T <: Int}:Point(a.size) = make(a);


    /**  The point <code>+p</code> is the same as <code>p</code>.
     */
    public operator + this: Point(rank) = this;

    /**  The point <code>-p</code> is the same as <code>p</code> with each index negated.
     */
    public operator - this: Point(rank) 
       = Point.make(rank, (i:Int)=>-this(i));

    /**  The ith coordinate of point <code>p+q</code> is <code>p(i)+q(i)</code>.
     */
    public operator this + (that: Point(rank)): Point(rank) 
       = Point.make(rank, (i:Int)=> this(i) + that(i));


    /**  The ith coordinate of point <code>p-q</code> is <code>p(i)-q(i)</code>.
     */
    public operator this - (that: Point(rank)): Point(rank) 
       = Point.make(rank, (i:Int)=> this(i) - that(i));

    /**  The ith coordinate of point <code>p*q</code> is <code>p(i)*q(i)</code>.
     */
    public operator this * (that: Point(rank)): Point(rank) 
       = Point.make(rank, (i:Int)=> this(i) * that(i));

    /**  The ith coordinate of point <code>p/q</code> is <code>p(i)/q(i)</code>.
     */
    public operator this / (that: Point(rank)): Point(rank) 
       = Point.make(rank, (i:Int)=> this(i) / that(i));

    /**  The ith coordinate of point <code>p+c</code> is <code>p(i)+c</code>.
     */
    public operator this + (c: int): Point(rank) 
       = Point.make(rank, (i:Int) => this(i) + c);

    /**  The ith coordinate of point <code>p-c</code> is <code>p(i)-c</code>.
     */
    public operator this - (c: int): Point(rank) 
       = Point.make(rank, (i:Int) => this(i) - c);

    /**  The ith coordinate of point <code>p*c</code> is <code>p(i)*c</code>.
     */
    public operator this * (c: int): Point(rank) 
       = Point.make(rank, (i:Int) => this(i) * c);

    /**  The ith coordinate of point <code>p/c</code> is <code>p(i)/c</code>.
     */
    public operator this / (c: int): Point(rank) 
       = Point.make(rank, (i:Int) => this(i) / c);

    /**  The ith coordinate of point <code>c+p</code> is <code>c+p(i)</code>.
     */
    public operator (c: int) + this: Point(rank) 
       = Point.make(rank, (i:Int) => c + this(i));

    /**  The ith coordinate of point <code>c-p</code> is <code>c-p(i)</code>.
     */
    public operator (c: int) - this: Point(rank) 
       = Point.make(rank, (i:Int) => c - this(i));

    /**  The ith coordinate of point <code>c*p</code> is <code>c*p(i)</code>.
     */
    public operator (c: int) * this: Point(rank) 
       = Point.make(rank, (i:Int) => c * this(i));

    /**  The ith coordinate of point <code>c/p</code> is <code>c/p(i)</code>.
     */
    public operator (c: int) / this: Point(rank) 
       = Point.make(rank, (i:Int) => c / this(i));

    /**
     * {@link Comparable#compareTo}
     */
    public def compareTo(that:Point(rank)) {
        return this.equals(that) ? 0 : this < that ? -1 : 1;
    }

    /**
     * Compute the hashCode of a point by combining the
     * the coordinates in a multiple/xor chain.  This
     * should increase the randomness and overcomes the
     * fact that coordinates are biased to be small
     * positive numbers.
     */
    public def hashCode():int {
        var hc:int = this(0);
        for (var i:int = 1; i<rank; i++) {
            hc = (hc * 17) ^ this(i);
        }
	return hc;
    }

    /** Two points of the same rank are equal if and only if their
     * corresponding indices are equal.
     */
    public def equals(other:Any):boolean {
        if (!(other instanceof Point)) return false;
	val otherPoint = other as Point;
        if (rank != otherPoint.rank) return false;
        for (var i: int = 0; i<rank; i++)
            if (!(this(i)==otherPoint(i)))
                return false;
        return true;
    }

    /** For points a, b, <code> a &lt; b</code> if <code>a</code> is lexicographically smaller than <code>b</code>.
     */
    public operator this < (that: Point(rank)): boolean {
        for (var i: int = 0; i<rank-1; i++) {
	    val a = this(i);
	    val b = that(i);
            if (a > b) return false;
	    if (a < b) return true;
	}
        return this(rank-1) < that(rank-1);
    }
    /** For points <code>a, b</code>, <code> a &gt; b</code> if <code>a</code> is lexicographically bigger than
	<code> b</code>.
     */
    public operator this > (that: Point(rank)): boolean {
        for (var i: int = 0; i<rank-1; i++) {
	    val a = this(i);
	    val b = that(i);
            if (a < b) return false;
	    if (a > b) return true;
	}
        return this(rank-1) > that(rank-1);
    }
    /** For points <code>a, b</code>, <code> a &le; b</code> if <code>a</code> is lexicographically less than
	<code> b</code> or the same as <code>b</code>.
     */
    public operator this <= (that: Point(rank)): boolean {
        for (var i: int = 0; i<rank-1; i++) {
	    val a = this(i);
	    val b = that(i);
            if (a > b) return false;
	    if (a < b) return true;
	}
        return this(rank-1) <= that(rank-1);
    }
    /** For points <code>a, b</code>, <code> a &ge; b</code> if <code>a</code> is lexicographically greater than
	<code> b</code> or the same as <code>b</code>.
     */
    public operator this >= (that: Point(rank)): boolean {
        for (var i: int = 0; i<rank-1; i++) {
	    val a = this(i);
	    val b = that(i);
            if (a < b) return false;
	    if (a > b) return true;
	}
        return this(rank-1) >= that(rank-1);
    }

    /** A point with coordinates <code>i1,..., ik</code> is printed as <code>[i1,.., ik]</code>.
     */
    public def toString() {
        var s:String = "[";
        if (rank>0) s += this(0); 
        for (var i:int=1; i<rank; i++)
            s += "," + this(i); 
        s += "]";
        return s;
    }
}
public type Point(r: Int) = Point{self.rank==r};
