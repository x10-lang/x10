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
public class NoAsyncInAtomic_MustFailCompile extends x10Test {

	boolean b;
	
	public boolean run() {
		atomic {
		  async {
		    System.out.println("Cannot reach this point.");
		  }
		  }
		  return true;
	}

	public static void main(String[] args) {
		new NoAsyncInAtomic_MustFailCompile().execute();
	}
}
