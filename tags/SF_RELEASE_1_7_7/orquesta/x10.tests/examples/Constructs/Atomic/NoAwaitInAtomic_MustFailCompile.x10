/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * An await statement cannot occur in an atomic.
 * @vj
 */
public class NoAwaitInAtomic_MustFailTest extends x10Test {

	var b: boolean;
	
	public def run(): boolean = {
		atomic {
		  await b; // must be caught at compile time.
		  }
		  return true;
	}

	public static def main(var args: Rail[String]): void = {
		new NoAwaitInAtomic_MustFailTest().execute();
	}
}
