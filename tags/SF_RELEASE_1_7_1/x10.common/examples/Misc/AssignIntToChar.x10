/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Assigning a char literal to a char array.
 */
public class AssignIntToChar extends x10Test {

	/**
	 * Testing comments for run
	 */
	public boolean run() {
		char[] a = new char[4];
		boolean bit1 = true;
		boolean bit2 = false;
		a[1] = (bit2 ? 'A' : 'C') ;
		return true;
	}

	/**
	 * Testing comments for main
	 */
	public static void main(String[] args) {
		new AssignIntToChar().execute();
	}
}

