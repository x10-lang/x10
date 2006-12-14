/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Casting a null constant to a nullable is legal.
 * @author vcave
 **/
public class CastNullToConstrainedNullable extends x10Test {

	public boolean run() {
		// null can be cast to any nullable 
		nullable<X10DepTypeClassOne(:p==1)> nullableVarCasted2 = 
			(nullable<X10DepTypeClassOne(:p==1)>) null;

		return nullableVarCasted2 == null;
	}

	public static void main(String[] args) {
		new CastNullToConstrainedNullable().execute();
	}

}
 