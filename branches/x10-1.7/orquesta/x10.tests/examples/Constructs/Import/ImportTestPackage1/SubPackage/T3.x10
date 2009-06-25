/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package ImportTestPackage1.SubPackage;

import ImportTestPackage2._T4;
import harness.x10Test;;

/**
 * auxiliary class for ImportTest, also a test by itself.
 */
public class T3 extends x10Test {
	public static def m3(val x: int): boolean = {
		return (future(here) ( _T4.m4(x))).force();
	}
	public def run(): boolean = {
		return m3(49);
	}

	public static def main(var args: Rail[String]): void = {
		new T3().execute();
	}
}
