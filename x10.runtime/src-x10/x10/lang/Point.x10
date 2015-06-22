/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.lang;

import x10.util.Ordered;

/**
 * The type <code>Point(rank)</code> represents a point in a
 * rank-dimensional space. The coordinates of a point <code>p</code>
 * may be accessed individually (with zero-based indexing) using
 * <code>p(i)</code> because <code>Point</code> implements
 * <code>(Long)=>Long</code>.
 * Point arithmetic is supported.
 */
public final class Point(rank:Long) implements (Long) => Long, 
                                              Ordered[Point(rank)], 
                                              Comparable[Point(rank)] {
    private val c0:Long;
    private val c1:Long;
    private val c2:Long;
    private val c3:Long;
    private val cs:Rail[Long];  // Will be null if rank<5

    def this(coords:Rail[Long]):Point(coords.size) {
        property(coords.size);

	c0 = coords(0);
	c1 = rank>1 ? coords(1) : 0;
        c2 = rank>2 ? coords(2) : 0;
        c3 = rank>3 ? coords(3) : 0;
        cs = rank>4 ? coords : null;
    }

    def this(i0:Long):Point(1) {
        property(1);
        c0 = i0;
        c1 = c2 = c3 = 0;
        cs = null;
    }

    def this(i0:Long, i1:Long):Point(2) {
        property(2);
        c0 = i0;
        c1 = i1;
        c2 = c3 = 0;
        cs = null;
    }

    def this(i0:Long, i1:Long, i2:Long):Point(3) {
        property(3);
        c0 = i0;
        c1 = i1;
        c2 = i2;
        c3 = 0;
        cs = null;
    }

    def this(i0:Long, i1:Long, i2:Long, i3:Long):Point(4) {
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
    public operator this(i:Long):Long {
        if (i<0 || i>= rank) throw new ArrayIndexOutOfBoundsException("index "+i+" not contained in "+this);
        if (i == 0) return c0;
        if (i == 1) return c1;
        if (i == 2) return c2;
        if (i == 3) return c3;
        return cs(i);
    }

    /**
     * Returns the coordinates as a <code>(Long)=>Long</code>.
     */
    public def coords():(Long)=>Long = (i:Long)=> this(i);

    /**
     * Constructs a Point from a Rail[Int]
     */
    public static def make(r:Rail[Int]):Point(r.size) {
        if (r.size == 1) {
            return new Point(r(0)) as Point(r.size); //{self.rank==r.size};
        } else if (r.size == 2) {
            return new Point(r(0), r(1)) as Point(r.size);
        } else if (r.size == 3) {
            return new Point(r(0), r(1), r(2)) as Point(r.size);
        } else if (r.size == 4) {
            return new Point(r(0), r(1), r(2), r(3)) as Point(r.size);
        } else {
            return new Point(new Rail[Long](r.size, (i:Long)=>(r(i) as Long)));
        }
    }

    /**
     * Constructs a Point from a Rail[Long]
     */
    public static def make(r:Rail[Long]):Point(r.size) {
        if (r.size == 1) {
            return new Point(r(0)) as Point(r.size);//{self.rank==r.size};
        } else if (r.size == 2) {
            return new Point(r(0), r(1)) as Point(r.size);
        } else if (r.size == 3) {
            return new Point(r(0), r(1), r(2)) as Point(r.size);
        } else if (r.size == 4) {
            return new Point(r(0), r(1), r(2), r(3)) as Point(r.size);
        } else {
            return new Point(new Rail[Long](r.size, r));
        }
    }

    /**
     * Returns a <code>Point p</code> of rank <code>rank</code> with <code>p(i)=init(i)</code>.
     */
    public static def make(rank:Long, init:(i:Long)=>Long):Point(rank) {
        if (rank == 1) {
            return new Point(init(0)) as Point(rank);
        } else if (rank == 2) {
            return new Point(init(0), init(1)) as Point(rank);
        } else if (rank == 3) {
            return new Point(init(0), init(1), init(2)) as Point(rank);
        } else if (rank == 4) {
            return new Point(init(0), init(1), init(2), init(3)) as Point(rank);
        } else {
            return new Point(new Rail[Long](rank, (l:Long)=>init(l))) as Point(rank);
        }
    }


    public static def make(i0:Int) = new Point(i0);
    public static def make(i0:Int, i1:Int) = new Point(i0, i1);
    public static def make(i0:Int, i1:Int, i2:Int) = new Point(i0, i1, i2);
    public static def make(i0:Int, i1:Int, i2:Int, i3:Int) = new Point(i0, i1, i2, i3);

    public static def make(i0:Long) = new Point(i0);
    public static def make(i0:Long, i1:Long) = new Point(i0, i1);
    public static def make(i0:Long, i1:Long, i2:Long) = new Point(i0, i1, i2);
    public static def make(i0:Long, i1:Long, i2:Long, i3:Long) = new Point(i0, i1, i2, i3);


    /** 
     * A <code>Rail</code> <code>r</code> of length <code>k</code> can be converted to a point <code>p</code>
     * of rank <code>k</code> with <code>p(i)=r(i)</code>.
     * LONG_RAIL: unsafe int cast
     */
    public static operator(r:Rail[Long]):Point(r.size) = make(r);

    /** 
     * A <code>Rail</code> <code>r</code> of length <code>k</code> can be converted to a point <code>p</code>
     * of rank <code>k</code> with <code>p(i)=r(i)</code>.
     */
    public static operator(r:Rail[Int]):Point(r.size) = make(r);

    /**
     * The point <code>+p</code> is the same as <code>p</code>.
     */
    public operator + this: Point(rank) = this;

    /**  
     * The point <code>-p</code> is the same as <code>p</code> with each index negated.
     */
    public operator - this: Point(rank) 
       = Point.make(rank, (i:Long)=>-this(i));

    /**
     * The ith coordinate of point <code>p+q</code> is <code>p(i)+q(i)</code>.
     */
    public operator this + (that:Point(rank)): Point(rank) 
       = Point.make(rank, (i:Long)=> this(i) + that(i));


    /**
     * The ith coordinate of point <code>p-q</code> is <code>p(i)-q(i)</code>.
     */
    public operator this - (that:Point(rank)): Point(rank) 
       = Point.make(rank, (i:Long)=> this(i) - that(i));

    /** 
     * The ith coordinate of point <code>p*q</code> is <code>p(i)*q(i)</code>.
     */
    public operator this * (that:Point(rank)): Point(rank) 
       = Point.make(rank, (i:Long)=> this(i) * that(i));

    /**  
     * The ith coordinate of point <code>p/q</code> is <code>p(i)/q(i)</code>.
     */
    public operator this / (that: Point(rank)): Point(rank) 
       = Point.make(rank, (i:Long)=> this(i) / that(i));

    /**
     * The ith coordinate of point <code>p+c</code> is <code>p(i)+c</code>.
     */
    public operator this + (c:Long): Point(rank) 
       = Point.make(rank, (i:Long) => this(i) + c);

    /** 
     * The ith coordinate of point <code>p-c</code> is <code>p(i)-c</code>.
     */
    public operator this - (c:Long): Point(rank) 
       = Point.make(rank, (i:Long) => this(i) - c);

    /**
     * The ith coordinate of point <code>p*c</code> is <code>p(i)*c</code>.
     */
    public operator this * (c:Long): Point(rank) 
       = Point.make(rank, (i:Long) => this(i) * c);

    /**
     * The ith coordinate of point <code>p/c</code> is <code>p(i)/c</code>.
     */
    public operator this / (c:Long): Point(rank) 
       = Point.make(rank, (i:Long) => this(i) / c);

    /**
     * The ith coordinate of point <code>c+p</code> is <code>c+p(i)</code>.
     */
    public operator (c:Long) + this: Point(rank) 
       = Point.make(rank, (i:Long) => c + this(i));

    /**
     * The ith coordinate of point <code>c-p</code> is <code>c-p(i)</code>.
     */
    public operator (c:Long) - this: Point(rank) 
       = Point.make(rank, (i:Long) => c - this(i));

    /**
     * The ith coordinate of point <code>c*p</code> is <code>c*p(i)</code>.
     */
    public operator (c:Long) * this: Point(rank) 
       = Point.make(rank, (i:Long) => c * this(i));

    /**
     * The ith coordinate of point <code>c/p</code> is <code>c/p(i)</code>.
     */
    public operator (c:Long) / this: Point(rank) 
       = Point.make(rank, (i:Long) => c / this(i));

    /**
     * {@link Comparable#compareTo}
     */
    public def compareTo(that:Point(rank)) {
        return this.equals(that) ? 0n : this < that ? -1n : 1n;
    }

    /**
     * Compute the hashCode of a point by combining the
     * the coordinates in a multiple/xor chain.  This
     * should increase the randomness and overcomes the
     * fact that coordinates are biased to be small
     * positive numbers.
     */
    public def hashCode():Int {
        var hc:Long = this(0);
        for (i in 1..(rank-1)) {
            hc = (hc * 17) ^ this(i);
        }
	return hc as Int;
    }

    /** 
     * Two points of the same rank are equal if and only if their
     * corresponding indices are equal.
     */
    public def equals(other:Any):Boolean {
        if (!(other instanceof Point)) return false;
	val otherPoint = other as Point;
        if (rank != otherPoint.rank) return false;
        for (i in 0..(rank-1)) {
            if (!(this(i)==otherPoint(i)))
                return false;
        }
        return true;
    }

    /**
     * For points a, b, <code> a &lt; b</code> if <code>a</code> is lexicographically smaller than <code>b</code>.
     */
    public operator this < (that: Point(rank)):Boolean {
        for (i in 0..(rank-2)) {
	    val a = this(i);
	    val b = that(i);
            if (a > b) return false;
	    if (a < b) return true;
	}
        return this(rank-1) < that(rank-1);
    }

    /** 
     * For points <code>a, b</code>, <code> a &gt; b</code> if <code>a</code> 
     * is lexicographically bigger than <code> b</code>.
     */
    public operator this > (that: Point(rank)):Boolean {
        for (i in 0..(rank-2)) {
	    val a = this(i);
	    val b = that(i);
            if (a < b) return false;
	    if (a > b) return true;
	}
        return this(rank-1) > that(rank-1);
    }

    /**
     * For points <code>a, b</code>, <code> a &le; b</code> if <code>a</code> is 
     * lexicographically less than <code> b</code> or the same as <code>b</code>.
     */
    public operator this <= (that: Point(rank)):Boolean {
        for (i in 0..(rank-2)) {
	    val a = this(i);
	    val b = that(i);
            if (a > b) return false;
	    if (a < b) return true;
	}
        return this(rank-1) <= that(rank-1);
    }

    /**
     * For points <code>a, b</code>, <code> a &ge; b</code> if <code>a</code> is 
     * lexicographically greater than <code> b</code> or the same as <code>b</code>.
     */
    public operator this >= (that: Point(rank)):Boolean {
        for (i in 0..(rank-2)) {
	    val a = this(i);
	    val b = that(i);
            if (a < b) return false;
	    if (a > b) return true;
	}
        return this(rank-1) >= that(rank-1);
    }

    /**
     * A point with coordinates <code>i1,..., ik</code> is printed as <code>[i1,.., ik]</code>.
     */
    public def toString() {
        var s:String = "[";
        if (rank>0) s += this(0); 
        for (i in 1..(rank-1)) {
            s += "," + this(i);
        }
        s += "]";
        return s;
    }
}

public type Point(r:Long) = Point{self.rank==r};
