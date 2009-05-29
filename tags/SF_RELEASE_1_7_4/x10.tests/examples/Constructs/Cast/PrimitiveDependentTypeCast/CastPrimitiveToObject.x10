/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks explicit boxing from a litteral to Object is working.
 * @author vcave
 **/
 public class CastPrimitiveToObject extends x10Test {

	public def run(): boolean = {
		var obj: x10.lang.Object = 3 as x10.lang.Object;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new CastPrimitiveToObject().execute();
	}
}
