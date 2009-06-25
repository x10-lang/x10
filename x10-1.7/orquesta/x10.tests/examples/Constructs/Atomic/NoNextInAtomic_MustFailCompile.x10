/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * A next statement cannot occur in an atomic.
 * @vj
 */
public class NoNextInAtomic_MustFailCompile extends x10Test {

	var b: boolean;
	
	public def run(): boolean = {
	async {
	  var c: clock = clock.make();
		atomic {
		  next;
		  }
		 
		  }
		   return true;
	}

	public static def main(var args: Rail[String]): void = {
		new NoNextInAtomic_MustFailCompile().execute();
	}
}
