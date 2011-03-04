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
 * @author vcave
 **/
public class NullableObjectToNullableConstrainedType extends x10Test {
	 
	public boolean run() {
		nullable<X10DepTypeClassOne> nullableVarNotNull = 
			new X10DepTypeClassOne(1);
		return (nullableVarNotNull instanceof nullable<X10DepTypeClassOne(:p==1)>);
	}
	
	public static void main(String[] args) {
		new NullableObjectToNullableConstrainedType().execute();
	}
}
 