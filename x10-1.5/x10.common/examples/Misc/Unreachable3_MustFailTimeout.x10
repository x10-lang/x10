/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test resulted in unreachable statement message
 * in the compiler, as of 7/29/2005.
 *
 * @author Armando Solar-Lezama
 */
public class Unreachable3_MustFailTimeout extends x10Test {

	public boolean run() {
		async (here) {
			while (true) { }
		}
		return true;
	}

	public static void main(String[] args) {
		new Unreachable3_MustFailTimeout().execute();
	}
}

