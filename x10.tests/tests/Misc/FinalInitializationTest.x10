/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

/**
 * Tests assignments to final fields in constructor.
 *
 * @author kemal, 3/2005
 */
public class FinalInitializationTest extends x10Test {

	static class myval {
		val intval: int;
		val cval: complex;
		val refval: foo;
		def this(intval: int, cval: complex, refval: foo): myval = {
			this.intval = intval;
			this.cval = cval;
			this.refval = refval;
		}
		def eq(other: myval): boolean = {
			return
				this.intval == other.intval &&
				this.cval.eq(other.cval) &&
				this.refval == other.refval;
		}
	}

	static class foo {
		var w: int = 19n;
	}

	static class complex {
		val re: int;
		val im: int;
		def this(re: int, im: int): complex = {
			this.re = re;
			this.im = im;
		}
		def add(var other: complex) = new complex(this.re+other.re,this.im+other.im);
		def eq(var other: complex) = this.re == other.re && this.im == other.im;
	}

	public def run(): boolean = {
		val f = new foo();
		val x  = new myval(1n, new complex(2n, 3n), f);
		val y  = new myval(1n, (new complex(1n, 4n)).add(new complex(1n, -1n)), f);
		return (x.eq(y));
	}

	public static def main(Rail[String])  {
		new FinalInitializationTest().execute();
	}
}
