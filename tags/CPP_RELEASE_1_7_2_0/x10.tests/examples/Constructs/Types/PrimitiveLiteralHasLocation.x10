/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.
import harness.x10Test;;

/**
 * 3 should be an int, and ints are objects.
 *
 * @author vj, igor 09/06
 */
public class PrimitiveLiteralHasLocation extends x10Test {

	public def run(): boolean = {
		return 3.loc==null;
	}

	public static def main(var args: Rail[String]): void = {
		new PrimitiveLiteralHasLocation().execute();
	}
}
