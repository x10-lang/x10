/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Check primitive variable assignment to primitive dependent type.
 * Issue: Variable j is statically known as an int. Conversion from int as int(:c) is forbidden.
 * @author vcave
 **/
public class AssignmentPrimitiveToPrimitiveConstrained_MustFailCompile extends x10Test {

	public def run(): boolean = {
		
		try { 
			var i: int{self == 0} = 0;
			var j: int = 0;
			// Even if j equals zero, types are not compatible
			// A cast would be necessary to check conversion validity
			i = j; // should fail
		}catch(e: Throwable) {
			return false;
		}

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new AssignmentPrimitiveToPrimitiveConstrained_MustFailCompile().execute();
	}

}
