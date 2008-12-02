/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Simple array test.
 *
 * Only uses the longhand forms such as ia.get(p) for ia[p].
 * Note: this a test only.  It is not the recommended way to
 * write x10 code.
 */
public class Array1 extends x10Test {

	public boolean run() {
		region e = region.factory.region(1, 10);
		region r = region.factory.region(new region[] {e, e});
		dist d = dist.factory.constant(r, here);
		int[.] ia = new int[d];

		for (point p: e)
			for (point q: e) {
				int i = p.get(0);
				int j = q.get(0);
				chk(ia.get(i,j) == 0);
				ia.set(i+j, i, j);
			}

		for (point p: d) {
			int i = p.get(0);
			int j = p.get(1);
			point q1 = point.factory.point(new int[] {i, j});
			chk(i == q1.get(0));
			chk(j == q1.get(1));
			chk(ia.get(i, j) == i+j);
			chk(ia.get(i, j) == ia.get(p));
			chk(ia.get(q1) == ia.get(p));
			ia.set(ia.get(p)-1, p);
			chk(ia.get(p) == i+j-1);
			chk(ia.get(q1) == ia.get(p));
		}

		return true;
	}

	public static void main(String[] args) {
		new Array1().execute();
	}
}

