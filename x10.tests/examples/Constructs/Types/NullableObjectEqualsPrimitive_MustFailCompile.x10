/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;
import x10.util.Box;
/**
 * Checks 3 is an object which can be compared to a nullable
 *
 * @author vcave
 */
public class NullableObjectEqualsPrimitive_MustFailCompile extends x10Test {

	public def run(): boolean = {
		var x: Box[Any] = null;
		// This use of == must generate a compiler error
		var res1: boolean = 3==x; 
		var res2: boolean = x==3; // should be false
		return !res1 && !res2;
	}

	public static def main(Rail[String]) {
		new NullableObjectEqualsPrimitive_MustFailCompile().execute();
	}
}
