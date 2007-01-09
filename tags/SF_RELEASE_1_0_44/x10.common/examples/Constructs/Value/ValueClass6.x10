/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.
import harness.x10Test;

/**
 * Value class test:
 *
 * Tests equality of value arrays whose elements
 * have user defined value and reference types.
 *
 * @author kemal, 4/2004
 */
public class ValueClass6 extends x10Test {

	public boolean run() {
		final dist D = [0:9]->here;
		// different value arrays whose elements are the same
		// reference objects, must be equal
		final foo value[.] X1 = new foo value[D](point p[i]) { return new foo(); };
		final foo value[.] Y1 = new foo value[D](point p[i]) { return X1[i]; };
		System.out.println("1");
		if (X1 != Y1) return false;
		// different value arrays whose elements are different
		// value objects
		// that have the same contents, must be ==
		final complex value[.] X2 = new complex value[D](point p[i]) {
			System.out.println("The currentplace for X2" + i  + " is " + ( here));
			return new complex(i,i);
		};
		final complex value[.] Y2 = new complex value[D](point p[i]) {
			System.out.println("The currentplace for Y2" + i  + " is " + ( here));
			return new complex(i,i);
		};
		System.out.println("2");
		if (X2 != Y2) return false;
		// different value arrays whose elements are
		// different reference objects
		// which have the same contents, must not be ==
		final foo value[.] X3 = new foo value[D](point p[i]) {
			System.out.println("The currentplace for X3" + i  + " is " + ( here));
			return new foo();
		};
		final foo value[.] Y3 = new foo value[D](point p[i]) {
			System.out.println("The currentplace for Y3" + i  + " is " + ( here));
			return new foo();
		};
		System.out.println("3");
		if (X3 == Y3) return false;
		// different reference arrays must never be ==
		// even the arrays have the same contents
		final foo[.] X4 = new foo[D](point p[i]) { return new foo(); };
		final foo[.] Y4 = new foo[D](point p[i]) { return X4[i]; };
		System.out.println("4");
		if (X4 == Y4) return false;
		return true;
	}

	public static void main(String[] args) {
		new ValueClass6().execute();
	}

	static class foo {
		int w = 19;
	}

	static final value complex {
		int re;
		int im;
		complex(int re, int im) {
			this.re = re;
			this.im = im;
		}
	}
}

