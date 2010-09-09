/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Purpose: Checks constant promotion works.
 * Note: The compiler promotes constant's type from int to int(:self==0)
 * @author vcave
 **/
public class CastPrimitiveLitteralToPrimitiveConstrained1 extends x10Test {

	public def run(): boolean = {
		var i: int{self == 0} = 0;
		i = 0 as int{self == 0};
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new CastPrimitiveLitteralToPrimitiveConstrained1().execute();
	}

}
