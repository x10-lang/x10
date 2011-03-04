/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Test for for loop with x10 for (point p: D) syntax
 *
 * @author kemal, 12/2004
 */
public class ForLoop extends x10Test {

	public const N: int = 100;
	var nActivities: int = 0;

	public def run(): boolean = {
		var r: region = [0..N-1];
		var P0: place = here;
		var d: dist = r->P0;

		if (!d.region.equals(r)) return false;
		if (d.region.low() != 0) return false;
		if (d.region.high() != N-1) return false;

		//Ensure iterator works in lexicographic order
		var n: int = 0;
		var prev: int = d.region.low()-1;
		for (val p: point in d) {
			n += p(0);
			if (prev+1 != p(0)) return false;
			prev = p(0);
			if (P0 != d(p)) return false;
		}
		if (n != N*(N-1)/2) return false;

		// now iterate over a region
		n = 0;
		prev = r.low()-1;
		for (val p: point in r) {
			n += p(0);
			if (prev+1 != p(0)) return false;
			prev = p(0);
		}
		if (n != N*(N-1)/2) return false;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ForLoop().execute();
	}
}
