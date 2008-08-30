/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.lang;

/**
 * @author Igor Peshansky
 */
public class RankMismatchException extends x10.lang.Exception {
	private val p: Box[Point];
	private val r: Box[Region];
	private val d: Box[Dist];
	private val n: int;

	public def this(p_: Point, n_:int)  = {
		p = p_;
		r = null;
		d = null;
		n = n_;
	}

	public def this(r_: Region, n_:int) = {
		p = null;
		r = r_;
		d = null;
		n = n_;
	}

    public def this(d_: Dist, n_:Int) = {
		p = null;
		r = null;
		d = d_;
		n = n_;
	}

	public def toString() =  "RankMismatchException(" +
			(p != null ? "point " + p :
			 r != null ? "region " + r :
			 "distribution " + d) +
			" accessed as rank " + n + ")";
}

