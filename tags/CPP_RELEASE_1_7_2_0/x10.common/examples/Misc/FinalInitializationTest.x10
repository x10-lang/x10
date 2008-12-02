/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Tests assignments to final fields in constructor.
 *
 * @author kemal, 3/2005
 */
public class FinalInitializationTest extends x10Test {

	static class myval {
		final int intval;
		final complex cval;
		final foo refval;
		myval(int intval, complex cval, foo refval) {
			this.intval = intval;
			this.cval = cval;
			this.refval = refval;
		}
		boolean eq(myval other) {
			return
				this.intval == other.intval &&
				this.cval.eq(other.cval) &&
				this.refval == other.refval;
		}
	}

	static class foo {
		int w = 19;
	}

	static class complex {
		final int re;
		final int im;
		complex(int re, int im) {
			this.re = re;
			this.im = im;
		}
		complex add (complex other) {
			return new complex(this.re+other.re,this.im+other.im);
		}
		boolean eq(complex other) {
			return this.re == other.re && this.im == other.im;
		}
	}

	public boolean run() {
		foo f = new foo();
		myval x = new myval(1, new complex(2, 3), f);
		myval y = new myval(1, (new complex(1, 4)).add(new complex(1, -1)), f);
		return (x.eq(y));
	}

	public static void main(String[] args) {
		new FinalInitializationTest().execute();
	}
}

