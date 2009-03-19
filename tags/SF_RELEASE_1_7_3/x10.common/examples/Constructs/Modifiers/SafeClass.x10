/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that the safe annotation is recognized on classes, 
 * and automatically added to methods. Therefore methods of
 * such classes can be called from inside atomic.
 * @author vj  9/2006
 */
public safe class SafeClass extends x10Test {
   
    public  void m() {
    
    }
	public boolean run() {
		atomic { m(); }
		
		return true;
	}

	public static void main(String[] args) {
		new SafeClass().execute();
	}

	
}

