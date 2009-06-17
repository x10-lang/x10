/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Purpose: Illustrates float dependent type usage and Checks the numeric 
 *          expression to cast is not evaluated several time while checking for constraint
 * Note: The cast should not be inlined to avoid several execution of j*=2
 * Note: Append an 'F' force constraint representation to be a float.
 * @author vcave
 **/
public class Float_ConstraintDeclaredAsDouble_MustFailCompile extends x10Test {

	public def run(): boolean = {
		var j: float = 0.00001F;
		
		// invalid assignment 
		// float/*(:{self=2.0E-5F})*/ <-- double/*(:{self=2.0E-5})*/  
		var i1: float{self == 0.00002F} = 0.00002;

		// invalid assignment 
		// float/*(:{self=2.0E-5F})*/ <-- double/*(:{self=2.0E-5})*/  
		var i2: float{self == 0.00002F} = 0.00002D;

		// invalid assignment 
		// float/*(:{self=2.0E-5F})*/ <-- float
		var i3: float{self == 0.00002F} = 0.00002 to float;

		// invalid assignment 
		// float/*(:{self=2.0E-5F})*/ <-- float
		var i4: float{self == 0.00002F} = 0.00002D to float;

		// invalid assignment 
		// float/*(:{self=2.0E-5F})*/ <-- float
		var i5: float{self == 0.00002F} = 0.00002F to float;
		
		return false;
	}

	public static def main(var args: Rail[String]): void = {
		new Float_ConstraintDeclaredAsDouble_MustFailCompile().execute();
	}

}
