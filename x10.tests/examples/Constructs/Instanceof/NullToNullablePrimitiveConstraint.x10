/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import x10.util.Box;
import harness.x10Test;

/**
 * Purpose: Check null is an instanceof a nullable primitive.
 * Note: The compiler directly replace instanceof expression by true.
 * @author vcave
 **/
public class NullToNullablePrimitiveConstraint extends x10Test {
	 
	public def run(): boolean = {
		return !(null instanceof Box[Int]);
	}
	
	public static def main(var args: Rail[String]): void = {
		new NullToNullablePrimitiveConstraint().execute();
	}
}
