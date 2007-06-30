/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that the nonblocking annotation is recognized.
 * @author vj  9/2006
 */
public class NonBlocking extends x10Test {

    public nonblocking void m() {
    
    }
	public boolean run() {
		m();
		return true;
	}

	public static void main(String[] args) {
		new NonBlocking().execute();
	}

	
}

