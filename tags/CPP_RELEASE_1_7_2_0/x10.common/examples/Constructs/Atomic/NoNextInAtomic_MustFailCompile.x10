/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * A next statement cannot occur in an atomic.
 * @vj
 */
public class NoNextInAtomic_MustFailCompile extends x10Test {

	boolean b;
	
	public boolean run() {
	async {
	  clock c = clock.factory.clock();
		atomic {
		  next;
		  }
		 
		  }
		   return true;
	}

	public static void main(String[] args) {
		new NoNextInAtomic_MustFailCompile().execute();
	}
}

