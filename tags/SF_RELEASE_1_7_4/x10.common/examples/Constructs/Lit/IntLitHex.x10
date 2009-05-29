/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */

import harness.x10Test;

/**
 * Check that the compiler can accept int lits in hex.
 *
 * @author vj 1/2006
 */
public class IntLitHex extends x10Test {

	public boolean run() {
		System.out.println(0xABCABC);
		return true;
	}

	public static void main(String[] args) {
		new IntLitHex().execute();
	}

	
}

