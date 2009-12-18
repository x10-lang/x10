/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Check a nullable is not an instanceof a constrained type.
 * @author vcave
 **/
public class NullableObjectToConstrainedType2 extends x10Test {
	 
	public def run(): boolean = {
		var nullableVarNotNull: X10DepTypeClassOne = new X10DepTypeClassOne(1);
		return (nullableVarNotNull instanceof X10DepTypeClassOne{p==1});
	}
	
	public static def main(var args: Rail[String]): void = {
		new NullableObjectToConstrainedType2().execute();
	}
}
