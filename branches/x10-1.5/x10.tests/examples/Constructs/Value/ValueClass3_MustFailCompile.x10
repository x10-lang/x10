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
		var d: Dist = [0..9]->here;
		val f: foo = new foo();
		var x: myval = new myval(1, new complex(2,3), f, new Array[int](d));
		var y: myval = new myval(1, new complex(2,3), f, new Array[int](d));
		//==== > compiler error should occur here
		x.arrayval(0) = 1; // java will not object
		// x10 compiler should flag this

		if (x.arrayval(0) != y.arrayval(0)+1) return false;
		var z: Array[int] = new Array[int](d);
		//==== > compiler error should occur here
		z(0) = x.arrayval(0);
		return (z(0) == 1);
	}

	public static def main(var args: Rail[String]): void = {
		new ValueClass3_MustFailCompile().execute();
	}

        static final value class myval {
                var intval: int;
                var cval: complex;
                var refval: foo;
                var arrayval: ValArray[int];

                def this(intval: int, cval: complex, refval: foo, arrayval: ValArray[int]) = {
                        this.intval = intval;
                        this.cval = cval;
                        this.refval = refval;
                        this.arrayval = arrayval;
                }
        }

        static class foo {
                var w: int = 19;
        }

        static final value complex {
                var re: int;
                var re: int;
                def this(re: int, im: int) {
                        this.re = re;
                        this.im = im;
                }
                def add (complex other): complex {
                        return new complex(this.re+other.re, this.im+other.im); 
                }
        }
}
