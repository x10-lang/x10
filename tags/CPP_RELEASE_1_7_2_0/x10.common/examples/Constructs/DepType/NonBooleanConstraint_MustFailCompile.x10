/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The test checks that a nonboolean constraint is rejected.
 *
 * @author pvarma
 */
public class NonBooleanConstraint_MustFailCompile(int i, int j : i ) extends x10Test {

	public NonBooleanConstraint_MustFailCompile(int k) {
	    property(k,k);
	}
	public boolean run() {
	    return true;
	}
	public static void main(String[] args) {
		new NonBooleanConstraint_MustFailCompile(2).execute();
	}
}


