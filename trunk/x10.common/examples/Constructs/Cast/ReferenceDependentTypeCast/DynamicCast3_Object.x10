/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Several test to checks dynamic cast to wider constraint works.
 * Note: The following code will use an inlining strategy to dynamically checks constraints.
 * @author vcave
 **/
public class DynamicCast3_Object extends x10Test {

	public boolean run() {
		try {						
			// object declaration. We "loose" constraint info at compile time.
			x10.lang.Object object = 
				new X10DepTypeClassTwo(0,1);
			
			// identity cast and assignment
			X10DepTypeClassTwo(:p==0&&q==1) convertedObject2 =
				(X10DepTypeClassTwo(:p==0&&q==1)) object;
			
			// widening cast to a subset of the constraint
			X10DepTypeClassTwo(:q==1) convertedObject3 =
				(X10DepTypeClassTwo(:q==1)) object;
			
			// widening cast to a subset of the constraint
			X10DepTypeClassTwo(:p==0) convertedObject1 =
				(X10DepTypeClassTwo(:p==0)) object;

			// widening cast to base type
			X10DepTypeClassTwo convertedObject4 =
				(X10DepTypeClassTwo) object;

			// identity cast, widening assignment
			X10DepTypeClassTwo(:p==0) convertedObject5 =
				(X10DepTypeClassTwo(:p==0&&q==1)) object;
		}catch(Throwable e) {
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		new DynamicCast3_Object().execute();
	}

}
 