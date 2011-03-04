/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test that a function type is not an object.
 * @author kemal, 12/2004
 */
public class FunIsNotObject_MustFailCompile extends x10Test {

	public const N: int = 100;
	var nActivities: int = 0;

	public def run() {
		val f = (x1:int, x2:int)=> x1+x2;
		val x:Object = f;
	}
	public static def main(Rail[String]) {
		new FunIsNotObject_MustFailCompile().execute();
	}
}
