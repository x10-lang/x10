/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks variable name shadowing works correctly.
 * @author vcave
 **/
public class X10DepTypeClassOne(int p) extends x10Test implements X10InterfaceOne {

	
	public X10DepTypeClassOne(:self.p==p)(final int p) {
	    property(p);
	}

	public boolean run() {
		final int p = 1;
		X10DepTypeClassOne one = 
			new X10DepTypeClassOne(p);
		return one.p() == p;
	}
	
	public void interfaceMethod() {

	}
	
	public static void main(String[] args) {
		new X10DepTypeClassOne(0).execute();
	}
}

