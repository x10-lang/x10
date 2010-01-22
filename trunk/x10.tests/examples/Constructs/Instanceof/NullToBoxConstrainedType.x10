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
 * Purpose: Checks a null value is an instance of a constrained nullable type.
 * @author vcave
 **/
public class NullToBoxConstrainedType extends x10Test {
	 
	public def run()=!(null instanceof Box[ValueClass{p==1}]);
	
	public static def main(var args: Rail[String]): void = {
		new NullToBoxConstrainedType().execute();
	}
}
