/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.
import harness.x10Test;;

/**
 * Value class test:
 *
 * Tests equality of value arrays whose elements
 * have user defined value and reference types.
 *
 * @author kemal, 4/2004
 */
public class ValueClass6 extends x10Test {

	public def run(): boolean = {
		val D: dist = [0..9]->here;
		// different value arrays whose elements are the same
		// reference objects, must be equal
		val X1: Array[foo] = new Array[foo](D, (p(i): point): foo => new foo());
		val Y1: Array[foo] = new Array[foo](D, (p(i): point): foo => X1(i));
		System.out.println("1");
		if (X1 != Y1) return false;
		// different value arrays whose elements are different
		// value objects
		// that have the same contents, must be ==
		val X2: Array[complex] = new Array[complex](D, (p: point): complex {
			System.out.println("The currentplace for X2" + i  + " is " + ( here));
			return new complex(i,i);
		});
		val Y2: Array[complex] = new Array[complex](D, (p: point): complex => {
			System.out.println("The currentplace for Y2" + i  + " is " + ( here));
			return new complex(i,i);
		});
		System.out.println("2");
		if (X2 != Y2) return false;
		// different value arrays whose elements are
		// different reference objects
		// which have the same contents, must not be ==
		val X3: Array[foo] = new Array[foo](D, (p(i): point): foo => {
			System.out.println("The currentplace for X3" + i  + " is " + ( here));
			return new foo();
		});
		val Y3: Array[foo] = new Array[foo](D, (p(i): point): foo => {
			System.out.println("The currentplace for Y3" + i  + " is " + ( here));
			return new foo();
		});
		System.out.println("3");
		if (X3 == Y3) return false;
		// different reference arrays must never be ==
		// even the arrays have the same contents
		val X4: Array[foo] = new Array[foo](D, (p(i): point) => new foo());
		val Y4: Array[foo] = new Array[foo](D, (p(i): point) => X4(i));
		System.out.println("4");
		if (X4 == Y4) return false;
		return true;
	}

	public static def main(args: Rail[String]): void = {
		new ValueClass6().execute();
	}

	static class foo {
		var w: int = 19;
	}

	static final value complex {
		var re: int;
		var im: int;
		def this(re: int, im: int): complex = {
			this.re = re;
			this.im = im;
		}
	}
}
