/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

public class RegionWithHoles2 extends x10Test {

	public def run(): boolean = {
		// all of those are contiguous
		var r: region{rank==1} = [0..10];
		var r1: region{rank==1} = [1..2];
		var r2: region{rank==1} = [5..6];

		// create wholes in r
		r = r - r1;
		r = r - r2;

		var a: ValArray[short] = new ValArray[short](r);

		// check if r is convex - it should not!
		var cv: boolean = r.isConvex();
		System.out.println("convex: " + cv + " (should be false)");
		chk(!cv);

		System.out.print("indexes: ");
		for (val (i): point in r) {
			System.out.print(i + " ");
		}
		System.out.println();
		for (val (i): point in r) {
			System.out.println("val[" + i + "] = " + a(i));
		}

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new RegionWithHoles2().execute();
	}
}
