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
 * Purpose: 
 * @author vcave
 **/
public class PrimitiveToNullablePrimitiveConstrained1 extends x10Test {
	val three:Box[Int(3)] = 3;
	public def run()= three instanceof Box[Int(3)];
	
	public static def main(var args: Rail[String]): void = {
		new PrimitiveToNullablePrimitiveConstrained1().execute();
	}
}
