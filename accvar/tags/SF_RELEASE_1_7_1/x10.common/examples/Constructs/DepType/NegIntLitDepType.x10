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
public class NegIntLitDepType extends x10Test {
	public boolean run() {
		int(:self==-2) f =  -2;
		return true;
	}

	public static void main(String[] args) {
		new NegIntLitDepType().execute();
	}


}

