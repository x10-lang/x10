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
public class PrimitiveToNullablePrimitiveConstrained2_MustFailCompile extends x10Test {
	 
	public def run() = !(3 instanceof Box[Int(4)]);
	
	public static def main(var args: Rail[String]): void = {
		new PrimitiveToNullablePrimitiveConstrained2_MustFailCompile().execute();
	}
}
