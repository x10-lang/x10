/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import x10.util.Box;

import harness.x10Test;
import x10.regionarray.*;

/**
 * Test #2 for for loop with for (Point p: D) syntax
 *
 * @author kemal, 1/2005
 */
public class ForLoop2 extends x10Test {

	public static N: long = 3;

	public def run(): boolean {
		val r  = Region.make(0, N-1);
		val r3 = r*r*r;
		val P0 = here;
		val d3 = r3->P0;

		if (!d3.region.equals(r3)) return false;

		//Ensure iterator works in lexicographic order
		var n: long = 0L;
		var prev: Box[Point] = null;
		for (p in d3) {
			if (!successor(prev, p)) return false;
			prev = new Box[Point](p);
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
	static def successor(var prev: Box[Point], var p: Point): boolean {
		if (prev == null) return true;
		val pt = prev();
		var i: long = pt(0);
		var j: long = pt(1);
		var k: long = pt(2);
		//x10.io.Console.OUT.println("Prev:"+i+" "+j+" "+k);
		//x10.io.Console.OUT.println("Actual:"+ p[0]+" "+p[1]+" "+p[2]);
		k++;
		if (k == N) {
			k = 0L;
			j++;
			if (j == N) {
				j = 0L;
				i++;
			}
		}
		//x10.io.Console.OUT.println("Expected:"+i+" "+j+" "+k);
		if (i != p(0)) return false;
		if (j != p(1)) return false;
		if (k != p(2)) return false;
		return true;
	}

	public static def main(var args: Rail[String]): void {
		new ForLoop2().execute();
	}
}
