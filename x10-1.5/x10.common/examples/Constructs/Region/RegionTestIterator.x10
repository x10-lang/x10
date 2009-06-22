/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import java.util.Iterator;
import harness.x10Test;

/**
 * Testing the standard region iterator.
 */
public class RegionTestIterator extends x10Test {

	public boolean run() {
		region r = region.factory.region(0, 100); // (low, high)
		region[] r2 = new region[] { r, r };
		region reg = region.factory.region(r2);

		int sum = 0;
		for (Iterator it = reg.iterator(); it.hasNext(); ) {
			point p = (point) it.next();
			sum += p[0]-p[1];
		}
		// for (point [i,j]: reg) sum += i - j;
		return sum == 0;
	}

	public static void main(String[] args) {
		new RegionTestIterator().execute();
	}
}

