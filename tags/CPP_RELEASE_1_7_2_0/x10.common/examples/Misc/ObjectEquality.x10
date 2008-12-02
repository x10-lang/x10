/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Comparing objects should not be rewritten to boxed calls.
 * Distilled from the old CompilerNullPointerException test.
 *
 * @author Igor Peshansky
 */
public class ObjectEquality extends x10Test {

	nullable<x10.lang.Object> objField;

	public boolean run() {
		final x10.lang.Object obj = new x10.lang.Object();
		if (obj == objField)
			return false;
		return true;
	}

	public static void main(String[] args) {
		new ObjectEquality().execute();
	}
}

