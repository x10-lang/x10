/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Testing that a local variable named val is accepted.
 */
public class FieldNamedValTest extends x10Test {
	/**
	 * Testing a comment before instance field.
	 */
	public var val: int;

	/**
	 * Testing a comment before nullary constructor.
	 */
	public def this(): FieldNamedValTest = {
		val = 10;
	}

	/**
	 * Testing a comment before unary constructor.
	 */
	public def this(var v: int): FieldNamedValTest = {
		val = v;
	}

	/**
	 * Testing comments for runp
	 */
	public def run(): boolean = (val == 10);

	/**
	 * Testing comments for main
	 */
	public static def main(var args: Rail[String]): void = {
		new FieldNamedValTest().execute();
	}
}
