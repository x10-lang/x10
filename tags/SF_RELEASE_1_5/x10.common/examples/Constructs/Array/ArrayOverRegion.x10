/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Region r occuring in a distribution context is converted to r->here
 */
public class ArrayOverRegion extends x10Test {

	public boolean run() {
		region r = [1:10, 1:10];
		double[.] ia = new double[r] (point [i,j]) { return i+j; };
		chk(ia.distribution.equals(r->here));

		for (point p[i,j]: [1:10,1:10]) chk(ia[p] == i+j);

		return true;
	}

	public static void main(String[] args) {
		new ArrayOverRegion().execute();
	}
}

