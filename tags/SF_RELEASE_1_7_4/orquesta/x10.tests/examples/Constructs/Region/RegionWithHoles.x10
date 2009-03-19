/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

public class RegionWithHoles extends x10Test {

	def test1D(): boolean = {
		// all of those are contiguous
		var r: region{rank==1} = [0..10];
		var r1: region{rank==1} = [1..2];
		var r2: region{rank==1} = [5..6];

		// create holes in r
		r = r - r1;
		r = r - r2;

		var a: ValArray[short] = new ValArray[short](r);
		chk(!r.isConvex());
		// check if r is convex - it should not!
		System.out.println("convex: " + r.isConvex() + " (should be false)");

		System.out.print("indexes: ");
		for (val (i): point in r) {
			System.out.print(i + " ");
		}

		try {
			for (val (i): point in r) {
				if (a(i) != 0)
					System.out.println("val[" + i + "] = " + a(i));
			}
		}
		catch (var t: Throwable) {
			System.out.println(t);
			return false;
		}
		return true;
	}

	def test2D(): boolean = {
		System.out.println("testing 2d");
		// all of those are contiguous
		var r: region{rank==2} = [0..10, 0..3];
		var r1: region{rank==2} = [1..2, 0..3];
		var r2: region{rank==2} = [5..6, 0..3];

		// create holes in r
		r = r - r1;
		r = r - r2;

		var a: Array[short] = new Array[short](r);

		// check if r is convex - it should not!
		System.out.println("convex: " + r.isConvex() + " (should be false)");
		chk(!r.isConvex());

		try {
			for (val point[i,j]: point in r) {
				if (a(i, j) != 0)
					System.out.println("val[" + i + "] = " + a(i, j));
			}
		}
		catch (var t: Throwable) {
			System.out.println(t);
			return false;
		}
		return true;
	}

	def test3D(): boolean = {
		// all of those are contiguous
		var r: region{rank==3} = [0..10, 0..3, 0..0];
		var r1: region{rank==3} = [1..2, 0..3, 0..0];
		var r2: region{rank==3} = [5..6, 0..3, 0..0];

		// create holes in r
		r = r - r1;
		r = r - r2;

		var a: Array[short] = new Array[short](r);
		chk(!r.isConvex());
		// check if r is convex - it should not!
		System.out.println("convex: " + r.isConvex() + " (should be false)");

		try {
			for (val point[i,j,k]: point in r) {
				if (a(i, j, k) != 0)
					System.out.println("val[" + i + "] = " + a(i, j, k));
			}
		}
		catch (var t: Throwable) {
			System.out.println(t);
			return false;
		}
		return true;
	}

	def test4D(): boolean = {
		// all of those are contiguous
		var r: region{rank==4} = [0..0, 0..10, 0..3, 0..0];
		var r1: region{rank==4} = [0..0, 1..2, 0..3, 0..0];
		var r2: region{rank==4} = [0..0, 5..6, 0..3, 0..0];

		// create holes in r
		r = r - r1;
		r = r - r2;

		var a: Array[short] = new Array[short](r);
		chk(!r.isConvex());
		// check if r is convex - it should not!
		System.out.println("4d:convex: " + r.isConvex() + " (should be false)");

		if (false) {
			System.out.print("indexes: ");
			for (val point[i]: point in r) {
				System.out.print(i + " ");
			}
		}

		try {
			for (val point[i,j,k,l]: point in r) {
				if (a(i, j, k, l) != 0)
					System.out.println("val[" + i + "] = " + a(i, j, k, l));
			}
		}
		catch (var e: x10.lang.Exception) {
			System.out.println(e);
			return false;
		}
		return true;
	}

	def testPoint(): boolean = {
		System.out.println("testing point");
		// all of those are contiguous
		var r: region{rank==1} = [0..10];
		var r1: region{rank==1} = [1..2];
		var r2: region{rank==1} = [5..6];

		// create holes in r
		r = r - r1;
		r = r - r2;

		var a: Array[short] = new Array[short](r);
		chk(!r.isConvex());
		// check if r is convex - it should not!
		System.out.println("convex: " + r.isConvex() + " (should be false)");

		try {
			for (val p: point in r) {
				if (a(p) != 0)
					System.out.println("val[" + p + "] = " + a(p));
			}
		}
		catch (var t: Throwable) {
			System.out.println(t);
			return false;
		}
		return true;
	}

	public def run(): boolean = {
		return test1D() && test2D() && test3D() && test4D() && testPoint();
	}

	public static def main(var args: Rail[String][]): void = {
		new RegionWithHoles().execute();
	}
}
