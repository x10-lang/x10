/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Test #2 for for loop with for (point p: D) syntax
 *
 * @author kemal, 1/2005
 */
public class ForLoop2 extends x10Test {

	public const N: int = 3;

	public def run(): boolean = {
		var r: region{rank==1} = [0..N-1];
		var r3: region = [r, r, r];
		var P0: place = here;
		var d3: dist = r3->P0;

		if (!d3.region.equals(r3)) return false;

		//Ensure iterator works in lexicographic order
		var n: int = 0;
		var prev: Box[point] = null;
		for (val p: point in d3) {
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
	static def successor(var prev: Box[point], var p: point): boolean = {
		if (prev == null) return true;
		var i: int = prev(0);
		var j: int = prev(1);
		var k: int = prev(2);
		//System.out.println("Prev:"+i+" "+j+" "+k);
		//System.out.println("Actual:"+ p[0]+" "+p[1]+" "+p[2]);
		k++;
		if (k == N) {
			k = 0;
			j++;
			if (j == N) {
				j = 0;
				i++;
			}
		}
		//System.out.println("Expected:"+i+" "+j+" "+k);
		if (i != p(0)) return false;
		if (j != p(1)) return false;
		if (k != p(2)) return false;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ForLoop2().execute();
	}
}
