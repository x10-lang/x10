/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * This gives the error:
	 C:\eclipse\ws2\x10.common\examples\Constructs\Array\ArrayConstructor.x10:18:
		    Local variable "x" multiply defined. Previous definition at :16,13-19.
		1 error.
@author vj
 */
public class ArrayConstructor extends x10Test {

	public boolean run() {
		int x = 1.0;
		double[.] a = new double[[0:5]](point p) {
			int x=2.0;
			return x;
		};
	}

	public static void main(String[] args) {
		new ArrayConstructor().execute();
	}
}

