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
 * Issue: Value to cast does not meet constraint requirement of target type.
 * @author vcave
 **/
public class CastPrimitiveToPrimitiveConstrained1 extends x10Test {

	public def run(): boolean = {
		
		try { 
			var i: int{self == 0} = 0;
			var j: int = 1;
			i = j as int{self == 0};
		}catch(e: ClassCastException) {
			return true;
		}

		return false;
	}

	public static def main(var args: Rail[String]): void = {
		new CastPrimitiveToPrimitiveConstrained1().execute();
	}

}
