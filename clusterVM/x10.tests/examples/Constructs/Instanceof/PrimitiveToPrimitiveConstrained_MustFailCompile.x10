/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks a boxed litteral primitive is effectively checked against
 *          a constrained primitive type.
 * @author vcave
 **/
public class PrimitiveToPrimitiveConstrained_MustFailCompile extends x10Test {
	 
	public def run() = 3 instanceof int{self==4};
	
	public static def main(var args: Rail[String]): void = {
		new PrimitiveToPrimitiveConstrained_MustFailCompile().execute();
	}
}
