/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a weird annotation is not recognized.
 * @author vj  9/2006
 */
public class WeirdModifier_MustFailCompile extends x10Test {

    public what void m() = { }

	public def run(): boolean {
		m();
		return true;
	}

	public static def main(args: Rail[String]) {
		new WeirdModifier_MustFailCompile().execute();
	}

	
}

