/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Value class test:
 *
 * Testing that an assignment to a value array element
 * causes a compiler error.
 *
 * @author kemal 4/2005
 */
public class ValueClass3_MustFailCompile extends x10Test {

	public def run(): boolean = {
		var d: Dist = 0..9->here;
		val f: foo = new foo();
		var x: myval = new myval(1, new complex(2,3), f, Array.make[int](d));
		var y: myval = new myval(1, new complex(2,3), f, Array.make[int](d));
		//==== > compiler error should occur here
		x.arrayval(0) = 1; // java will not object
		// x10 compiler should flag this

		if (x.arrayval(0) != y.arrayval(0)+1) return false;
		var z: Array[int] = Array.make[int](d);
		//==== > compiler error should occur here
		z(0) = x.arrayval(0);
		return (z(0) == 1);
	}

	public static def main(var args: Rail[String]): void = {
		new ValueClass3_MustFailCompile().execute();
	}

    static final struct myval {
        val intval: int;
        val cval: complex;
        val refval: foo;
        val arrayval: Array[int];

        def this(intval: int, cval: complex, refval: foo, arrayval: Array[int]) = {
                this.intval = intval;
                this.cval = cval;
                this.refval = refval;
                this.arrayval = arrayval;
        }
}

static class foo {
        var w: int = 19;
}

static final struct complex {
        re: int;
        im: int;
        def this(re: int, im: int) {
                this.re = re;
                this.im = im;
        }
        final def add (other:complex): complex {
                return new complex(this.re+other.re, this.im+other.im); 
        }
}
}
