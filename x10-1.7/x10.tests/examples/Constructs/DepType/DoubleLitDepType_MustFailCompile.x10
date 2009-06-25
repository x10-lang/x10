/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a double lit generates a dep clause.
 */
public class DoubleLitDepType_MustFailCompile extends x10Test {
	public def run(): boolean = {
		var f: double{self==1.2D} = 1.3D;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new DoubleLitDepType_MustFailCompile().execute();
	}


}
