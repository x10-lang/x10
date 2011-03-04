/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Use Box 
 * @author vcave
 **/
public class PrimitiveToPrimitiveConstrained3 extends x10Test {
	 
	public def run(): boolean = {
		val a: int = 3;
		return !(a instanceof int{self==4});
	}
	
	public static def main(var args: Rail[String]): void = {
		new PrimitiveToPrimitiveConstrained3().execute();
	}
}
