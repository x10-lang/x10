/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Checks comparison between a primitive and a boxed one
 *
 * @author vcave
 */
public class ObjectEqualsPrimitive extends x10Test {

	public def run(): boolean = {
		var x: Any = 2+1;
		var res: boolean = 3==x;
		res &= x==3;
		return res;
	}

	public static def main(var args: Rail[String]): void = {
		new ObjectEqualsPrimitive().execute();
	}
}
