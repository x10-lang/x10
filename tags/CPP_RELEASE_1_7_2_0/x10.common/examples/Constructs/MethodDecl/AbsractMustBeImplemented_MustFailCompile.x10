/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that the local annotation is recognized.
 * @author vj  9/2006
 */
public class AbstractMustBeImplemented_MustFailCompile extends x10Test {

    void m();
	public boolean run() {
		m();
		return true;
	}

	public static void main(String[] args) {
		new AbstractMustBeImplemented_MustFailCompile().execute();
	}

	
}

