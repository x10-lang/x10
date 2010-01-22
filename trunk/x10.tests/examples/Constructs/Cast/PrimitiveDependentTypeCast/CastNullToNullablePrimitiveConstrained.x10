/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: A nullable type can always cast null.
 * @author vcave
 **/
 public class CastNullToNullablePrimitiveConstrained extends x10Test {

	public def run(): boolean = {
      var i: x10.util.Box[int(3)] = null as x10.util.Box[int(3)];
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new CastNullToNullablePrimitiveConstrained().execute();
	}
}
