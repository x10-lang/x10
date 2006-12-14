/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * 3 should be an int, and ints are objects.
 *
 * @author vj, igor 09/06
 */
public class PrimitiveLiteralHasLocation extends x10Test {

	public boolean run() {
		return 3.location==here;
	}

	public static void main(String[] args) {
		new PrimitiveLiteralHasLocation().execute();
	}
}

