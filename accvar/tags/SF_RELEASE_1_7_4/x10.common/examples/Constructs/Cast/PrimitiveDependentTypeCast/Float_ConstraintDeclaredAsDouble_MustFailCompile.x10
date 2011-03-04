/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Illustrates float dependent type usage and Checks the numeric 
 *          expression to cast is not evaluated several time while checking for constraint
 * Note: The cast should not be inlined to avoid several execution of j*=2
 * Note: Append an 'F' force constraint representation to be a float.
 * @author vcave
 **/
public class Float_ConstraintDeclaredAsDouble_MustFailCompile extends x10Test {

	public boolean run() {
		float j = 0.00001F;
		
		// float (: self == 0.00002F) i1 = j;
		// invalid assignment 
		// float/*(:{self=2.0E-5F})*/ <-- double/*(:{self=2.0E-5})*/  
		float (: self == 0.00002F) i1 = 0.00002;

		// invalid assignment 
		// float/*(:{self=2.0E-5F})*/ <-- double/*(:{self=2.0E-5})*/  
		float (: self == 0.00002F) i2 = 0.00002D;

		// invalid assignment 
		// float/*(:{self=2.0E-5F})*/ <-- float
		float (: self == 0.00002F) i3 = (float) 0.00002;

		// invalid assignment 
		// float/*(:{self=2.0E-5F})*/ <-- float
		float (: self == 0.00002F) i4 = (float) 0.00002D;

		// invalid assignment 
		// float/*(:{self=2.0E-5F})*/ <-- float
		float (: self == 0.00002F) i5 = (float) 0.00002F;
		
		// i = (float (: self == 0.00002)) (j*=2);

		return false;
		// return ((j == 0.00002) && (i==0.00002));
	}

	public static void main(String[] args) {
		new Float_ConstraintDeclaredAsDouble_MustFailCompile().execute();
	}

}
 