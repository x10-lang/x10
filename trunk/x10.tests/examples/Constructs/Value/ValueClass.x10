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
 * == for value class instances should be implemented by deep
 * recursive, field by field, comparison.
 *
 * @author kemal, 12/2004
 */
public class ValueClass extends x10Test {

	public def run(): boolean = {

		var r: Region = [0..9];
		var d: Dist = r->here;
		val f: foo = new foo();
		var x: myval = new myval(1, new complex(2,3), f, Array.make[int](d));
		var y: myval = new myval(1, new complex(2,3), f, Array.make[int](d));
		// even if x and y are different objects
		// their fields are equal, and thus they are ==
		x10.io.Console.OUT.println("1");
		if (x != y) return false;
		val one: complex = new complex(1,1);
		val minusone: complex = new complex(-1,-1);
		val t: complex = x.cval.add(one).add(minusone);
		y = new myval(x.intval, t, x.refval, x.arrayval);
		// x and y are still equal
		x10.io.Console.OUT.println("2");
		if (x != y) return false;
		// objects with different values are not equal
		y = new myval(2, new complex(6,3), f, x.arrayval);
		x10.io.Console.OUT.println("3");
		if (x == y) return false;
		y = x;
		x.refval.w++;
		// ensure foo is treated as a reference object
		// so both x and y see the update
		x10.io.Console.OUT.println("4");
		if (y.refval.w != x.refval.w) return false;
		x10.io.Console.OUT.println("5");
		if (y.refval.w != 20) return false;
		val P0: Place = here;
		// the "place" of a value class instance is here
		var n: int;
		{ val y0: myval = y;
			n = y0.intval;
			
		}
		x10.io.Console.OUT.println("6");
		return n != -1;
	}

	public static def main(var args: Rail[String]): void = {
		new ValueClass().execute();
	}

        static final value class myval {
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

        static final value complex {
                val re: int;
                val im: int;
                def this(re: int, im: int) {
                        this.re = re;
                        this.im = im;
                }
                def add (other: complex): complex {
                        return new complex(this.re+other.re, this.im+other.im); 
                }
        }
}
