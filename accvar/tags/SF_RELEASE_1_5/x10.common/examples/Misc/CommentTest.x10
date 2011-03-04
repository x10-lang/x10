/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Javadoc comments before a
 * constructor method caused an x10 compiler error (1/2005).
 */
public class CommentTest extends x10Test {
	/**
	 * Testing a comment before instance field.
	 * Testing a comment before instance field.
	 */
	public int val;

	/**
	 * Testing a comment before nullary constructor.
	 * Testing a comment before nullary constructor.
	 * Testing a comment before nullary constructor.
	 */
	public CommentTest() {
		val = 10;
	}

	/**
	 * Testing a comment before unary constructor.
	 * Testing a comment before unary constructor.
	 * Testing a comment before unary constructor.
	 */
	public CommentTest(int x) {
		val = x;
	}

	/**
	 * Testing comments for run
	 */
	public boolean run() {
		return val == 10;
	}

	/**
	 * Testing comments for main
	 */
	public static void main(String[] args) {
		new CommentTest().execute();
	}
}

