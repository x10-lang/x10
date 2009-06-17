/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * An await statement cannot occur in an atomic.
 * @vj
 */
public class NoForEachInAtomic_MustFailCompile extends x10Test {

	var b: boolean;
	
	public def run(): boolean = {
		atomic {
		  foreach (val p: Point in [1..10]) {
		    x10.io.Console.OUT.println("Cannot reach this point.");
		  }
		  }
		  return true;
	}

	public static def main(var args: Rail[String]): void = {
		new NoForEachInAtomic_MustFailCompile().execute();
	}
}
