/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Tests point (p[i,j]) notation.
 */
public class Array1Exploded extends x10Test {

	public int select(point p[i,j], point [k,l]) {
		return i + k;
	}

	public boolean run() {
		dist d =  [1:10,1:10]->here;
		int[.] ia = new int[d];

		for (point p[i,j]: [1:10,1:10]) {
			chk(ia[p] == 0);
			ia[p] = i+j;
		}

		for (point p[i,j]: d) {
			point q1 = [i,j];
			chk(i == q1[0]);
			chk(j == q1[1]);
			chk(ia[i,j] == i+j);
			chk(ia[i,j] == ia[p]);
			chk(ia[q1] == ia[p]);
		}

		chk(4 == select([1,2], [3,4]));

		return true;
	}

	public static void main(String[] args) {
		new Array1Exploded().execute();
	}
}

