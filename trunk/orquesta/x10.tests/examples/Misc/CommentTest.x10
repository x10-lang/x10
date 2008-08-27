/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Javadoc comments before a
 * constructor method caused an x10 compiler error (1/2005).
 */
public class CommentTest extends x10Test {
	/**
	 * Testing a comment before instance field.
	 */
	public var v: int;

	/**
	 * Testing a comment before nullary constructor.
	 */
	public def this(): CommentTest = {
		v = 10;
	}

	/**
	 * Testing a comment before unary constructor.
	 */
	public def this(var x: int): CommentTest = {
		v = x;
	}

	/**
	 * Testing comments for run
	 */
	public def run(): boolean = v == 10;

	/**
	 * Testing comments for main
	 */
	public static def main(var args: Rail[String]): void = {
		new CommentTest().execute();
	}
}
