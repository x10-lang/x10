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
	final int re;
	final int im;
	_complex(int re1, int im1) {
		re = re1;
		im = im1;
	}

	_complex add(_complex other) {
		return new _complex(this.re+other.re, this.im+other.im);
	}

	public boolean equals(x10.lang.Object o) {
		boolean ret = false;
		if (o instanceof _complex) {
			_complex c = (_complex) o;
			ret = c.re == re && c.im == im;
		}
		return ret;
	}
}

