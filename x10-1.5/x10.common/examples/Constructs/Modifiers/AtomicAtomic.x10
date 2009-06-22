/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a safe method can be overridden only by a safe method.
 * @author vj  9/2006
 */
public class AtomicAtomic extends x10Test {

    atomic void m2() {
    	
    }
	public atomic boolean run() {
		m2();
		return true;
	}

	public static void main(String[] args) {
		new AtomicAtomic().execute();
	}

	
}
