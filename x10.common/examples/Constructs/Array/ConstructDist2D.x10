/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Tests 2D distributions constructed from regions.
 */
public class ConstructDist2D extends x10Test {

	public boolean run() {
		region(:rank==1) e = [1:10];
		region r = [e,e];
		dist d = r->here;
		return d.equals([1:10,1:10]->here);
	}

	public static void main(String[] args) {
		new ConstructDist2D().execute();
	}
}

