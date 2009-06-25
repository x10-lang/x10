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
public class VariableNamedValTest extends x10Test {
	/**
	 * Testing a comment before instance field.
	 */
	public var v: int;

	/**
	 * Testing a comment before nullary constructor.
	 */
	public def this(): VariableNamedValTest = {
		v = 10;
	}

	/**
	 * Testing a comment before unary constructor.
	 */
	public def this(var val: int): VariableNamedValTest = {
		val = v;
	}

	/**
	 * Testing comments for run
	 */
	public def run(): boolean = (v == 10);

	/**
	 * Testing comments for main
	 */
	public static def main(var args: Rail[String]): void = {
		new VariableNamedValTest().execute();
	}
}
