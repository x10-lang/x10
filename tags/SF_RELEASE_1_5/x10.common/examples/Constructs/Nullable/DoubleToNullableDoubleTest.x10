/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

public class DoubleToNullableDoubleTest extends x10Test {
	public boolean run() {
		nullable<double> data = 1.0;
		return true;
	}

	public static void main(String[] args) {
		new DoubleToNullableDoubleTest().execute();
	}
}

