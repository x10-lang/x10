/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks a boxed struct is an instanceof of the same type.
 * @author vcave
 **/
public class ObjectToNullablePrimitive extends x10Test {
	 
	public def run(): boolean = {
		var primitive:Any = 3;
		// Type to check is transformed to BoxedInt
		return (primitive instanceof Int);
	}
	
	public static def main(var args: Rail[String]): void = {
		new ObjectToNullablePrimitive().execute();
	}
}
