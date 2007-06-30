/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing quotes in strings.
 */
public class StringTest extends x10Test {

	public int val;
	public StringTest() {
		val = 10;
	}

	public boolean run() {
		String foo = "the number is "+val;
		if (!(val == 10 && foo.equals("the number is "+"10"))) return false;
		if (foo.charAt(2) != 'e') return false;
		return true;
	}

	public static void main(String[] args) {
		new StringTest().execute();
	}
}

