/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that an int lit generates the correct dep clause.
 */
public class IntLitDepType extends x10Test {
	public boolean run() {
		int(:self==1) f =  1;
		return true;
	}

	public static void main(String[] args) {
		new IntLitDepType().execute();
	}


}

