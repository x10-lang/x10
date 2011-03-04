/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Cast's dependent type constraint must be satisfied by the primitive.
 * @author vcave
 **/
public class CastPrimitiveToPrimitiveConstrained2 extends x10Test {

	public def run(): boolean = {
	
		var i: int{self == 0} = 0;
		var j: int = 0;
		i = j as int{self == 0};

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new CastPrimitiveToPrimitiveConstrained2().execute();
	}

}
