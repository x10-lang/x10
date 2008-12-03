/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

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

	public boolean run() {
		final int N = 3;
		final region(:rank==1)[.] ra = new region[[0:N-1]] (point[i]) { return [1:0]; };
		for (point[i]:ra)  {
			ra[i] = ((region(:rank==1)) ra[i]) || [10*i:10*i+9]; //TODOVJ -- Remove Cast
			ra[i] = ((region(:rank==1)) ra[i]) && [10*i+1:10*i+21];
		}
		for (point[i]:ra)
			System.out.println("ra["+i+"] = "+ra[i]);

		for (point[i]:ra)
			chk(ra[i].equals([10*i+1:10*i+9]));

		for (point [i]:ra) {
			int n = 0;
			for (point[k]:ra[i]) {
				chk(k >= 10*i+1 && k <= 10*i+9 &&
						ra[i].contains([k]));
				++n;
			}
			chk(n == 9);
		}

		return true;
	}

	public static void main(String[] args) {
		new ArrayOfRegions().execute();
	}
}

