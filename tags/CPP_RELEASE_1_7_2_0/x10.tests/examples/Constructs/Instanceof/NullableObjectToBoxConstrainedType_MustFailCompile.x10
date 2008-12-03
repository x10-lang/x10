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
public class NullableObjectToBoxConstrainedType_MustFailCompile extends x10Test {
	 
	public def run(): boolean = {
		var nullableVarNotNull: Box[ValueClass] = new ValueClass(1);
		return nullableVarNotNull instanceof Box[ValueClass{p==1}];
	}
	
	public static def main(var args: Rail[String]): void = {
		new NullableObjectToBoxConstrainedType_MustFailCompile().execute();
	}
}
