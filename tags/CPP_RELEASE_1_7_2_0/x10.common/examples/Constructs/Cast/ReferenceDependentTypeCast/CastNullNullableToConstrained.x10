/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Nullable constraint must be taken into account while performing cast.
 * Issue: Cast fails as we try to assign null value from a nullable to a non-nullable.
 * @author vcave
 **/
public class CastNullNullableToConstrained extends x10Test {
	public boolean run() {		
		try {
			nullable<X10DepTypeClassTwo> nullObject1 = null;
			
			// from null nullable to non-nullable (inlined) -> should fail
			X10DepTypeClassTwo(:p==0&&q==1) nonNull = 
				(X10DepTypeClassTwo(:p==0&&q==1)) nullObject1;
		} catch(ClassCastException e) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		new CastNullNullableToConstrained().execute();
	}
}
 