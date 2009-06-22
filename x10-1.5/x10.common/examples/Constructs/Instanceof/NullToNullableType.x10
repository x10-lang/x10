/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks a null value is not an instance of a nullable type
 * Note: The compiler statically replaces instanceof expression by true
 * @author vcave
 **/
public class NullToNullableType extends x10Test {
	 
	public boolean run() {
		return !(null instanceof nullable<X10DepTypeClassOne>);
	}
	
	public static void main(String[] args) {
		new NullToNullableType().execute();
	}
}
 