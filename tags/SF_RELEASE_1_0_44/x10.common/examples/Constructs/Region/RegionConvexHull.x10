/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Tests convex hull operation on regions.
 *
 * Expected implementation of "convex hull" is simply a bounding box
 *
 * convexHull( { (x1,y1),...(xN,yN) } ) is
 *
 * [min(i = 1:N)(xi):max(i = 1:N)(xi),
 *  min(i = 1:N)(yi):max(i = 1:N)(yi)]
 *
 * A region is convex iff it is equal to its bounding box.
 *
 * @author kemal 4/2005
 */
public class RegionConvexHull extends x10Test {

	public boolean run() {
		final dist P = dist.factory.unique();
		final region(:rank==2) R1 = [0:1,0:7]; // horizontal strip
		pr("R1", R1);
		chk(R1.isConvex());
		chk(R1.convexHull().equals(R1));
		final region(:rank==2) R2 = [4:5,0:7]; // horizontal strip
		pr("R2", R2);
		chk(R2.isConvex());
		chk(R2.convexHull().equals(R2));
		final region(:rank==2) R3 = [0:7,4:5]; // vertical strip
		pr("R3", R3);
		chk(R3.isConvex());
		chk(R3.convexHull().equals(R3));
		final region(:rank==2) R1orR2 = (R1 || R2);
		pr("R1orR2", R1orR2);
		chk(!R1orR2.isConvex());
		final region(:rank==2) R1orR2ConvexHull = (region(:rank==2)) R1orR2.convexHull();
		pr("R1orR2ConvexHull", R1orR2ConvexHull);
		chk(R1orR2ConvexHull.isConvex());
		chk(R1orR2ConvexHull.equals([0:5,0:7]));
		final region(:rank==2) R1orR2andR3 = R1orR2 && R3;
		pr("R1orR2andR3", R1orR2andR3);
		chk(R1orR2andR3.equals([0:1,4:5] || [4:5,4:5]));
		chk(R1orR2.contains(R1orR2andR3) && R3.contains(R1orR2andR3));
		chk(!R1orR2andR3.isConvex());
		final region(:rank==2) R1orR2andR3ConvexHull = (region(:rank==2)) R1orR2andR3.convexHull();
		pr("R1orR2andR3ConvexHull", R1orR2andR3ConvexHull);
		chk(R1orR2andR3ConvexHull.isConvex());
		chk(R1orR2andR3ConvexHull.equals([0:5,4:5]));
		final region(:rank==2) R1orR2orR3 = R1 || R2 || R3;
		pr("R1orR2orR3", R1orR2orR3);
		chk(R1orR2orR3.equals([0:1,0:7] || [4:5,0:7] ||
					[2:3,4:5] || [6:7,4:5]));
		chk(R1orR2orR3.contains(R1) && R1orR2orR3.contains(R2) &&
				R1orR2orR3.contains(R3));
		chk(!R1orR2orR3.isConvex());
		final region R1orR2orR3ConvexHull = R1orR2orR3.convexHull();
		pr("R1orR2orR3ConvexHull", R1orR2orR3ConvexHull);
		chk(R1orR2orR3ConvexHull.isConvex());
		//just the bounding box for all points in region
		chk(R1orR2orR3ConvexHull.equals([0:7,0:7]));
		final region(:rank==2) R1orR2minusR3 = R1orR2 - R3;
		pr("R1orR2minusR3", R1orR2minusR3);
		chk(R1orR2minusR3.equals([0:1,0:3] || [0:1,6:7] ||
					[4:5,0:3] || [4:5,6:7]));
		chk(R1orR2.contains(R1orR2minusR3) && R1orR2minusR3.disjoint(R3));
		chk(!R1orR2minusR3.isConvex());
		final region(:rank==2) R1orR2minusR3ConvexHull = (region(:rank==2)) R1orR2minusR3.convexHull();
		pr("R1orR2minusR3ConvexHull", R1orR2minusR3ConvexHull);
		chk(R1orR2minusR3ConvexHull.isConvex());
		chk(R1orR2minusR3ConvexHull.equals([0:5,0:7]));
		final region(:rank==2) R4 = ([0:0,4:4] || [1:1,3:3] || [5:5,2:2] || [3:3,6:6]);
		pr("R4", R4);
		final region(:rank==2) R4convexHull = (region(:rank==2))R4.convexHull();
		pr("R4convexHull", R4convexHull);
		// just bounding box
		chk(R4convexHull.equals([0:5,2:6]));
		final region(:rank==2) R1andR2 = (R1 && R2);
		// an empty region is convex and its
		// bounding box is also the empty region
		pr("R1andR2", R1andR2);
		chk(R1andR2.isConvex());
		chk(R1andR2.convexHull().equals(R1andR2));

		return true;
	}

	static boolean iff(boolean x, boolean y) {
		return (x == y);
	}

	static void pr(String s, region r) {
		System.out.println();
		System.out.println("printing region "+s);
		int k = 0;
		final int N = 8;
		for (point [i,j]: [0:N-1,0:N-1]) {
			System.out.print(" "+(r.contains([i,j]) ? "+" : "."));
			if ((++k) % N == 0) System.out.println();
		}
	}

	public static void main(String[] args) {
		new RegionConvexHull().execute();
	}
}

