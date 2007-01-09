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
public class CastNullNullableToNullable extends x10Test {

	public boolean run() {
		// no dep type hence regular java cast apply which allows null value casting
		nullable<X10DepTypeClassTwo> nullableVarCasted1 = 
			(nullable<X10DepTypeClassTwo>) null;

		nullable<X10DepTypeClassTwo> nullableVarCasted2 = 
			(nullable<X10DepTypeClassTwo>) getNullNullable();

		return true;
	}
	
	public nullable<X10DepTypeClassTwo> getNullNullable() {
		return null;
	}
	
	public static void main(String[] args) {
		new CastNullNullableToNullable().execute();
	}
}
 