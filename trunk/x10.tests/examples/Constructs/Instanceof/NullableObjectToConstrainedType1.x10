/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Purpose: Check a nullable is not an instanceof a constrained type.
 * @author vcave
 * @author vj -- Reference classes are nullable by default in 1.7
 **/
public class NullableObjectToConstrainedType1 extends x10Test {
	 
	public def run(): boolean = {
		var nullableVarNotNull: X10DepTypeClassOne = new X10DepTypeClassOne(2);
		return !(nullableVarNotNull instanceof X10DepTypeClassOne{p==1});
	}
	
	public static def main(var args: Rail[String]): void = {
		new NullableObjectToConstrainedType1().execute();
	}
}
