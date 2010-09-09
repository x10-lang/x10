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
public class ObjectToNullablePrimitiveConstrained2 extends x10Test {
	 
	public def run(): boolean = {
		var primitive: x10.lang.Object = 3;
		return !(primitive instanceof Box[int{self==4}]);
	}
	
	public static def main(var args: Rail[String]): void = {
		new ObjectToNullablePrimitiveConstrained2().execute();
	}
}
