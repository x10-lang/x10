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

	public boolean run() {
		bar b = new bar(here);
		place p0 = here;

		foo!place x1 = new foo!place();
		foo!activity x2 = new foo!activity();
		foo!here x3 = new foo();
		foo!(b.pl) x4 = new foo();
		foo!p0 x5 = future(p0) { new foo() }.force();
		foo!? x6 = new foo();
		foo x7 = new foo();
		nullable foo!place[.] u1;
		nullable foo!activity[.] u2;
		nullable foo!here[.] u3;
		nullable foo!(b.pl)[.] u4;
		nullable foo!p0[.] u5;
		nullable foo!?[.] u6;
		nullable foo[.] u7;
		foo!place y1 = (foo!place)x1;
		foo!activity y2 = (foo!activity)x2;
		foo!here y3 = (foo!here)x3;
		foo!(b.pl) y4 = (foo!(b.pl))x4;
		foo!p0 y5 = (foo!p0)x5;
		foo!? y6 = (foo!?)x6;
		foo y7 = (foo)x7;

		return true;
	}

	public static void main(String[] args) {
		new StorageClassesTest().execute();
	}

	static class foo {
		int val;
	}

	static class bar {
		place pl;
		public bar(place p) { this.pl = p; }
	}
}

