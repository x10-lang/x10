/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test for for loop with for (point p:[1:N,1:N,1:N]) syntax.
 *
 * @author kemal, 1/2005
 */
public class ForLoop4 extends x10Test {

	const int N = 3;

	public boolean run() {
		//Ensure iterator works in lexicographic order
		int n = 0;
		nullable<point> prev = null;
		for (point p:[0:N-1,0:N-1,0:N-1]->here) {
			if (!successor(prev, p)) return false;
			prev = p;
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
	static boolean successor(nullable<point> prev, point p) {
		if (prev == null) return true;
		int i = prev[0];
		int j = prev[1];
		int k = prev[2];
		//System.out.println("Prev:"+i+" "+j+" "+k);
		//System.out.println("Actual:"+ p.get(0)+" "+p.get(1)+" "+p.get(2));
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
		if (i != p[0]) return false;
		if (j != p[1]) return false;
		if (k != p[2]) return false;
		return true;
	}

	public static void main(String[] args) {
		new ForLoop4().execute();
	}
}

