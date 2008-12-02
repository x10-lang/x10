/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Check negative integer in deptype clause.
 * @author vcave
 **/
public class CastNegativeConstraint extends x10Test {

	 public boolean run() {
		try {						
			x10.lang.Object object = 
				(X10DepTypeClassTwo(:p==0&&q==-1)) new X10DepTypeClassTwo(0,-1);
			// contraint not meet
			X10DepTypeClassTwo(:p==0&&q==-2) convertedObject =
				(X10DepTypeClassTwo(:p==0&&q==-2)) object;
		}catch(ClassCastException e) {
			return true;
		}

		return false;
	}

	public static void main(String[] args) {
		new CastNegativeConstraint().execute();
	}
}
 

 