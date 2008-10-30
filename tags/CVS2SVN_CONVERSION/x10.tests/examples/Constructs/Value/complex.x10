//*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
final public value complex extends x10.lang.Object*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
final public value complex extends x10.lang.Object {
	val re: int;
	val im: int;
	def this(re: int, im: int): complex = {
		this.re = re;
		this.im = im;
	}
	def add(other: complex): complex = {
		return new complex(this.re+other.re, this.im+other.im);
	}
}
