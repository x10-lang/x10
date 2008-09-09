/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Purpose: Use Box 
 * @author vcave
 **/
public class PrimitiveToBoxPrimitiveConstrained4 extends x10Test {
	 
	public def run(): boolean = {
		val a = 3;
		return !(a instanceof Box[int{self==4}]);
	}
	
	public static def main(var args: Rail[String]): void = {
		new PrimitiveToBoxPrimitiveConstrained4().execute();
	}
}
