/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/**
 * Complex value class, not a test by itself.
 */
final value _complex extends _dummy {
	val re: int;
	val im: int;
	def this(var re1: int, var im1: int): _complex = {
		re = re1;
		im = im1;
	}

	def add(var other: _complex): _complex = {
		return new _complex(this.re+other.re, this.im+other.im);
	}

	public def equals(var o: x10.lang.Object): boolean = {
		var ret: boolean = false;
		if (o instanceof _complex) {
			var c: _complex = o as _complex;
			ret = c.re == re && c.im == im;
		}
		return ret;
	}
}
