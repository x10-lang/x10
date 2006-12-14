/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks a null value is an instanceof a nullable type 
 * Issue: 
 * @author vcave
 **/
public class NullObjectToNullable extends x10Test {
	 
	public boolean run() {
		X10DepTypeClassOne [] array = new X10DepTypeClassOne [1];
		X10DepTypeClassOne var = array[0];
		nullable<X10DepTypeClassOne> nullableVarNull = null;

		// array[0] is null hence it is not an instance of targeted non nullable type
		boolean res1 = !(array[0] instanceof nullable<X10DepTypeClassOne>);
		
		// var is null hence it is not an instance of targeted non nullable type
		boolean res2 = !(var instanceof nullable<X10DepTypeClassOne>);
		
		// nullableVarNull is null hence it is an instance of the nullable type
		boolean res3 = !(nullableVarNull instanceof nullable<X10DepTypeClassOne>);
		
		// getNullNullable() is null hence it is an instance of the nullable type
		boolean res4 = !(getNullNullable() instanceof nullable<X10DepTypeClassOne>);

		return res1 && res2 && res3 && res4;
	}
	
	public nullable<X10DepTypeClassOne> getNullNullable() {
		return null;
	}
	
	public static void main(String[] args) {
		new NullObjectToNullable().execute();
	}
}
 