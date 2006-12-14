/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: 
 * Issue: 
 * @author vcave
 **/
public class CastNullableToNonNullable extends x10Test {

	public boolean run() {
		X10DepTypeClassTwo var = new X10DepTypeClassTwo(1,2);
		
		// no dep type hence regular java cast apply which allows null value casting
		nullable<X10DepTypeClassTwo> nullableVarCasted1 = 
			(nullable<X10DepTypeClassTwo>) var;

		// to nullable but expr != null
		nullable<X10DepTypeClassTwo> nullableVarCasted2 = 
			(nullable<X10DepTypeClassTwo>) new X10DepTypeClassTwo(1,2) ;

		return true;
	}

	public static void main(String[] args) {
		new CastNullableToNonNullable().execute();
	}
}
 