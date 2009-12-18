/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks boxed integer value is checks against primtive dependent type.
 * Issue: Constraint on self is not meet.
 * @author vcave
 **/
public class ObjectToPrimitiveConstrained3 extends x10Test {
	 
	public def run(): boolean = {
		var primitive: x10.lang.Object = 3;
		return !(primitive instanceof Int(4));
	}
	
	public static def main(var args: Rail[String]): void = {
		new ObjectToPrimitiveConstrained3().execute();
	}
}
