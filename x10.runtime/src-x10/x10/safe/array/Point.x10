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

package safe.array;

import safe.util.Ordered;
import safe.lang.Comparable;
import safe.lang.Int;
import safe.lang.Boolean; 
import safe.lang.Rail;

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
    private val c0:Int;
    private val c1:Int;
    private val c2:Int;
    private val c3:Int;
    private val cs:Rail[Int];  // Will be null if rank<5

    def this(coords:Rail[Int]):Point(coords.size) {
        property(coords.size);

	c0 = coords(0);
	c1 = (rank>Int.asInt(1))() ? coords(1) : Int.asInt(0);
    c2 = (rank>Int.asInt(2))() ? coords(2) : Int.asInt(0);
    c3 = (rank>Int.asInt(3))() ? coords(3) : Int.asInt(0);
    cs = (rank>Int.asInt(4))() ? coords : null;
    }

    def this(i0:Int):Point(Int.asInt(1)) {
        property(Int.asInt(1));
        c0 = i0;
        c1 = c2 = c3 = 0;
        cs = null;
    }

    def this(i0:Int, i1:Int):Point(Int.asInt(2)) {
        property(Int.asInt(2));
        c0 = i0;
        c1 = i1;
        c2 = c3 = Int.asInt(0);
        cs = null;
    }

    def this(i0:Int, i1:Int, i2:Int):Point(Int.asInt(3)) {
        property(Int.asInt(3));
        c0 = i0;
        c1 = i1;
        c2 = i2;
        c3 = Int.asInt(0);
        cs = null;
    }

    def this(i0:Int, i1:Int, i2:Int, i3:Int):Point(Int.asInt(4)) {
        property(Int.asInt(4));
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
        if (Boolean.asX10Boolean(i<Int.asInt(0) || i>= rank)) 
        	throw new ArrayIndexOutOfBoundsException("index "+i+" not contained in "+this);
        if (i == Int.asInt(0))
        	return c0; 
        if (i == Int.asInt(1))
        	return c0; 
        if (i == Int.asInt(2))
        	return c0; 
        if (i == Int.asInt(3))
        	return c0; 
        return cs(i); 
    }

    /**
     * Returns the coordinates as a <code>(Int)=>Int</code>.
     */
    public def coords():(Int)=>Int = (i:Int)=> this(i);

    /**
     * Constructs a Point from a Array[Int](Int.asInt(1))
     */
    
    
    public static def make[T](r:Array[T](Int.asInt(1) as Int{self== Int.asInt(1)})){T<: Int}:Point(r.size) {
    	if ((Int.sequals(r.size, 1))()) 
    		return new Point(r(0)) as Point(r.size);//{self.rank==r.size};
    	if ((Int.sequals(r.size, 2))()) 
    		return new Point(r(0), r(1)) as Point(r.size);
    	if ((Int.sequals(r.size, 3))()) 
    		return new Point(r(0), r(1), r(2)) as Point(r.size);
    	if ((Int.sequals(r.size, 4))()) 
    		return new Point(r(0), r(1), r(2), r(3)) as Point(r.size);

    	return  new Point(new Array[Int](r.size, (i:Int)=>r(i)));
    }

    /**
     * Returns a <code>Point p</code> of rank <code>rank</code> with <code>p(i)=init(i)</code>.
     */
    public static def make(rank:Int, init:(i:Int)=>Int):Point(rank) {
    	if (rank == Int.asInt(1)) 
    		return new Point(init(Int.asInt(0))) as Point(rank);
    	
    	if (rank == Int.asInt(1)) 
    		return new Point(init(Int.asInt(0))) as Point(rank);
    	if (rank == Int.asInt(2)) 
    		return new Point(init(Int.asInt(0)), init(Int.asInt(1))) as Point(rank);
    	if (rank == Int.asInt(3)) 
    		return new Point(init(Int.asInt(0)), init(Int.asInt(1)), init(Int.asInt(2))) as Point(rank);
    	if (rank == Int.asInt(4)) 
    		return new Point(init(Int.asInt(0)), init(Int.asInt(1)), init(Int.asInt(2)), init(Int.asInt(3))) as Point(rank);
    	return new Point(new Array[Int](rank, init));

    }


    public static def make(i0:Int) = new Point(i0);
    public static def make(i0:Int, i1:Int) = new Point(i0, i1);
    public static def make(i0:Int, i1:Int, i2:Int) = new Point(i0, i1, i2);
    public static def make(i0:Int, i1:Int, i2:Int, i3:Int) = new Point(i0, i1, i2, i3);

    /** A <code>Array</code> <code>r</code> of length <code>k</code> can be converted to a point <code>p</code>
	of the same rank with <code>p(i)=r(i)</code>.
     */
    //public static operator[T] (a:Array[T](Int.asInt(1))){T <: Int}:Point(a.size) = make(a);


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
    public operator this + (c: Int): Point(rank) 
       = Point.make(rank, (i:Int) => this(i) + c);

    /**  The ith coordinate of point <code>p-c</code> is <code>p(i)-c</code>.
     */
    public operator this - (c: Int): Point(rank) 
       = Point.make(rank, (i:Int) => this(i) - c);

    /**  The ith coordinate of point <code>p*c</code> is <code>p(i)*c</code>.
     */
    public operator this * (c: Int): Point(rank) 
       = Point.make(rank, (i:Int) => this(i) * c);

    /**  The ith coordinate of point <code>p/c</code> is <code>p(i)/c</code>.
     */
    public operator this / (c: Int): Point(rank) 
       = Point.make(rank, (i:Int) => this(i) / c);

    /**  The ith coordinate of point <code>c+p</code> is <code>c+p(i)</code>.
     */
    public operator (c: Int) + this: Point(rank) 
       = Point.make(rank, (i:Int) => c + this(i));

    /**  The ith coordinate of point <code>c-p</code> is <code>c-p(i)</code>.
     */
    public operator (c: Int) - this: Point(rank) 
       = Point.make(rank, (i:Int) => c - this(i));

    /**  The ith coordinate of point <code>c*p</code> is <code>c*p(i)</code>.
     */
    public operator (c: Int) * this: Point(rank) 
       = Point.make(rank, (i:Int) => c * this(i));

    /**  The ith coordinate of point <code>c/p</code> is <code>c/p(i)</code>.
     */
    public operator (c: Int) / this: Point(rank) 
       = Point.make(rank, (i:Int) => c / this(i));

    /**
     * {@link Comparable#compareTo}
     */
    public def compareTo(that:Point(rank)) {
    	if (this.equals(that))
    		return Int.asInt(0); 
    	if (Boolean.asX10Boolean(this < that))
    		return Int.asInt(-1); 
    	return Int.asInt(1); 
        //return this.equals(that) ? Int.asInt(0) : (this < that) ? -Int.asInt(1) : Int.asInt(1);
    }

    /**
     * Compute the hashCode of a point by combining the
     * the coordinates in a multiple/xor chain.  This
     * should increase the randomness and overcomes the
     * fact that coordinates are biased to be small
     * positive numbers.
     */
    public def hashCode():x10.lang.Int {
        var hc:Int = this(Int.asInt(0));
        for (var i:Int = Int.asInt(1); Boolean.asX10Boolean(i<rank); i = i +1) {
            hc = (hc * 17) ^ this(i);
        }
	return Int.asX10Int(hc);
    }

    /** Two points of the same rank are equal if and only if their
     * corresponding indices are equal.
     */
    public def equals(other:Any):x10.lang.Boolean {
        if (!(other instanceof Point)) return false;
	val otherPoint = other as Point;
        if (rank != otherPoint.rank) return false;
        for (var i: Int = Int.asInt(0); Boolean.asX10Boolean(i<rank); i = i + 1)
            if (!(this(i)==otherPoint(i)))
                return false;
        return true;
    }

    /** For points a, b, <code> a &lt; b</code> if <code>a</code> is lexicographically smaller than <code>b</code>.
     */
    public operator this < (that: Point(rank)): Boolean {
        for (var i: Int = Int.asInt(0); Boolean.asX10Boolean(i<rank-Int.asInt(1)); i = i + 1) {
	    val a = this(i);
	    val b = that(i);
            if (Boolean.asX10Boolean(a > b)) return false;
	    if (Boolean.asX10Boolean(a < b)) return true;
	}
        return this(rank-Int.asInt(1)) < that(rank-Int.asInt(1));
    }
    /** For points <code>a, b</code>, <code> a &gt; b</code> if <code>a</code> is lexicographically bigger than
	<code> b</code>.
     */
    public operator this > (that: Point(rank)): Boolean {
        for (var i: Int = Int.asInt(0); Boolean.asX10Boolean(i<rank-Int.asInt(1)); i = i + 1) {
	    val a = this(i);
	    val b = that(i);
            if (Boolean.asX10Boolean(a < b)) return false;
	    if (Boolean.asX10Boolean(a > b)) return true;
	}
        return this(rank-Int.asInt(1)) > that(rank-Int.asInt(1));
    }
    /** For points <code>a, b</code>, <code> a &le; b</code> if <code>a</code> is lexicographically less than
	<code> b</code> or the same as <code>b</code>.
     */
    public operator this <= (that: Point(rank)): Boolean {
        for (var i: Int = Int.asInt(0); Boolean.asX10Boolean(i<rank-Int.asInt(1)); i = i + 1) {
	    val a = this(i);
	    val b = that(i);
            if (Boolean.asX10Boolean(a > b)) return false;
	    if (Boolean.asX10Boolean(a < b)) return true;
	}
        return this(rank-Int.asInt(1)) <= that(rank-Int.asInt(1));
    }
    /** For points <code>a, b</code>, <code> a &ge; b</code> if <code>a</code> is lexicographically greater than
	<code> b</code> or the same as <code>b</code>.
     */
    public operator this >= (that: Point(rank)): Boolean {
        for (var i: Int = Int.asInt(0); Boolean.asX10Boolean(i<rank-Int.asInt(1)); i = i + 1) {
	    val a = this(i);
	    val b = that(i);
            if (Boolean.asX10Boolean(a < b)) return false;
	    if (Boolean.asX10Boolean(a > b)) return true;
	}
        return this(rank-Int.asInt(1)) >= that(rank-Int.asInt(1));
    }

    /** A point with coordinates <code>i1,..., ik</code> is printed as <code>[i1,.., ik]</code>.
     */
    // public def toString() {
    //     var s:String = "[";
    //     if ((rank>Int.asInt(0))) s += this(Int.asInt(0)); 
    //     for (var i:Int=Int.asInt(1); (i<rank); i = i + 1)
    //         s += "," + this(i); 
    //     s += "]";
    //     return s;
    // }
}
public type Point(r: Int) = Point{self.rank==r};
