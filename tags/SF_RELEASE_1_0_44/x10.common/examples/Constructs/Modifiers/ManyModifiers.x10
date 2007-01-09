/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that many annotations on a method are recognized.
 * @author vj  9/2006
 */
public class ManyModifiers extends x10Test {

    public local nonblocking safe sequential void m() {
    
    }
	public boolean run() {
		m();
		return true;
	}

	public static void main(String[] args) {
		new ManyModifiers().execute();
	}

	
}

