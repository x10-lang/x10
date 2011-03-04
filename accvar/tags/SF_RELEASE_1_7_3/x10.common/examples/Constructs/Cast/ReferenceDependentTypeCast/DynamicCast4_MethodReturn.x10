/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks dynamic cast should fail at runtime.
 * Issue: Constraint is not meet.
 * Note: The following code will use java reflexion to dynamically checks constraints. 
 * @author vcave
 **/
public class DynamicCast4_MethodReturn extends x10Test {
	public boolean run() {		
		try {						
			// constraint not meet
			X10DepTypeClassTwo(:p==0&&q==2) convertedObject =
				(X10DepTypeClassTwo(:p==0&&q==2)) this.objectReturner();
			
		}catch(ClassCastException e) {
			return true;
		}

		return false;
	}
	
	public x10.lang.Object objectReturner() {
		return new X10DepTypeClassTwo(0,1);
	}

	public static void main(String[] args) {
		new DynamicCast4_MethodReturn().execute();
	}
}
 