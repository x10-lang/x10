/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that the safe annotation is recognized.
 * @author vj  9/2006
 */
public class SafeStatic extends x10Test {

    
	public boolean run() {
		atomic {
		  chk(true);
		  }
		return true;
	}

	public static void main(String[] args) {
		new SafeStatic().execute();
	}

	
}

