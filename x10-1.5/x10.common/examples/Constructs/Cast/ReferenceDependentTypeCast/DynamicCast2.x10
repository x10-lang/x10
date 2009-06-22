/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks dynamic cast detects constraint restriction is not valid. 
 * X10DepTypeClassTwo(:p==1) <-- X10DepTypeClassTwo(:p==0&&q==1)
 * @author vcave
 **/
public class DynamicCast2 extends x10Test {

	public boolean run() {
		try {						
			x10.lang.Object object = 
				new X10DepTypeClassTwo(0,1);
			
			// contraint widening but illegal
			X10DepTypeClassTwo(:p==1) convertedObject =
				(X10DepTypeClassTwo(:p==1)) object;
		}catch(ClassCastException e) {
			return true;
		}

		return false;
	}

	public static void main(String[] args) {
		new DynamicCast2().execute();
	}

}
 