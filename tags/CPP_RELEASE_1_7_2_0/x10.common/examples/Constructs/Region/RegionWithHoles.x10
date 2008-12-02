/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

public class RegionWithHoles extends x10Test {

	boolean test1D() {
		// all of those are contiguous
		region(:rank==1) r = [0:10];
		region(:rank==1) r1 = [1:2];
		region(:rank==1) r2 = [5:6];

		// create holes in r
		r = r - r1;
		r = r - r2;

		short[.] a = new short[r];
		chk(!r.isConvex());
		// check if r is convex - it should not!
		System.out.println("convex: " + r.isConvex() + " (should be false)");

		System.out.print("indexes: ");
		for (point[i] : r) {
			System.out.print(i + " ");
		}

		try {
			for (point[i] : r) {
				if (a[i] != 0)
					System.out.println("val[" + i + "] = " + a[i]);
			}
		}
		catch (Throwable t) {
			System.out.println(t);
			return false;
		}
		return true;
	}

	boolean test2D() {
		System.out.println("testing 2d");
		// all of those are contiguous
		region(:rank==2) r = [0:10,0:3];
		region(:rank==2) r1 = [1:2,0:3];
		region(:rank==2) r2 = [5:6,0:3];

		// create holes in r
		r = r - r1;
		r = r - r2;

		short[.] a = new short[r];

		// check if r is convex - it should not!
		System.out.println("convex: " + r.isConvex() + " (should be false)");
		chk(!r.isConvex());

		try {
			for (point[i,j] : r) {
				if (a[i,j] != 0)
					System.out.println("val[" + i + "] = " + a[i,j]);
			}
		}
		catch (Throwable t) {
			System.out.println(t);
			return false;
		}
		return true;
	}

	boolean test3D() {
		// all of those are contiguous
		region(:rank==3) r = [0:10,0:3,0:0];
		region(:rank==3) r1 = [1:2,0:3,0:0];
		region(:rank==3) r2 = [5:6,0:3,0:0];

		// create holes in r
		r = r - r1;
		r = r - r2;

		short[.] a = new short[r];
		chk(!r.isConvex());
		// check if r is convex - it should not!
		System.out.println("convex: " + r.isConvex() + " (should be false)");

		try {
			for (point[i,j,k] : r) {
				if (a[i,j,k] != 0)
					System.out.println("val[" + i + "] = " + a[i,j,k]);
			}
		}
		catch (Throwable t) {
			System.out.println(t);
			return false;
		}
		return true;
	}

	boolean test4D() {
		// all of those are contiguous
		region(:rank==4) r = [0:0,0:10,0:3,0:0];
		region(:rank==4) r1 = [0:0,1:2,0:3,0:0];
		region(:rank==4) r2 = [0:0,5:6,0:3,0:0];

		// create holes in r
		r = r - r1;
		r = r - r2;

		short[.] a = new short[r];
		chk(!r.isConvex());
		// check if r is convex - it should not!
		System.out.println("4d:convex: " + r.isConvex() + " (should be false)");

		if (false) {
			System.out.print("indexes: ");
			for (point[i] : r) {
				System.out.print(i + " ");
			}
		}

		try {
			for (point[i,j,k,l] : r) {
				if (a[i,j,k,l] != 0)
					System.out.println("val[" + i + "] = " + a[i,j,k,l]);
			}
		}
		catch (x10.lang.Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}

	boolean testPoint() {
		System.out.println("testing point");
		// all of those are contiguous
		region(:rank==1) r = [0:10];
		region(:rank==1) r1 = [1:2];
		region(:rank==1) r2 = [5:6];

		// create holes in r
		r = r - r1;
		r = r - r2;

		short[.] a = new short[r];
		chk(!r.isConvex());
		// check if r is convex - it should not!
		System.out.println("convex: " + r.isConvex() + " (should be false)");

		try {
			for (point p : r) {
				if (a[p] != 0)
					System.out.println("val[" + p + "] = " + a[p]);
			}
		}
		catch (Throwable t) {
			System.out.println(t);
			return false;
		}
		return true;
	}

	public boolean run() {
		return test1D() && test2D() && test3D() && test4D() && testPoint();
	}

	public static void main(String args[]) {
		new RegionWithHoles().execute();
	}
}

