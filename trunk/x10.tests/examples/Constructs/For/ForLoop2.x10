/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import x10.util.Box;

import harness.x10Test;

/**
 * Test #2 for for loop with for (Point p: D) syntax
 *
 * @author kemal, 1/2005
 */
public class ForLoop2 extends x10Test {

	public const N: int = 3;

	public def run(): boolean = {
		var r: Region{rank==1} = [0..N-1];
		var r3: Region = [r, r, r];
		var P0: Place = here;
		var d3: Dist = r3->P0;

		if (!d3.region.equals(r3)) return false;

		//Ensure iterator works in lexicographic order
		var n: int = 0;
		var prev: Box[Point] = null;
		for (val p: Point in d3) {
			if (!successor(prev, p)) return false;
			prev = p;
			if (P0 != d3(p)) return false;
			n++;
		}
		if (n != N*N*N) return false;
		return true;
	}

	/**
	 * return true iff p is the lexicographic successor of prev
	 * For example for a [0..2,0..2,0..2] region
	 * i.e. we expect the order (0,0,0), (0,0,1),(0,0,2)
	 *  (0,1,0) ... (2,2,2) (row-major order)
	 */
	static def successor(var prev: Box[Point]!, var p: Point): boolean = {
		if (prev == null) return true;
		val pt = prev();
		var i: int = pt(0);
		var j: int = pt(1);
		var k: int = pt(2);
		//x10.io.Console.OUT.println("Prev:"+i+" "+j+" "+k);
		//x10.io.Console.OUT.println("Actual:"+ p[0]+" "+p[1]+" "+p[2]);
		k++;
		if (k == N) {
			k = 0;
			j++;
			if (j == N) {
				j = 0;
				i++;
			}
		}
		//x10.io.Console.OUT.println("Expected:"+i+" "+j+" "+k);
		if (i != p(0)) return false;
		if (j != p(1)) return false;
		if (k != p(2)) return false;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ForLoop2().execute();
	}
}
