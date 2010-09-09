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
 * Minimal test to check if storage classes are implemented.
 *
 * @author kemal 4/2005
 */
public class StorageClassesTest extends x10Test {

	public def run(): boolean {
		val b: bar = new bar(here);
		val p0: Place = here;

		x1: foo!place = new foo!place();
		x2: foo!activity = new foo!activity();
		x3: foo!here = new foo();
		x4: foo!(b.pl) = new foo();
		x5: foo!p0 = (future (p0) (new foo())).force();
		x6: foo!? = new foo();
		x7: foo = new foo();
		u1: Array[foo!place];
		u2: Array[foo!activity];
		u3: Array[foo!here];
		u4: Array[foo!(b.pl)];
		u5: Array[foo!p0];
		u6: Array[foo!?];
		u7: Array[foo];
		y1: foo!place = x1 as foo!place;
		y2: foo!activity = x2 as foo!activity;
		y3: foo!here = x3 as foo!here;
		y4: foo!(b.pl) = x4 as foo!(b.pl);
		y5: foo!p0 = x5 as foo!p0;
		y6: foo!? = x6 as foo!?;
		y7: foo = x7 as foo;

		return true;
	}

	public static def main(args: Rail[String]): void = {
		new StorageClassesTest().execute();
	}

	static class foo {
		var val: int;
	}

	static class bar {
		var pl: Place;
		public def this(p: Place) = { this.pl = p; }
	}
}

