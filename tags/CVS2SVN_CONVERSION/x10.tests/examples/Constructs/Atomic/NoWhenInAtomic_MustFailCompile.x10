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
public class NoWhenInAtomic_MustFailCompile extends x10Test {

	var b: boolean;
	
	public def run(): boolean = {
		atomic {
		  when (b) {
		    System.out.println("Cannot come here"); // must be caught at compile time.
		  }
		  }
		  return true;
	}

	public static def main(var args: Rail[String]): void = {
		new NoWhenInAtomic_MustFailCompile().execute();
	}
}
