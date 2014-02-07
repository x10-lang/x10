/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

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
		v = 10n;
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
	public def run(): boolean = v == 10n;

	/**
	 * Testing comments for main
	 */
	public static def main(var args: Rail[String]): void = {
		new CommentTest().execute();
	}
}
