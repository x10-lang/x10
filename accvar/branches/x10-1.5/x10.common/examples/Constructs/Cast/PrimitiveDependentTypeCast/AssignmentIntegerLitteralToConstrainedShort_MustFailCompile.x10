/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Illustrates various scenario where constraints may causes problems with short.
 * Issue: Contraint value is stored as an integer
 * @author vcave
 **/
public class AssignmentIntegerLitteralToConstrainedShort_MustFailCompile extends x10Test {

	public boolean run() {
		final short constraint = 0;
		short (:self == constraint) i = 0;
		return false;
	}

	public static void main(String[] args) {
		new AssignmentIntegerLitteralToConstrainedShort_MustFailCompile().execute();
	}

}
 