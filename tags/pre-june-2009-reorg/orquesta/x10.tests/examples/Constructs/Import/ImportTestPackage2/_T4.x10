/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package ImportTestPackage2;

/**
 * Auxiliary class for ImportTest
 */
public class _T4 {
	public static def m4(val x: int): boolean = {
		return (future(here) ( x == 49)).force();
	}
}
