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
public class PrimitiveToPrimitiveConstrained2 extends x10Test {
	 
	public def run(): boolean = {
		var a: int = 4;
		return !(a instanceof int{self==3});
	}
	
	public static def main(var args: Rail[String]): void = {
		new PrimitiveToPrimitiveConstrained2().execute();
	}
}
