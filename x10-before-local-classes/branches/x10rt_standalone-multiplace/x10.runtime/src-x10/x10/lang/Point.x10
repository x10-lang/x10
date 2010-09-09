// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.lang;

import x10.compiler.ArithmeticOps;
import x10.compiler.ComparisonOps;


/**
 * The type <code>Point(rank)</code> represents a point in a
 * rank-dimensional space. The coordinates of a point <code>p</code>
 * may be accessed individually (with zero-based indexing) using
 * <code>p(i)</code> because <code>Point</code> implements
 * <code>(nat)=>int</code>. The coordinates may also be accessed as a
 * Rail[int]. Point arithmetic is supported.
 *
 * @author bdlucas
 * @author vj
 */

final public class Point(rank: nat) implements (nat) => Int {

    /**
     * Returns the value of the ith coordinate.
     */
    public global safe def apply(i: nat): Int = coords(i);

    /**
     * Returns the coordinates as a <code>ValRail[Int]</code>.
     */

    public global safe def coords(): ValRail[int] = coords;

    /**
     * Constructs a Point from a ValRail[int].
     */
    public static global safe def make(cs: ValRail[int]): Point(cs.length) = new Point(cs);

    /**
     * Constructs a Point from a Rail[int]
     */
    public static global safe def make(cs: Rail[int]!): Point(cs.length) {
	val a = ValRail.make[int](cs.length, (i:nat)=>cs(i));
        return make(a);
    }

    /**
     * Returns a <code>Point p</code> of rank <code>rank</code> with <code>p(i)=init(i)</code>.
     */
    public static global safe def make(rank:nat, init:(i:nat)=>int):Point(rank) {
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
    public static global safe operator (r: Rail[int]): Point(r.length) = make(r);

    /** A <code>ValRail</code> <code>r</code> of size <code>k</code> can be converted to a point <code>p</code>
	of the same rank with <code>p(i)=r(i)</code>.
     */
    public static global safe operator (r: ValRail[int]): Point(r.length) = make(r);


    /**  The point <code>+p</code> is the same as <code>p</code>.
     */
    public global safe operator + this: Point(rank) = this;

    /**  The point <code>-p</code> is the same as <code>p</code> with each index negated.
     */
    public global safe operator - this: Point(rank) 
       = Point.make(rank, (i:nat)=>-this.coords(i));

    /**  The ith coordinate of point <code>p+q</code> is <code>p(i)+q(i)</code>.
     */
    public global safe operator this + (that: Point(rank)): Point(rank) 
       = Point.make(rank, (i:Int)=> this.coords(i) + that.coords(i));


    /**  The ith coordinate of point <code>p-q</code> is <code>p(i)-q(i)</code>.
     */
    public global safe operator this - (that: Point(rank)): Point(rank) 
       = Point.make(rank, (i:Int)=> this.coords(i) - that.coords(i));

    /**  The ith coordinate of point <code>p*q</code> is <code>p(i)*q(i)</code>.
     */
    public global safe operator this * (that: Point(rank)): Point(rank) 
       = Point.make(rank, (i:Int)=> this.coords(i) * that.coords(i));

    /**  The ith coordinate of point <code>p/q</code> is <code>p(i)/q(i)</code>.
     */
    public global safe operator this / (that: Point(rank)): Point(rank) 
       = Point.make(rank, (i:Int)=> this.coords(i) / that.coords(i));

    /**  The ith coordinate of point <code>p+c</code> is <code>p(i)+c</code>.
     */
    public global safe operator this + (c: int): Point(rank) 
       = Point.make(rank, (i:Int) => this.coords(i) + c);

    /**  The ith coordinate of point <code>p-c</code> is <code>p(i)-c</code>.
     */
    public global safe operator this - (c: int): Point(rank) 
       = Point.make(rank, (i:Int) => this.coords(i) - c);

    /**  The ith coordinate of point <code>p*c</code> is <code>p(i)*c</code>.
     */
    public global safe operator this * (c: int): Point(rank) 
       = Point.make(rank, (i:Int) => this.coords(i) * c);

    /**  The ith coordinate of point <code>p/c</code> is <code>p(i)/c</code>.
     */
    public global safe operator this / (c: int): Point(rank) 
       = Point.make(rank, (i:Int) => this.coords(i) / c);

    /**  The ith coordinate of point <code>c+p</code> is <code>c+p(i)</code>.
     */
    public global safe operator (c: int) + this: Point(rank) 
       = Point.make(rank, (i:Int) => c + this.coords(i));

    /**  The ith coordinate of point <code>c-p</code> is <code>c-p(i)</code>.
     */
    public global safe operator (c: int) - this: Point(rank) 
       = Point.make(rank, (i:Int) => c - this.coords(i));

    /**  The ith coordinate of point <code>c*p</code> is <code>c*p(i)</code>.
     */
    public global safe operator (c: int) * this: Point(rank) 
       = Point.make(rank, (i:Int) => c * this.coords(i));

    /**  The ith coordinate of point <code>c/p</code> is <code>c/p(i)</code>.
     */
    public global safe operator (c: int) / this: Point(rank) 
       = Point.make(rank, (i:Int) => c / this.coords(i));

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

    /** A point with coordinates <code>i1,..., ik</code> is printed as <code>(i1,.., ik)</code>.
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
