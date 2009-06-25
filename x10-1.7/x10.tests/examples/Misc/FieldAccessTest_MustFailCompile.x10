/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * This produces an infinite loop for the type checker, on 06/25/06
 */
public class FieldAccessTest_MustFailCompile extends x10Test {
	public var n: Object;

	/**
	 * The method is deliberately type-incorrect.
	 * It should return nullable Object.
	 * The problem is that this incorrect program causes the compiler to loop.
	 */
	public def n(): FieldAccessTest_MustFailCompile = n;

	public def run()  = true;

	public static def main(var args: Rail[String]): void = {
		new FieldAccessTest_MustFailCompile().execute();
	}
}
