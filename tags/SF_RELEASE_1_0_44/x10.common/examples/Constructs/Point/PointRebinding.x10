/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.
import harness.x10Test;

/**
 * Must allow binding components to an existing point.
 *
 * @author igor, 1/2006
 */
public class PointRebinding extends x10Test {

	public boolean run() {
		point p = [1,2];
		point [i,j] = p;

		return (i == 1 && j == 2);
	}

	public static void main(String[] args) {
		new PointRebinding().execute();
	}
}

