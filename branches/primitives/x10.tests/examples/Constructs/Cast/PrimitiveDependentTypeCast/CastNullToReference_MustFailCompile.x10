/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks null litteral can't be cast to a non-nullable type.
 * Issue: null is not an instanceof x10.lang.Object, but would be one of nullable<x10.lang.Object>
 * @author vcave
 **/
 public class CastNullToReference_MustFailCompile extends x10Test {

	public def run(): boolean = {
		var obj: x10.lang.Object = null as Object; // fails in 1.5, ok in 1.7
		var v: x10.lang.Value = null as Value; // fails in 1.7
		return false;
	}

	public static def main(var args: Rail[String]): void = {
		new CastNullToReference_MustFailCompile().execute();
	}
}
