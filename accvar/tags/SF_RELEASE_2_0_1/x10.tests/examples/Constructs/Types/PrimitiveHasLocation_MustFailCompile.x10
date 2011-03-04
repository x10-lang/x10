/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * 3 should be an int, and ints are objects.
 *
 * @author vj, igor 09/06
 */
public class PrimitiveHasLocation_MustFailCompile extends x10Test {

	public def run(): boolean = {
		var x: int = 3;
		//this must fail compilation
		return x.home==null;
	}

	public static def main(Rail[String]) {
		new PrimitiveHasLocation_MustFailCompile().execute();
	}
}
