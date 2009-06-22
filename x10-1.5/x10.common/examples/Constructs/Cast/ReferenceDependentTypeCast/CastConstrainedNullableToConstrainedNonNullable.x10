/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Nullable constraint must be taken into account while performing casting.
 * @author vcave
 **/
public class CastConstrainedNullableToConstrainedNonNullable extends x10Test {

	public boolean run() {
		nullable<X10DepTypeClassTwo(:p==0&&q==1)> nullObject1 = 
			 (X10DepTypeClassTwo(:p==0&&q==1)) new X10DepTypeClassTwo(0,1);
			
		// from non-null nullable to non-nullable (inlined)
		X10DepTypeClassTwo(:p==0&&q==1) nonNull1 = 
			(X10DepTypeClassTwo(:p==0&&q==1)) nullObject1;
		
		// from non-null nullable to non-nullable
		X10DepTypeClassTwo(:p==0&&q==1) nonNull2 = 
			(X10DepTypeClassTwo(:p==0&&q==1)) this.getNullable();

		return true;
	}
	
	private nullable<X10DepTypeClassTwo(:p==0&&q==1)> getNullable() {
		return (X10DepTypeClassTwo(:p==0&&q==1)) new X10DepTypeClassTwo(0,1);
	}

	public static void main(String[] args) {
		new CastConstrainedNullableToConstrainedNonNullable().execute();
	}

}
 