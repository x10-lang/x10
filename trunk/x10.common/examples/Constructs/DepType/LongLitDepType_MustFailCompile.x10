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
public class LongLitDepType_MustFailCompile extends x10Test {
	public boolean run() {
		long(:self==100L) f =  200L;
		return true;
	}

	public static void main(String[] args) {
		new LongLitDepType_MustFailCompile().execute();
	}


}

