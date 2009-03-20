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
	private final x10.lang.point p_;
	private final x10.lang.region r_;
	private final x10.lang.dist d_;
	private final int n_;

	public RankMismatchException(x10.lang.point p, int n) {
		p_ = p;
		r_ = null;
		d_ = null;
		n_ = n;
	}

	public RankMismatchException(x10.lang.region r, int n) {
		p_ = null;
		r_ = r;
		d_ = null;
		n_ = n;
	}

	public RankMismatchException(x10.lang.dist d, int n) {
		p_ = null;
		r_ = null;
		d_ = d;
		n_ = n;
	}

	public String toString() {
		return "RankMismatchException(" +
			(p_ != null ? "point " + p_ :
			 r_ != null ? "region " + r_ :
			 "distribution " + d_) +
			" accessed as rank " + n_ + ")";
	}
}

