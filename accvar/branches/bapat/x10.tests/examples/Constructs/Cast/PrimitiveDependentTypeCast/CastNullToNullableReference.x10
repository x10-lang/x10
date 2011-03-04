/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks nullable cast is working for references type.
 * @author vcave
 **/
 public class CastNullToNullableReference extends x10Test {

	public def run(): boolean = {
		var obj: Box[Object] = null as Box[Object];
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new CastNullToNullableReference().execute();
	}
}
