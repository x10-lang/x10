/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

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

	public def run(): boolean = {
		val P: dist = dist.makeUnique();
		val R1: region{rank==2} = [0..1, 0..7]; // horizontal strip
		pr("R1", R1);
		chk(R1.isConvex());
		chk(R1.convexHull().equals(R1));
		val R2: region{rank==2} = [4..5, 0..7]; // horizontal strip
		pr("R2", R2);
		chk(R2.isConvex());
		chk(R2.convexHull().equals(R2));
		val R3: region{rank==2} = [0..7, 4..5]; // vertical strip
		pr("R3", R3);
		chk(R3.isConvex());
		chk(R3.convexHull().equals(R3));
		val R1orR2: region{rank==2} = (R1 || R2);
		pr("R1orR2", R1orR2);
		chk(!R1orR2.isConvex());
		val R1orR2ConvexHull: region{rank==2} = R1orR2.convexHull();
		pr("R1orR2ConvexHull", R1orR2ConvexHull);
		chk(R1orR2ConvexHull.isConvex());
		chk(R1orR2ConvexHull.equals([0..5, 0..7]));
		val R1orR2andR3: region{rank==2} = R1orR2 && R3;
		pr("R1orR2andR3", R1orR2andR3);
		chk(R1orR2andR3.equals([0..1, 4..5] || [4..5, 4..5]));
		chk(R1orR2.contains(R1orR2andR3) && R3.contains(R1orR2andR3));
		chk(!R1orR2andR3.isConvex());
		val R1orR2andR3ConvexHull: region{rank==2} = R1orR2andR3.convexHull() as region{rank==2};
		pr("R1orR2andR3ConvexHull", R1orR2andR3ConvexHull);
		chk(R1orR2andR3ConvexHull.isConvex());
		chk(R1orR2andR3ConvexHull.equals([0..5, 4..5]));
		val R1orR2orR3: region{rank==2} = R1 || R2 || R3;
		pr("R1orR2orR3", R1orR2orR3);
		chk(R1orR2orR3.equals([0..1, 0..7] || [4..5, 0..7] ||
					[2..3, 4..5] || [6..7, 4..5]));
		chk(R1orR2orR3.contains(R1) && R1orR2orR3.contains(R2) &&
				R1orR2orR3.contains(R3));
		chk(!R1orR2orR3.isConvex());
		val R1orR2orR3ConvexHull: region = R1orR2orR3.convexHull();
		pr("R1orR2orR3ConvexHull", R1orR2orR3ConvexHull);
		chk(R1orR2orR3ConvexHull.isConvex());
		//just the bounding box for all points in region
		chk(R1orR2orR3ConvexHull.equals([0..7, 0..7]));
		val R1orR2minusR3: region{rank==2} = R1orR2 - R3;
		pr("R1orR2minusR3", R1orR2minusR3);
		chk(R1orR2minusR3.equals([0..1, 0..3] || [0..1, 6..7] ||
					[4..5, 0..3] || [4..5, 6..7]));
		chk(R1orR2.contains(R1orR2minusR3) && R1orR2minusR3.disjoint(R3));
		chk(!R1orR2minusR3.isConvex());
		val R1orR2minusR3ConvexHull: region{rank==2} = R1orR2minusR3.convexHull() as region{rank==2};
		pr("R1orR2minusR3ConvexHull", R1orR2minusR3ConvexHull);
		chk(R1orR2minusR3ConvexHull.isConvex());
		chk(R1orR2minusR3ConvexHull.equals([0..5, 0..7]));
		val R4: region{rank==2} = ([0..0, 4..4] || [1..1, 3..3] || [5..5, 2..2] || [3..3, 6..6]);
		pr("R4", R4);
		val R4convexHull: region{rank==2} = R4.convexHull() as region{rank==2};
		pr("R4convexHull", R4convexHull);
		// just bounding box
		chk(R4convexHull.equals([0..5, 2..6]));
		val R1andR2: region{rank==2} = (R1 && R2);
		// an empty region is convex and its
		// bounding box is also the empty region
		pr("R1andR2", R1andR2);
		chk(R1andR2.isConvex());
		chk(R1andR2.convexHull().equals(R1andR2));

		return true;
	}

	static def iff(var x: boolean, var y: boolean): boolean = {
		return (x == y);
	}

	static def pr(var s: String, var r: region): void = {
		System.out.println();
		System.out.println("printing region "+s);
		var k: int = 0;
		val N: int = 8;
		for (val (i,j): point in [0..N-1, 0..N-1]) {
			System.out.print(" "+(r.contains([i, j]) ? "+" : "."));
			if ((++k) % N == 0) System.out.println();
		}
	}

	public static def main(var args: Rail[String]): void = {
		new RegionConvexHull().execute();
	}
}
