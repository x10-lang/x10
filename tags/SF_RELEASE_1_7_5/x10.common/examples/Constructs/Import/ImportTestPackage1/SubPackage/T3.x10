/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package ImportTestPackage1.SubPackage;

import ImportTestPackage2._T4;
import harness.x10Test;

/**
 * auxiliary class for ImportTest, also a test by itself.
 */
public class T3 extends x10Test {
	public static boolean m3(final int x) {
		return future(here) { _T4.m4(x) }.force();
	}
	public boolean run() {
		return m3(49);
	}

	public static void main(String[] args) {
		new T3().execute();
	}
}

