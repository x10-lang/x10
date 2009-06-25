/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * region[.] ra = ...;
 * for (point[k]:ra[i]) {...} does not compile
 * as of 11/2005.
 * (Parentheses missing in generated java code)
 *
 * Bug reported by Mehmet Fatih Su.
 *
 * @author kemal 11/2005
 */
public class ArrayOfRegions extends x10Test {

	public def run(): boolean = {
		val N: int = 3;
		val ra: Array[region{rank==1}] = new Array[region]([0..N-1], ((i): point): region => { return [1..0]; });
		for (val (i): point in ra) {
			ra(i) = ((region{rank==1})) ra(i)) || [10*i..10*i+9]; //TODOVJ -- Remove Cast
			ra(i) = ((region{rank==1})) ra(i)) && [10*i+1..10*i+21];
		}
		for (val (i): point in ra) System.out.println("ra["+i+"] = "+ra(i));

		for (val (i): point in ra) chk(ra(i).equals([10*i+1..10*i+9]));

		for (val (i): point in ra) {
			var n: int = 0;
			for (val (k): point in ra(i)) {
				chk(k >= 10*i+1 && k <= 10*i+9 &&
						ra(i).contains([k]));
				++n;
			}
			chk(n == 9);
		}

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ArrayOfRegions().execute();
	}
}
