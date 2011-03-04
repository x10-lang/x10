/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Value class test:
 *
 * == for value class instances should be implemented by deep
 * recursive, field by field, comparison.
 *
 * @author kemal, 12/2004
 */
public class ValueClass extends x10Test {

	public def run(): boolean = {

		var r: region = [0..9];
		var d: dist = r->here;
		val f: foo = new foo();
		var x: myval = new myval(1, new complex(2,3), f, new Array[int](d));
		var y: myval = new myval(1, new complex(2,3), f, new Array[int](d));
		// even if x and y are different objects
		// their fields are equal, and thus they are ==
		System.out.println("1");
		if (x != y) return false;
		val one: complex = new complex(1,1);
		val minusone: complex = new complex(-1,-1);
		val t: complex = x.cval.add(one).add(minusone);
		y = new myval(x.intval, t, x.refval, x.arrayval);
		// x and y are still equal
		System.out.println("2");
		if (x != y) return false;
		// objects with different values are not equal
		y = new myval(2, new complex(6,3), f, x.arrayval);
		System.out.println("3");
		if (x == y) return false;
		y = x;
		x.refval.w++;
		// ensure foo is treated as a reference object
		// so both x and y see the update
		System.out.println("4");
		if (y.refval.w != x.refval.w) return false;
		System.out.println("5");
		if (y.refval.w != 20) return false;
		val P0: place = here;
		// the "place" of a value class instance is here
		var n: int;
		{ val y0: myval = y;
			n = (future(y0) (
				(here != P0) ? -1 : y0.intval
			)).force();
		}
		System.out.println("6");
		return n != -1;
	}

	public static def main(var args: Rail[String]): void = {
		new ValueClass().execute();
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
