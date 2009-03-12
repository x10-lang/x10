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
public class PrimitiveToPrimitiveConstrained1 extends x10Test {
	 
	public def run() = 3 instanceof int{self==3};
	
	public static def main(var args: Rail[String]): void = {
		new PrimitiveToPrimitiveConstrained1().execute();
	}
}
