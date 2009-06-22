/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks a boxed primitive is an instanceof this same primitive type .
 * @author vcave
 **/
public class ObjectToPrimitive extends x10Test {
	 
	public def run(): boolean = {
		var primitive: x10.lang.Object = 3;
		return (primitive instanceof Int);
	}
	
	public static def main(var args: Rail[String]): void = {
		new ObjectToPrimitive().execute();
	}
}
